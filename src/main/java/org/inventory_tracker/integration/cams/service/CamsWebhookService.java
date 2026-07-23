package org.inventory_tracker.integration.cams.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import org.inventory_tracker.integration.cams.PendingPayment.card.PendingCardPayment;
import org.inventory_tracker.integration.cams.PendingPayment.card.PendingCardPaymentService;
import org.inventory_tracker.integration.cams.PendingPayment.transfer.PendingTransfer;
import org.inventory_tracker.integration.cams.PendingPayment.transfer.PendingTransferService;
import org.inventory_tracker.integration.cams.dto.CamsPaymentNotification;
import org.inventory_tracker.integration.cams.dto.CardPaymentNotification;
import org.inventory_tracker.service.PaymentService;



@Slf4j
@Service
@RequiredArgsConstructor
public class CamsWebhookService {

    private final PaymentService paymentService;
    private final PendingTransferService pendingTransferService;
    private final PendingCardPaymentService pendingCardPaymentService;


    public void processTransferPayment(CamsPaymentNotification notification) {

        PendingTransfer pendingTransfer = pendingTransferService.find(notification.getVirtualAccountNumber());

        if (pendingTransfer == null) {
            log.info("Ignoring payment. No pending FuelFlow transfer for VA={}", notification.getVirtualAccountNumber());
            return;
        }

        log.info("Received CAMS payment notification. requestReference={}", notification.getRequestReference());
        paymentService.processCamsTransferPayment(notification, pendingTransfer);
    }


    public void processCardPayment(CardPaymentNotification notification) {

        if (!"00".equals(notification.getResponseCode())) {
            log.info("Ignoring unsuccessful card payment.");
            return;
        }

        PendingCardPayment pending = pendingCardPaymentService.consume(notification.getTerminalId(), notification.getAmount());

        if (pending == null) {
            log.info("Card payment does not belong to FuelFlow.");
            return;
        }

        paymentService.processCamsCardPayment(notification, pending);
    }
}

