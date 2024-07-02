package com.demo.eventify.event;

import com.demo.eventify.authentication.AuthenticationRequest;
import com.demo.eventify.config.JwtService;
import com.demo.eventify.tag.TagEntity;
import com.demo.eventify.tag.TagEntity$;
import com.demo.eventify.user.UserRepository;
import com.speedment.jpastreamer.application.JPAStreamer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/event", produces = "application/json")
public class EventController {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventService eventService;
    private final EventMapper eventMapper;
    private final JPAStreamer jpaStreamer;
    private final JwtService jwtService;

    @Autowired
    public EventController(EventRepository eventRepository, UserRepository userRepository, EventService eventService, EventMapper eventMapper, JPAStreamer jpaStreamer, JwtService jwtService) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.eventService = eventService;
        this.eventMapper = eventMapper;
        this.jpaStreamer = jpaStreamer;
        this.jwtService = jwtService;
    }

    @GetMapping("events")
    public ResponseEntity<List<EventDto>> getEvent() {
        return new ResponseEntity<>(eventMapper.EventListToEventDto(eventRepository.findAll(Sort.by(Sort.Direction.ASC, "datetime"))), HttpStatus.OK);//sorted by date
    }

    @GetMapping("/AllPlace")
    public ResponseEntity<List<String>> getAllPlace() {
        List<EventEntity> events = jpaStreamer.stream(EventEntity.class).filter(EventEntity$.place.isNotNull()).toList();
        List<String> places = new ArrayList<String>();
        for (EventEntity event : events) {
            if (!places.contains(event.getPlace())) places.add(event.getPlace());
        }
        return new ResponseEntity<>(places, HttpStatus.OK);
    }
    @GetMapping("/myEvents")
    public ResponseEntity<Optional<List<EventDto>>> getCreatedEvent(@RequestHeader ("Authorization") String token){
        String jwt = token.substring(7);
        String email = jwtService.extractUsername(jwt);
        return new ResponseEntity<>(Optional.of(eventMapper.EventListToEventDto(eventRepository.findEventEntitiesByOwner(userRepository.findUserEntityByEmail(email)))),HttpStatus.OK);

    }
      @GetMapping("/AllTag")
    public ResponseEntity<List<String>> getAllTag() {
        List<TagEntity> tags = jpaStreamer.stream(TagEntity.class).filter(TagEntity$.tag.isNotNull()).toList();
        List<String> taglist = new ArrayList<String>();
        for (TagEntity tag : tags) {
            if (!taglist.contains(tag.getTag())) taglist.add(tag.getTag());
        }
        return new ResponseEntity<>(taglist, HttpStatus.OK);
    }
    @PostMapping("/myEvents")
    public ResponseEntity<Optional<List<EventDto>>> getCreatedEvent(@RequestBody EventRequest request){
        return new ResponseEntity<>(Optional.of(eventMapper.EventListToEventDto(eventRepository.findEventEntitiesByOwner(userRepository.findUserEntityByEmail(request.getEmail())))),HttpStatus.OK);
    }

    @PostMapping("/registerevents")
    public ResponseEntity<EventResponse> registerevent(@RequestBody EventRequest request,@RequestHeader("Authorization") String token ) {
       String jwt = token.substring(7);
        String email = jwtService.extractUsername(jwt);
//        System.out.println("line79////////////////////////////////////////////////////////////////////////////"+request.getImage()[1]);
        return new ResponseEntity<>(eventService.eventregister(request,email), HttpStatus.OK);
    }

    @PostMapping("/registerUserToEvent")
    public ResponseEntity<String> registeredToEvent(@RequestBody EventRequest request) {
        return new ResponseEntity<>(eventService.registerToEvent(request), HttpStatus.OK);
    }

    @PostMapping("/unregisterUserToEvent")
    public ResponseEntity<String> unregisteredToEvent(@RequestBody EventRequest request) {

        return new ResponseEntity<>(eventService.unregisterToEvent(request), HttpStatus.OK);
    }

    @PostMapping("/findByTitle")
    public ResponseEntity<Optional<List<EventEntity>>> getEventByTitle(@RequestBody EventRequest request) {
//        System.out.println("title//////////////////////   " + request.getTitle());
//        System.out.println("event//////////////////////   " + eventRepository.findByTitle(request.getTitle()));
        return new ResponseEntity<>(eventRepository.findByTitle(request.getTitle()), HttpStatus.OK);
    }
    @PostMapping("/findById")
    public ResponseEntity<Optional<EventDto>> getEventById(@RequestBody EventRequest request) {
//        System.out.println("title//////////////////////   " + request.getTitle());
//        System.out.println("event//////////////////////   " + eventRepository.findByTitle(request.getTitle()));
        return new ResponseEntity<>(Optional.of(eventMapper.EventToEventDto(eventRepository.findEventEntityById(request.getId()))), HttpStatus.OK);
    }

    @PostMapping("/findByUser")
    public ResponseEntity<Optional<List<EventEntity>>> getAllEventByUser(@RequestBody EventRequest request) {
//        System.out.println(request.getPlace());
//        System.out.println("event.findByUser//////////////////////   " + eventRepository.findEventEntityByOwner(userRepository.findUserEntityByEmail(request.getPlace())));
        return new ResponseEntity<>(eventRepository.findEventEntityByOwner(userRepository.findUserEntityByEmail(request.getPlace())), HttpStatus.OK);
    }

    @PostMapping("/findByPlace")
    public ResponseEntity<Optional<List<EventEntity>>> getAllEventByPlace(@RequestBody EventRequest request) {
//        System.out.println(request.getPlace());
//        System.out.println("place//////////////////////   " + eventRepository.findByPlace(request.getPlace()));
        return new ResponseEntity<>(eventRepository.findByPlace(request.getPlace()), HttpStatus.OK);
    }

    @PostMapping("/findByDateTime")
    public ResponseEntity<Optional<List<EventEntity>>> getAllEventByDateTime(@RequestBody EventSearchRequest request) {
//        System.out.println("datetime//////////////////////   " + eventRepository.findByDatetime(request.getFrom()));
        return new ResponseEntity<>(eventRepository.findByDatetime(request.getFrom()), HttpStatus.OK);
    }

    @PostMapping("/findEventSubscribed")
    public ResponseEntity<Optional<List<EventDto>>> getAllEventSubscribed(@RequestBody EventRequest request) {
        System.out.println(request.getEmail());
        return new ResponseEntity<>(Optional.of(eventMapper.EventListToEventDto(eventRepository.findAllBySubscribed(userRepository.findUserByEmail(request.getEmail())))), HttpStatus.OK);
    }
    @PostMapping("/upgradeEvent")
    public ResponseEntity<String> upgradeEvent(@RequestBody EventRequest request){
        return new ResponseEntity<>(eventService.updateEvent(request),HttpStatus.OK);
    }

    @DeleteMapping("/delete_event")
    public ResponseEntity<EventResponse> deleteEvent(@RequestBody AuthenticationRequest userRequest) {
        try {
            return new ResponseEntity<>(eventService.deleteEvent(userRequest), HttpStatus.OK);
        } catch (EventNotFoundException e) {
            return new ResponseEntity<>(EventResponse.builder().error("USER NOT FOUND").build(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(EventResponse.builder().error("500 INTERNAL SERVER ERROR").build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/search")
    public ResponseEntity<List<EventDto>> search(@RequestBody EventSearchRequest request) {
        return new ResponseEntity<>(eventMapper.EventListToEventDto(eventService.searchEvent(request)), HttpStatus.OK);
    }

    @GetMapping("/exp")
    public ResponseEntity<String>isExpired(){
        return  new ResponseEntity<>("good job",HttpStatus.OK);
    }
}
