package com.demo.eventify.tag;

import com.demo.eventify.event.EventEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@RequiredArgsConstructor
@Entity
@Table
public class TagEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tag;
    @ManyToMany(mappedBy = "tags")
//    @JsonIgnore
    private Set<EventEntity> events=new HashSet<>();
    public void addEvent(EventEntity event) {
        events.add(event);
        if (Hibernate.isInitialized(event.getTags())) {
            event.getTags().add(this);
        }
    }

    public void removeEvent(EventEntity event) {
        events.remove(event);
        if (Hibernate.isInitialized(event.getTags())) {
            event.getTags().remove(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        TagEntity tag = (TagEntity) o;
        return id != null && Objects.equals(id, tag.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "name = " + tag + ")";
    }
}
