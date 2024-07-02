package com.demo.eventify.event;

import com.demo.eventify.tag.TagEntity;
import com.demo.eventify.tag.TagRepository;
import com.demo.eventify.user.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcType;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
//@Setter
@Entity
@Table
public class EventEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String place;
    private LocalDateTime datetime;
    //@Column(length
// = )
//    @JsonIgnore
    @Column(columnDefinition = "text[]", nullable = false)
    private String[] image;

    //    img
    @ManyToOne
    @JoinColumn(name = "owner_id")
    @JsonIgnoreProperties("event")
    private UserEntity owner;

    @ManyToMany
    @JoinTable(name = "event_subscribed",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "subscribed_id"))
    private List<UserEntity> subscribed;

    @ManyToMany
    @JoinTable(name = "event_tags",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<TagEntity> tags=new LinkedHashSet<>();

    public void setSubscribed(UserEntity subscribed) {
        this.subscribed.add(subscribed);
    }

    public void setListSubscribed(List<UserEntity> subscribed) {
        this.subscribed = subscribed;
    }

    public List<String> toList(Set<TagEntity> tags) {
        List<String> ret = new ArrayList<>();
        for (TagEntity tag : tags) {
            ret.add(tag.getTag());
        }
        return ret;
    }

    public void addTag(@NonNull TagEntity tag) {
        tags.add(tag);
        if (Hibernate.isInitialized(tag.getEvents())) {
            tag.getEvents().add(this);
        }
    }

    public void removeTag(TagEntity tag) {
        tags.remove(tag);
        if (Hibernate.isInitialized(tag.getEvents())) {
            tag.getEvents().remove(this);
        }
    }

//
//    public void addTags(Set<TagEntity> tags,TagEntity tag) {
//
//        for (String sTag : sTags) {
//
//            tags
//
//
//        }
//
//    }
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    EventEntity event = (EventEntity) o;
    return id != null && Objects.equals(id, event.id);
}

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "title = " + title + ")";
    }
}
