package org.inventory_tracker.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.inventory_tracker.dto.request.CreateSaleRequest;
import org.inventory_tracker.dto.request.PaymentFilterRequest;
import org.inventory_tracker.entity.specification.PaymentSpecification;
import org.inventory_tracker.config.mapper.PaymentMapper;
import org.inventory_tracker.config.mapper.SaleMapper;
import org.inventory_tracker.dto.response.PaymentResponse;
import org.inventory_tracker.dto.response.SaleResponse;
import org.inventory_tracker.entity.Attendant;
import org.inventory_tracker.entity.Merchant;
import org.inventory_tracker.entity.Payment;
import org.inventory_tracker.entity.Product;
import org.inventory_tracker.entity.Pump;
import org.inventory_tracker.entity.PumpAssignment;
import org.inventory_tracker.entity.PumpAudit;
import org.inventory_tracker.entity.Sale;
import org.inventory_tracker.entity.Station;
import org.inventory_tracker.entity.StationInventory;
import org.inventory_tracker.entity.Terminal;
// import org.inventory_tracker.entity.security.MerchantContext;
import org.inventory_tracker.enums.InventoryTransactionType;
import org.inventory_tracker.enums.PaymentMethod;
import org.inventory_tracker.enums.PaymentStatus;
import org.inventory_tracker.enums.SaleStatus;
import org.inventory_tracker.exception.BadRequestException;
import org.inventory_tracker.exception.DuplicateResourceException;
import org.inventory_tracker.exception.ResourceNotFoundException;
import org.inventory_tracker.integration.cams.PendingPayment.card.PendingCardPayment;
import org.inventory_tracker.integration.cams.PendingPayment.card.PendingCardPaymentService;
import org.inventory_tracker.integration.cams.PendingPayment.transfer.PendingTransfer;
import org.inventory_tracker.integration.cams.PendingPayment.transfer.PendingTransferService;
import org.inventory_tracker.integration.cams.dto.CamsPaymentNotification;
import org.inventory_tracker.integration.cams.dto.CardPaymentNotification;
import org.inventory_tracker.repository.PaymentRepository;
import org.inventory_tracker.repository.PumpAssignmentRepository;
import org.inventory_tracker.repository.PumpAuditRepository;
import org.inventory_tracker.repository.PumpRepository;
import org.inventory_tracker.repository.SaleRepository;
import org.inventory_tracker.repository.StationInventoryRepository;
import org.inventory_tracker.repository.TerminalRepository;
import org.inventory_tracker.security.AuthenticatedUserService;
import org.inventory_tracker.security.MerchantPrincipal;
import org.inventory_tracker.util.ShiftUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final SaleRepository saleRepository;
    private final TerminalRepository terminalRepository;
    private final PendingTransferService pendingTransferService;
    private final PendingCardPaymentService pendingCardPaymentService;
    private final SaleMapper saleMapper;
    private final StationInventoryRepository stationInventoryRepository;
    private final InventoryTransactionService inventoryTransactionService;
    private final PumpRepository pumpRepository;
    private final PumpAssignmentRepository pumpAssignmentRepository;
    private final PumpAuditRepository pumpAuditRepository;
    private final AuthenticatedUserService authenticatedUserService;

    @Transactional
    public PaymentResponse recordCashPayment(Long saleId) {
        Sale sale = saleRepository.findById(saleId).orElseThrow(() -> new ResourceNotFoundException("Sale not found."));

        verifySaleOwnership(sale);
        validateSale(sale);
        validatePaymentMethod(PaymentMethod.CASH);

        if (paymentRepository.findBySaleId(saleId).isPresent()) {
            throw new BadRequestException("Payment already exists for this sale.");
        }

        Payment payment = new Payment();

        payment.setPaymentNumber(generatePaymentNumber());
        payment.setSale(sale);
        payment.setAmount(sale.getNetAmount());
        payment.setPaymentMethod(PaymentMethod.CASH);
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setTransactionReference(sale.getSaleNumber());
        payment.setPaymentTime(LocalDateTime.now());

        validateAmount(sale, payment);
        payment = paymentRepository.save(payment);
        return paymentMapper.toResponse(payment);
    }

    @Transactional
    public PaymentResponse processCamsTransferPayment(CamsPaymentNotification notification, PendingTransfer pendingTransfer) {
        Sale sale = saleRepository.findBySaleNumber(pendingTransfer.getSaleNumber())
                            .orElseThrow(() -> new ResourceNotFoundException("Sale not found."));

        Terminal terminal = terminalRepository.findByTid(notification.getDeviceSerial())
                                .orElseThrow(() -> new ResourceNotFoundException("Terminal not found."));

        paymentRepository.findByGatewayReference(notification.getRequestReference())
                    .ifPresent(payment -> { throw new DuplicateResourceException("Payment has already been processed."); });

        verifySaleTerminalRelationship(sale, terminal);

        return createTransferPaymentFromCamsNotification(sale, notification, terminal, pendingTransfer);
    }

    @Transactional
    public PaymentResponse processCamsCardPayment(CardPaymentNotification cardPaymentNotification, PendingCardPayment pendingCardPayment) {
        Sale sale = saleRepository.findBySaleNumber(pendingCardPayment.getSaleNumber())
                        .orElseThrow(() -> new ResourceNotFoundException("Sale not found."));

        Terminal terminal = terminalRepository.findByTid(cardPaymentNotification.getTerminalId())
                                .orElseThrow(() -> new ResourceNotFoundException("Terminal not found."));
        verifySaleTerminalRelationship(sale, terminal);

        paymentRepository.findByGatewayReference(cardPaymentNotification.getRrn())
                        .ifPresent(payment -> { throw new DuplicateResourceException("Payment has already been processed."); });

        return createCardPaymentFromCamsNotification(sale, cardPaymentNotification, terminal, pendingCardPayment);
    }

    @Transactional
    public SaleResponse createTerminalSale(CreateSaleRequest request) {
        try {
                Pump pump = pumpRepository.findById(request.getPumpId())
                                .orElseThrow(() -> new ResourceNotFoundException("Pump not found"));
                verifyStationOwnership(pump.getStation());

                PumpAssignment assignment = pumpAssignmentRepository.findFirstByPumpIdAndActiveTrue(pump.getId())
                                                .orElseThrow(() ->new ResourceNotFoundException("Pump is not currently assigned."));

                Terminal terminal = assignment.getTerminal();
                Attendant attendant = assignment.getAttendant();

                Station station = pump.getStation();
                Product product = pump.getProduct();

                StationInventory inventory = stationInventoryRepository.findByStationIdAndProductId(station.getId(), product.getId())
                                                .orElseThrow(() -> new ResourceNotFoundException("Station inventory not found"));

                BigDecimal costPerUnit = inventory.getCostPerUnit();
                BigDecimal sellingPrice = inventory.getSellingPrice();
                BigDecimal quantity;
                BigDecimal grossAmount;

                if (request.getQuantity() != null) {
                        quantity = request.getQuantity();
                        grossAmount =quantity.multiply(sellingPrice);
                }
                else {
                        grossAmount = request.getAmount();
                        if (sellingPrice.compareTo(BigDecimal.ZERO) == 0) { throw new BadRequestException("Selling price cannot be zero."); }
                        quantity = grossAmount.divide(sellingPrice, 3, RoundingMode.HALF_UP);
                }

                if (inventory.getCurrentQuantity().compareTo(quantity) < 0) {
                        throw new BadRequestException("Insufficient stock available.");
                }

                Sale sale = saleMapper.toEntity(request);
                sale.setStation(station);
                sale.setPump(pump);
                sale.setTerminal(terminal);
                sale.setAttendant(attendant);
                sale.setProduct(product);
                sale.setSaleNumber(generateSaleNumber(station));
                sale.setReceiptNumber(generateReceiptNumber());
                sale.setSaleTime(LocalDateTime.now());
                sale.setSellingPrice(sellingPrice);
                sale.setShift(assignment.getShift());
                sale.setBusinessDate(assignment.getAssignmentDate());
                sale.setQuantity(quantity);
                sale.setGrossAmount(grossAmount);

                BigDecimal discount = request.getDiscountAmount() == null ? BigDecimal.ZERO : request.getDiscountAmount();
                sale.setDiscountAmount(discount);
                sale.setNetAmount(calculateNetAmount(grossAmount, discount));

                sale.setInventoryUpdated(false);

                switch (request.getPaymentMethod()) {
                case CASH -> {
                        sale.setPaymentStatus(PaymentStatus.SUCCESS);
                        sale.setSaleStatus(SaleStatus.PENDING);
                }

                case CARD, TRANSFER, MIXED -> {
                        sale.setPaymentStatus(PaymentStatus.PENDING);
                        sale.setSaleStatus(SaleStatus.PENDING);
                }

                default -> throw new BadRequestException(
                        "Unsupported payment method.");
                }

                saleRepository.save(sale);

                switch (request.getPaymentMethod()) {
                        case TRANSFER -> pendingTransferService.registerPendingTransfer(station.getMerchantAccountNumber(), sale.getSaleNumber(), sale.getNetAmount(), terminal.getTerminalSerialNumber());
                        case CARD -> pendingCardPaymentService.register(sale.getSaleNumber(), sale.getNetAmount(),terminal.getTerminalSerialNumber(), terminal.getTid());
                        case CASH ->  {}
                        case MIXED -> {}
                }

                if (sale.getPaymentMethod() == PaymentMethod.CASH) {
                        recordCashPayment(sale.getId());
                        return completeCashSale(sale.getId());
                }

                return saleMapper.toResponse(sale);
        }
        catch (ResourceNotFoundException | BadRequestException e) { throw e; } 
        catch (ArithmeticException e) {
                throw new BadRequestException("Calculation error during transaction: " + e.getMessage());
        } 
        catch (Exception e) {
                throw new RuntimeException("Failed to process sale due to an internal error: " + e.getMessage(), e);
        }
    }

    @Transactional
    public SaleResponse createSale(CreateSaleRequest request) {
        try {
            Terminal terminal = terminalRepository.findByTerminalSerialNumberAndActiveTrue(request.getTerminalSerialNumber())
                            .orElseThrow(() -> new ResourceNotFoundException("Active terminal not found"));

            Station station = terminal.getStation();

            if (station == null) {
                throw new ResourceNotFoundException("Terminal is not associated with a station");
            }

            Merchant merchant = station.getMerchant();

            if (merchant == null) {
                throw new ResourceNotFoundException("Station is not associated with a merchant");
            }

            Pump pump = pumpRepository.findByIdAndStation_IdAndStation_Merchant_CamsMerchantId(
                                    request.getPumpId(),
                                    station.getId(),
                                    merchant.getCamsMerchantId())
                            .orElseThrow(() -> new ResourceNotFoundException("Pump not found for this terminal's station"));

            PumpAssignment assignment = pumpAssignmentRepository
                            .findFirstByTerminal_IdAndPump_IdAndActiveTrue(terminal.getId(), pump.getId())
                            .orElseThrow(() -> new ResourceNotFoundException( "Pump is not currently assigned to this terminal"));

            Attendant attendant = assignment.getAttendant();

            if (attendant == null) {
                throw new ResourceNotFoundException("No attendant assigned to this pump");
            }

            Product product = pump.getProduct();

            if (product == null) {
                throw new ResourceNotFoundException("No product configured for this pump");
            }

            StationInventory inventory = stationInventoryRepository
                            .findByStation_IdAndStation_Merchant_CamsMerchantIdAndProduct_Id(
                                    station.getId(),
                                    merchant.getCamsMerchantId(),
                                    product.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Station inventory not found"));

            BigDecimal costPerUnit = inventory.getCostPerUnit();
            BigDecimal sellingPrice = inventory.getSellingPrice();

            if (sellingPrice == null) {
                throw new BadRequestException("Selling price is not configured for this product");
            }

            BigDecimal quantity;
            BigDecimal grossAmount;

            if (request.getQuantity() != null) {
                quantity = request.getQuantity();

                if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new BadRequestException("Quantity must be greater than zero.");
                }
                grossAmount = quantity.multiply(sellingPrice);
            } 
            else {
                grossAmount = request.getAmount();

                if (grossAmount == null || grossAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new BadRequestException("Amount must be greater than zero.");
                }

                if (sellingPrice.compareTo(BigDecimal.ZERO) == 0) {
                    throw new BadRequestException("Selling price cannot be zero.");
                }

                quantity = grossAmount.divide(sellingPrice, 3, RoundingMode.HALF_UP);
            }

            if (inventory.getCurrentQuantity().compareTo(quantity) < 0) {
                throw new BadRequestException("Insufficient stock available.");
            }

            Sale sale = saleMapper.toEntity(request);
            sale.setStation(station);
            sale.setPump(pump);
            sale.setTerminal(terminal);
            sale.setAttendant(attendant);
            sale.setProduct(product);
            sale.setSaleNumber(generateSaleNumber(station));
            sale.setReceiptNumber(generateReceiptNumber());
            sale.setSaleTime(LocalDateTime.now());
            sale.setSellingPrice(sellingPrice);
            sale.setShift(assignment.getShift());
            sale.setBusinessDate(assignment.getAssignmentDate());
            sale.setQuantity(quantity);
            sale.setGrossAmount(grossAmount);

            BigDecimal discount = request.getDiscountAmount() == null ? BigDecimal.ZERO : request.getDiscountAmount();
            sale.setDiscountAmount(discount);
            sale.setNetAmount(calculateNetAmount(grossAmount, discount));
            sale.setInventoryUpdated(false);

            switch (request.getPaymentMethod()) {
                case CASH -> {
                    sale.setPaymentStatus(PaymentStatus.SUCCESS);
                    sale.setSaleStatus(SaleStatus.PENDING);
                }

                case CARD, TRANSFER, MIXED -> {
                    sale.setPaymentStatus(PaymentStatus.PENDING);
                    sale.setSaleStatus(SaleStatus.PENDING);
                }

                default -> throw new BadRequestException("Unsupported payment method.");
            }

            saleRepository.save(sale);

            switch (request.getPaymentMethod()) {
                case TRANSFER ->
                        pendingTransferService.registerPendingTransfer(
                                station.getMerchantAccountNumber(),
                                sale.getSaleNumber(),
                                sale.getNetAmount(),
                                terminal.getTerminalSerialNumber());

                case CARD ->
                        pendingCardPaymentService.register(
                                sale.getSaleNumber(),
                                sale.getNetAmount(),
                                terminal.getTerminalSerialNumber(),
                                terminal.getTid());

                case CASH -> {}
                case MIXED -> {}
            }

            if (sale.getPaymentMethod() == PaymentMethod.CASH) {
                recordCashPayment(sale.getId());
                return completeCashSale(sale.getId());
            }

            return saleMapper.toResponse(sale);
        } 
        catch (ResourceNotFoundException | BadRequestException e) {
            throw e;
        } 
        catch (ArithmeticException e) {
            throw new BadRequestException("Calculation error during transaction: "+ e.getMessage());
        } 
        catch (Exception e) {
            throw new RuntimeException("Failed to process sale due to an internal error: " + e.getMessage(), e);
        }
    }

    @Transactional
    public SaleResponse completeSale(Long saleId, String transactionReference, PaymentStatus paymentStatus, LocalDateTime paidAt) {
        Sale sale = saleRepository.findById(saleId).orElseThrow(() -> new ResourceNotFoundException("Sale not found"));
        verifySaleOwnership(sale);

        if (sale.getInventoryUpdated()) { return saleMapper.toResponse(sale); }

        StationInventory inventory = stationInventoryRepository.findByStationIdAndProductId(sale.getStation().getId(), sale.getProduct().getId())
                                                .orElseThrow(() -> new ResourceNotFoundException("Station inventory not found"));

        if (inventory.getCurrentQuantity().compareTo(sale.getQuantity()) < 0) {
                throw new BadRequestException("Insufficient inventory.");
        }

        Long inventoryId = inventory.getId();

        inventoryTransactionService.recordTransaction(
                inventoryId,
                InventoryTransactionType.SALE,
                sale.getQuantity(),
                sale.getSaleNumber(),
                "SALE-" + sale.getSaleNumber());

        sale.setInventoryUpdated(true);
        sale.setPaymentStatus(paymentStatus);
        sale.setSaleStatus(SaleStatus.COMPLETED);
        sale.setTransactionReference(transactionReference);
        sale.setPaidAt(paidAt);

        saleRepository.save(sale);
        updateCurrentPumpAudit(sale);
        return saleMapper.toResponse(sale);
    }

    @Transactional
    public SaleResponse completeCashSale(Long saleId) {
        return completeSale(saleId, saleRepository.findById(saleId).orElseThrow().getSaleNumber(), PaymentStatus.SUCCESS, LocalDateTime.now());
    }

    // @Transactional
    // public PaymentResponse recordElectronicPayment(Long saleId, String transactionReference, PaymentMethod paymentMethod,
    //                     PaymentStatus paymentStatus, String rrn, String stan, Long terminalId, String merchantId,
    //                     String outletId, String authorizationCode, String cardScheme, String responseCode,
    //                     String responseMessage, LocalDateTime paymentTime) {

    //     validatePaymentMethod(paymentMethod);
    //     validatePaymentStatus(paymentStatus);
    //     validateDuplicatePayment(transactionReference);

    //     if (paymentMethod == PaymentMethod.CASH) {
    //         throw new BadRequestException("Use recordCashPayment() for cash payments.");
    //     }

    //     Sale sale = saleRepository.findById(saleId).orElseThrow(() -> new ResourceNotFoundException("Sale not found."));

    //     if (paymentRepository.existsByTransactionReference(transactionReference)) {
    //         return paymentMapper.toResponse(paymentRepository.findByTransactionReference(transactionReference).orElseThrow());
    //     }

    //     Terminal terminal = null;

    //     if (terminalId != null) {
    //         terminal = terminalRepository.findById(terminalId).orElseThrow(() -> new ResourceNotFoundException("Terminal not found."));
    //     }

    //     validateTerminal(terminal, paymentMethod);

    //     Payment payment = new Payment();
    //     payment.setPaymentNumber(generatePaymentNumber());

    //     payment.setSale(sale);
    //     payment.setAmount(sale.getNetAmount());
    //     payment.setPaymentMethod(paymentMethod);
    //     payment.setPaymentStatus(paymentStatus);
    //     payment.setTransactionReference(transactionReference);
    //     payment.setRrn(rrn);
    //     payment.setStan(stan);
    //     payment.setTerminal(terminal);
    //     payment.setMerchantId(merchantId);
    //     payment.setOutletId(outletId);
    //     payment.setAuthorizationCode(authorizationCode);
    //     payment.setCardScheme(cardScheme);
    //     payment.setResponseCode(responseCode);
    //     payment.setResponseMessage(responseMessage);
    //     payment.setPaymentTime(paymentTime != null ? paymentTime : LocalDateTime.now());

    //     validateAmount(sale, payment);
    //     payment = paymentRepository.save(payment);
    //     return paymentMapper.toResponse(payment);
    // }

    @Transactional(readOnly = true)
    public List<PaymentResponse> filterPayments(PaymentFilterRequest request) {
        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
        if(principal == null) {
            throw new ResourceNotFoundException("Merchant is not authenticated");
        }

        if (request.getMinAmount() != null
                && request.getMaxAmount() != null
                && request.getMinAmount().compareTo(request.getMaxAmount()) > 0) {

            throw new BadRequestException("Minimum amount cannot be greater than maximum amount.");
        }

        if (request.getStartDate() != null
                && request.getEndDate() != null
                && request.getStartDate().isAfter(request.getEndDate())) {

            throw new BadRequestException("Start date cannot be after end date.");
        }

        // return paymentRepository.findAll(PaymentSpecification.filter(request))
        //             .stream().map(paymentMapper::toResponse).toList();

        Specification<Payment> specification = PaymentSpecification.filter(request)
                    .and((root, query, criteriaBuilder) -> criteriaBuilder.equal(
                                    root.get("sale").get("station")
                                            .get("merchant").get("id"), principal.getMerchantDbId()));
        return paymentRepository.findAll(specification).stream()
                        .map(paymentMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Payment not found."));

        verifyPaymentOwnership(payment);
        return paymentMapper.toResponse(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByTransactionReference(String transactionReference) {
        Payment payment = paymentRepository.findByTransactionReference(transactionReference)
                        .orElseThrow(() -> new ResourceNotFoundException("Payment not found."));

        verifyPaymentOwnership(payment);
        return paymentMapper.toResponse(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentBySale(Long saleId) {
        Sale sale = saleRepository.findById(saleId)
                        .orElseThrow(() -> new ResourceNotFoundException("Sale not found."));

        verifySaleOwnership(sale);
        Payment payment = paymentRepository.findBySaleId(saleId)
                        .orElseThrow(() -> new ResourceNotFoundException("Payment not found."));

        return paymentMapper.toResponse(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments() {
        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
        if(principal == null) {
            throw new ResourceNotFoundException("Merchant is not authenticated");
        }
        return paymentRepository.findAllByMerchantIdOrderByPaymentTimeDesc( principal.getMerchantDbId()).stream()
                                    .map(paymentMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByStatus(PaymentStatus status) {
        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();

        if(principal == null) {
            throw new ResourceNotFoundException("Merchant is not authenticated");
        }
        return paymentRepository.findBySale_Station_Merchant_IdAndPaymentStatusOrderByPaymentTimeDesc(principal.getMerchantDbId(), status)
                                    .stream().map(paymentMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByMethod(PaymentMethod method) {
        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
        if(principal == null) {
            throw new ResourceNotFoundException("Merchant is not authenticated");
        }
        return paymentRepository.findBySale_Station_Merchant_IdAndPaymentMethodOrderByPaymentTimeDesc(principal.getMerchantDbId(), method)
                                    .stream().map(paymentMapper::toResponse).toList();
    }

    @Transactional
    public PaymentResponse updatePaymentStatus(String transactionReference, PaymentStatus paymentStatus, String responseCode, String responseMessage) {
        Payment payment = paymentRepository.findByTransactionReference(transactionReference).orElseThrow(() -> new ResourceNotFoundException("Payment not found."));
        verifyPaymentOwnership(payment);
        
        payment.setPaymentStatus(paymentStatus);
        payment.setResponseCode(responseCode);
        payment.setResponseMessage(responseMessage);
        payment = paymentRepository.save(payment);

        return paymentMapper.toResponse(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByPaymentNumber(String paymentNumber) {
        Payment payment = paymentRepository.findByPaymentNumber(paymentNumber)
                                    .orElseThrow(() -> new ResourceNotFoundException("Payment not found."));
        verifyPaymentOwnership(payment);
        return paymentMapper.toResponse(payment);
    }

    @Transactional
    public PaymentResponse cancelPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> new ResourceNotFoundException("Payment not found."));
        verifyPaymentOwnership(payment);
        validatePaymentCanBeCancelled(payment);

        payment.setPaymentStatus(PaymentStatus.CANCELLED);
        payment = paymentRepository.save(payment);

        return paymentMapper.toResponse(payment);
    }

    private String generatePaymentNumber() {
        return "PAY-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }

    // remove this validation and redesign the payment model if multiple Payment records per Sale is eventually allowed
    private void validatePaymentMethod(PaymentMethod paymentMethod) {
        if (paymentMethod == null) { throw new BadRequestException("Payment method is required."); }
        if (paymentMethod == PaymentMethod.MIXED) { throw new BadRequestException("Mixed payments are not currently supported."); }
    }

    private void validatePaymentStatus(PaymentStatus paymentStatus) {
        if (paymentStatus == null) { throw new BadRequestException("Payment status is required."); }
    }

    private void validateAmount(Sale sale, Payment payment) {
        if (payment.getAmount() == null) { throw new BadRequestException("Payment amount is required."); }
        if (payment.getAmount().compareTo(sale.getNetAmount()) != 0) { throw new BadRequestException("Payment amount does not match sale amount."); }
    }

    private void validateDuplicatePayment(String transactionReference) {
        if (transactionReference == null|| transactionReference.isBlank()) {
            throw new BadRequestException( "Transaction reference is required.");
        }

        if (paymentRepository.existsByTransactionReference(transactionReference)) {
            throw new BadRequestException("Payment with transaction reference '"
                            + transactionReference
                            + "' already exists.");
        }
    }

    private void validateSale(Sale sale) {
        if (sale == null) { throw new BadRequestException("Sale is required."); }
        if (sale.getSaleStatus() == SaleStatus.CANCELLED) { throw new BadRequestException("Cannot process payment for a cancelled sale.");}
    }

    private void validateTerminal(Terminal terminal, PaymentMethod paymentMethod) {
        if (paymentMethod == PaymentMethod.CASH) { return; }
        if (terminal == null) { throw new BadRequestException("Electronic payments require a terminal.");}
    }

    private void validatePaymentCanBeCancelled(Payment payment) {
        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            throw new BadRequestException("Successful payments cannot be cancelled.");
        }
    }

    private PaymentResponse createTransferPaymentFromCamsNotification(Sale sale, CamsPaymentNotification notification, Terminal terminal, PendingTransfer pendingTransfer) {

        Payment payment = Payment.builder()
                .paymentNumber(generatePaymentNumber())
                .sale(sale)
                // .paymentMethod(PaymentMethod.TRANSFER)
                .paymentStatus(PaymentStatus.SUCCESS)
                .gatewayReference(notification.getRequestReference())
                .gatewayTransactionReference(notification.getSessionId())
                .amount(notification.getAmount())
                .paidAt(notification.getPaymentTime())
                .payerName(notification.getPayerName())
                .payerAccountNumber(notification.getPayerAccountNumber())
                .payerBank(notification.getPayerBankName())
                .paymentMethod(notification.getPaymentMethod())
                .terminal(terminal)
                .gatewayTransactionReference(notification.getSessionId())
                .outletId(notification.getOutletId())
                .narration(notification.getNarration())
                .processor("CAMS")
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        sale.setPaymentMethod(PaymentMethod.TRANSFER);

        // sale.setPaymentStatus(PaymentStatus.SUCCESS);
        // sale.setSaleStatus(SaleStatus.COMPLETED);
        // sale.setPaidAt(notification.getPaymentTime());
        // sale.setTransactionReference(notification.getRequestReference());

        saleRepository.save(sale);
        completeSale(sale.getId(), notification.getRequestReference(), PaymentStatus.SUCCESS, notification.getPaymentTime());
        pendingTransferService.delete(pendingTransfer);
        return paymentMapper.toResponse(savedPayment);
    }

    private PaymentResponse createCardPaymentFromCamsNotification(Sale sale, CardPaymentNotification cardPaymentNotification, Terminal terminal, PendingCardPayment pendingCardPayment) {
        Payment payment = Payment.builder()
                .paymentNumber(generatePaymentNumber())
                .sale(sale)
                .paymentStatus(PaymentStatus.SUCCESS)
                .paymentMethod(PaymentMethod.CARD)
                .gatewayReference(cardPaymentNotification.getRrn())
                .gatewayTransactionReference(cardPaymentNotification.getStan())
                .amount(cardPaymentNotification.getAmount())
                .paidAt(cardPaymentNotification.getTransactionTime())
                .terminal(terminal)
                // .outletId(cardPaymentNotification.getOutletId())
                .processor("CAMS")
                // .authCode(cardPaymentNotification.getAuthCode())
                // .bankName(cardPaymentNotification.getBankName())

                .build();

        Payment savedPayment = paymentRepository.save(payment);
        sale.setPaymentMethod(PaymentMethod.CARD);

        saleRepository.save(sale);
        completeSale(sale.getId(), cardPaymentNotification.getRrn(), PaymentStatus.SUCCESS, cardPaymentNotification.getTransactionTime());
        pendingCardPaymentService.consume(terminal.getTid(), cardPaymentNotification.getAmount());
        return paymentMapper.toResponse(savedPayment);
    }

    private String generateSaleNumber(Station station) {
        return "FuelFlow-"
                    + ShiftUtil.businessDate(station.getTimeZone())
                    + "-"
                    + UUID.randomUUID()
                    .toString()
                    .substring(0, 8)
                    .toUpperCase();
    }

    private String generateReceiptNumber() {
        return "RCP-"
                    + UUID.randomUUID()
                            .toString()
                            .substring(0, 8)
                            .toUpperCase();
    }


    private BigDecimal calculateNetAmount(BigDecimal grossAmount, BigDecimal discount) {
        if (discount == null) { discount = BigDecimal.ZERO; }
            return grossAmount.subtract(discount);
    }

    @Transactional
    private void updateCurrentPumpAudit(Sale sale) {
        if (sale == null || sale.getStation() == null) {
            throw new ResourceNotFoundException("Sale station not found");
        }

        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
        if(principal == null) {
            throw new ResourceNotFoundException("Merchant is not authenticated");
        }

        Station saleStation = sale.getStation();
        if (saleStation.getMerchant() == null || !saleStation.getMerchant().getId().equals(principal.getMerchantDbId())) {
            throw new ResourceNotFoundException("Sale not found");
        }

        PumpAssignment assignment = pumpAssignmentRepository
                            .findByPumpIdAndAssignmentDateAndShiftAndActiveTrue(
                                    sale.getPump().getId(),
                                    sale.getBusinessDate(),
                                    sale.getShift())
                            .orElseThrow(() -> new ResourceNotFoundException("Pump assignment not found"));

        if (assignment.getStation() == null || !assignment.getStation().getId().equals(saleStation.getId())) {
            throw new ResourceNotFoundException("Pump assignment not found");
        }

        if (assignment.getStation().getMerchant() == null || !assignment.getStation().getMerchant().getId().equals(principal.getMerchantDbId())) {
            throw new ResourceNotFoundException("Pump assignment not found");
        }

        PumpAudit audit = pumpAuditRepository.findByPumpAssignment_Id(assignment.getId())
                                    .orElseThrow(() -> new ResourceNotFoundException("Pump audit not found"));

        BigDecimal newClosing = audit.getClosingReading().add(sale.getQuantity());

        audit.setClosingReading(newClosing);
        audit.setTotalDispensed(newClosing.subtract(audit.getOpeningReading()));
        pumpAuditRepository.save(audit);
    }

    private void verifySaleOwnership(Sale sale) {
            MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
            if(principal == null) {
                throw new ResourceNotFoundException("Merchant is not authenticated");
            }

            if (sale.getStation() == null || sale.getStation().getMerchant() == null ||
                    !sale.getStation().getMerchant().getId().equals(principal.getMerchantDbId())) {

                throw new ResourceNotFoundException("Sale not found");
            }
    }

    private void verifyStationOwnership(Station station) {
        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();

        if(principal == null) {
                throw new ResourceNotFoundException("Merchant is not authenticated");
        }

        if (station == null || station.getMerchant() == null ||
                    !station.getMerchant().getId().equals(principal.getMerchantDbId())) {

                throw new ResourceNotFoundException("Station not found");
        }
    }

    private void verifyPaymentOwnership(Payment payment) {
        if (payment.getSale() == null) {
                throw new ResourceNotFoundException("Payment not found");
        }

        verifySaleOwnership(payment.getSale());
    }

    private void verifySaleTerminalRelationship(Sale sale,Terminal terminal) {
    if (sale.getTerminal() == null || !sale.getTerminal().getId().equals(terminal.getId())) {
        throw new BadRequestException("Payment terminal does not match the sale terminal.");
    }
}

}
