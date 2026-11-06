package org.inventory_tracker.integration.cams.service;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inventory_tracker.exception.BadRequestException;
import org.inventory_tracker.integration.cams.PendingPayment.card.PendingCardPayment;
import org.inventory_tracker.integration.cams.PendingPayment.card.PendingCardPaymentService;
import org.inventory_tracker.integration.cams.PendingPayment.transfer.PendingTransfer;
import org.inventory_tracker.integration.cams.PendingPayment.transfer.PendingTransferService;
import org.inventory_tracker.integration.cams.dto.CamsPaymentNotification;
import org.inventory_tracker.integration.cams.dto.CamsWebhookResponse;
import org.inventory_tracker.integration.cams.dto.CardPaymentNotification;
import org.inventory_tracker.integration.cams.event.CamsWebhookEvent;
import org.inventory_tracker.integration.cams.event.CamsWebhookEventRepository;
import org.inventory_tracker.integration.cams.support.EventIds;
import org.inventory_tracker.integration.cams.support.PendingMatcher;
import org.inventory_tracker.repository.PaymentRepository;
import org.inventory_tracker.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles a CAMS notice (contract v1). Whatever the outcome, the whole notice is handled in one database
 * transaction: if anything fails nothing is saved, the reply is a 5xx, and CAMS sends it again.
 * A notice that is a duplicate, is not FuelFlow's, or matches nothing is answered with 200, so CAMS stops retrying.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CamsWebhookService {

    private static final String TRANSFER = "TRANSFER";
    private static final String CARD = "CARD";

    private final PaymentService paymentService;
    private final PendingTransferService pendingTransferService;
    private final PendingCardPaymentService pendingCardPaymentService;
    private final PaymentRepository paymentRepository;
    private final CamsWebhookEventRepository eventRepository;

    /** FuelFlow's institution on CAMS. When it is empty, the institution is not checked. */
    @Value("${cams.institution-id:}")
    private String fuelFlowInstitutionId;

    @PostConstruct
    void warnIfInstitutionIsNotConfigured() {
        if (fuelFlowInstitutionId == null || fuelFlowInstitutionId.isBlank()) {
            log.warn("cams.institution-id (CAMS_INSTITUTION_ID) is not set: CAMS notices are not checked against FuelFlow's institution.");
        }
    }

    @Transactional
    public CamsWebhookResponse handleTransfer(CamsPaymentNotification notification, String rawBody) {
        String eventId = EventIds.resolve(notification.getEventId(), notification.getRequestReference());
        if (eventId == null) {
            throw new BadRequestException("eventId or requestReference is required.");
        }
        if (notification.getAmount() == null) {
            throw new BadRequestException("amount is required.");
        }
        if (isDuplicate(eventId, notification.getRequestReference())) {
            return CamsWebhookResponse.duplicate("Already processed.");
        }
        String terminal = firstNonBlank(notification.getDeviceSerial(), notification.getTid());
        String problem = institutionProblem(notification.getInstitutionId());
        if (problem == null && terminal == null) {
            problem = "the notice has no terminal id";
        }
        if (problem != null) {
            return ignored(eventId, TRANSFER, terminal, notification.getAmount(), problem, rawBody);
        }

        List<PendingTransfer> open = pendingTransferService.findOpen(terminal);
        PendingMatcher.Decision<PendingTransfer> decision =
                PendingMatcher.decide(candidates(open, PendingTransfer::getAmount), notification.getAmount());

        if (decision.kind() == PendingMatcher.Kind.MATCH) {
            notification.setDeviceSerial(terminal);
            paymentService.processCamsTransferPayment(notification, decision.pending());
            String sale = decision.pending().getSaleNumber();
            record(eventId, TRANSFER, "PROCESSED", terminal, notification.getAmount(), "sale " + sale, null);
            return CamsWebhookResponse.processed("Sale " + sale + " paid.");
        }
        if (decision.kind() == PendingMatcher.Kind.AMOUNT_MISMATCH) {
            notification.setDeviceSerial(terminal);
            paymentService.recordUnmatchedCamsTransfer(notification, decision.pending());
            String sale = decision.pending().getSaleNumber();
            record(eventId, TRANSFER, "AMOUNT_MISMATCH", terminal, notification.getAmount(),
                    "sale " + sale + " expected " + decision.pending().getAmount(), rawBody);
            return CamsWebhookResponse.processed("The amount does not match sale " + sale
                    + ". It was recorded for review and the sale was not completed.");
        }
        return ignored(eventId, TRANSFER, terminal, notification.getAmount(),
                "no open transfer payment on terminal " + terminal, rawBody);
    }

    @Transactional
    public CamsWebhookResponse handleCard(CardPaymentNotification notification, String rawBody) {
        String eventId = EventIds.resolve(notification.getEventId(),
                firstNonBlank(notification.getRequestReference(), notification.getRrn()));
        if (eventId == null) {
            throw new BadRequestException("eventId, requestReference or rrn is required.");
        }
        if (notification.getAmount() == null) {
            throw new BadRequestException("amount is required.");
        }
        if (isDuplicate(eventId, notification.getRrn())) {
            return CamsWebhookResponse.duplicate("Already processed.");
        }
        String terminal = notification.getTerminalSerialNumber();
        String problem = institutionProblem(notification.getInstitutionId());
        if (problem == null && !"00".equals(notification.getResponseCode())) {
            problem = "not an approved card sale";
        }
        if (problem == null && (terminal == null || terminal.isBlank())) {
            problem = "the notice has no terminal id";
        }
        if (problem != null) {
            return ignored(eventId, CARD, terminal, notification.getAmount(), problem, rawBody);
        }

        List<PendingCardPayment> open = pendingCardPaymentService.findOpen(terminal);
        PendingMatcher.Decision<PendingCardPayment> decision =
                PendingMatcher.decide(candidates(open, PendingCardPayment::getAmount), notification.getAmount());

        if (decision.kind() == PendingMatcher.Kind.MATCH) {
            paymentService.processCamsCardPayment(notification, decision.pending());
            String sale = decision.pending().getSaleNumber();
            record(eventId, CARD, "PROCESSED", terminal, notification.getAmount(), "sale " + sale, null);
            return CamsWebhookResponse.processed("Sale " + sale + " paid.");
        }
        if (decision.kind() == PendingMatcher.Kind.AMOUNT_MISMATCH) {
            paymentService.recordUnmatchedCamsCard(notification, decision.pending());
            String sale = decision.pending().getSaleNumber();
            record(eventId, CARD, "AMOUNT_MISMATCH", terminal, notification.getAmount(),
                    "sale " + sale + " expected " + decision.pending().getAmount(), rawBody);
            return CamsWebhookResponse.processed("The amount does not match sale " + sale
                    + ". It was recorded for review and the sale was not completed.");
        }
        return ignored(eventId, CARD, terminal, notification.getAmount(),
                "no open card payment on terminal " + terminal, rawBody);
    }

    private boolean isDuplicate(String eventId, String paymentReference) {
        if (eventRepository.existsByEventId(eventId)) {
            return true;
        }
        // guards against a null or blank reference: that would match payments that have no gateway reference at all
        return paymentReference != null && !paymentReference.isBlank()
                && paymentRepository.findByGatewayReference(paymentReference).isPresent();
    }

    /** Null when the notice is for FuelFlow's institution (or the institution is not configured), else the reason. */
    private String institutionProblem(String noticeInstitutionId) {
        if (fuelFlowInstitutionId == null || fuelFlowInstitutionId.isBlank()) {
            return null;
        }
        if (noticeInstitutionId == null || !fuelFlowInstitutionId.trim().equalsIgnoreCase(noticeInstitutionId.trim())) {
            return "the notice is not for FuelFlow's institution";
        }
        return null;
    }

    private CamsWebhookResponse ignored(String eventId, String kind, String terminal, BigDecimal amount, String reason, String rawBody) {
        log.info("CAMS {} notice {} ignored: {}", kind, eventId, reason);
        record(eventId, kind, "IGNORED", terminal, amount, reason, rawBody);
        return CamsWebhookResponse.ignored(reason);
    }

    private void record(String eventId, String kind, String outcome, String terminal, BigDecimal amount, String detail, String rawBody) {
        eventRepository.save(CamsWebhookEvent.builder()
                .eventId(eventId)
                .kind(kind)
                .outcome(outcome)
                .terminalSerial(terminal)
                .amount(amount)
                .detail(detail == null ? null : detail.substring(0, Math.min(detail.length(), 300)))
                .rawBody(rawBody)
                .receivedAt(LocalDateTime.now())
                .build());
    }

    private static <T> List<PendingMatcher.Candidate<T>> candidates(List<T> open, Function<T, BigDecimal> amountOf) {
        List<PendingMatcher.Candidate<T>> candidates = new ArrayList<>();
        for (T pending : open) {
            candidates.add(new PendingMatcher.Candidate<>(pending, amountOf.apply(pending)));
        }
        return candidates;
    }

    private static String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first.trim();
        }
        if (second != null && !second.isBlank()) {
            return second.trim();
        }
        return null;
    }
}
