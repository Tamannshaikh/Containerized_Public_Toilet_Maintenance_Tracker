package com.tracker.toilet.repository;

import com.tracker.toilet.model.ToiletFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ToiletFacilityRepository extends JpaRepository<ToiletFacility, Long> {

    Optional<ToiletFacility> findByFacilityCode(String facilityCode);

    List<ToiletFacility> findByWardIgnoreCase(String ward);

    List<ToiletFacility> findByOperationalStatusIgnoreCase(String operationalStatus);
}
