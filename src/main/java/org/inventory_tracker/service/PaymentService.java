package org.inventory_tracker.service;


import lombok.RequiredArgsConstructor;
import org.inventory_tracker.config.mapper.PaymentMapper;
import org.inventory_tracker.dto.response.PaymentResponse;
import org.inventory_tracker.entity.Payment;
import org.inventory_tracker.entity.Sale;
import org.inventory_tracker.entity.Terminal;
import org.inventory_tracker.enums.PaymentMethod;
import org.inventory_tracker.enums.PaymentStatus;
import org.inventory_tracker.enums.SaleStatus;
import org.inventory_tracker.exception.BadRequestException;
import org.inventory_tracker.exception.ResourceNotFoundException;
import org.inventory_tracker.repository.PaymentRepository;
import org.inventory_tracker.repository.SaleRepository;
import org.inventory_tracker.repository.TerminalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final SaleRepository saleRepository;
    private final TerminalRepository terminalRepository;

    @Transactional
    public PaymentResponse recordCashPayment(Long saleId) {

        Sale sale = saleRepository.findById(saleId).orElseThrow(() -> new ResourceNotFoundException("Sale not found."));

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
    public PaymentResponse recordElectronicPayment(Long saleId, String transactionReference, PaymentMethod paymentMethod,
                        PaymentStatus paymentStatus, String rrn, String stan, Long terminalId, String merchantId,
                        String outletId, String authorizationCode, String cardScheme, String responseCode,
                        String responseMessage, LocalDateTime paymentTime) {

        validatePaymentMethod(paymentMethod);
        validatePaymentStatus(paymentStatus);
        validateDuplicatePayment(transactionReference);

        if (paymentMethod == PaymentMethod.CASH) {
            throw new BadRequestException("Use recordCashPayment() for cash payments.");
        }

        Sale sale = saleRepository.findById(saleId).orElseThrow(() -> new ResourceNotFoundException("Sale not found."));

        if (paymentRepository.existsByTransactionReference(transactionReference)) {
            return paymentMapper.toResponse(paymentRepository.findByTransactionReference(transactionReference).orElseThrow());
        }

        Terminal terminal = null;

        if (terminalId != null) {
            terminal = terminalRepository.findById(terminalId).orElseThrow(() -> new ResourceNotFoundException("Terminal not found."));
        }

        validateTerminal(terminal, paymentMethod);

        Payment payment = new Payment();
        payment.setPaymentNumber(generatePaymentNumber());

        payment.setSale(sale);
        payment.setAmount(sale.getNetAmount());
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentStatus(paymentStatus);
        payment.setTransactionReference(transactionReference);
        payment.setRrn(rrn);
        payment.setStan(stan);
        payment.setTerminal(terminal);
        payment.setMerchantId(merchantId);
        payment.setOutletId(outletId);
        payment.setAuthorizationCode(authorizationCode);
        payment.setCardScheme(cardScheme);
        payment.setResponseCode(responseCode);
        payment.setResponseMessage(responseMessage);
        payment.setPaymentTime(paymentTime != null ? paymentTime : LocalDateTime.now());

        validateAmount(sale, payment);
        payment = paymentRepository.save(payment);
        return paymentMapper.toResponse(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id) {

        return paymentRepository.findById(id).map(paymentMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found."));
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByTransactionReference(String transactionReference) {
        return paymentRepository.findByTransactionReference(transactionReference).map(paymentMapper::toResponse)
                        .orElseThrow(() -> new ResourceNotFoundException("Payment not found."));
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentBySale(Long saleId) {
        return paymentRepository.findBySaleId(saleId).map(paymentMapper::toResponse)
                        .orElseThrow(() -> new ResourceNotFoundException("Payment not found."));
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAllByOrderByPaymentTimeDesc().stream()
                                    .map(paymentMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByPaymentStatusOrderByPaymentTimeDesc(status).stream()
                                        .map(paymentMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByMethod(PaymentMethod method) {
        return paymentRepository.findByPaymentMethodOrderByPaymentTimeDesc(method).stream()
                                        .map(paymentMapper::toResponse).toList();
    }

    // processPaymentResult(CamsPaymentDTO dto)
    @Transactional
    public PaymentResponse updatePaymentStatus(String transactionReference, PaymentStatus paymentStatus, String responseCode, String responseMessage) {

        Payment payment = paymentRepository.findByTransactionReference(transactionReference).orElseThrow(() -> new ResourceNotFoundException("Payment not found."));

        payment.setPaymentStatus(paymentStatus);
        payment.setResponseCode(responseCode);
        payment.setResponseMessage(responseMessage);
        payment = paymentRepository.save(payment);

        return paymentMapper.toResponse(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByPaymentNumber(String paymentNumber) {
        return paymentRepository.findByPaymentNumber(paymentNumber).map(paymentMapper::toResponse)
                                    .orElseThrow(() -> new ResourceNotFoundException("Payment not found."));
    }

    @Transactional
    public PaymentResponse cancelPayment(Long paymentId) {

        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> new ResourceNotFoundException("Payment not found."));

        // if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
        //     throw new BadRequestException("Successful payments cannot be cancelled. Use a reversal or refund process.");
        // }
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

}
