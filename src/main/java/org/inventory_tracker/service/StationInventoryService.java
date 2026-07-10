package org.inventory_tracker.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.inventory_tracker.config.mapper.StationInventoryMapper;
import org.inventory_tracker.dto.request.CreateStationInventoryRequest;
import org.inventory_tracker.dto.response.StationInventoryResponse;
import org.inventory_tracker.entity.Station;
import org.inventory_tracker.entity.StationInventory;
import org.inventory_tracker.repository.StationInventoryRepository;
import org.inventory_tracker.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.inventory_tracker.repository.ProductRepository;
import org.inventory_tracker.entity.Product;
import org.inventory_tracker.entity.ProductPriceHistory;
import org.inventory_tracker.repository.ProductPriceHistoryRepository;
import org.inventory_tracker.exception.DuplicateResourceException;
import org.inventory_tracker.exception.ResourceNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.inventory_tracker.dto.request.UpdateStationInventoryRequest;
import org.inventory_tracker.util.ShiftUtil;
import java.util.List;


@Service
@RequiredArgsConstructor
public class StationInventoryService {

    private final StationInventoryRepository stationInventoryRepository;
    private final StationInventoryMapper stationInventoryMapper;

    private final StationRepository stationRepository;
    private final ProductRepository productRepository;

    private final ProductPriceHistoryRepository priceHistoryRepository;

    @Transactional
    public StationInventoryResponse createStationInventory(
            CreateStationInventoryRequest request) {

        if (stationInventoryRepository.existsByStationIdAndProductId(
                request.getStationId(),
                request.getProductId())) {

            throw new DuplicateResourceException(
                    "Inventory already exists for this product at the station.");
        }

        Station station =
                stationRepository.findById(request.getStationId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Station not found"));

        Product product =
                productRepository.findById(request.getProductId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found"));

        StationInventory inventory =
                stationInventoryMapper.toEntity(request);

        inventory.setStation(station);
        inventory.setProduct(product);
        inventory.setCurrentQuantity(request.getOpeningQuantity());
        inventory.setSellingPrice(request.getSellingPrice());
        inventory.setReorderLevel(request.getReorderLevel());
        inventory.setActive(true);

        StationInventory saved =
                stationInventoryRepository.save(inventory);

        ProductPriceHistory history =
                new ProductPriceHistory();

        history.setStation(station);
        history.setProduct(product);
        history.setOldPrice(BigDecimal.ZERO);
        history.setNewPrice(request.getSellingPrice());
        history.setReason("Initial selling price");
        history.setChangedBy("SYSTEM");
        history.setBusinessDate(
                ShiftUtil.businessDate(station.getTimeZone()));
        history.setChangedAt(LocalDateTime.now(station.getTimeZone()));

        priceHistoryRepository.save(history);

        return stationInventoryMapper.toResponse(saved);
    }

    @Transactional
    public StationInventoryResponse updateStationInventory(
            Long id,
            UpdateStationInventoryRequest request) {

        StationInventory inventory =
                stationInventoryRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Inventory not found"));

        BigDecimal oldPrice =
                inventory.getSellingPrice();

        inventory.setSellingPrice(request.getSellingPrice());
        inventory.setReorderLevel(request.getReorderLevel());

        StationInventory updated =
                stationInventoryRepository.save(inventory);

        if (oldPrice.compareTo(request.getSellingPrice()) != 0) {

            ProductPriceHistory history =
                    new ProductPriceHistory();

            history.setStation(updated.getStation());
            history.setProduct(updated.getProduct());

            history.setOldPrice(oldPrice);
            history.setNewPrice(request.getSellingPrice());

            history.setReason("Price updated");

            history.setChangedBy("SYSTEM");

            history.setBusinessDate(
                    ShiftUtil.businessDate(
                            updated.getStation().getTimeZone()));

            history.setChangedAt(
                    LocalDateTime.now(
                            updated.getStation().getTimeZone()));

            priceHistoryRepository.save(history);
        }

        return stationInventoryMapper.toResponse(updated);
    }

    @Transactional(readOnly = true)
    public StationInventoryResponse getInventoryById(Long id) {

        StationInventory inventory =
                stationInventoryRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Inventory not found"));

        return stationInventoryMapper.toResponse(inventory);
    }

    @Transactional(readOnly = true)
    public List<StationInventoryResponse> getStationInventory(
            Long stationId) {

        if (!stationRepository.existsById(stationId)) {
            throw new ResourceNotFoundException(
                    "Station not found");
        }

        return stationInventoryMapper.toResponseList(

                stationInventoryRepository
                        .findByStationIdOrderByProduct_NameAsc(
                                stationId)
        );
    }

    @Transactional(readOnly = true)
    public List<StationInventoryResponse> getAllInventories() {

        return stationInventoryMapper.toResponseList(

                stationInventoryRepository
                        .findAllByOrderByStation_NameAscProduct_NameAsc()
        );
    }

    @Transactional(readOnly = true)
    public List<StationInventoryResponse> getActiveInventories() {

        return stationInventoryMapper.toResponseList(

                stationInventoryRepository
                        .findByActiveTrueOrderByStation_NameAsc()
        );
    }

    @Transactional
    public StationInventoryResponse activateInventory(Long id) {

        StationInventory inventory =
                stationInventoryRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Inventory not found"));

        if (Boolean.TRUE.equals(inventory.getActive())) {

            throw new DuplicateResourceException(
                    "Inventory is already active");
        }

        inventory.setActive(true);

        return stationInventoryMapper.toResponse(

                stationInventoryRepository.save(inventory));
    }

    @Transactional
    public StationInventoryResponse deactivateInventory(Long id) {

        StationInventory inventory =
                stationInventoryRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Inventory not found"));

        if (Boolean.FALSE.equals(inventory.getActive())) {

            throw new DuplicateResourceException(
                    "Inventory is already inactive");
        }

        inventory.setActive(false);

        return stationInventoryMapper.toResponse(

                stationInventoryRepository.save(inventory));
    }

}