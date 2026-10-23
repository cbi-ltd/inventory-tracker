// package org.inventory_tracker.service;

// import org.springframework.stereotype.Service;
// import lombok.RequiredArgsConstructor;
// import org.inventory_tracker.entity.OfflinePayment;
// import org.inventory_tracker.entity.Sale;
// import org.inventory_tracker.entity.Merchant;
// import org.inventory_tracker.entity.Station;
// import org.inventory_tracker.enums.PaymentStatus;
// import org.inventory_tracker.repository.OfflinePaymentRepository;
// import org.inventory_tracker.exception.ResourceNotFoundException;
// import org.inventory_tracker.exception.BadRequestException;
// import org.inventory_tracker.exception.DuplicateResourceException;
// import org.inventory_tracker.dto.response.OfflinePaymentResponse;
// import org.inventory_tracker.security.*;
// import org.springframework.transaction.annotation.Transactional;
// import java.time.LocalDateTime;
// import java.util.UUID;
// import java.util.Optional;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Sort;
// import org.springframework.data.domain.Sort.Direction;
// import org.springframework.data.domain.PageImpl;
// import java.util.List;
// import java.util.stream.Collectors;


// @Service
// @RequiredArgsConstructor
// public class OfflinePaymentService {

//     private final OfflinePaymentRepository offlinePaymentRepository;
//     private final AuthenticatedUserService authenticatedUserService;


//     @Transactional
//     public OfflinePaymentResponse createForSale(Sale sale) {

//         Merchant merchant = authenticatedUserService.getCurrentMerchant();
//         if (merchant == null) {
//             throw new ResourceNotFoundException("Merchant is not authenticated");
//         }

//         Station station = sale.getPump().getStation();
//         if (station == null) {
//             throw new BadRequestException("The sale is not associated with a station");
//         }

//         if (offlinePaymentRepository.existsBySale_Id(sale.getId())) {
//             throw new DuplicateResourceException("An offline payment already exists for this sale");
//         }

//         // OfflinePaymentAccount account = accountRepository
//         //         .findByStation_IdAndMerchant_IdAndActiveTrue(
//         //                 station.getId(), merchant.getId())
//         //         .stream()
//         //         .findFirst()
//         //         .orElseThrow(() -> new ResourceNotFoundException(
//         //                 "No active offline payment account is configured "
//         //                 + "for this station"));

//         if (sale.getNetAmount() == null || sale.getNetAmount().signum() <= 0) {
//             throw new BadRequestException("Sale amount must be greater than zero");
//         }

//         OfflinePayment payment = OfflinePayment.builder()
//                 .reference("OFF-" + UUID.randomUUID())
//                 .sale(sale)
//                 .station(station)
//                 .merchant(merchant)
//                 .offlinePaymentAccount(account)
//                 .amount(sale.getNetAmount())
//                 .status(PaymentStatus.PENDING)
//                 .destinationBankName(account.getBankName())
//                 .destinationAccountNumber(account.getAccountNumber())
//                 .destinationAccountName(account.getAccountName())
//                 .build();

//         return toResponse(offlinePaymentRepository.save(payment));
//     }

//     @Transactional(readOnly = true)
//     public OfflinePaymentResponse getById(Long id) {

//         Merchant merchant = authenticatedUserService.getCurrentMerchant();
//         if (merchant == null) {
//             throw new ResourceNotFoundException("Merchant is not authenticated");
//         }

//         OfflinePayment payment = offlinePaymentRepository
//                 .findByIdAndMerchant_Id(id, merchant.getId())
//                 .orElseThrow(() -> new ResourceNotFoundException("Offline payment not found"));

//         return toResponse(payment);
//     }

//                                 // @Transactional
//                                 // public OfflinePaymentResponse confirmReceipt(Long id, ConfirmOfflinePaymentRequest request) {

//                                 //     Merchant merchant = authenticatedUserService.getCurrentMerchant();

//                                 //     if (merchant == null) {
//                                 //         throw new ResourceNotFoundException("Merchant is not authenticated");
//                                 //     }

//                                 //     OfflinePayment payment = offlinePaymentRepository
//                                 //             .findByIdAndMerchant_Id(id, merchant.getId())
//                                 //             .orElseThrow(() -> new ResourceNotFoundException(
//                                 //                     "Offline payment not found"));

//                                 //     if (payment.getStatus() != OfflinePaymentStatus.PENDING) {
//                                 //         throw new BadRequestException(
//                                 //                 "Only pending payments can be confirmed");
//                                 //     }

//                                 //     /*
//                                 //      * IMPORTANT:
//                                 //      * Verify the bank credit before reaching this point.
//                                 //      * Do not treat the customer's supplied reference as verification.
//                                 //      */

//                                 //     payment.setCustomerTransferReference(
//                                 //             request.getCustomerTransferReference().trim());

//                                 //     payment.setReceivedAt(LocalDateTime.now());

//                                 //     // Replace with the authenticated operator's actual ID/username.
//                                 //     payment.setConfirmedBy("AUTHORIZED_OPERATOR");

//                                 //     payment.setStatus(
//                                 //             OfflinePaymentStatus.RECONCILIATION_PENDING);

//                                 //     return toResponse(offlinePaymentRepository.save(payment));
//                                 // }

//     private OfflinePaymentResponse toResponse(OfflinePayment payment) {

//         return OfflinePaymentResponse.builder()
//                 .id(payment.getId())
//                 .reference(payment.getReference())
//                 .saleId(payment.getSale().getId())
//                 .stationId(payment.getStation().getId())
//                 .amount(payment.getAmount())
//                 .status(payment.getStatus())
//                 .bankName(payment.getDestinationBankName())
//                 .accountNumber(payment.getDestinationAccountNumber())
//                 .accountName(payment.getDestinationAccountName())
//                 .customerTransferReference(
//                         payment.getCustomerTransferReference())
//                 .receivedAt(payment.getReceivedAt())
//                 .build();
//     }
// }
