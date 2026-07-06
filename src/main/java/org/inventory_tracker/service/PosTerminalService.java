package org.inventory_tracker.service;

import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.request.CreatePosTerminalRequest;
import org.inventory_tracker.dto.response.PosTerminalResponse;
import org.inventory_tracker.entity.PosTerminal;
import org.inventory_tracker.entity.Station;
import org.inventory_tracker.config.mapper.PosTerminalMapper;
import org.inventory_tracker.repository.PosTerminalRepository;
import org.inventory_tracker.repository.StationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PosTerminalService {

    private final PosTerminalRepository posTerminalRepository;
    private final StationRepository stationRepository;
    private final PosTerminalMapper posTerminalMapper;

    public PosTerminalResponse create(
            CreatePosTerminalRequest request
    ) {

        if (posTerminalRepository.existsBySerialNumber(
                request.getSerialNumber()
        )) {
            throw new RuntimeException(
                    "Serial number already exists"
            );
        }

        if (posTerminalRepository.existsByMacAddress(
                request.getMacAddress()
        )) {
            throw new RuntimeException(
                    "MAC address already exists"
            );
        }

        Station station = stationRepository.findById(
                request.getStationId()
        ).orElseThrow(() ->
                new RuntimeException("Station not found")
        );

        PosTerminal terminal =
                posTerminalMapper.toEntity(request);

        terminal.setStation(station);

        terminal.setActive(true);

        PosTerminal savedTerminal =
                posTerminalRepository.save(terminal);

        return posTerminalMapper.toResponse(savedTerminal);
    }

    public List<PosTerminalResponse> getAll() {

        List<PosTerminal> terminals =
                posTerminalRepository.findAll();

        return posTerminalMapper.toResponseList(terminals);
    }

    public PosTerminalResponse getById(Long id) {

        PosTerminal terminal =
                posTerminalRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "POS terminal not found"
                                )
                        );

        return posTerminalMapper.toResponse(terminal);
    }

    public PosTerminalResponse update(
            Long id,
            CreatePosTerminalRequest request
    ) {

        PosTerminal terminal =
                posTerminalRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "POS terminal not found"
                                )
                        );

        Station station = stationRepository.findById(
                request.getStationId()
        ).orElseThrow(() ->
                new RuntimeException("Station not found")
        );

        terminal.setSerialNumber(request.getSerialNumber());
        terminal.setMacAddress(request.getMacAddress());
        terminal.setPosType(request.getPosType());
        terminal.setStation(station);

        PosTerminal updatedTerminal =
                posTerminalRepository.save(terminal);

        return posTerminalMapper.toResponse(updatedTerminal);
    }

    public void delete(Long id) {

        PosTerminal terminal =
                posTerminalRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "POS terminal not found"
                                )
                        );

        posTerminalRepository.delete(terminal);
    }
}