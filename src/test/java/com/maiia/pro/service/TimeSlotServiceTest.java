package com.maiia.pro.service;

import com.maiia.pro.EntityFactory;
import com.maiia.pro.dto.TimeSlotDto;
import com.maiia.pro.entity.TimeSlot;
import com.maiia.pro.exception.BadRequestException;
import com.maiia.pro.repository.AvailabilityRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class TimeSlotServiceTest {

	@Autowired
	private AvailabilityRepository availabilityRepository;
	@Autowired
	private ProTimeSlotService proTimeSlotService;
	private final EntityFactory entityFactory = new EntityFactory();

	@Test
	void it_should_throw_exception_when_time_slot_overlaps_with_existing_availabilities() {
		TimeSlotDto timeSlotDto = TimeSlotDto.builder().practitionerId(1)
		                                     .startDate(LocalDateTime.now())
		                                     .endDate(LocalDateTime.now().plusMinutes(15))
		                                     .build();

		assertThatThrownBy(() -> proTimeSlotService.save(timeSlotDto))
				.isInstanceOf(BadRequestException.class)
				.hasMessageContaining("Time slot overlaps with existing availabilities");
	}

	@Test
	void it_should_save_time_slot_when_no_overlap_with_existing_availabilities() throws BadRequestException {
		LocalDateTime startDate = LocalDateTime.of(2020, Month.FEBRUARY, 5, 12, 0, 0);
		availabilityRepository.save(entityFactory.createAvailability(1, startDate, startDate.plusHours(1)));
		TimeSlotDto timeSlotDto = TimeSlotDto.builder()
		                                     .practitionerId(1)
		                                     .startDate(startDate)
		                                     .endDate(startDate.plusMinutes(15))
		                                     .build();

		TimeSlot timeSlot = proTimeSlotService.save(timeSlotDto);

		SoftAssertions.assertSoftly(soft -> {
			soft.assertThat(timeSlot).isNotNull();
			soft.assertThat(timeSlot.getPractitionerId()).isEqualTo(1);
			soft.assertThat(timeSlot.getStartDate()).isEqualTo(startDate);
			soft.assertThat(timeSlot.getEndDate()).isEqualTo(startDate.plusMinutes(15));
		});
	}

}
