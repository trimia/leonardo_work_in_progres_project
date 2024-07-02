package com.demo.eventify.event;

import com.demo.eventify.authentication.AuthenticationRequest;
import com.demo.eventify.tag.TagEntity;
import com.demo.eventify.tag.TagRepository;
import com.demo.eventify.user.UserEntity;
import com.demo.eventify.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Service
@RequiredArgsConstructor
public class EventService {
    @Autowired
    private final EventRepository repository;
    private final UserRepository userRepository;
    @Autowired
    private final TagRepository tagRepository;

    public Set<TagEntity> setTagsservice(String[] tags, EventRequest request) {
        Set<EventEntity> events = new HashSet<>();
        events.add(repository.findEventEntityById(request.getId()));
        Set<TagEntity> ret = new HashSet<>();
        Set<TagEntity> old = new HashSet<>(tagRepository.findAll());
        for (String t : tags) {
            if (old.contains(tagRepository.findTagEntityByTag(t))) {
                TagEntity tempp = tagRepository.findTagEntityByTag(t);
                tempp.setEvents(events);
                ret.add(tagRepository.save(tempp));
            } else {
                TagEntity temp = TagEntity.builder().tag(t).events(events).build();
                ret.add(tagRepository.save(temp));
            }
        }
        System.out.println("*********************tag service************************" + ret);
        return ret;
    }

    public EventResponse eventregister(EventRequest request, String email) {
//        System.out.println("getimage()//////////////////////////////"+Arrays.toString(request.getImage()));
        var event = EventEntity.builder().owner(userRepository.findUserByEmail(email)).image(request.getImage()).title(request.getTitle()).description(request.getDescription()).tags(setTagsservice(request.getTags(), request)).place(request.getPlace()).datetime(request.getDatetime()).build();
//        event.setImage(request.getImage());
        var savedevent = repository.save(event);

        return EventResponse.builder().id(savedevent.getId()).title(savedevent.getTitle()).datetime(savedevent.getDatetime()).description(savedevent.getDescription()).place(savedevent.getPlace()).build();
    }

    @Transactional ///// Method has to change a bit
    public EventResponse deleteEvent(AuthenticationRequest userRequest) throws EventNotFoundException {
        UserEntity user = userRepository.findUserByEmail(userRequest.getEmail());
        Optional<List<EventEntity>> eventsOptional = repository.findEventEntityByOwner(user); //eventRequest.getTitle()
        List<EventEntity> events = eventsOptional.orElse(null);
        if (eventsOptional.isEmpty()) {
            throw new EventNotFoundException("Event from user " + userRequest.getEmail() + " not found");
        }
        for (EventEntity event : events) {
            if (event.getOwner().getEmail().equals(user.getEmail())) {
//				System.out.println(event + "linea 67 ////////////////////////////////////////////////////////");
                repository.delete(event);
                return EventResponse.builder().build();
            }
        }
        throw new EventNotFoundException("Event from user " + userRequest.getEmail() + " not found");
    }

    public String registerToEvent(EventRequest request) {
//        System.out.println("***************" + request.getEmail() + " " + request.getId());
        EventEntity event = repository.findEventEntityById(request.getId());
        if (!event.getSubscribed().contains(userRepository.findUserByEmail(request.getEmail()))) {
            event.setSubscribed(userRepository.findUserByEmail(request.getEmail()));
            EventEntity pippo = repository.save(event);
//            System.out.println("*****************************" + pippo);
            return "successfully registered";
        } else return "alredy registered";
//        return "registration failed";
    }

    public String unregisterToEvent(EventRequest request) {
        EventEntity event = repository.findEventEntityById(request.getId());
        if (event.getSubscribed().contains(userRepository.findUserByEmail(request.getEmail()))) {
            List<UserEntity> temp = event.getSubscribed();
            temp.remove(userRepository.findUserByEmail(request.getEmail()));
//            System.out.println("///////////////////////////////////////////" + temp);
            event.setListSubscribed(temp);
            repository.save(event);
            return "successfully unregistered";
        }
        return "unregistration failed";
    }

