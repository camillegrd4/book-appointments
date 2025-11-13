package com.maiia.pro.controller;

import com.maiia.pro.dto.TimeSlotDto;
import com.maiia.pro.entity.TimeSlot;
import com.maiia.pro.exception.BadRequestException;
import com.maiia.pro.service.ProTimeSlotService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/timeSlots", produces = MediaType.APPLICATION_JSON_VALUE)
public class TimeSlotController {

	private final ProTimeSlotService proTimeSlotService;

	public TimeSlotController(ProTimeSlotService proTimeSlotService) {
		this.proTimeSlotService = proTimeSlotService;
	}

	@ApiOperation(value = "Save time slot")
	@PostMapping
	public TimeSlot save(@RequestBody final TimeSlotDto timeSlot) throws BadRequestException {
		return proTimeSlotService.save(timeSlot);
	}

	@ApiOperation(value = "List of time slots")
	@GetMapping
	public List<TimeSlot> getTimeSlots(@RequestParam final Integer practitionerId) {
		return proTimeSlotService.findByPractitionerId(practitionerId);
	}
}
