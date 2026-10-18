package com.tracker.toilet.repository;

import com.tracker.toilet.model.MaintenanceTicket;
import com.tracker.toilet.model.TicketPriority;
import com.tracker.toilet.model.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaintenanceTicketRepository extends JpaRepository<MaintenanceTicket, Long> {

    Optional<MaintenanceTicket> findByTicketNumber(String ticketNumber);

    List<MaintenanceTicket> findByFacilityId(Long facilityId);

    List<MaintenanceTicket> findByStatus(TicketStatus status);

    List<MaintenanceTicket> findByPriority(TicketPriority priority);

    List<MaintenanceTicket> findByFacilityWardIgnoreCase(String ward);

    List<MaintenanceTicket> findAllByOrderByCreatedAtDesc();
}
