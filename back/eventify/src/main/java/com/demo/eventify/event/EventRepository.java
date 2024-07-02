package com.demo.eventify.event;
import com.demo.eventify.tag.TagEntity;
import com.demo.eventify.user.UserEntity;
import jakarta.persistence.Id;

import lombok.NonNull;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.type.descriptor.converter.spi.JpaAttributeConverter;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Id> ,JpaSpecificationExecutor<EventEntity> {

    Optional<List<EventEntity>> findByTitle(String title);
    Optional<List<EventEntity>> findByDatetime(@NonNull LocalDateTime datetime);
    Optional<List<EventEntity>> findByPlace(String place);
    Optional<List<EventEntity>> findEventEntityByOwner(UserEntity user);
    Optional<List<EventEntity>> findEventEntitiesBySubscribed(UserEntity user);
    List<EventEntity> findEventEntitiesByOwner(UserEntity owner);
    List<EventEntity> findAllBySubscribed(UserEntity user);
    Set<EventEntity> findEventEntitiesByTags(TagEntity tag);
    EventEntity findEventEntityById(Long id);
    List<EventEntity> findAllByTitleContainsIgnoreCase(String title);
    List<EventEntity> findAllByDatetimeAfter(LocalDateTime after);
    List<EventEntity> findAllByDatetimeBefore(LocalDateTime before);
    List<EventEntity> findAllByPlace(String place);
    EventEntity findEventEntityByTags(TagEntity tag);
//    Set<EventEntity> findAllByTags(Set<TagEntity> tags);
}
