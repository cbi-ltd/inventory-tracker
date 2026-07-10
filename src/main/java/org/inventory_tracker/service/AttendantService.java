package org.inventory_tracker.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.request.CreateAttendantRequest;
import org.inventory_tracker.dto.response.AttendantResponse;
import org.inventory_tracker.entity.Attendant;
import org.inventory_tracker.entity.Station;
import org.inventory_tracker.config.mapper.AttendantMapper;
import org.inventory_tracker.repository.AttendantRepository;
import org.inventory_tracker.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.inventory_tracker.exception.DuplicateResourceException;
import org.inventory_tracker.exception.ResourceNotFoundException;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AttendantService {

    private final AttendantRepository attendantRepository;
    private final StationRepository stationRepository;
    private final AttendantMapper attendantMapper;
    

    @Transactional
    public AttendantResponse createAttendant(CreateAttendantRequest request) {
        if (attendantRepository.existsByUsername(request.getUsername())) { throw new DuplicateResourceException("Username already exists"); }

        Station station = stationRepository.findById(request.getStationId())
                .orElseThrow(() -> new ResourceNotFoundException("Station not found"));

        Attendant attendant = attendantMapper.toEntity(request);
        attendant.setStation(station);
        Attendant savedAttendant = attendantRepository.save(attendant);

        return attendantMapper.toResponse(savedAttendant);
    }

    public AttendantResponse getAttendantById(Long id) {
        Attendant attendant = attendantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendant not found"));

        return attendantMapper.toResponse(attendant);
    }

    @Transactional
    public AttendantResponse updateAttendant(Long id, CreateAttendantRequest request) {

        Attendant attendant =
                attendantRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Attendant not found"));

        Station station =
                stationRepository.findById(
                        request.getStationId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Station not found"));

        if (!attendant.getUsername().equals(request.getUsername())
                && attendantRepository.existsByUsername(
                        request.getUsername())) {

            throw new DuplicateResourceException(
                    "Username already exists");
        }

        attendant.setUsername(request.getUsername());
        attendant.setPin(request.getPin());
        attendant.setFullName(request.getFullName());
        attendant.setStation(station);

        return attendantMapper.toResponse(
                attendantRepository.save(attendant));
    }


    @Transactional
    public AttendantResponse deactivateAttendant(Long id) {

        Attendant attendant = attendantRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Attendant not found"));

        if (Boolean.FALSE.equals(attendant.getActive())) {
                throw new DuplicateResourceException(
                        "Attendant is already inactive"
                );
        }

        attendant.setActive(false);

        Attendant updated = attendantRepository.save(attendant);

        return attendantMapper.toResponse(updated);
   }


    @Transactional(readOnly = true)
    public List<AttendantResponse> getAllAttendants() {

        return attendantMapper.toResponseList(
                attendantRepository.findAllByOrderByFullNameAsc()
        );
    }


   @Transactional(readOnly = true)
   public List<AttendantResponse> getAttendantsByStation(Long stationId) {

        if (!stationRepository.existsById(stationId)) {
                throw new ResourceNotFoundException("Station not found");
        }

        return attendantMapper.toResponseList(
                attendantRepository.findByStation_IdOrderByFullNameAsc(stationId)
        );
    }


   @Transactional(readOnly = true)
   public List<AttendantResponse> getActiveAttendants() {

        return attendantMapper.toResponseList(
                attendantRepository.findByActiveTrueOrderByFullNameAsc()
        );
   }


   @Transactional
   public AttendantResponse activateAttendant(Long id) {

        Attendant attendant = attendantRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Attendant not found"));

        if (Boolean.TRUE.equals(attendant.getActive())) {
                throw new DuplicateResourceException(
                        "Attendant is already active"
                );
        }

        attendant.setActive(true);

        Attendant updated = attendantRepository.save(attendant);

        return attendantMapper.toResponse(updated);
   }
}

