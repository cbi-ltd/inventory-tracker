// package org.inventory_tracker.service;

// import org.inventory_tracker.exception.*;
// import org.inventory_tracker.entity.OfflinePayment;
// import org.inventory_tracker.dto.response.OfflinePaymentReconciliationResponse;
// import org.inventory_tracker.dto.request.CreateReconciliationRequest;
// import org.inventory_tracker.security.AuthenticatedUserService;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
// import org.inventory_tracker.repository.OfflinePaymentRepository;
// import org.inventory_tracker.repository.OfflinePaymentReconciliationRepository;
// import org.inventory_tracker.entity.Merchant;
// import lombok.RequiredArgsConstructor;
// import org.inventory_tracker.enums.ReconciliationStatus;
// import org.inventory_tracker.enums.PaymentStatus;
// import org.inventory_tracker.entity.OfflinePaymentReconciliation;
// import java.time.LocalDateTime;
// import java.util.UUID;


// @Service
// @RequiredArgsConstructor
// public class OfflinePaymentReconciliationService {

//     private final OfflinePaymentRepository offlinePaymentRepository;
//     private final OfflinePaymentReconciliationRepository reconciliationRepository;
//     private final AuthenticatedUserService authenticatedUserService;

//     @Transactional
//     public OfflinePaymentReconciliationResponse create(
//             CreateReconciliationRequest request) {

//         Merchant merchant = authenticatedUserService.getCurrentMerchant();

//         if (merchant == null) {
//             throw new ResourceNotFoundException(
//                     "Merchant is not authenticated");
//         }

//         OfflinePayment payment = offlinePaymentRepository
//                 .findByIdAndMerchant_Id(
//                         request.getOfflinePaymentId(), merchant.getId())
//                 .orElseThrow(() -> new ResourceNotFoundException(
//                         "Offline payment not found"));

//         if (payment.getStatus() != PaymentStatus.OFFLINE_RECEIVED) {
//             throw new BadRequestException("Only payments confirmed as received can be reconciled");
//         }

//         String transferReference = request.getTransferReference().trim();

//         if (reconciliationRepository
//                 .existsByTransferReferenceAndMerchant_Id(
//                         transferReference, merchant.getId())) {
//             throw new DuplicateResourceException(
//                     "This transfer reference has already been recorded");
//         }

//         OfflinePaymentReconciliation reconciliation =
//                 OfflinePaymentReconciliation.builder()
//                         .reference("REC-" + UUID.randomUUID())
//                         .offlinePayment(payment)
//                         .station(payment.getStation())
//                         .merchant(merchant)
//                         .amount(payment.getAmount())
//                         .transferReference(transferReference)
//                         .sourceAccountNumber(
//                                 payment.getDestinationAccountNumber())
//                         .destinationAccountNumber(
//                                 request.getDestinationAccountNumber().trim())
//                         .status(ReconciliationStatus.PENDING)
//                         .initiatedAt(LocalDateTime.now())
//                         .initiatedBy("AUTHORIZED_OPERATOR")
//                         .build();

//         OfflinePaymentReconciliation saved = reconciliationRepository.save(reconciliation);

//         payment.setStatus(PaymentStatus.RECONCILIATION_PENDING);

//         offlinePaymentRepository.save(payment);

//         return toResponse(saved);
//     }

//     @Transactional
//     public OfflinePaymentReconciliationResponse confirm(Long id) {

//         Merchant merchant = authenticatedUserService.getCurrentMerchant();

//         if (merchant == null) {
//             throw new ResourceNotFoundException(
//                     "Merchant is not authenticated");
//         }

//         OfflinePaymentReconciliation reconciliation =
//                 reconciliationRepository.findByIdAndMerchant_Id(
//                         id, merchant.getId())
//                         .orElseThrow(() -> new ResourceNotFoundException(
//                                 "Reconciliation record not found"));

//         if (reconciliation.getStatus() != ReconciliationStatus.PENDING) {
//             throw new BadRequestException(
//                     "Only pending reconciliations can be confirmed");
//         }

//         /*
//          * Verify the credit in the station's normal account before confirming.
//          * This method must only be called after that verification succeeds.
//          */

//         reconciliation.setStatus(ReconciliationStatus.CONFIRMED);
//         reconciliation.setConfirmedAt(LocalDateTime.now());
//         reconciliation.setConfirmedBy("AUTHORIZED_OPERATOR");

//         OfflinePayment payment = reconciliation.getOfflinePayment();
//         payment.setStatus(PaymentStatus.RECONCILED);

//         reconciliationRepository.save(reconciliation);
//         offlinePaymentRepository.save(payment);

//         return toResponse(reconciliation);
//     }

//     private OfflinePaymentReconciliationResponse toResponse(
//             OfflinePaymentReconciliation r) {

//         return OfflinePaymentReconciliationResponse.builder()
//                 .id(r.getId())
//                 .reference(r.getReference())
//                 .offlinePaymentId(r.getOfflinePayment().getId())
//                 .paymentReference(r.getOfflinePayment().getReference())
//                 .amount(r.getAmount())
//                 .transferReference(r.getTransferReference())
//                 .sourceAccountNumber(r.getSourceAccountNumber())
//                 .destinationAccountNumber(r.getDestinationAccountNumber())
//                 .status(r.getStatus())
//                 .initiatedAt(r.getInitiatedAt())
//                 .confirmedAt(r.getConfirmedAt())
//                 .build();
//     }
// }
