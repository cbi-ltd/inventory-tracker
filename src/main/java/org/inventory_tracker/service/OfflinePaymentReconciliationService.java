package org.inventory_tracker.service;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import org.inventory_tracker.dto.request.CreateReconciliationRequest;
import org.inventory_tracker.dto.response.OfflinePaymentReconciliationResponse;
import org.inventory_tracker.entity.Merchant;
import org.inventory_tracker.entity.OfflinePayment;
import org.inventory_tracker.entity.OfflinePaymentReconciliation;
import org.inventory_tracker.entity.Station;
import org.inventory_tracker.enums.PaymentStatus;
import org.inventory_tracker.enums.ReconciliationStatus;
import org.inventory_tracker.exception.BadRequestException;
import org.inventory_tracker.exception.ResourceNotFoundException;
import org.inventory_tracker.repository.OfflinePaymentReconciliationRepository;
import org.inventory_tracker.repository.OfflinePaymentRepository;
import org.inventory_tracker.security.AuthenticatedUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OfflinePaymentReconciliationService {

    private final OfflinePaymentReconciliationRepository reconciliationRepository;
    private final OfflinePaymentRepository offlinePaymentRepository;
    private final AuthenticatedUserService authenticatedUserService;

    @Transactional
    public OfflinePaymentReconciliationResponse createReconciliation(CreateReconciliationRequest request) {
        Merchant merchant = authenticatedUserService.getCurrentMerchant();

        OfflinePayment offlinePayment = offlinePaymentRepository
                .findByIdAndMerchant_Id(request.getOfflinePaymentId(), merchant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Offline payment not found"));

        if (offlinePayment.getStatus() != PaymentStatus.OFFLINE_RECEIVED) {
            throw new BadRequestException("Only offline payments marked as received can be reconciled");
        }

        Station station = offlinePayment.getStation();

        if (station.getMerchantAccountNumber() == null
                || station.getMerchantAccountNumber().isBlank()) {
            throw new BadRequestException("The station's merchant account number is not configured");
        }

        String transferReference = request.getTransferReference().trim();

        if (transferReference.isBlank()) {
            throw new BadRequestException("Transfer reference is required");
        }

        OfflinePaymentReconciliation reconciliation = OfflinePaymentReconciliation.builder()
                        .reference("REC-" + UUID.randomUUID())
                        .offlinePayment(offlinePayment)
                        .station(station)
                        .merchant(merchant)
                        .amount(offlinePayment.getAmount())
                        .transferReference(transferReference)
                        .sourceAccountNumber(offlinePayment.getDestinationAccountNumber())
                        .destinationAccountNumber(station.getMerchantAccountNumber())
                        .status(ReconciliationStatus.PENDING)
                        .initiatedAt(LocalDateTime.now())
                        .build();

        OfflinePaymentReconciliation savedReconciliation = reconciliationRepository.save(reconciliation);
        offlinePayment.setStatus(PaymentStatus.RECONCILIATION_PENDING);
        offlinePayment.getSale().setPaymentStatus(PaymentStatus.RECONCILIATION_PENDING);

        offlinePaymentRepository.save(offlinePayment);
        return toResponse(savedReconciliation);
    }

    @Transactional(readOnly = true)
    public OfflinePaymentReconciliationResponse getReconciliation(Long id) {
        Merchant merchant = authenticatedUserService.getCurrentMerchant();

        OfflinePaymentReconciliation reconciliation =reconciliationRepository
                        .findByIdAndMerchant_Id(id, merchant.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Reconciliation not found"));

        return toResponse(reconciliation);
    }

    @Transactional(readOnly = true)
    public Page<OfflinePaymentReconciliationResponse> getReconciliations(Pageable pageable) {
        Merchant merchant = authenticatedUserService.getCurrentMerchant();

        return reconciliationRepository
                .findAllByMerchant_Id(merchant.getId(), pageable)
                .map(this::toResponse);
    }

    /**
     * Call this only after independently verifying that the transfer
     * reached the destination merchant account.
     */
    @Transactional
    public OfflinePaymentReconciliationResponse confirmReconciliation(Long reconciliationId) {
        Merchant merchant = authenticatedUserService.getCurrentMerchant();

        OfflinePaymentReconciliation reconciliation =
                reconciliationRepository.findByIdAndMerchant_Id(reconciliationId, merchant.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Reconciliation not found"));

        if (reconciliation.getStatus() != ReconciliationStatus.PENDING) {
            throw new BadRequestException("Only pending reconciliations can be confirmed");
        }

        OfflinePayment offlinePayment = reconciliation.getOfflinePayment();
        if (offlinePayment.getStatus() != PaymentStatus.RECONCILIATION_PENDING) {
            throw new BadRequestException("The offline payment is not awaiting reconciliation");
        }

        // Do not confirm based solely on the transfer reference.
        // Verify the actual credit to the destination account first.

        reconciliation.setStatus(ReconciliationStatus.CONFIRMED);
        reconciliation.setConfirmedAt(LocalDateTime.now());
        reconciliation.setConfirmedBy(getCurrentActor());

        offlinePayment.setStatus(PaymentStatus.RECONCILED);
        offlinePayment.getSale().setPaymentStatus(PaymentStatus.RECONCILED);

        reconciliationRepository.save(reconciliation);
        offlinePaymentRepository.save(offlinePayment);

        return toResponse(reconciliation);
    }

    @Transactional
    public OfflinePaymentReconciliationResponse failReconciliation(Long reconciliationId, String failureReason) {
        Merchant merchant = authenticatedUserService.getCurrentMerchant();

        OfflinePaymentReconciliation reconciliation = reconciliationRepository
                        .findByIdAndMerchant_Id(reconciliationId, merchant.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Reconciliation not found"));

        if (reconciliation.getStatus() != ReconciliationStatus.PENDING) {
            throw new BadRequestException("Only pending reconciliations can be marked as failed");
        }

        if (failureReason == null || failureReason.isBlank()) {
            throw new BadRequestException("A failure reason is required");
        }

        OfflinePayment offlinePayment = reconciliation.getOfflinePayment();

        reconciliation.setStatus(ReconciliationStatus.FAILED);
        reconciliation.setFailureReason(failureReason.trim());
        reconciliation.setConfirmedAt(LocalDateTime.now());
        reconciliation.setConfirmedBy(getCurrentActor());

        // The reconciliation attempt failed, but the original offline
        // payment was received. It can be reconciled again.
        offlinePayment.setStatus(PaymentStatus.OFFLINE_RECEIVED);
        offlinePayment.getSale().setPaymentStatus(PaymentStatus.OFFLINE_RECEIVED);

        reconciliationRepository.save(reconciliation);
        offlinePaymentRepository.save(offlinePayment);

        return toResponse(reconciliation);
    }

    private OfflinePaymentReconciliationResponse toResponse( OfflinePaymentReconciliation reconciliation) {
        return OfflinePaymentReconciliationResponse.builder()
                .id(reconciliation.getId())
                .reference(reconciliation.getReference())
                .offlinePaymentId(reconciliation.getOfflinePayment().getId())
                .stationId(reconciliation.getStation().getId())
                .amount(reconciliation.getAmount())
                .transferReference(reconciliation.getTransferReference())
                .sourceAccountNumber(reconciliation.getSourceAccountNumber())
                .destinationAccountNumber(reconciliation.getDestinationAccountNumber())
                .status(reconciliation.getStatus())
                .initiatedBy(reconciliation.getInitiatedBy())
                .confirmedBy(reconciliation.getConfirmedBy())
                .initiatedAt(reconciliation.getInitiatedAt())
                .confirmedAt(reconciliation.getConfirmedAt())
                .failureReason(reconciliation.getFailureReason())
                .build();
    }

    private String getCurrentActor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new BadRequestException("An authenticated operator is required");
        }

        return authentication.getName();
    }
}
