package com.logbookmaster.repository;

import com.logbookmaster.model.LogbookEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogbookEntryRepository extends JpaRepository<LogbookEntry, Long> {
    List<LogbookEntry> findByUserIdOrderByCreatedAtDesc(Long userId);
}
