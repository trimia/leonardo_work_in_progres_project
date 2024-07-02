package com.demo.eventify.tag;

import com.demo.eventify.event.EventEntity;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository

public interface TagRepository extends JpaRepository<TagEntity, Id> {

    TagEntity findTagEntityByTag(String tag);
//    List<TagEntity> findTagEntitiesByEvents(EventEntity event);
    Set<TagEntity> findTagEntitiesByEvents(EventEntity event);

    Set<TagEntity> findAllById(Long id);
}
