package com.demo.eventify.event;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventSearchRequest {
    private String title;
    private String[] tags;
    private String[] place;
    private LocalDateTime to;
    private LocalDateTime from;
}