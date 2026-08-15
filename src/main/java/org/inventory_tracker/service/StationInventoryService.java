package org.inventory_tracker.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.inventory_tracker.config.mapper.StationInventoryMapper;
import org.inventory_tracker.dto.request.CreateStationInventoryRequest;
import org.inventory_tracker.dto.response.StationInventoryResponse;
import org.inventory_tracker.dto.request.ChangeSellingPriceRequest;
import org.inventory_tracker.entity.Station;
import org.inventory_tracker.entity.StationInventory;
// import org.inventory_tracker.entity.security.MerchantContext;
import org.inventory_tracker.repository.StationInventoryRepository;
import org.inventory_tracker.repository.StationRepository;
import org.inventory_tracker.security.AuthenticatedUserService;
import org.springframework.stereotype.Service;
import org.inventory_tracker.repository.ProductRepository;
import org.inventory_tracker.entity.Merchant;
import org.inventory_tracker.entity.Product;
import org.inventory_tracker.entity.ProductPriceHistory;
import org.inventory_tracker.repository.ProductPriceHistoryRepository;
import org.inventory_tracker.exception.DuplicateResourceException;
import org.inventory_tracker.exception.ResourceNotFoundException;
import org.inventory_tracker.exception.BadRequestException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private final AuthenticatedUserService authenticatedUserService;


        @Transactional
        public StationInventoryResponse changeSellingPrice(ChangeSellingPriceRequest request) {
                        Merchant merchant = authenticatedUserService.getCurrentMerchant();

                StationInventory inventory = stationInventoryRepository.findByIdAndStation_Merchant_Id(request.getStationInventoryId(), merchant.getId())
                                                .orElseThrow(() ->new ResourceNotFoundException("Station inventory not found"));

                BigDecimal oldPrice = inventory.getSellingPrice();

                if (oldPrice.compareTo(request.getNewSellingPrice()) == 0) {
                        throw new BadRequestException("The new selling price is the same as the current selling price.");
                }

                inventory.setSellingPrice(request.getNewSellingPrice());
                StationInventory updatedInventory = stationInventoryRepository.save(inventory);
                Station station = updatedInventory.getStation();

                ProductPriceHistory history = ProductPriceHistory.builder()
                        .station(station)
                        .product(updatedInventory.getProduct())
                        .oldPrice(oldPrice)
                        .newPrice(request.getNewSellingPrice())
                        .changedBy(request.getChangedBy())
                        .reason(request.getReason())
                        .businessDate(ShiftUtil.businessDate(station.getTimeZone()))
                        .changedAt(LocalDateTime.now(station.getTimeZone()))
                        .build();

                history.setPriceDifference(request.getNewSellingPrice().subtract(oldPrice));
                priceHistoryRepository.save(history);
                return stationInventoryMapper.toResponse(updatedInventory);
        }

    @Transactional
    public StationInventoryResponse createStationInventory(CreateStationInventoryRequest request) {
                Merchant merchant = authenticatedUserService.getCurrentMerchant();

        if (stationInventoryRepository.existsByStationIdAndProductIdAndStation_Merchant_Id(
                request.getStationId(),
                request.getProductId(), merchant.getId())) {

            throw new DuplicateResourceException("Inventory already exists for this product at the station.");
        }

        Station station = stationRepository.findByIdAndMerchantId(request.getStationId(), merchant.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Station not found"));

        Product product = productRepository.findById(request.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        StationInventory inventory = stationInventoryMapper.toEntity(request);

        inventory.setStation(station);
        inventory.setProduct(product);
        inventory.setCurrentQuantity(request.getCurrentQuantity());
        inventory.setCostPerUnit(BigDecimal.ZERO);
        inventory.setSellingPrice(request.getSellingPrice());
        inventory.setActive(true);

        BigDecimal reorderLevel = inventory.getCurrentQuantity().multiply(new BigDecimal("0.20"));
        inventory.setReorderLevel(reorderLevel.setScale(3, RoundingMode.HALF_UP));

        StationInventory saved = stationInventoryRepository.save(inventory);
        ProductPriceHistory history = new ProductPriceHistory();

        history.setStation(station);
        history.setProduct(product);
        history.setOldPrice(BigDecimal.ZERO);
        history.setNewPrice(request.getSellingPrice());
        history.setReason("Initial selling price");
        history.setChangedBy("SYSTEM");
        history.setBusinessDate(ShiftUtil.businessDate(station.getTimeZone()));
        history.setChangedAt(LocalDateTime.now(station.getTimeZone()));
        // history.setPriceDifference(request.getSellingPrice().subtract(history.getOldPrice()));

        priceHistoryRepository.save(history);
        return stationInventoryMapper.toResponse(saved);
    }

    @Transactional
    public StationInventoryResponse updateStationInventory(Long id, UpdateStationInventoryRequest request) {
                Merchant merchant = authenticatedUserService.getCurrentMerchant();

        StationInventory inventory = stationInventoryRepository.findByIdAndStation_Merchant_Id(id, merchant.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));

        BigDecimal oldPrice = inventory.getSellingPrice();

        if (request.getCostPerUnit() != null) { inventory.setCostPerUnit(request.getCostPerUnit()); }
        if (request.getSellingPrice() != null) { inventory.setSellingPrice(request.getSellingPrice()); }
        if (request.getReorderLevel() != null) { inventory.setReorderLevel(request.getReorderLevel()); }
        

        StationInventory updated = stationInventoryRepository.save(inventory);

        if (request.getSellingPrice() != null && oldPrice.compareTo(request.getSellingPrice()) != 0) {
            ProductPriceHistory history = new ProductPriceHistory();
            history.setStation(updated.getStation());
            history.setProduct(updated.getProduct());

            history.setOldPrice(oldPrice);
            history.setNewPrice(request.getSellingPrice());
            history.setPriceDifference(request.getSellingPrice().subtract(oldPrice));

            history.setReason("Price updated");
            history.setChangedBy("SYSTEM");

            history.setBusinessDate(ShiftUtil.businessDate(updated.getStation().getTimeZone()));
            history.setChangedAt(LocalDateTime.now(updated.getStation().getTimeZone()));
            priceHistoryRepository.save(history);
        }

        return stationInventoryMapper.toResponse(updated);
    }

    @Transactional(readOnly = true)
    public StationInventoryResponse getInventoryById(Long id) {
                Merchant merchant = authenticatedUserService.getCurrentMerchant();

        StationInventory inventory = stationInventoryRepository.findByIdAndStation_Merchant_Id(id, merchant.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));

        return stationInventoryMapper.toResponse(inventory);
    }

    @Transactional(readOnly = true)
    public List<StationInventoryResponse> getStationInventory(Long stationId) {
        Merchant merchant = authenticatedUserService.getCurrentMerchant();
        if (!stationRepository.existsByIdAndMerchant_Id(stationId, merchant.getId())) {
            throw new ResourceNotFoundException("Station not found");
        }

        return stationInventoryMapper.toResponseList(
                stationInventoryRepository.findByStationIdAndStation_Merchant_IdOrderByProduct_NameAsc(stationId, merchant.getId()));
    }

    @Transactional(readOnly = true)
    public List<StationInventoryResponse> getAllInventories() {
                Merchant merchant = authenticatedUserService.getCurrentMerchant();

        return stationInventoryMapper.toResponseList(stationInventoryRepository
                        .findByStation_Merchant_IdOrderByStation_NameAscProduct_NameAsc(merchant.getId()));
    }

    @Transactional(readOnly = true)
    public List<StationInventoryResponse> getActiveInventories() {
                Merchant merchant = authenticatedUserService.getCurrentMerchant();

        return stationInventoryMapper.toResponseList(stationInventoryRepository.findByActiveTrueAndStation_Merchant_IdOrderByStation_NameAsc(merchant.getId()));
    }

    @Transactional
    public StationInventoryResponse activateInventory(Long id) {
                Merchant merchant = authenticatedUserService.getCurrentMerchant();

        StationInventory inventory = stationInventoryRepository.findByIdAndStation_Merchant_Id(id, merchant.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));

        if (Boolean.TRUE.equals(inventory.getActive())) {
            throw new DuplicateResourceException("Inventory is already active");
        }

        inventory.setActive(true);
        return stationInventoryMapper.toResponse(stationInventoryRepository.save(inventory));
    }

    @Transactional
    public StationInventoryResponse deactivateInventory(Long id) {
        Merchant merchant = authenticatedUserService.getCurrentMerchant();
        StationInventory inventory = stationInventoryRepository.findByIdAndStation_Merchant_Id(id, merchant.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));

        if (Boolean.FALSE.equals(inventory.getActive())) {
            throw new DuplicateResourceException("Inventory is already inactive");
        }

        inventory.setActive(false);
        return stationInventoryMapper.toResponse(stationInventoryRepository.save(inventory));
    }

    @Transactional(readOnly = true)
    public BigDecimal getCurrentSellingPrice(Long stationId, Long productId) {
                Merchant merchant = authenticatedUserService.getCurrentMerchant();

        StationInventory inventory = stationInventoryRepository.findByStationIdAndProductIdAndStation_Merchant_Id(stationId,productId, merchant.getId())
                                        .orElseThrow(() -> new ResourceNotFoundException("Station inventory not found"));

        return inventory.getSellingPrice();
    }

    // private Merchant getCurrentMerchant() {
    //     Merchant merchant = MerchantContext.getCurrentMerchant();
    //     if (merchant == null) {
    //        throw new ResourceNotFoundException("Merchant is not authenticated");
    //     }
    //     return merchant;
    // }


}