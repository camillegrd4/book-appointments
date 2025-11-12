package com.maiia.pro.service;

import com.maiia.pro.dto.AppointmentRequestDto;
import com.maiia.pro.entity.Appointment;
import com.maiia.pro.entity.Availability;
import com.maiia.pro.exception.BadRequestException;
import com.maiia.pro.exception.NotFoundException;
import com.maiia.pro.repository.AppointmentRepository;
import javassist.tools.web.BadHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

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

	public Appointment save(AppointmentRequestDto appointment) throws NotFoundException, BadRequestException {
		if (!patientService.exists(appointment.getPatientId())) {
			throw new NotFoundException("Patient not found");
		}
		if (!practitionerService.existsById(appointment.getPractitionerId())) {
			throw new NotFoundException("Practitioner not found");
		}
		List<Availability> availabilities = availabilityService.generateAvailabilities(appointment.getPractitionerId());

		boolean isAvailable = availabilities.stream()
		                                    .noneMatch(availability -> appointment.getStartDate().isBefore(availability.getEndDate())
				                                      && appointment.getEndDate().isAfter(availability.getStartDate()));
		if (!isAvailable) {
			throw new BadRequestException("Appointment not available");
		}
		Appointment appointment1 = Appointment.builder()
		                                      .practitionerId(appointment.getPractitionerId())
		                                      .patientId(appointment.getPatientId())
		                                      .startDate(appointment.getStartDate())
		                                      .endDate(appointment.getEndDate())
		                                      .build();
		return appointmentRepository.save(appointment1);
	}

}
