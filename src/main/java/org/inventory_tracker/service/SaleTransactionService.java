package org.inventory_tracker.service;

import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.request.CreateTransactionRequest;
import org.inventory_tracker.dto.response.SaleTransactionResponse;
import org.inventory_tracker.entity.*;
import org.inventory_tracker.config.mapper.SaleTransactionMapper;
import org.inventory_tracker.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SaleTransactionService {

    private final SaleTransactionRepository
            saleTransactionRepository;

    private final StationRepository stationRepository;

    private final AttendantRepository attendantRepository;

    private final BusinessTypeRepository
            businessTypeRepository;

    private final PosTerminalRepository
            posTerminalRepository;

    private final SaleTransactionMapper
            saleTransactionMapper;

    public SaleTransactionResponse create(
            CreateTransactionRequest request
    ) {

        Station station = stationRepository.findById(
                request.getStationId()
        ).orElseThrow(() ->
                new RuntimeException("Station not found")
        );

        Attendant attendant = attendantRepository.findById(
                request.getAttendantId()
        ).orElseThrow(() ->
                new RuntimeException("Attendant not found")
        );

        BusinessType businessType =
                businessTypeRepository.findById(
                        request.getBusinessTypeId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Business type not found"
                        )
                );

        PosTerminal terminal = posTerminalRepository.findById(
                request.getPosTerminalId()
        ).orElseThrow(() ->
                new RuntimeException("POS terminal not found")
        );

        SaleTransaction transaction =
                saleTransactionMapper.toEntity(request);

        transaction.setTransactionReference(
                UUID.randomUUID().toString()
        );

        transaction.setStation(station);

        transaction.setAttendant(attendant);

        transaction.setBusinessType(businessType);

        transaction.setPosTerminal(terminal);

        SaleTransaction savedTransaction =
                saleTransactionRepository.save(transaction);

        return saleTransactionMapper.toResponse(
                savedTransaction
        );
    }

    public List<SaleTransactionResponse> getAll() {

        List<SaleTransaction> transactions =
                saleTransactionRepository.findAll();

        return saleTransactionMapper.toResponseList(
                transactions
        );
    }

    public SaleTransactionResponse getById(Long id) {

        SaleTransaction transaction =
                saleTransactionRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Transaction not found"
                                )
                        );

        return saleTransactionMapper.toResponse(
                transaction
        );
    }

    public SaleTransactionResponse update(
            Long id,
            CreateTransactionRequest request
    ) {

        SaleTransaction transaction =
                saleTransactionRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Transaction not found"
                                )
                        );

        Station station = stationRepository.findById(
                request.getStationId()
        ).orElseThrow(() ->
                new RuntimeException("Station not found")
        );

        Attendant attendant = attendantRepository.findById(
                request.getAttendantId()
        ).orElseThrow(() ->
                new RuntimeException("Attendant not found")
        );

        BusinessType businessType =
                businessTypeRepository.findById(
                        request.getBusinessTypeId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Business type not found"
                        )
                );

        PosTerminal terminal = posTerminalRepository.findById(
                request.getPosTerminalId()
        ).orElseThrow(() ->
                new RuntimeException("POS terminal not found")
        );

        transaction.setTransactionType(
                request.getTransactionType()
        );

        transaction.setPaymentMethod(
                request.getPaymentMethod()
        );

        transaction.setStatus(request.getStatus());

        transaction.setAmount(request.getAmount());

        transaction.setNarration(request.getNarration());

        transaction.setStation(station);

        transaction.setAttendant(attendant);

        transaction.setBusinessType(businessType);

        transaction.setPosTerminal(terminal);

        SaleTransaction updatedTransaction =
                saleTransactionRepository.save(transaction);

        return saleTransactionMapper.toResponse(
                updatedTransaction
        );
    }

    public void delete(Long id) {

        SaleTransaction transaction =
                saleTransactionRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Transaction not found"
                                )
                        );

        saleTransactionRepository.delete(transaction);
    }
}