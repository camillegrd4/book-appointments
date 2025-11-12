package com.maiia.pro.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentDto {
	private Integer id;
	private Integer patientId;
	private Integer practitionerId;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
}