    private Set<EventEntity> searchTitle(String title) {
        Set<EventEntity> ret = new HashSet<EventEntity>(repository.findAllByTitleContainsIgnoreCase(title));
        return ret.isEmpty() ? new HashSet<>(repository.findAll()) : ret;
    }


    private Set<EventEntity> searchFrom(LocalDateTime from, LocalDateTime to) {
        Set<EventEntity> after = new HashSet<>(repository.findAllByDatetimeAfter(from));
        Set<EventEntity> before = new HashSet<>();
        if (to != null) {
            before.addAll(repository.findAllByDatetimeBefore(to));
        }
        if (!before.isEmpty()) {
            Set<EventEntity> result = new HashSet<>(after);
            result.retainAll(before);
            return result;
        }
        return after.isEmpty() ? new HashSet<>(repository.findAll()) : after;
    }

    private Set<EventEntity> searchPlace(String[] places) {
        Set<EventEntity> ret = new HashSet<>();
        for (String place : places) {
            ret.addAll(repository.findAllByPlace(place));
        }
        return ret.isEmpty() ? new HashSet<>(repository.findAll()) : ret;
    }

    private Set<EventEntity> searchTag(String[] tags) {
        Set<EventEntity> ret = new HashSet<>();
        for (String tag : tags) {
            var tagFound = tagRepository.findTagEntityByTag(tag);
            ret.add(repository.findEventEntityByTags(tagFound));
        }
        return ret.isEmpty() ? new HashSet<>(repository.findAll()) : ret;
    }

    @Transactional
    public String updateEvent(EventRequest request) {
        EventEntity temp = repository.findEventEntityById(request.getId());
        if (!temp.getTitle().equals(request.getTitle())) {
            temp.setTitle(request.getTitle());
        }
        if (!temp.getDescription().equals(request.getDescription())) {
            temp.setDescription(request.getDescription());
        }
        if (!temp.getDatetime().equals(request.getDatetime())) {
            temp.setDatetime(request.getDatetime());
        }
        if (!temp.getPlace().equals(request.getPlace())) {
            temp.setPlace(request.getPlace());
        }
        if (!Arrays.equals(temp.getImage(), request.getImage())) {
            temp.setImage(request.getImage());
        }
//        System.out.println("***********************before********************************\n" + temp.getTitle() + temp.getTags());
        for (String t : request.getTags()) {
            Set<TagEntity> allTags = new HashSet<>(tagRepository.findAll());
            TagEntity tag = tagRepository.findTagEntityByTag(t);

            Set<TagEntity> oldTags = new HashSet<>(tagRepository.findTagEntitiesByEvents(temp));
            if (!allTags.contains(tagRepository.findTagEntityByTag(t))) {
                TagEntity riTag = new TagEntity();
                riTag.setTag(t);
                temp.addTag(riTag);
                for (TagEntity oldTag : oldTags) {
                    if (tag != oldTag) {
                        temp.removeTag(tag);
                    }
                }
            }
        }

//        System.out.println("*****************tag******************"+prova);

//        System.out.println("***********************after********************************\n" + temp.getTitle() + temp.getTags());
        repository.saveAndFlush(temp);
//        System.out.println("***********************post save********************************"+temp.getTitle()+temp.getTags());
        return "successfully updated";

    }

    public List<EventEntity> searchEvent(EventSearchRequest request) {
        List<Set<EventEntity>> arr = new ArrayList<>();
        arr.add(searchTitle(request.getTitle()));
        arr.add(searchFrom(request.getFrom(), request.getTo()));
        arr.add(searchPlace(request.getPlace()));
        arr.add(searchTag(request.getTags()));
        Set<EventEntity> allSet = new HashSet<>(repository.findAll());
        for (Set<EventEntity> t : arr) {
            allSet.retainAll(t);
        }
        return allSet.stream().toList();
    }
}
