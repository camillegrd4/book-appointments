package com.maiia.pro.service;

import com.maiia.pro.dto.AppointmentDto;
import com.maiia.pro.dto.AppointmentRequestDto;
import com.maiia.pro.dto.AvailabilityDto;
import com.maiia.pro.entity.Appointment;
import com.maiia.pro.entity.Availability;
import com.maiia.pro.exception.BadRequestException;
import com.maiia.pro.exception.NotFoundException;
import com.maiia.pro.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProAppointmentService {

	private final AppointmentRepository appointmentRepository;
	private final ProPatientService patientService;
	private final ProPractitionerService practitionerService;
	private final ProAvailabilityService availabilityService;

	public ProAppointmentService(AppointmentRepository appointmentRepository, ProPatientService patientService, ProPractitionerService practitionerService, ProAvailabilityService availabilityService) {
		this.appointmentRepository = appointmentRepository;
		this.patientService = patientService;
		this.practitionerService = practitionerService;
		this.availabilityService = availabilityService;
	}

	public Appointment find(String appointmentId) {
		return appointmentRepository.findById(appointmentId).orElseThrow();
	}

	public List<Appointment> findAll() {
		return appointmentRepository.findAll();
	}

	public List<Appointment> findByPractitionerId(Integer practitionerId) {
		return appointmentRepository.findByPractitionerId(practitionerId);
	}

	public Appointment save(AppointmentRequestDto appointmentRequestDto) throws NotFoundException, BadRequestException {
		checkEntityExists(appointmentRequestDto);
		List<AvailabilityDto> availabilities = availabilityService.generateAvailabilities(appointmentRequestDto.getPractitionerId());
		checkAvailability(appointmentRequestDto, availabilities);
		Appointment appointment = Appointment.builder()
		                                      .practitionerId(appointmentRequestDto.getPractitionerId())
		                                      .patientId(appointmentRequestDto.getPatientId())
		                                      .startDate(appointmentRequestDto.getStartDate())
		                                      .endDate(appointmentRequestDto.getEndDate())
		                                      .build();

		return appointmentRepository.save(appointment);
	}

	private void checkEntityExists(AppointmentRequestDto appointment) throws NotFoundException {
		if (!patientService.exists(appointment.getPatientId())) {
			throw new NotFoundException("Patient not found");
		}
		if (!practitionerService.existsById(appointment.getPractitionerId())) {
			throw new NotFoundException("Practitioner not found");
		}
	}

	private static void checkAvailability(AppointmentRequestDto appointment, List<AvailabilityDto> availabilities) throws BadRequestException {
		boolean isWithinAvailableSlot = availabilities.stream()
		                                              .anyMatch(availability ->
				                                                        (appointment.getStartDate().isAfter(availability.getStartDate())
						                                                        || appointment.getStartDate().isEqual(availability.getStartDate()))
						                                                        && (appointment.getEndDate().isBefore(availability.getEndDate())
						                                                        || appointment.getEndDate().isEqual(availability.getEndDate())));
		if (!isWithinAvailableSlot) {
			throw new BadRequestException("Appointment not available");
		}
	}

}
