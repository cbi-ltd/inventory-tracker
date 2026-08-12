package org.inventory_tracker.service;


import org.inventory_tracker.dto.request.CreateDeliveryRequest;
import org.inventory_tracker.dto.response.DeliveryResponse;
import org.inventory_tracker.entity.Delivery;
import org.inventory_tracker.entity.Merchant;
import org.inventory_tracker.enums.DeliveryStatus;
import org.inventory_tracker.config.mapper.DeliveryMapper;
import org.inventory_tracker.repository.DeliveryRepository;
import org.inventory_tracker.dto.request.DeliveryFilterRequest;
import org.inventory_tracker.dto.request.ReverseDeliveryRequest;
import org.inventory_tracker.exception.BadRequestException;
import org.inventory_tracker.exception.DuplicateResourceException;
import org.inventory_tracker.exception.ResourceNotFoundException;
import org.inventory_tracker.entity.StationInventory;
import org.inventory_tracker.entity.security.MerchantContext;
import org.inventory_tracker.entity.specification.DeliverySpecification;
import org.inventory_tracker.repository.StationInventoryRepository;
import org.inventory_tracker.enums.InventoryTransactionType;
import org.inventory_tracker.entity.Station;
import org.inventory_tracker.util.ShiftUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final StationInventoryRepository stationInventoryRepository;
    private final DeliveryMapper deliveryMapper;
    private final InventoryTransactionService inventoryTransactionService;


    public DeliveryResponse createDelivery(CreateDeliveryRequest request) {
        Merchant merchant = getCurrentMerchant();
        if (deliveryRepository.existsByDeliveryNumber(request.getDeliveryNumber())) {
            throw new DuplicateResourceException("Delivery number already exists.");
        }

        StationInventory stationInventory = stationInventoryRepository
                        .findByIdAndStation_Merchant_Id(request.getStationInventoryId(), merchant.getId())
                        .orElseThrow(() ->new ResourceNotFoundException("Station inventory not found."));

        Delivery delivery = deliveryMapper.toEntity(request);
        delivery.setStationInventory(stationInventory);
        delivery.setStation(stationInventory.getStation());
        delivery.setProduct(stationInventory.getProduct());
        delivery.setStatus(DeliveryStatus.PENDING);
        delivery.setBusinessDate(null);
        delivery.setReceivedAt(null);
        // delivery.setBusinessDate(ShiftUtil.businessDate(stationInventory.getStation().getTimeZone()));
        // delivery.setReceivedAt(LocalDateTime.now(stationInventory.getStation().getTimeZone()));

        Delivery saved = deliveryRepository.save(delivery);
        return deliveryMapper.toResponse(saved);
    }


    public DeliveryResponse receiveDelivery(Long deliveryId) {
        Merchant merchant = getCurrentMerchant();
        Delivery delivery = deliveryRepository
                        .findByIdAndStation_Merchant_Id(deliveryId, merchant.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Delivery not found."));

        if (delivery.getStatus() == DeliveryStatus.RECEIVED) {
            throw new BadRequestException( "Delivery has already been received.");
        }

        if (delivery.getStatus() == DeliveryStatus.CANCELLED) {
            throw new BadRequestException("Cancelled deliveries cannot be received.");
        }

        Station station = delivery.getStation();
        delivery.setStatus(DeliveryStatus.RECEIVED);
        delivery.setBusinessDate(ShiftUtil.businessDate(station.getTimeZone()));
        delivery.setReceivedAt(LocalDateTime.now(station.getTimeZone()));
        deliveryRepository.save(delivery);

        StationInventory inventory = delivery.getStationInventory();
        inventory.setCostPerUnit(delivery.getCostPerUnit());
        stationInventoryRepository.save(inventory);

        inventoryTransactionService.recordTransaction(
                delivery.getStationInventory().getId(),
                InventoryTransactionType.DELIVERY,
                delivery.getQuantityDelivered(),
                delivery.getRemarks(),
                delivery.getDeliveryNumber()
        );

        return deliveryMapper.toResponse(delivery);
    }


    public DeliveryResponse cancelDelivery(Long deliveryId) {
        Merchant merchant = getCurrentMerchant();
        Delivery delivery = deliveryRepository.findByIdAndStation_Merchant_Id(deliveryId, merchant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found."));

        if (delivery.getStatus() == DeliveryStatus.RECEIVED) {
            throw new BadRequestException("Received deliveries cannot be cancelled.");
        }

        if (delivery.getStatus() == DeliveryStatus.CANCELLED) {
            throw new BadRequestException("Delivery has already been cancelled.");
        }

        delivery.setStatus(DeliveryStatus.CANCELLED);
        Delivery updated = deliveryRepository.save(delivery);
        return deliveryMapper.toResponse(updated);
    }


    @Transactional(readOnly = true)
    public DeliveryResponse getDeliveryById(Long id) {
        Merchant merchant = getCurrentMerchant();
        return deliveryMapper.toResponse(
                deliveryRepository.findByIdAndStation_Merchant_Id(id, merchant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found."))
        );
    }


    @Transactional(readOnly = true)
    public DeliveryResponse getDeliveryByDeliveryNumber(String deliveryNumber) {
        Merchant merchant = getCurrentMerchant();
        Delivery delivery = deliveryRepository.findByDeliveryNumberAndStation_Merchant_Id(deliveryNumber, merchant.getId())
                                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found."));

        return deliveryMapper.toResponse(delivery);
    }

    @Transactional(readOnly = true)
    public List<DeliveryResponse> filterDeliveries(DeliveryFilterRequest request) {
        if (request.getStartDate() != null && request.getEndDate() != null
                && request.getStartDate().isAfter(request.getEndDate())) {

                throw new BadRequestException("Start date cannot be after end date.");
        }

        Merchant merchant = getCurrentMerchant();
        return deliveryMapper.toResponseList(deliveryRepository.findAll(DeliverySpecification.filter(request, merchant.getId())));
    }

    @Transactional
    public DeliveryResponse reverseDelivery(Long deliveryId, ReverseDeliveryRequest request) {
        Merchant merchant = getCurrentMerchant();
        Delivery delivery = deliveryRepository.findByIdAndStation_Merchant_Id(deliveryId, merchant.getId())
                                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found."));

        if (delivery.getStatus() != DeliveryStatus.RECEIVED) {
                throw new BadRequestException("Only received deliveries can be reversed.");
        }

        StationInventory inventory = delivery.getStationInventory();
        BigDecimal availableStock = inventory.getCurrentQuantity();

        if (availableStock.compareTo(delivery.getQuantityDelivered()) < 0) {
                throw new BadRequestException("Delivery cannot be reversed because some or all of the stock has already been consumed.");
        }

        inventoryTransactionService.recordTransaction(
                inventory.getId(),
                InventoryTransactionType.DELIVERY_REVERSAL,
                delivery.getQuantityDelivered(),
                request.getReason(),
                delivery.getDeliveryNumber()

        );

        delivery.setStatus(DeliveryStatus.REVERSED);
        delivery.setReversedAt(LocalDateTime.now(delivery.getStation().getTimeZone()));
        delivery.setReversalReason(request.getReason());
        deliveryRepository.save(delivery);
        return deliveryMapper.toResponse(delivery);
    }


    @Transactional(readOnly = true)
    public List<DeliveryResponse> getAllDeliveries() {
        Merchant merchant = getCurrentMerchant();
        return deliveryMapper.toResponseList(deliveryRepository
                .findByStation_Merchant_IdOrderByBusinessDateDescReceivedAtDesc(merchant.getId()));
    }


    @Transactional(readOnly = true)
    public List<DeliveryResponse> getStationDeliveries(Long stationId) {
        Merchant merchant = getCurrentMerchant();
        return deliveryMapper.toResponseList(deliveryRepository
                        .findByStation_Merchant_IdAndStation_IdOrderByBusinessDateDescReceivedAtDesc(merchant.getId(), stationId));
    }


    @Transactional(readOnly = true)
    public List<DeliveryResponse> getProductDeliveries(Long productId) {
        Merchant merchant = getCurrentMerchant();
        return deliveryMapper.toResponseList(deliveryRepository.findByStation_Merchant_IdAndProduct_IdOrderByBusinessDateDescReceivedAtDesc(merchant.getId(), productId));
    }


    @Transactional(readOnly = true)
    public List<DeliveryResponse> getInventoryDeliveries(Long stationInventoryId) {
        Merchant merchant = getCurrentMerchant();
        return deliveryMapper.toResponseList(deliveryRepository
                        .findByStation_Merchant_IdAndStationInventory_IdOrderByReceivedAtDesc(merchant.getId(), stationInventoryId));
    }


    @Transactional(readOnly = true)
    public List<DeliveryResponse> getDeliveriesByStatus(DeliveryStatus status) {
        Merchant merchant = getCurrentMerchant();
        return deliveryMapper.toResponseList(deliveryRepository.findByStation_Merchant_IdAndStatusOrderByBusinessDateDescReceivedAtDesc(merchant.getId(), status));
    }


    @Transactional(readOnly = true)
    public List<DeliveryResponse> getDeliveriesBetweenDates(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException( "Start date cannot be after end date.");
        }
        Merchant merchant = getCurrentMerchant();

        return deliveryMapper.toResponseList(
                deliveryRepository.findByStation_Merchant_IdAndBusinessDateBetweenOrderByReceivedAtDesc(merchant.getId(), startDate, endDate)
        );
    }


    @Transactional(readOnly = true)
    public List<DeliveryResponse> getStationDeliveriesBetweenDates(Long stationId, LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date cannot be after end date.");
        }
        Merchant merchant = getCurrentMerchant();

        return deliveryMapper.toResponseList(deliveryRepository
                        .findByStation_Merchant_IdAndStation_IdAndBusinessDateBetweenOrderByReceivedAtDesc(merchant.getId(), stationId, startDate, endDate));
    }

    private Merchant getCurrentMerchant() {
        Merchant merchant = MerchantContext.getCurrentMerchant();
        if (merchant == null) {
                throw new ResourceNotFoundException("Merchant is not authenticated");
        }
        return merchant;
    }

}
