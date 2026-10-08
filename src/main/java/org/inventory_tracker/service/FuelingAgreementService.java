package org.inventory_tracker.service;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.inventory_tracker.config.mapper.FuelingAgreementMapper;
import org.inventory_tracker.dto.request.CreateFuelingAgreementRequest;
import org.inventory_tracker.dto.request.RecordPaymentRequest;
import org.inventory_tracker.dto.request.UpdateFuelingAgreementRequest;
import org.inventory_tracker.dto.response.CompanyAccountTransactionResponse;
import org.inventory_tracker.dto.response.FuelingAgreementBalanceResponse;
import org.inventory_tracker.dto.response.FuelingAgreementResponse;
import org.inventory_tracker.entity.CompanyAccountTransaction;
import org.inventory_tracker.entity.FuelingAgreement;
import org.inventory_tracker.entity.FuelingCompany;
import org.inventory_tracker.entity.Merchant;
import org.inventory_tracker.entity.Sale;
import org.inventory_tracker.enums.CompanyAccountTransactionType;
import org.inventory_tracker.enums.FuelingSettlementType;
import org.inventory_tracker.exception.BadRequestException;
import org.inventory_tracker.exception.DuplicateResourceException;
import org.inventory_tracker.exception.ResourceNotFoundException;
import org.inventory_tracker.repository.CompanyAccountTransactionRepository;
import org.inventory_tracker.repository.FuelingAgreementRepository;
import org.inventory_tracker.repository.FuelingCompanyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.inventory_tracker.security.*;


@Service
@RequiredArgsConstructor
@Transactional
public class FuelingAgreementService {

    private final FuelingAgreementRepository fuelingAgreementRepository;
    private final FuelingCompanyRepository fuelingCompanyRepository;
    private final CompanyAccountTransactionRepository companyAccountTransactionRepository;
    private final FuelingAgreementMapper fuelingAgreementMapper;
    private final AuthenticatedUserService authenticatedUserService;

