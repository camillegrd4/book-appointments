package com.maiia.pro.controller;

import com.maiia.pro.dto.AvailabilityDto;
import com.maiia.pro.entity.Availability;
import com.maiia.pro.exception.BadRequestException;
import com.maiia.pro.service.ProAvailabilityService;
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
@RequestMapping(value = "/availabilities", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProAvailabilityController {

	private final ProAvailabilityService proAvailabilityService;

	public ProAvailabilityController(ProAvailabilityService proAvailabilityService) {
		this.proAvailabilityService = proAvailabilityService;
	}

	@ApiOperation(value = "Get availabilities by practitionerId")
	@GetMapping
	public List<AvailabilityDto> getAvailabilities(@RequestParam final Integer practitionerId) {
		return proAvailabilityService.findByPractitionerId(practitionerId);
	}

	@ApiOperation(value = "POST availabilities by practitionerId")
	@PostMapping
	public Availability save(@RequestBody AvailabilityDto availabilityDto) throws BadRequestException {
		return proAvailabilityService.save(availabilityDto);
	}

}
