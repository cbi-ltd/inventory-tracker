package org.inventory_tracker.integration.cams.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.inventory_tracker.integration.cams.PendingPayment.PendingTransfer;
import org.inventory_tracker.integration.cams.PendingPayment.PendingTransferService;
import org.inventory_tracker.integration.cams.dto.CamsPaymentNotification;
import org.inventory_tracker.service.PaymentService;



@Slf4j
@Service
@RequiredArgsConstructor
public class CamsWebhookServiceImpl implements CamsWebhookService {

    private final PaymentService paymentService;
    private final PendingTransferService pendingTransferService;

    @Override
    public void processTransferPayment(CamsPaymentNotification notification) {

        PendingTransfer pendingTransfer = pendingTransferService.find(notification.getVirtualAccountNumber());

        if (pendingTransfer == null) {
            log.info("Ignoring payment. No pending FuelFlow transfer for VA={}", notification.getVirtualAccountNumber());
            return;
        }

        log.info("Received CAMS payment notification. requestReference={}", notification.getRequestReference());
        paymentService.processCamsPayment(notification, pendingTransfer);
    }
}