    public FuelingAgreementResponse createAgreement(CreateFuelingAgreementRequest request) {
        Merchant merchant = authenticatedUserService.getCurrentMerchant();

        if (merchant == null) {
            throw new ResourceNotFoundException("Merchant is not authenticated");
        }

        FuelingCompany company = fuelingCompanyRepository
                .findByIdAndMerchant_IdAndActiveTrue(request.getCompanyId(), merchant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Fueling company not found"));

        validateAgreementDates(request.getStartDate(), request.getEndDate());
        validateSettlementConfiguration(request.getSettlementType(), request.getPrepaidBalance(), request.getCreditLimit());

        if (hasOverlappingActiveAgreement(
                company.getId(),
                merchant.getId(),
                request.getStartDate(),
                request.getEndDate(),
                null)) {

            throw new DuplicateResourceException("An active fueling agreement already exists for this company for the selected period");
        }

        FuelingAgreement agreement = FuelingAgreement.builder()
                .company(company)
                .merchant(merchant)
                .settlementType(request.getSettlementType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .prepaidBalance(request.getPrepaidBalance() == null ? BigDecimal.ZERO : request.getPrepaidBalance())
                .outstandingBalance(BigDecimal.ZERO)
                .creditLimit(request.getCreditLimit())
                .active(true)
                .reference(request.getReference())
                .remarks(request.getRemarks())
                .build();

        FuelingAgreement saved = fuelingAgreementRepository.save(agreement);
        return fuelingAgreementMapper.toResponse(saved);
    }

@Transactional(readOnly = true)
public FuelingAgreementResponse getAgreement(Long id) {

    Merchant merchant = authenticatedUserService.getCurrentMerchant();

    FuelingAgreement agreement =
            fuelingAgreementRepository.findByIdAndMerchant_Id(
                    id,
                    merchant.getId())
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Fueling agreement not found"));

    return fuelingAgreementMapper.toResponse(agreement);
}

@Transactional(readOnly = true)
public List<FuelingAgreementResponse> getAgreements() {

    Merchant merchant = authenticatedUserService.getCurrentMerchant();

    List<FuelingAgreement> agreements =
            fuelingAgreementRepository
                    .findByMerchant_IdOrderByStartDateDesc(
                            merchant.getId());

    return fuelingAgreementMapper.toResponseList(agreements);
}

@Transactional(readOnly = true)
public List<FuelingAgreementResponse> getCompanyAgreements(
        Long companyId) {

    Merchant merchant = authenticatedUserService.getCurrentMerchant();

    FuelingCompany company = fuelingCompanyRepository
            .findByIdAndMerchant_IdAndActiveTrue(
                    companyId,
                    merchant.getId())
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Fueling company not found"));

    List<FuelingAgreement> agreements =
            fuelingAgreementRepository
                    .findByCompany_IdAndMerchant_IdOrderByStartDateDesc(
                            company.getId(),
                            merchant.getId());

    return fuelingAgreementMapper.toResponseList(agreements);
}

public FuelingAgreementResponse updateAgreement(
        Long id,
        UpdateFuelingAgreementRequest request) {

    Merchant merchant = authenticatedUserService.getCurrentMerchant();

    FuelingAgreement agreement =
            fuelingAgreementRepository
                    .findByIdAndMerchant_Id(
                            id,
                            merchant.getId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Fueling agreement not found"));

    LocalDate startDate = request.getStartDate() != null
            ? request.getStartDate()
            : agreement.getStartDate();

    LocalDate endDate = request.getEndDate() != null
            ? request.getEndDate()
            : agreement.getEndDate();

    FuelingSettlementType settlementType =
            request.getSettlementType() != null
                    ? request.getSettlementType()
                    : agreement.getSettlementType();

    validateAgreementDates(startDate, endDate);

    validateSettlementConfiguration(
            settlementType,
            agreement.getPrepaidBalance(),
            request.getCreditLimit() != null
                    ? request.getCreditLimit()
                    : agreement.getCreditLimit());

    if (hasOverlappingActiveAgreement(
            agreement.getCompany().getId(),
            merchant.getId(),
            startDate,
            endDate,
            agreement.getId())) {

        throw new DuplicateResourceException(
                "An active fueling agreement already exists for this company for the selected period");
    }

    agreement.setSettlementType(settlementType);
    agreement.setStartDate(startDate);
    agreement.setEndDate(endDate);

    if (request.getCreditLimit() != null) {
        agreement.setCreditLimit(request.getCreditLimit());
    }

    agreement.setReference(request.getReference());
    agreement.setRemarks(request.getRemarks());

    return fuelingAgreementMapper.toResponse(
            fuelingAgreementRepository.save(agreement));
}

public FuelingAgreementResponse activateAgreement(Long id) {

    Merchant merchant = authenticatedUserService.getCurrentMerchant();

    FuelingAgreement agreement =
            fuelingAgreementRepository
                    .findByIdAndMerchant_Id(
                            id,
                            merchant.getId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Fueling agreement not found"));

    if (agreement.getActive()) {
        throw new BadRequestException(
                "Fueling agreement is already active");
    }

    LocalDate today = LocalDate.now();

    if (today.isAfter(agreement.getEndDate())) {
        throw new BadRequestException(
                "Cannot activate an expired agreement");
    }

    if (hasOverlappingActiveAgreement(
            agreement.getCompany().getId(),
            merchant.getId(),
            agreement.getStartDate(),
            agreement.getEndDate(),
            agreement.getId())) {

        throw new DuplicateResourceException(
                "Another active fueling agreement overlaps this agreement");
    }

    agreement.setActive(true);

    return fuelingAgreementMapper.toResponse(
            fuelingAgreementRepository.save(agreement));
}

public FuelingAgreementResponse deactivateAgreement(Long id) {

    Merchant merchant = authenticatedUserService.getCurrentMerchant();

    FuelingAgreement agreement =
            fuelingAgreementRepository
                    .findByIdAndMerchant_Id(
                            id,
                            merchant.getId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Fueling agreement not found"));

    if (!agreement.getActive()) {
        throw new BadRequestException(
                "Fueling agreement is already inactive");
    }

    BigDecimal outstanding =
            agreement.getOutstandingBalance() == null
                    ? BigDecimal.ZERO
                    : agreement.getOutstandingBalance();

    BigDecimal prepaid =
            agreement.getPrepaidBalance() == null
                    ? BigDecimal.ZERO
                    : agreement.getPrepaidBalance();

    if (outstanding.compareTo(BigDecimal.ZERO) > 0) {
        throw new BadRequestException(
                "Cannot deactivate an agreement with an outstanding balance");
    }

    if (prepaid.compareTo(BigDecimal.ZERO) > 0) {
        throw new BadRequestException(
                "Cannot deactivate an agreement with a remaining prepaid balance");
    }

    agreement.setActive(false);

    return fuelingAgreementMapper.toResponse(
            fuelingAgreementRepository.save(agreement));
}

@Transactional(readOnly = true)
public FuelingAgreementBalanceResponse getAgreementBalance(Long id) {

    Merchant merchant = authenticatedUserService.getCurrentMerchant();

    FuelingAgreement agreement =
            fuelingAgreementRepository
                    .findByIdAndMerchant_Id(
                            id,
                            merchant.getId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Fueling agreement not found"));

    BigDecimal prepaid =
            agreement.getPrepaidBalance() == null
                    ? BigDecimal.ZERO
                    : agreement.getPrepaidBalance();

    BigDecimal outstanding =
            agreement.getOutstandingBalance() == null
                    ? BigDecimal.ZERO
                    : agreement.getOutstandingBalance();

    BigDecimal availableBalance;

    if (agreement.getSettlementType()
            == FuelingSettlementType.PREPAID) {

        availableBalance = prepaid;

    } else {

        BigDecimal creditLimit =
                agreement.getCreditLimit() == null
                        ? BigDecimal.ZERO
                        : agreement.getCreditLimit();

        availableBalance = creditLimit.subtract(outstanding);

        if (availableBalance.compareTo(BigDecimal.ZERO) < 0) {
            availableBalance = BigDecimal.ZERO;
        }
    }

    return FuelingAgreementBalanceResponse.builder()
            .agreementId(agreement.getId())
            .settlementType(agreement.getSettlementType())
            .prepaidBalance(prepaid)
            .outstandingBalance(outstanding)
            .creditLimit(agreement.getCreditLimit())
            .availableBalance(availableBalance)
            .active(agreement.getActive())
            .build();
}

public void recordPostpaidFueling(
        FuelingAgreement agreement,
        Sale sale) {

    if (agreement.getSettlementType()
            != FuelingSettlementType.POSTPAID) {

        throw new BadRequestException(
                "Agreement is not a postpaid agreement");
    }

    validateAgreementUsable(agreement, sale.getBusinessDate());

    BigDecimal outstanding =
            agreement.getOutstandingBalance() == null
                    ? BigDecimal.ZERO
                    : agreement.getOutstandingBalance();

    BigDecimal newOutstanding =
            outstanding.add(sale.getNetAmount());

    if (agreement.getCreditLimit() != null
            && newOutstanding.compareTo(
                    agreement.getCreditLimit()) > 0) {

        throw new BadRequestException(
                "Company credit limit exceeded");
    }

    agreement.setOutstandingBalance(newOutstanding);

    fuelingAgreementRepository.save(agreement);

    CompanyAccountTransaction transaction =
            CompanyAccountTransaction.builder()
                    .fuelingAgreement(agreement)
                    .sale(sale)
                    .type(CompanyAccountTransactionType.FUEL_PURCHASE)
                    .amount(sale.getNetAmount().negate())
                    .transactionDate(
                            LocalDateTime.now(
                                    sale.getStation().getTimeZone()))
                    .reference(sale.getSaleNumber())
                    .description(
                            "Postpaid fuel purchase")
                    .build();

    companyAccountTransactionRepository.save(transaction);
}

public CompanyAccountTransactionResponse recordPrepaidPayment(
        Long agreementId,
        RecordPaymentRequest request) {

    Merchant merchant = authenticatedUserService.getCurrentMerchant();

    FuelingAgreement agreement =
            fuelingAgreementRepository
                    .findByIdAndMerchant_Id(
                            agreementId,
                            merchant.getId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Fueling agreement not found"));

    if (!agreement.getActive()) {
        throw new BadRequestException(
                "Cannot record payment against an inactive agreement");
    }

    if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
        throw new BadRequestException(
                "Payment amount must be greater than zero");
    }

    LocalDateTime transactionDate = LocalDateTime.now();

    if (agreement.getSettlementType()
            == FuelingSettlementType.PREPAID) {

        BigDecimal balance =
                agreement.getPrepaidBalance() == null
                        ? BigDecimal.ZERO
                        : agreement.getPrepaidBalance();

        agreement.setPrepaidBalance(
                balance.add(request.getAmount()));

    } else {

        BigDecimal outstanding =
                agreement.getOutstandingBalance() == null
                        ? BigDecimal.ZERO
                        : agreement.getOutstandingBalance();

        if (request.getAmount().compareTo(outstanding) > 0) {
            throw new BadRequestException(
                    "Payment cannot exceed outstanding balance");
        }

        agreement.setOutstandingBalance(
                outstanding.subtract(request.getAmount()));
    }

    fuelingAgreementRepository.save(agreement);

    CompanyAccountTransaction transaction =
            CompanyAccountTransaction.builder()
                    .fuelingAgreement(agreement)
                    .type(
                            agreement.getSettlementType()
                                    == FuelingSettlementType.PREPAID
                                    ? CompanyAccountTransactionType.DEPOSIT
                                    : CompanyAccountTransactionType.PAYMENT)
                    .amount(request.getAmount())
                    .transactionDate(transactionDate)
                    .reference(request.getReference())
                    .description(request.getRemarks())
                    .build();

    CompanyAccountTransaction saved =
            companyAccountTransactionRepository.save(transaction);

    return toTransactionResponse(saved);
}

    private void validateAgreementDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new BadRequestException("Start date and end date are required");
        }

        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date cannot be after end date");
        }
    }

private void validateAgreementUsable(
        FuelingAgreement agreement,
        LocalDate businessDate) {

    if (!Boolean.TRUE.equals(agreement.getActive())) {
        throw new BadRequestException(
                "Fueling agreement is inactive");
    }

    if (businessDate.isBefore(agreement.getStartDate())) {
        throw new BadRequestException(
                "Fueling agreement has not started");
    }

    if (businessDate.isAfter(agreement.getEndDate())) {
        throw new BadRequestException(
                "Fueling agreement has expired");
    }
}

@Transactional(readOnly = true)
public List<CompanyAccountTransactionResponse> getTransactions(Long agreementId) {
    Merchant merchant = authenticatedUserService.getCurrentMerchant();

    FuelingAgreement agreement =
            fuelingAgreementRepository
                    .findByIdAndMerchant_Id(
                            agreementId,
                            merchant.getId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Fueling agreement not found"));

    return companyAccountTransactionRepository
            .findByFuelingAgreement_IdOrderByTransactionDateDesc(
                    agreement.getId())
            .stream()
            .map(this::toTransactionResponse)
            .toList();
}

private CompanyAccountTransactionResponse toTransactionResponse(
        CompanyAccountTransaction transaction) {

    return CompanyAccountTransactionResponse.builder()
            .id(transaction.getId())
            .agreementId(
                    transaction.getFuelingAgreement().getId())
            .saleId(
                    transaction.getSale() != null
                            ? transaction.getSale().getId()
                            : null)
            .type(transaction.getType())
            .amount(transaction.getAmount())
            .transactionDate(transaction.getTransactionDate())
            .reference(transaction.getReference())
            .description(transaction.getDescription())
            .build();
}

    private void validateSettlementConfiguration(FuelingSettlementType settlementType, BigDecimal prepaidBalance, BigDecimal creditLimit) {
        if (settlementType == null) { 
            throw new BadRequestException("Settlement type is required");
        }

        if (prepaidBalance != null && prepaidBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Prepaid balance cannot be negative");
        }

        if (creditLimit != null && creditLimit.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Credit limit cannot be negative");
        }

        if (settlementType == FuelingSettlementType.PREPAID) {
            if (prepaidBalance == null || prepaidBalance.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("Prepaid agreement must have a prepaid balance greater than zero");
            }

            if (creditLimit != null) {
                throw new BadRequestException("Credit limit must be null for a prepaid agreement");
            }
        } 
        else {
            if (prepaidBalance != null && prepaidBalance.compareTo(BigDecimal.ZERO) != 0) {
                throw new BadRequestException("Prepaid balance must be zero for a postpaid agreement");
            }

            if (creditLimit == null) {
                throw new BadRequestException("Credit limit is required for a postpaid agreement");
            }
        }
    }

private boolean hasOverlappingActiveAgreement(
        Long companyId,
        Long merchantId,
        LocalDate startDate,
        LocalDate endDate,
        Long agreementId) {

    if (agreementId == null) {

        return fuelingAgreementRepository
                .existsByCompany_IdAndMerchant_IdAndActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        companyId,
                        merchantId,
                        endDate,
                        startDate);
    }

    return fuelingAgreementRepository
            .existsByCompany_IdAndMerchant_IdAndActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndIdNot(
                    companyId,
                    merchantId,
                    endDate,
                    startDate,
                    agreementId);
}
}
