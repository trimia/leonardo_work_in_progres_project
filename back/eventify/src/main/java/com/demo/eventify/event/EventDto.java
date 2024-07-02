package com.demo.eventify.event;

import com.demo.eventify.tag.TagEntity;
import com.demo.eventify.user.UserDto;
import com.demo.eventify.user.UserEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
//@NoArgsConstructor
//@AllArgsConstructor
public class EventDto {
	@JsonFormat
	private Long id;
	private String title;
	private String owner;
	private String description;
	private String place;
	private String[] image;
	private LocalDateTime datetime;
	private List<String> tags;

//	public void setOwner(UserDto owner) {
//		this.owner = owner;
//	}
}
