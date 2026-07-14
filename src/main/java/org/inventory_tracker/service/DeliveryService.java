package org.inventory_tracker.service;


import org.inventory_tracker.dto.request.CreateDeliveryRequest;
import org.inventory_tracker.dto.response.DeliveryResponse;
import org.inventory_tracker.entity.Delivery;
import org.inventory_tracker.enums.DeliveryStatus;
import org.inventory_tracker.config.mapper.DeliveryMapper;
import org.inventory_tracker.repository.DeliveryRepository;
import org.inventory_tracker.exception.BadRequestException;
import org.inventory_tracker.exception.DuplicateResourceException;
import org.inventory_tracker.exception.ResourceNotFoundException;
import org.inventory_tracker.entity.StationInventory;
import org.inventory_tracker.repository.StationInventoryRepository;
import org.inventory_tracker.enums.InventoryTransactionType;
import org.inventory_tracker.entity.Station;
import org.inventory_tracker.util.ShiftUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
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


    /**
     * Creates a delivery record.
     *
     * This DOES NOT increase inventory.
     * Inventory only increases when receiveDelivery()
     * is called.
     */
    public DeliveryResponse createDelivery(
            CreateDeliveryRequest request) {

        if (deliveryRepository.existsByDeliveryNumber(
                request.getDeliveryNumber())) {

            throw new DuplicateResourceException(
                    "Delivery number already exists.");
        }

        StationInventory stationInventory =
                stationInventoryRepository
                        .findById(request.getStationInventoryId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Station inventory not found."));

        Delivery delivery =
                deliveryMapper.toEntity(request);

        delivery.setStationInventory(stationInventory);

        delivery.setStation(
                stationInventory.getStation());

        delivery.setProduct(
                stationInventory.getProduct());

        delivery.setStatus(
                DeliveryStatus.PENDING);

        /*
         * These are only populated after the
         * delivery is physically received.
         */
        delivery.setBusinessDate(null);
        delivery.setReceivedAt(null);

        Delivery saved =
                deliveryRepository.save(delivery);

        return deliveryMapper.toResponse(saved);
    }


    /**
     * Confirms that the tanker has been received.
     *
     * This is the point where inventory increases.
     */
    public DeliveryResponse receiveDelivery(
            Long deliveryId) {

        Delivery delivery =
                deliveryRepository
                        .findById(deliveryId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Delivery not found."));

        if (delivery.getStatus() ==
                DeliveryStatus.RECEIVED) {

            throw new BadRequestException(
                    "Delivery has already been received.");
        }

        if (delivery.getStatus() ==
                DeliveryStatus.CANCELLED) {

            throw new BadRequestException(
                    "Cancelled deliveries cannot be received.");
        }

        Station station =
                delivery.getStation();

        delivery.setStatus(
                DeliveryStatus.RECEIVED);

        delivery.setBusinessDate(
                ShiftUtil.businessDate(
                        station.getTimeZone()));

        delivery.setReceivedAt(
                LocalDateTime.now(
                        station.getTimeZone()));

        /*
         * Save delivery before updating inventory.
         */
        deliveryRepository.save(delivery);

        /*
         * Create immutable inventory ledger entry.
         */
        inventoryTransactionService.recordTransaction(

                delivery.getStationInventory().getId(),

                InventoryTransactionType.DELIVERY,

                delivery.getQuantityDelivered(),

                delivery.getRemarks(),

                delivery.getDeliveryNumber()

        );

        return deliveryMapper.toResponse(delivery);
    }

        /**
     * Cancels a pending delivery.
     */
    public DeliveryResponse cancelDelivery(Long deliveryId) {

        Delivery delivery = findDelivery(deliveryId);

        if (delivery.getStatus() == DeliveryStatus.RECEIVED) {

            throw new BadRequestException(
                    "Received deliveries cannot be cancelled.");
        }

        if (delivery.getStatus() == DeliveryStatus.CANCELLED) {

            throw new BadRequestException(
                    "Delivery has already been cancelled.");
        }

        delivery.setStatus(DeliveryStatus.CANCELLED);

        Delivery updated =
                deliveryRepository.save(delivery);

        return deliveryMapper.toResponse(updated);
    }


    @Transactional(readOnly = true)
    public DeliveryResponse getDeliveryById(Long id) {

        return deliveryMapper.toResponse(
                findDelivery(id)
        );
    }


    @Transactional(readOnly = true)
    public DeliveryResponse getDeliveryByDeliveryNumber(
            String deliveryNumber) {

        Delivery delivery =
                deliveryRepository
                        .findByDeliveryNumber(deliveryNumber)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Delivery not found."));

        return deliveryMapper.toResponse(delivery);
    }


    @Transactional(readOnly = true)
    public List<DeliveryResponse> getAllDeliveries() {

        return deliveryMapper.toResponseList(
                deliveryRepository.findAllByOrderByBusinessDateDescReceivedAtDesc()
                //deliveryRepository.findAllByOrderByReceivedAtDesc()
        );
    }


    @Transactional(readOnly = true)
    public List<DeliveryResponse> getStationDeliveries(
            Long stationId) {

        return deliveryMapper.toResponseList(

                deliveryRepository
                        .findByStationIdOrderByBusinessDateDescReceivedAtDesc(
                                stationId
                        )
                        // .findByStationIdOrderByReceivedAtDesc(
                        //         stationId
                        // )
        );
    }


    @Transactional(readOnly = true)
    public List<DeliveryResponse> getProductDeliveries(
            Long productId) {

        return deliveryMapper.toResponseList(

                deliveryRepository
                        .findByProductIdOrderByBusinessDateDescReceivedAtDesc(
                                productId
                        )
                        // .findByProductIdOrderByReceivedAtDesc(
                        //         productId
                        // )
        );
    }


    @Transactional(readOnly = true)
    public List<DeliveryResponse> getInventoryDeliveries(
            Long stationInventoryId) {

        return deliveryMapper.toResponseList(

                deliveryRepository
                        .findByStationInventoryIdOrderByReceivedAtDesc(
                                stationInventoryId
                        )
        );
    }


    @Transactional(readOnly = true)
    public List<DeliveryResponse> getDeliveriesByStatus(
            DeliveryStatus status) {

        return deliveryMapper.toResponseList(

                deliveryRepository
                        .findByStatusOrderByBusinessDateDescReceivedAtDesc(
                                status
                        )
                        // .findByStatusOrderByReceivedAtDesc(
                        //         status
                        // )
        );
    }


    @Transactional(readOnly = true)
    public List<DeliveryResponse> getDeliveriesBetweenDates(
            LocalDate startDate,
            LocalDate endDate) {

        if (startDate.isAfter(endDate)) {

            throw new BadRequestException(
                    "Start date cannot be after end date.");
        }

        return deliveryMapper.toResponseList(

                deliveryRepository
                        .findByBusinessDateBetweenOrderByReceivedAtDesc(
                                startDate,
                                endDate
                        )
        );
    }


    @Transactional(readOnly = true)
    public List<DeliveryResponse> getStationDeliveriesBetweenDates(
            Long stationId,
            LocalDate startDate,
            LocalDate endDate) {

        if (startDate.isAfter(endDate)) {

            throw new BadRequestException(
                    "Start date cannot be after end date.");
        }

        return deliveryMapper.toResponseList(

                deliveryRepository
                        .findByStationIdAndBusinessDateBetweenOrderByReceivedAtDesc(
                                stationId,
                                startDate,
                                endDate
                        )
        );
    }


    /**
     * Helper method for loading deliveries.
     */
    private Delivery findDelivery(Long deliveryId) {

        return deliveryRepository
                .findById(deliveryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Delivery not found."));
    }

}
