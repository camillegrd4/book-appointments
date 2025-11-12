package com.maiia.pro.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityDto {
	private Integer practitionerId;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
}
