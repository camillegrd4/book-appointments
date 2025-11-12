package com.maiia.pro.service;

import com.maiia.pro.EntityFactory;
import com.maiia.pro.dto.AppointmentRequestDto;
import com.maiia.pro.entity.Appointment;
import com.maiia.pro.exception.BadRequestException;
import com.maiia.pro.exception.NotFoundException;
import com.maiia.pro.repository.TimeSlotRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ProAppointmentServiceTest {

	@Autowired
	private ProAppointmentService proAppointmentService;
	@Autowired
	private TimeSlotRepository timeSlotRepository;
	private final EntityFactory entityFactory = new EntityFactory();

	@Test
	void it_should_throw_when_patient_does_not_exist() {
		AppointmentRequestDto appointment = AppointmentRequestDto.builder()
		                                                         .patientId(99)
		                                                         .practitionerId(1)
		                                                         .startDate(LocalDateTime.now())
		                                                         .endDate(LocalDateTime.now().plusHours(1))
		                                                         .build();

		assertThatThrownBy(() -> proAppointmentService.save(appointment))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Patient not found");
	}

	@Test
	void it_should_throw_when_practitioner_does_not_exist() {
		AppointmentRequestDto appointment = AppointmentRequestDto.builder()
		                                                         .patientId(1)
		                                                         .practitionerId(1)
		                                                         .startDate(LocalDateTime.now())
		                                                         .endDate(LocalDateTime.now().plusHours(1))
		                                                         .build();

		assertThatThrownBy(() -> proAppointmentService.save(appointment))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Practitioner not found");
	}

	@Test
	void it_should_throw_when_appointment_is_not_available() {
		AppointmentRequestDto appointment = AppointmentRequestDto.builder()
		                                                         .patientId(1)
		                                                         .practitionerId(13)
		                                                         .startDate(LocalDateTime.now().minusYears(1))
		                                                         .endDate(LocalDateTime.now().minusYears(1).plusHours(1))
		                                                         .build();
		assertThatThrownBy(() -> proAppointmentService.save(appointment))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Appointment not available");
	}

	@Test
	void it_should_throw_when_appointment_already_booked() throws NotFoundException, BadRequestException {
		LocalDateTime startDate = LocalDateTime.of(2020, Month.FEBRUARY, 5, 12, 0, 0);
		timeSlotRepository.save(entityFactory.createTimeSlot(13, startDate, startDate.plusHours(1)));

		AppointmentRequestDto requestAppointment = AppointmentRequestDto.builder()
		                                                                .patientId(1)
		                                                                .practitionerId(13)
		                                                                .startDate(startDate)
		                                                                .endDate(startDate.plusMinutes(15))
		                                                                .build();
		AppointmentRequestDto requestAppointment2 = AppointmentRequestDto.builder()
		                                                                 .patientId(6)
		                                                                 .practitionerId(13)
		                                                                 .startDate(startDate)
		                                                                 .endDate(startDate.plusMinutes(15))
		                                                                 .build();

		proAppointmentService.save(requestAppointment);

		assertThatThrownBy(() -> proAppointmentService.save(requestAppointment2))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Appointment not available");
	}

	@Test
	void it_should_save_availability() throws NotFoundException, BadRequestException {
		LocalDateTime startDate = LocalDateTime.of(2020, Month.FEBRUARY, 5, 11, 0, 0);
		timeSlotRepository.save(entityFactory.createTimeSlot(13, startDate, startDate.plusHours(1)));

		AppointmentRequestDto requestAppointment = AppointmentRequestDto.builder()
		                                                                .patientId(1)
		                                                                .practitionerId(13)
		                                                                .startDate(startDate)
		                                                                .endDate(startDate.plusMinutes(15))
		                                                                .build();

		Appointment appointment = proAppointmentService.save(requestAppointment);

		SoftAssertions.assertSoftly(soft -> {
			soft.assertThat(appointment).isNotNull();
			soft.assertThat(appointment.getPractitionerId()).isEqualTo(13);
			soft.assertThat(appointment.getStartDate()).isEqualTo(startDate);
			soft.assertThat(appointment.getEndDate()).isEqualTo(startDate.plusMinutes(15));
			soft.assertThat(appointment.getPatientId()).isEqualTo(1);
		});
	}

}
