package com.demo.eventify.event;

import com.demo.eventify.user.UserEntity;
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
public class EventRequest {
	private Long id;
	private String email;
	private UserEntity user;
	private String title;
	private String description;
	private String place;
	private String[]tags;
	private String[] image;
	private LocalDateTime datetime;
}
