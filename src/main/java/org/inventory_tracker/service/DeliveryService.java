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
// import org.inventory_tracker.entity.security.MerchantContext;
import org.inventory_tracker.entity.specification.DeliverySpecification;
import org.inventory_tracker.repository.StationInventoryRepository;
import org.inventory_tracker.security.AuthenticatedUserService;
import org.inventory_tracker.security.MerchantPrincipal;
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
    private final AuthenticatedUserService authenticatedUserService;


    public DeliveryResponse createDelivery(CreateDeliveryRequest request) {
        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
        if (deliveryRepository.existsByDeliveryNumber(request.getDeliveryNumber())) {
            throw new DuplicateResourceException("Delivery number already exists.");
        }

        StationInventory stationInventory = stationInventoryRepository
                        .findByIdAndStation_Merchant_Id(request.getStationInventoryId(), principal.getMerchantDbId())
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
        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
        Delivery delivery = deliveryRepository
                        .findByIdAndStation_Merchant_Id(deliveryId, principal.getMerchantDbId())
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

        inventoryTransactionService.recordTransactionForDashboard(
                delivery.getStationInventory().getId(),
                InventoryTransactionType.DELIVERY,
                delivery.getQuantityDelivered(),
                delivery.getRemarks(),
                delivery.getDeliveryNumber()
        );

        return deliveryMapper.toResponse(delivery);
    }


    public DeliveryResponse cancelDelivery(Long deliveryId) {
        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
        Delivery delivery = deliveryRepository.findByIdAndStation_Merchant_Id(deliveryId, principal.getMerchantDbId())
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
        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
        return deliveryMapper.toResponse(
                deliveryRepository.findByIdAndStation_Merchant_Id(id, principal.getMerchantDbId())
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found."))
        );
    }


    @Transactional(readOnly = true)
    public DeliveryResponse getDeliveryByDeliveryNumber(String deliveryNumber) {
        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
        Delivery delivery = deliveryRepository.findByDeliveryNumberAndStation_Merchant_Id(deliveryNumber, principal.getMerchantDbId())
                                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found."));

        return deliveryMapper.toResponse(delivery);
    }

    @Transactional(readOnly = true)
    public List<DeliveryResponse> filterDeliveries(DeliveryFilterRequest request) {
        if (request.getStartDate() != null && request.getEndDate() != null
                && request.getStartDate().isAfter(request.getEndDate())) {

                throw new BadRequestException("Start date cannot be after end date.");
        }

        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
        return deliveryMapper.toResponseList(deliveryRepository.findAll(DeliverySpecification.filter(request, principal.getMerchantDbId())));
    }

    @Transactional
    public DeliveryResponse reverseDelivery(Long deliveryId, ReverseDeliveryRequest request) {
        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
        Delivery delivery = deliveryRepository.findByIdAndStation_Merchant_Id(deliveryId, principal.getMerchantDbId())
                                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found."));

        if (delivery.getStatus() != DeliveryStatus.RECEIVED) {
                throw new BadRequestException("Only received deliveries can be reversed.");
        }

        StationInventory inventory = delivery.getStationInventory();
        BigDecimal availableStock = inventory.getCurrentQuantity();

        if (availableStock.compareTo(delivery.getQuantityDelivered()) < 0) {
                throw new BadRequestException("Delivery cannot be reversed because some or all of the stock has already been consumed.");
        }

        inventoryTransactionService.recordTransactionForDashboard(
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
        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
        return deliveryMapper.toResponseList(deliveryRepository
                .findByStation_Merchant_IdOrderByBusinessDateDescReceivedAtDesc(principal.getMerchantDbId()));
    }


    @Transactional(readOnly = true)
    public List<DeliveryResponse> getStationDeliveries(Long stationId) {
        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
        return deliveryMapper.toResponseList(deliveryRepository
                        .findByStation_Merchant_IdAndStation_IdOrderByBusinessDateDescReceivedAtDesc(principal.getMerchantDbId(), stationId));
    }


    @Transactional(readOnly = true)
    public List<DeliveryResponse> getProductDeliveries(Long productId) {
        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
        return deliveryMapper.toResponseList(deliveryRepository.findByStation_Merchant_IdAndProduct_IdOrderByBusinessDateDescReceivedAtDesc(principal.getMerchantDbId(), productId));
    }


    @Transactional(readOnly = true)
    public List<DeliveryResponse> getInventoryDeliveries(Long stationInventoryId) {
        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
        return deliveryMapper.toResponseList(deliveryRepository
                        .findByStation_Merchant_IdAndStationInventory_IdOrderByReceivedAtDesc(principal.getMerchantDbId(), stationInventoryId));
    }


    @Transactional(readOnly = true)
    public List<DeliveryResponse> getDeliveriesByStatus(DeliveryStatus status) {
        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
        return deliveryMapper.toResponseList(deliveryRepository.findByStation_Merchant_IdAndStatusOrderByBusinessDateDescReceivedAtDesc(principal.getMerchantDbId(), status));
    }


    @Transactional(readOnly = true)
    public List<DeliveryResponse> getDeliveriesBetweenDates(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException( "Start date cannot be after end date.");
        }
        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();

        return deliveryMapper.toResponseList(
                deliveryRepository.findByStation_Merchant_IdAndBusinessDateBetweenOrderByReceivedAtDesc(principal.getMerchantDbId(), startDate, endDate)
        );
    }


    @Transactional(readOnly = true)
    public List<DeliveryResponse> getStationDeliveriesBetweenDates(Long stationId, LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date cannot be after end date.");
        }
        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();

        return deliveryMapper.toResponseList(deliveryRepository
                        .findByStation_Merchant_IdAndStation_IdAndBusinessDateBetweenOrderByReceivedAtDesc(principal.getMerchantDbId(), stationId, startDate, endDate));
    }

    // private Merchant getCurrentMerchant() {
    //     Merchant merchant = MerchantContext.getCurrentMerchant();
    //     if (merchant == null) {
    //             throw new ResourceNotFoundException("Merchant is not authenticated");
    //     }
    //     return merchant;
    // }

}
