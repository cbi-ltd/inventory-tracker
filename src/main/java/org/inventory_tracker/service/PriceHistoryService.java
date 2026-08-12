package org.inventory_tracker.service;

import lombok.RequiredArgsConstructor;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.inventory_tracker.config.mapper.ProductPriceHistoryMapper;
import org.inventory_tracker.dto.request.ProductPriceHistoryFilterRequest;
import org.inventory_tracker.dto.response.ProductPriceHistoryResponse;
import org.inventory_tracker.entity.Merchant;
import org.inventory_tracker.entity.ProductPriceHistory;
import org.inventory_tracker.entity.Station;
import org.inventory_tracker.entity.security.MerchantContext;
import org.inventory_tracker.entity.specification.PriceHistorySpecification;
import org.inventory_tracker.exception.ResourceNotFoundException;
import org.inventory_tracker.exception.BadRequestException;
import org.inventory_tracker.repository.ProductPriceHistoryRepository;
import org.inventory_tracker.repository.ProductRepository;
import org.inventory_tracker.repository.StationRepository;
import org.springframework.data.domain.Sort;
import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
public class PriceHistoryService {
    private final ProductPriceHistoryRepository productPriceHistoryRepository;
    private final ProductPriceHistoryMapper productPriceHistoryMapper;
    private final StationRepository stationRepository;
    private final ProductRepository productRepository;


    // @Transactional(readOnly = true)
    // public List<ProductPriceHistoryResponse> getAllPriceHistory() {

    //     return productPriceHistoryMapper.toResponseList(productPriceHistoryRepository.findAllByOrderByChangedAtDesc());
    // }

    @Transactional(readOnly = true)
    public List<ProductPriceHistoryResponse> filterPriceHistory(ProductPriceHistoryFilterRequest request) {
            if (request.getStartDate() != null && request.getEndDate() != null && request.getStartDate().isAfter(request.getEndDate())) {
                throw new BadRequestException("Start date cannot be after end date.");
            }

            Merchant merchant = MerchantContext.getCurrentMerchant();
            if (merchant == null) {
                throw new ResourceNotFoundException("Merchant is not authenticated");
            }

            // Specification<ProductPriceHistory> specification = PriceHistorySpecification.filter(request);
            Specification<ProductPriceHistory> specification =
            PriceHistorySpecification.filter(request).and((root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(root.get("station")
                                            .get("merchant")
                                            .get("id"), merchant.getId()));

            return productPriceHistoryRepository
                    .findAll(specification, Sort.by(Sort.Direction.DESC, "changedAt"))
                    .stream().map(productPriceHistoryMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProductPriceHistoryResponse getPriceHistoryById(Long historyId) {
        Merchant merchant = MerchantContext.getCurrentMerchant();
        if (merchant == null) {
            throw new ResourceNotFoundException("Merchant is not authenticated");
        }
        ProductPriceHistory history = productPriceHistoryRepository.findById(historyId)
                        .orElseThrow(() -> new ResourceNotFoundException("Price history not found"));

        if (history.getStation() == null || history.getStation().getMerchant() == null || !history.getStation()
                    .getMerchant().getId().equals(merchant.getId())) {

            throw new ResourceNotFoundException("Price history not found");
        }
        return productPriceHistoryMapper.toResponse(history);
    }

    @Transactional(readOnly = true)
    public List<ProductPriceHistoryResponse> getPriceHistoryByStation(Long stationId) {
        Merchant merchant = MerchantContext.getCurrentMerchant();
        if (merchant == null) {
            throw new ResourceNotFoundException("Merchant is not authenticated");
        }
        Station station = stationRepository.findById(stationId).orElseThrow(() ->
            new ResourceNotFoundException("Station not found"));

        if (station.getMerchant() == null || !station.getMerchant().getId().equals(merchant.getId())) {
            throw new ResourceNotFoundException("Station not found");
        }
        return productPriceHistoryMapper.toResponseList(productPriceHistoryRepository.findByStationIdOrderByChangedAtDesc(stationId));
    }

    @Transactional(readOnly = true)
    public List<ProductPriceHistoryResponse> getPriceHistoryByProduct(Long productId) {
        Merchant merchant = MerchantContext.getCurrentMerchant();
        if (merchant == null) {
            throw new ResourceNotFoundException("Merchant is not authenticated");
        }
        if (!productRepository.existsById(productId)) {
                throw new ResourceNotFoundException("Product not found");
        }

        return productPriceHistoryMapper.toResponseList(productPriceHistoryRepository
                        .findByProductIdAndStation_Merchant_IdOrderByChangedAtDesc(productId, merchant.getId()));
    }

    @Transactional(readOnly = true)
    public List<ProductPriceHistoryResponse>getPriceHistoryByStationAndProduct(Long stationId, Long productId) {
        Merchant merchant = MerchantContext.getCurrentMerchant();
        if (merchant == null) {
            throw new ResourceNotFoundException("Merchant is not authenticated");
        }
        if (!stationRepository.existsById(stationId)) {
            throw new ResourceNotFoundException( "Station not found");
        }

        Station station = stationRepository.findById(stationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Station not found"));

        if (station.getMerchant() == null || !station.getMerchant().getId().equals(merchant.getId())) {
            throw new ResourceNotFoundException("Station not found");
        }

        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found");
        }

        return productPriceHistoryMapper.toResponseList(productPriceHistoryRepository
                        .findByStationIdAndProductIdOrderByChangedAtDesc(stationId, productId));
    }

    @Transactional(readOnly = true)
    public List<ProductPriceHistoryResponse>getPriceHistoryByBusinessDate(LocalDate businessDate) {
        Merchant merchant = MerchantContext.getCurrentMerchant();
        if (merchant == null) {
            throw new ResourceNotFoundException("Merchant is not authenticated");
        }
        return productPriceHistoryMapper.toResponseList(productPriceHistoryRepository
                        .findByBusinessDateAndStation_Merchant_IdOrderByChangedAtDesc(businessDate, merchant.getId()));
    }

    @Transactional(readOnly = true)
    public List<ProductPriceHistoryResponse>getPriceHistoryBetweenDates(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) { throw new BadRequestException("Start date cannot be after end date.");}

        Merchant merchant = MerchantContext.getCurrentMerchant();
        if (merchant == null) {
            throw new ResourceNotFoundException(
                    "Merchant is not authenticated");
        }
        return productPriceHistoryMapper.toResponseList(productPriceHistoryRepository
                        .findByBusinessDateBetweenAndStation_Merchant_IdOrderByChangedAtDesc(startDate, endDate, merchant.getId()));
    }

    @Transactional(readOnly = true)
    public List<ProductPriceHistoryResponse>getPriceHistoryByChangedBy(String changedBy) {
        Merchant merchant = MerchantContext.getCurrentMerchant();
        if (merchant == null) {
            throw new ResourceNotFoundException("Merchant is not authenticated");
        }
        return productPriceHistoryMapper.toResponseList(productPriceHistoryRepository
                        .findByChangedByAndStation_Merchant_IdOrderByChangedAtDesc(changedBy, merchant.getId()));
    }
}
