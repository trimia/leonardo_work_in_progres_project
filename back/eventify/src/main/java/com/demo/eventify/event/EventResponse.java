package com.demo.eventify.event;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventResponse {
	@JsonProperty("event_id")
	private Long id;
	@JsonProperty("title")
	private String title;
	@JsonProperty("date")
	private LocalDateTime datetime;//now
	@JsonProperty("description")
	private String description;//255
	@JsonProperty("place")
	private String place;
	private String error;
}
