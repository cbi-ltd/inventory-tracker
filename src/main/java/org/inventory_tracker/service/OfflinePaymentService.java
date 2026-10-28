package org.inventory_tracker.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.inventory_tracker.entity.OfflinePayment;
import org.inventory_tracker.entity.Sale;
import org.inventory_tracker.entity.Merchant;
import org.inventory_tracker.entity.Station;
import org.inventory_tracker.enums.PaymentStatus;
import org.inventory_tracker.repository.OfflinePaymentRepository;
import org.inventory_tracker.exception.BadRequestException;
import org.inventory_tracker.exception.DuplicateResourceException;
import org.inventory_tracker.exception.ResourceNotFoundException;
import org.inventory_tracker.dto.response.OfflinePaymentResponse;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.inventory_tracker.security.AuthenticatedUserService;


@Service
@RequiredArgsConstructor
public class OfflinePaymentService {
    private final OfflinePaymentRepository offlinePaymentRepository;
    private final AuthenticatedUserService authenticatedUserService;

    @Transactional
    public OfflinePaymentResponse createForSale(Sale sale, Station station, Merchant merchant) {

        if (station.getOfflinePaymentAccountNumber() == null
                || station.getOfflinePaymentAccountNumber().isBlank()
                || station.getOfflinePaymentAccountName() == null
                || station.getOfflinePaymentAccountName().isBlank()
                || station.getOfflinePaymentBank() == null
                || station.getOfflinePaymentBank().isBlank()) {

            throw new BadRequestException("Offline payment account is not configured for this station");
        }

        if (sale.getNetAmount() == null || sale.getNetAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Sale amount must be greater than zero");
        }

        if (offlinePaymentRepository.existsBySale_Id(sale.getId())) {
            throw new DuplicateResourceException("An offline payment already exists for this sale");
        }

        OfflinePayment payment = OfflinePayment.builder()
                .reference("OFF-" + UUID.randomUUID())
                .sale(sale)
                .station(station)
                .merchant(merchant)
                .amount(sale.getNetAmount())
                .status(PaymentStatus.PENDING)
                .destinationBankName(station.getOfflinePaymentBank())
                .destinationAccountNumber(station.getOfflinePaymentAccountNumber())
                .destinationAccountName(station.getOfflinePaymentAccountName())
                .build();

        OfflinePayment savedPayment = offlinePaymentRepository.save(payment);
        sale.setOfflinePayment(savedPayment);

        return toResponse(savedPayment);
    }

    private OfflinePaymentResponse toResponse(OfflinePayment payment) {
        return OfflinePaymentResponse.builder()
                .id(payment.getId())
                .reference(payment.getReference())
                .saleId(payment.getSale().getId())
                .stationId(payment.getStation().getId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .bankName(payment.getDestinationBankName())
                .accountNumber(payment.getDestinationAccountNumber())
                .accountName(payment.getDestinationAccountName())
                .customerTransferReference(payment.getCustomerTransferReference())
                .receivedAt(payment.getReceivedAt())
                .build();
    }

    @Transactional
    public OfflinePaymentResponse markReceived(Long id, String customerTransferReference) {
        Merchant merchant = authenticatedUserService.getCurrentMerchant();

        if (merchant == null) {
            throw new ResourceNotFoundException("Merchant is not authenticated");
        }

        OfflinePayment offlinePayment = offlinePaymentRepository
                .findByIdAndMerchant_Id(id, merchant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Offline payment not found"));

        if (offlinePayment.getStatus() != PaymentStatus.PENDING) {
            throw new BadRequestException("Only pending offline payments can be marked as received");
        }

        if (customerTransferReference == null || customerTransferReference.isBlank()) {
            throw new BadRequestException("Customer transfer reference is required");
        }

        offlinePayment.setCustomerTransferReference(customerTransferReference.trim());
        offlinePayment.setReceivedAt(LocalDateTime.now());
        offlinePayment.setStatus(PaymentStatus.OFFLINE_RECEIVED);
        offlinePayment.setConfirmedBy(offlinePayment.getSale().getAttendant().getFullName());

        Sale sale = offlinePayment.getSale();
        sale.setPaymentStatus(PaymentStatus.OFFLINE_RECEIVED);
        offlinePaymentRepository.save(offlinePayment);

        return toResponse(offlinePayment);
    }

    @Transactional(readOnly = true)
    public OfflinePaymentResponse getOfflinePayment(Long id) {
        Merchant merchant = authenticatedUserService.getCurrentMerchant();

        OfflinePayment payment = offlinePaymentRepository.findByIdAndMerchant_Id(id, merchant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Offline payment not found"));

        return toResponse(payment);
    }

    @Transactional(readOnly = true)
    public Page<OfflinePaymentResponse> getOfflinePayments(Pageable pageable) {
        Merchant merchant = authenticatedUserService.getCurrentMerchant();

        return offlinePaymentRepository.findAllByMerchant_Id(merchant.getId(), pageable)
                .map(this::toResponse);
    }

}
