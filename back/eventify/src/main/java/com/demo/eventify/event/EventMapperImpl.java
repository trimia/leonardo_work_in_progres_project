package com.demo.eventify.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.demo.eventify.tag.TagRepository;
import com.demo.eventify.user.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventMapperImpl implements EventMapper {
    private UserMapper userMapper;
    @Autowired
    private TagRepository tagRepository;

    @Override
    public EventEntity toEvent(EventDto eventDto) {
        if (eventDto == null) {
            return null;
        }

        EventEntity.EventEntityBuilder eventEntity = EventEntity.builder();

        eventEntity.id(eventDto.getId());
        eventEntity.title(eventDto.getTitle());
        eventEntity.description(eventDto.getDescription());
        eventEntity.place(eventDto.getPlace());
        eventEntity.datetime(eventDto.getDatetime());
//        eventEntity.tags(eventDto.getTags());

        return eventEntity.build();
    }

    @Override
    public EventDto EventToEventDto(EventEntity event) {
        if (event == null) {
            return null;
        }

        EventDto eventDto = new EventDto();

        eventDto.setId(event.getId());
        eventDto.setTitle(event.getTitle());
        eventDto.setOwner(event.getOwner().getEmail());
        eventDto.setImage(event.getImage());
        eventDto.setDescription(event.getDescription());
        eventDto.setPlace(event.getPlace());
        eventDto.setDatetime(event.getDatetime());
//        System.out.println("///////////////////////////////"+event.getTags());
        eventDto.setTags(event.toList(tagRepository.findTagEntitiesByEvents(event)));
        return eventDto;
    }

    @Override
    public List<EventEntity> toEventList(List<EventDto> eventDtoList) {
        if (eventDtoList == null) {
            return null;
        }

        List<EventEntity> list = new ArrayList<EventEntity>(eventDtoList.size());
        for (EventDto eventDto : eventDtoList) {
            list.add(toEvent(eventDto));
        }

        return list;
    }

    @Override
    public List<EventDto> EventListToEventDto(List<EventEntity> event) {
        if (event == null) {
            return null;
        }

        List<EventDto> list = new ArrayList<EventDto>(event.size());
        for (EventEntity eventEntity : event) {
            list.add(EventToEventDto(eventEntity));
        }

        return list;
    }
}
