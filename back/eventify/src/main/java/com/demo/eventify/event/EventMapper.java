package com.demo.eventify.event;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventMapper {

    EventEntity toEvent(
            EventDto eventDto
    );

    EventDto EventToEventDto(
            EventEntity event
    );

    List<EventEntity> toEventList(
            List<EventDto> eventDtoList
    );

    List<EventDto> EventListToEventDto(
            List<EventEntity> event
    );
}