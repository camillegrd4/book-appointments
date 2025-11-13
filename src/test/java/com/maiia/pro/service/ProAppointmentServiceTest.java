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
		AppointmentRequestDto appointment = getAppointmentRequestDto(99, 1, LocalDateTime.now(), LocalDateTime.now().plusHours(1));

		assertThatThrownBy(() -> proAppointmentService.save(appointment))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Patient not found");
	}

	@Test
	void it_should_throw_when_practitioner_does_not_exist() {
		AppointmentRequestDto appointment = getAppointmentRequestDto(1, 1, LocalDateTime.now(), LocalDateTime.now().plusHours(1));

		assertThatThrownBy(() -> proAppointmentService.save(appointment))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Practitioner not found");
	}

	@Test
	void it_should_throw_when_appointment_is_not_available() {
		AppointmentRequestDto appointment = getAppointmentRequestDto(1, 13, LocalDateTime.now(), LocalDateTime.now().plusHours(1));

		assertThatThrownBy(() -> proAppointmentService.save(appointment))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Appointment not available");
	}

	@Test
	void it_should_throw_when_appointment_already_booked() throws NotFoundException, BadRequestException {
		LocalDateTime startDate = LocalDateTime.of(2020, Month.FEBRUARY, 5, 12, 0, 0);
		timeSlotRepository.save(entityFactory.createTimeSlot(13, startDate, startDate.plusHours(1)));
		AppointmentRequestDto requestAppointment = getAppointmentRequestDto(1, 13, startDate, startDate.plusMinutes(15));
		AppointmentRequestDto requestAppointment2 = getAppointmentRequestDto(6, 13, startDate, startDate.plusMinutes(15));

		proAppointmentService.save(requestAppointment);

		assertThatThrownBy(() -> proAppointmentService.save(requestAppointment2))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Appointment not available");
	}

	@Test
	void it_should_save_availability() throws NotFoundException, BadRequestException {
		LocalDateTime startDate = LocalDateTime.of(2020, Month.FEBRUARY, 5, 11, 0, 0);
		timeSlotRepository.save(entityFactory.createTimeSlot(13, startDate, startDate.plusHours(1)));
		AppointmentRequestDto requestAppointment = getAppointmentRequestDto(1, 13, startDate, startDate.plusMinutes(15));

		Appointment appointment = proAppointmentService.save(requestAppointment);

		SoftAssertions.assertSoftly(soft -> {
			soft.assertThat(appointment).isNotNull();
			soft.assertThat(appointment.getPractitionerId()).isEqualTo(13);
			soft.assertThat(appointment.getStartDate()).isEqualTo(startDate);
			soft.assertThat(appointment.getEndDate()).isEqualTo(startDate.plusMinutes(15));
			soft.assertThat(appointment.getPatientId()).isEqualTo(1);
		});
	}

	private static AppointmentRequestDto getAppointmentRequestDto(Integer patientId, Integer practitionerId, LocalDateTime startDate, LocalDateTime endDate) {
		return AppointmentRequestDto.builder()
		                            .patientId(patientId)
		                            .practitionerId(practitionerId)
		                            .startDate(startDate)
		                            .endDate(endDate)
		                            .build();
	}
}
