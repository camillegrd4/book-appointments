package com.maiia.pro.service;

import com.maiia.pro.entity.Appointment;
import com.maiia.pro.entity.Availability;
import com.maiia.pro.entity.TimeSlot;
import com.maiia.pro.repository.AppointmentRepository;
import com.maiia.pro.repository.AvailabilityRepository;
import com.maiia.pro.repository.TimeSlotRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

	public List<Availability> findByPractitionerId(Integer practitionerId) {
		return availabilityRepository.findByPractitionerId(practitionerId);
	}

	public List<Availability> generateAvailabilities(Integer practitionerId) {
		List<Appointment> appointments = appointmentRepository.findByPractitionerId(practitionerId);
		List<TimeSlot> timeSlots = timeSlotRepository.findByPractitionerId(practitionerId);
		List<Availability> availabilities = new ArrayList<>();

		for (TimeSlot timeSlot : timeSlots) {
			LocalDateTime timeSlotStart = timeSlot.getStartDate();
			LocalDateTime timeSlotEnd = timeSlot.getEndDate();

			while (timeSlotStart.isBefore(timeSlotEnd)) {
				LocalDateTime end = timeSlotStart.plusMinutes(APPOINTMENT_DURATION);

				if (!isBooked(timeSlotStart, end, appointments)) {
					availabilities.add(new Availability(practitionerId, timeSlotStart, end));
				}
				timeSlotStart = getNextSlotStart(appointments, timeSlotStart, end);
			}
		}
		return availabilities;
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
