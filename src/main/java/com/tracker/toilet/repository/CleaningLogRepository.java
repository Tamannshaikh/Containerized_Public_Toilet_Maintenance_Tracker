package com.tracker.toilet.repository;

import com.tracker.toilet.model.CleaningLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CleaningLogRepository extends JpaRepository<CleaningLog, Long> {

    List<CleaningLog> findByFacilityId(Long facilityId);
}
