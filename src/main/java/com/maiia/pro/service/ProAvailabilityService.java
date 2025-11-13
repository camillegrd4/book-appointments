package com.maiia.pro.service;

import com.maiia.pro.dto.AvailabilityDto;
import com.maiia.pro.entity.Appointment;
import com.maiia.pro.entity.Availability;
import com.maiia.pro.entity.TimeSlot;
import com.maiia.pro.repository.AppointmentRepository;
import com.maiia.pro.repository.AvailabilityRepository;
import com.maiia.pro.repository.TimeSlotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProAvailabilityService {

	private final AvailabilityRepository availabilityRepository;
	private final AppointmentRepository appointmentRepository;
	private final TimeSlotRepository timeSlotRepository;

	private static final long APPOINTMENT_DURATION = 15;

	public ProAvailabilityService(AvailabilityRepository availabilityRepository, AppointmentRepository appointmentRepository, TimeSlotRepository timeSlotRepository) {
		this.availabilityRepository = availabilityRepository;
		this.appointmentRepository = appointmentRepository;
		this.timeSlotRepository = timeSlotRepository;
	}

	public List<AvailabilityDto> findByPractitionerId(Integer practitionerId) {
		return availabilityRepository.findByPractitionerId(practitionerId)
		                             .stream()
		                             .map(availability -> AvailabilityDto.builder()
		                                                                 .practitionerId(availability.getPractitionerId())
		                                                                 .endDate(availability.getEndDate())
		                                                                 .startDate(availability.getStartDate())
		                                                                 .build())
		                             .collect(Collectors.toList());
	}

	@Transactional
	public List<AvailabilityDto> generateAvailabilities(Integer practitionerId) {
		List<Appointment> appointments = appointmentRepository.findByPractitionerId(practitionerId);
		List<TimeSlot> timeSlots = timeSlotRepository.findByPractitionerId(practitionerId);
		List<AvailabilityDto> availabilities = new ArrayList<>();

		for (TimeSlot timeSlot : timeSlots) {
			LocalDateTime timeSlotStart = timeSlot.getStartDate();
			LocalDateTime timeSlotEnd = timeSlot.getEndDate();

			while (timeSlotStart.isBefore(timeSlotEnd)) {
				LocalDateTime end = timeSlotStart.plusMinutes(APPOINTMENT_DURATION);

				if (!isBooked(timeSlotStart, end, appointments)) {
					availabilities.add(new AvailabilityDto(practitionerId, timeSlotStart, end));
				}
				timeSlotStart = getNextSlotStart(appointments, timeSlotStart, end);
			}
		}
		return availabilities;
	}

	@Transactional
	public Availability save(AvailabilityDto availabilityDto) {
		Availability availability = Availability.builder()
		                                        .practitionerId(availabilityDto.getPractitionerId())
		                                        .startDate(availabilityDto.getStartDate())
		                                        .endDate(availabilityDto.getEndDate())
		                                        .build();
		return availabilityRepository.save(availability);
	}

	public boolean isAvailable(Integer practitionerId, LocalDateTime start, LocalDateTime end) {
		List<Availability> availabilities = availabilityRepository.findByPractitionerId(practitionerId);
		return availabilities.stream()
		                    .anyMatch(availability -> !start.isBefore(availability.getStartDate()) && !end.isAfter(availability.getEndDate()));
	}

	private static LocalDateTime getNextSlotStart(List<Appointment> appointments, LocalDateTime slotStart, LocalDateTime slotEnd) {
		return appointments.stream()
		                   .filter(app -> slotStart.isBefore(app.getEndDate()) && slotEnd.isAfter(app.getStartDate()))
		                   .map(Appointment::getEndDate)
		                   .max(LocalDateTime::compareTo)
		                   .orElse(slotEnd);
	}

	private boolean isBooked(LocalDateTime start, LocalDateTime end, List<Appointment> appointments) {
		return appointments.stream()
		                   .anyMatch(appointment -> start.isBefore(appointment.getEndDate()) && end.isAfter(appointment.getStartDate()));
	}

}
