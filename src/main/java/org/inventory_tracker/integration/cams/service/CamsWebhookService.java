package org.inventory_tracker.integration.cams.service;


import org.inventory_tracker.integration.cams.dto.CamsPaymentNotification;

public interface CamsWebhookService {

    void processTransferPayment(CamsPaymentNotification notification);

}
