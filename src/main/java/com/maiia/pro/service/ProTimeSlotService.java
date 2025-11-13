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

	public ProTimeSlotService(TimeSlotRepository timeSlotRepository) {
		this.timeSlotRepository = timeSlotRepository;
	}

	public List<TimeSlot> findByPractitionerId(Integer practitionerId) {
		return timeSlotRepository.findByPractitionerId(practitionerId);
	}

	public TimeSlot save(TimeSlotDto timeSlot) throws BadRequestException {
		if (timeSlotRepository.existsByPractitionerIdAndStartDateAndEndDate(timeSlot.getPractitionerId(), timeSlot.getStartDate(), timeSlot.getEndDate())) {
			throw new BadRequestException("Time slot already exists for the given practitioner and time range");
		}
		return timeSlotRepository.save(TimeSlot.builder()
		                                       .practitionerId(timeSlot.getPractitionerId())
		                                       .startDate(timeSlot.getStartDate())
		                                       .endDate(timeSlot.getEndDate())
		                                       .build());
	}

}
