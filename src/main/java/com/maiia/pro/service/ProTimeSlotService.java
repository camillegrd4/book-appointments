package com.maiia.pro.service;

import com.maiia.pro.dto.TimeSlotDto;
import com.maiia.pro.entity.TimeSlot;
import com.maiia.pro.exception.BadRequestException;
import com.maiia.pro.repository.TimeSlotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProTimeSlotService {

	private final TimeSlotRepository timeSlotRepository;
	private final ProAvailabilityService availabilityService;

	public ProTimeSlotService(TimeSlotRepository timeSlotRepository, ProAvailabilityService availabilityService) {
		this.timeSlotRepository = timeSlotRepository;
		this.availabilityService = availabilityService;
	}

	public List<TimeSlot> findByPractitionerId(Integer practitionerId) {
		return timeSlotRepository.findByPractitionerId(practitionerId);
	}

	@Transactional
	public TimeSlot save(TimeSlotDto timeSlot) throws BadRequestException {
		if (!availabilityService.isAvailable(timeSlot.getPractitionerId(), timeSlot.getStartDate(), timeSlot.getEndDate())) {
			throw new BadRequestException("Time slot overlaps with existing availabilities");
		}
		return timeSlotRepository.save(TimeSlot.builder()
		                                       .practitionerId(timeSlot.getPractitionerId())
		                                       .startDate(timeSlot.getStartDate())
		                                       .endDate(timeSlot.getEndDate())
		                                       .build());
	}

}
