package com.maiia.pro.controller;

import com.maiia.pro.dto.AppointmentDto;
import com.maiia.pro.dto.AppointmentRequestDto;
import com.maiia.pro.entity.Appointment;
import com.maiia.pro.exception.BadRequestException;
import com.maiia.pro.exception.NotFoundException;
import com.maiia.pro.service.ProAppointmentService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/appointments", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProAppointmentController {

	private final ProAppointmentService proAppointmentService;

	public ProAppointmentController(ProAppointmentService proAppointmentService) {
		this.proAppointmentService = proAppointmentService;
	}

	@ApiOperation(value = "Get appointments by practitionerId")
	@GetMapping("/{practitionerId}")
	public List<Appointment> getAppointmentsByPractitioner(@PathVariable final Integer practitionerId) {
		return proAppointmentService.findByPractitionerId(practitionerId);
	}

	@ApiOperation(value = "Get all appointments")
	@GetMapping
	public List<Appointment> getAppointments() {
		return proAppointmentService.findAll();
	}

	@ApiOperation(value = "Save appointments")
	@PostMapping
	public AppointmentDto save(@RequestBody final AppointmentRequestDto appointment) throws NotFoundException, BadRequestException {
		Appointment app = proAppointmentService.save(appointment);

		return AppointmentDto.builder()
		                     .patientId(app.getPatientId())
		                     .practitionerId(app.getPractitionerId())
		                     .startDate(app.getStartDate())
		                     .endDate(app.getEndDate())
		                     .build();
	}

}
