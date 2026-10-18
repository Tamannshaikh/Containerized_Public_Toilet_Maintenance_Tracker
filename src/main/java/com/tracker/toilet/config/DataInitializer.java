package com.tracker.toilet.config;

import com.tracker.toilet.model.MaintenanceTicket;
import com.tracker.toilet.model.TicketPriority;
import com.tracker.toilet.model.TicketStatus;
import com.tracker.toilet.model.ToiletFacility;
import com.tracker.toilet.repository.MaintenanceTicketRepository;
import com.tracker.toilet.repository.ToiletFacilityRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ToiletFacilityRepository facilityRepository;
    private final MaintenanceTicketRepository ticketRepository;

    public DataInitializer(ToiletFacilityRepository facilityRepository, MaintenanceTicketRepository ticketRepository) {
        this.facilityRepository = facilityRepository;
        this.ticketRepository = ticketRepository;
    }

    @Override
    public void run(String... args) {
        if (facilityRepository.count() == 0) {
            ToiletFacility f1 = new ToiletFacility(
                    "FAC-MUM-001",
                    "Dadar Station West Public Convenience",
                    "G/North",
                    "Near Platform 1 Exit, Senapati Bapat Marg, Dadar West",
                    10
            );
            f1.setCleanlinessScore(88.5);
            f1.setOperationalStatus("OPERATIONAL");
            f1.setHasRunningWater(true);

            ToiletFacility f2 = new ToiletFacility(
                    "FAC-MUM-002",
                    "Bandra Bandstand Promenade Restroom",
                    "H/West",
                    "BJ Road, Bandra Bandstand, Bandra West",
                    6
            );
            f2.setCleanlinessScore(92.0);
            f2.setOperationalStatus("OPERATIONAL");
            f2.setHasRunningWater(true);

            ToiletFacility f3 = new ToiletFacility(
                    "FAC-MUM-003",
                    "Andheri East Metro Interchange Restroom",
                    "K/East",
                    "Near Metro Gate 2, Andheri-Kurla Road, Andheri East",
                    8
            );
            f3.setCleanlinessScore(74.0);
            f3.setOperationalStatus("MAINTENANCE_REQUIRED");
            f3.setHasRunningWater(true);

            facilityRepository.save(f1);
            facilityRepository.save(f2);
            facilityRepository.save(f3);

            MaintenanceTicket t1 = new MaintenanceTicket(
                    f1,
                    "Continuous flush leakage in Cabin 3",
                    "Flush valve stuck in open position, constant water flow observed.",
                    TicketPriority.MEDIUM,
                    "Citizen Suresh P."
            );
            t1.setTicketNumber("TKT-20261018-001");
            t1.setStatus(TicketStatus.OPEN);

            MaintenanceTicket t2 = new MaintenanceTicket(
                    f3,
                    "Exhaust ventilation fan malfunction",
                    "Ventilation motor vibrating heavily and stopped extracting fumes in gents section.",
                    TicketPriority.HIGH,
                    "Supervisor Anita Deshmukh"
            );
            t2.setTicketNumber("TKT-20261018-002");
            t2.setStatus(TicketStatus.IN_PROGRESS);
            t2.setAssignedStaff("Technician Rajesh Kumar");

            ticketRepository.save(t1);
            ticketRepository.save(t2);
        }
    }
}
