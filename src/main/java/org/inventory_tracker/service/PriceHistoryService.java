package org.inventory_tracker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.inventory_tracker.config.mapper.ProductPriceHistoryMapper;
import org.inventory_tracker.dto.response.ProductPriceHistoryResponse;
import org.inventory_tracker.entity.ProductPriceHistory;
import org.inventory_tracker.exception.ResourceNotFoundException;
import org.inventory_tracker.exception.BadRequestException;
import org.inventory_tracker.repository.ProductPriceHistoryRepository;
import org.inventory_tracker.repository.ProductRepository;
import org.inventory_tracker.repository.StationRepository;

import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
public class PriceHistoryService {
    private final ProductPriceHistoryRepository productPriceHistoryRepository;
    private final ProductPriceHistoryMapper productPriceHistoryMapper;
    private final StationRepository stationRepository;
    private final ProductRepository productRepository;


    @Transactional(readOnly = true)
    public List<ProductPriceHistoryResponse> getAllPriceHistory() {

        return productPriceHistoryMapper.toResponseList(productPriceHistoryRepository.findAllByOrderByChangedAtDesc());
    }

    @Transactional(readOnly = true)
    public ProductPriceHistoryResponse getPriceHistoryById(Long historyId) {

        ProductPriceHistory history = productPriceHistoryRepository.findById(historyId)
                        .orElseThrow(() -> new ResourceNotFoundException("Price history not found"));

        return productPriceHistoryMapper.toResponse(history);
    }

    @Transactional(readOnly = true)
    public List<ProductPriceHistoryResponse> getPriceHistoryByStation(Long stationId) {

        if (!stationRepository.existsById(stationId)) {
            throw new ResourceNotFoundException("Station not found");
        }

        return productPriceHistoryMapper.toResponseList(productPriceHistoryRepository.findByStationIdOrderByChangedAtDesc(stationId));
    }

    @Transactional(readOnly = true)
    public List<ProductPriceHistoryResponse> getPriceHistoryByProduct(Long productId) {

        if (!productRepository.existsById(productId)) {
                throw new ResourceNotFoundException("Product not found");
        }

        return productPriceHistoryMapper.toResponseList(productPriceHistoryRepository
                        .findByProductIdOrderByChangedAtDesc(productId));
    }

    @Transactional(readOnly = true)
    public List<ProductPriceHistoryResponse>getPriceHistoryByStationAndProduct(Long stationId, Long productId) {

        if (!stationRepository.existsById(stationId)) {
            throw new ResourceNotFoundException( "Station not found");
        }

        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found");
        }

        return productPriceHistoryMapper.toResponseList(productPriceHistoryRepository
                        .findByStationIdAndProductIdOrderByChangedAtDesc(stationId, productId));
    }

    @Transactional(readOnly = true)
    public List<ProductPriceHistoryResponse>getPriceHistoryByBusinessDate(LocalDate businessDate) {

        return productPriceHistoryMapper.toResponseList(productPriceHistoryRepository
                        .findByBusinessDateOrderByChangedAtDesc(businessDate));
    }

    @Transactional(readOnly = true)
    public List<ProductPriceHistoryResponse>getPriceHistoryBetweenDates(LocalDate startDate, LocalDate endDate) {

        if (startDate.isAfter(endDate)) { throw new BadRequestException("Start date cannot be after end date.");}

        return productPriceHistoryMapper.toResponseList(productPriceHistoryRepository
                        .findByBusinessDateBetweenOrderByChangedAtDesc(startDate, endDate));
    }

    @Transactional(readOnly = true)
    public List<ProductPriceHistoryResponse>getPriceHistoryByChangedBy(String changedBy) {

        return productPriceHistoryMapper.toResponseList(productPriceHistoryRepository
                        .findByChangedByOrderByChangedAtDesc(changedBy));
    }
}
