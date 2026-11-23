package kigali.clinc.clinicmanagement.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    // Quiz 1 leftovers still useful
    List<Appointment> findByStatusOrderByAppointmentDateAsc(AppointmentStatus status);

    List<Appointment> findByAppointmentDateBetweenOrderByAppointmentDateAsc(
            LocalDate start, LocalDate end);

    boolean existsByDoctor_IdAndAppointmentDateAndStatusNot(
            UUID doctorId, LocalDate appointmentDate, AppointmentStatus status);

    @Query("SELECT a.status, COUNT(a) FROM Appointment a GROUP BY a.status")
    List<Object[]> countAppointmentsByStatus();

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Appointment a SET a.status = :cancelled "
            + "WHERE a.doctor.id = :doctorId AND a.appointmentDate = :date "
            + "AND a.status <> :completed")
    int cancelAppointmentsForDoctorOnDate(
            @Param("doctorId") UUID doctorId,
            @Param("date") LocalDate date,
            @Param("cancelled") AppointmentStatus cancelled,
            @Param("completed") AppointmentStatus completed);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Appointment a WHERE a.status = :status AND a.appointmentDate < :date")
    int deleteByStatusAndAppointmentDateBefore(
            @Param("status") AppointmentStatus status,
            @Param("date") LocalDate date);

    Page<Appointment> findByDoctor_IdOrderByAppointmentDateAsc(UUID doctorId, Pageable pageable);

    // Quiz 2 D1: agenda for a day, exclude CANCELLED
    List<Appointment> findByDoctor_IdAndAppointmentDateAndStatusNotOrderByAppointmentDateAsc(
            UUID doctorId, LocalDate date, AppointmentStatus status);

    // Quiz 2 D2: upcoming for patient
    List<Appointment> findByPatient_SsNumberAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAsc(
            String ssNumber, LocalDate date, List<AppointmentStatus> statuses);

    long countByAppointmentDate(LocalDate date);

    long countByStatus(AppointmentStatus status);

    // Bonus workload
    @Query("SELECT d.firstName, d.lastName, COUNT(a) FROM Appointment a JOIN a.doctor d "
            + "WHERE a.status = :status AND a.appointmentDate BETWEEN :start AND :end "
            + "GROUP BY d.id, d.firstName, d.lastName "
            + "ORDER BY COUNT(a) DESC")
    List<Object[]> doctorWorkload(
            @Param("status") AppointmentStatus status,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);
}
