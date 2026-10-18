package com.tracker.toilet.repository;

import com.tracker.toilet.model.StaffMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffMemberRepository extends JpaRepository<StaffMember, Long> {

    Optional<StaffMember> findByStaffId(String staffId);

    List<StaffMember> findByAssignedWardIgnoreCase(String assignedWard);
}
