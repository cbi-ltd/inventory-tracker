package org.inventory_tracker.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.inventory_tracker.config.mapper.StationInventoryMapper;
import org.inventory_tracker.dto.request.CreateStationInventoryRequest;
import org.inventory_tracker.dto.response.StationInventoryResponse;
import org.inventory_tracker.entity.Station;
import org.inventory_tracker.entity.StationInventory;
import org.inventory_tracker.enums.ProductType;
import org.inventory_tracker.repository.StationInventoryRepository;
import org.inventory_tracker.repository.StationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StationInventoryService {

    private final StationInventoryRepository repository;
    private final StationRepository stationRepository;
    private final StationInventoryMapper stationInventoryMapper;

    public StationInventory createDailySnapshot(
            String merchantId,
            String outletId,
            ProductType productType,
            Double openingQuantity
    ) {

        StationInventory inventory = new StationInventory();

        inventory.setMerchantId(merchantId);
        inventory.setOutletId(outletId);
        inventory.setProductType(productType);

        inventory.setOpeningQuantity(openingQuantity);
        inventory.setClosingQuantity(openingQuantity);

        inventory.setQuantitySold(0.0);

        inventory.setBusinessDate(LocalDate.now());

        return repository.save(inventory);
    }

    @Transactional
    public void updateAfterSale(
            String outletId,
            ProductType productType,
            Double litresSold
    ) {

        Optional<StationInventory> inventory =
                repository.findTodayInventory(
                        outletId,
                        productType
                );

        inventory.get().setQuantitySold(
                inventory.get().getQuantitySold() + litresSold
        );

        inventory.get().setClosingQuantity(
                inventory.get().getClosingQuantity() - litresSold
        );

        repository.save(inventory.get());
    }

//  ___________________________________________

    public StationInventoryResponse create(CreateStationInventoryRequest request) {

        Station station = stationRepository.findById(
                request.getStationId()
        ).orElseThrow(() ->
                new RuntimeException("Station not found")
        );

        StationInventory inventory =
                stationInventoryMapper.toEntity(request);

//        inventory.setStation(station);

        StationInventory savedInventory =
                repository.save(inventory);

        return stationInventoryMapper.toResponse(
                savedInventory
        );
    }

    public List<StationInventoryResponse> getAll() {

        List<StationInventory> inventories =
                repository.findAll();

        return stationInventoryMapper.toResponseList(
                inventories
        );
    }

    public StationInventoryResponse getById(Long id) {

        StationInventory inventory =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Inventory not found"
                                )
                        );

        return stationInventoryMapper.toResponse(
                inventory
        );
    }

    public StationInventoryResponse update(
            Long id,
            CreateStationInventoryRequest request
    ) {

        StationInventory inventory =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Inventory not found"
                                )
                        );

        Station station = stationRepository.findById(
                request.getStationId()
        ).orElseThrow(() ->
                new RuntimeException("Station not found")
        );

        inventory.setProductType(request.getProductType());
//        inventory.setQuantity(request.getQuantity());
//        inventory.setStation(station);

        StationInventory updatedInventory =
                repository.save(inventory);

        return stationInventoryMapper.toResponse(
                updatedInventory
        );
    }

    public void delete(Long id) {

        StationInventory inventory =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Inventory not found"
                                )
                        );

        repository.delete(inventory);
    }

}




//import lombok.RequiredArgsConstructor;
//import org.inventory_tracker.dto.request.CreateStationInventoryRequest;
//import org.inventory_tracker.dto.response.StationInventoryResponse;
//import org.inventory_tracker.entity.Station;
//import org.inventory_tracker.entity.StationInventory;
//import org.inventory_tracker.config.mapper.StationInventoryMapper;
//import org.inventory_tracker.repository.StationInventoryRepository;
//import org.inventory_tracker.repository.StationRepository;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class StationInventoryService {
//
//    private final StationInventoryRepository
//            stationInventoryRepository;
//
//    private final StationRepository stationRepository;
//
//    private final StationInventoryMapper
//            stationInventoryMapper;
//
//    public StationInventoryResponse create(
//            CreateStationInventoryRequest request
//    ) {
//
//        Station station = stationRepository.findById(
//                request.getStationId()
//        ).orElseThrow(() ->
//                new RuntimeException("Station not found")
//        );
//
//        StationInventory inventory =
//                stationInventoryMapper.toEntity(request);
//
//        inventory.setStation(station);
//
//        StationInventory savedInventory =
//                stationInventoryRepository.save(inventory);
//
//        return stationInventoryMapper.toResponse(
//                savedInventory
//        );
//    }
//
//    public List<StationInventoryResponse> getAll() {
//
//        List<StationInventory> inventories =
//                stationInventoryRepository.findAll();
//
//        return stationInventoryMapper.toResponseList(
//                inventories
//        );
//    }
//
//    public StationInventoryResponse getById(Long id) {
//
//        StationInventory inventory =
//                stationInventoryRepository.findById(id)
//                        .orElseThrow(() ->
//                                new RuntimeException(
//                                        "Inventory not found"
//                                )
//                        );
//
//        return stationInventoryMapper.toResponse(
//                inventory
//        );
//    }
//
//    public StationInventoryResponse update(
//            Long id,
//            CreateStationInventoryRequest request
//    ) {
//
//        StationInventory inventory =
//                stationInventoryRepository.findById(id)
//                        .orElseThrow(() ->
//                                new RuntimeException(
//                                        "Inventory not found"
//                                )
//                        );
//
//        Station station = stationRepository.findById(
//                request.getStationId()
//        ).orElseThrow(() ->
//                new RuntimeException("Station not found")
//        );
//
//        inventory.setProductType(request.getProductType());
//        inventory.setQuantity(request.getQuantity());
//        inventory.setStation(station);
//
//        StationInventory updatedInventory =
//                stationInventoryRepository.save(inventory);
//
//        return stationInventoryMapper.toResponse(
//                updatedInventory
//        );
//    }
//
//    public void delete(Long id) {
//
//        StationInventory inventory =
//                stationInventoryRepository.findById(id)
//                        .orElseThrow(() ->
//                                new RuntimeException(
//                                        "Inventory not found"
//                                )
//                        );
//
//        stationInventoryRepository.delete(inventory);
//    }
//}