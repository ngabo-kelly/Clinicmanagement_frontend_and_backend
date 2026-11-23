package kigali.clinc.clinicmanagement.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.domain.Doctor;
import kigali.clinc.clinicmanagement.domain.Patient;
import kigali.clinc.clinicmanagement.repository.AppointmentRepository;
import kigali.clinc.clinicmanagement.repository.DoctorRepository;
import kigali.clinc.clinicmanagement.repository.PatientRepository;
import kigali.clinc.clinicmanagement.repository.PrescriptionRepository;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepo;

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private PrescriptionRepository prescriptionRepo;

    // B1 + Quiz 1 double booking
    public String saveAppointment(Appointment appointment) {
        if (appointment.getAppointmentDate() != null
                && appointment.getAppointmentDate().isBefore(LocalDate.now())) {
            return "Appointment date cannot be in the past";
        }
        if (appointment.getPatient() == null || appointment.getPatient().getId() == null) {
            return "Patient is required";
        }
        if (appointment.getDoctor() == null || appointment.getDoctor().getId() == null) {
            return "Doctor is required";
        }
        Optional<Patient> patient = patientRepo.findById(appointment.getPatient().getId());
        if (patient.isEmpty()) {
            return "Patient not found";
        }
        Optional<Doctor> doctor = doctorRepo.findById(appointment.getDoctor().getId());
        if (doctor.isEmpty()) {
            return "Doctor not found";
        }
        boolean booked = appointmentRepo.existsByDoctor_IdAndAppointmentDateAndStatusNot(
                doctor.get().getId(),
                appointment.getAppointmentDate(),
                AppointmentStatus.CANCELLED);
        if (booked) {
            return "Doctor is already booked on that date";
        }
        appointment.setPatient(patient.get());
        appointment.setDoctor(doctor.get());
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointmentRepo.save(appointment);
        return "Appointment is saved Successfully";
    }

    public List<Appointment> findAll() {
        return appointmentRepo.findAll();
    }

    public Optional<Appointment> findById(UUID id) {
        return appointmentRepo.findById(id);
    }

    // B2: PUT must not change status
    public String updateAppointment(UUID id, Appointment appointment) {
        Optional<Appointment> existing = appointmentRepo.findById(id);
        if (existing.isEmpty()) {
            return "Appointment not found";
        }
        Appointment dbAppointment = existing.get();
        dbAppointment.setAppointmentDate(appointment.getAppointmentDate());
        dbAppointment.setReason(appointment.getReason());
        // status is NOT updated here

        if (appointment.getPatient() != null && appointment.getPatient().getId() != null) {
            Optional<Patient> patient = patientRepo.findById(appointment.getPatient().getId());
            if (patient.isEmpty()) {
                return "Patient not found";
            }
            dbAppointment.setPatient(patient.get());
        }
        if (appointment.getDoctor() != null && appointment.getDoctor().getId() != null) {
            Optional<Doctor> doctor = doctorRepo.findById(appointment.getDoctor().getId());
            if (doctor.isEmpty()) {
                return "Doctor not found";
            }
            dbAppointment.setDoctor(doctor.get());
        }
        appointmentRepo.save(dbAppointment);
        return "Appointment is updated Successfully";
    }

    // C4 protect delete
    public String deleteAppointment(UUID id) {
        if (!appointmentRepo.existsById(id)) {
            return "Appointment not found";
        }
        if (prescriptionRepo.existsByAppointment_Id(id)) {
            return "That appointment has prescriptions, delete them first";
        }
        appointmentRepo.deleteById(id);
        return "Appointment is deleted Successfully";
    }

    public List<Appointment> findByStatus(AppointmentStatus status) {
        return appointmentRepo.findByStatusOrderByAppointmentDateAsc(status);
    }

    public List<Appointment> findBetween(LocalDate start, LocalDate end) {
        return appointmentRepo.findByAppointmentDateBetweenOrderByAppointmentDateAsc(start, end);
    }

    public List<Object[]> statsByStatus() {
        return appointmentRepo.countAppointmentsByStatus();
    }

    @Transactional
    public String cancelDay(UUID doctorId, LocalDate date) {
        int updated = appointmentRepo.cancelAppointmentsForDoctorOnDate(
                doctorId, date, AppointmentStatus.CANCELLED, AppointmentStatus.COMPLETED);
        return updated + " appointments cancelled";
    }

    public Page<Appointment> findPage(Pageable pageable) {
        return appointmentRepo.findAll(pageable);
    }

    @Transactional
    public String deleteCancelledBefore(LocalDate date) {
        int deleted = appointmentRepo.deleteByStatusAndAppointmentDateBefore(
                AppointmentStatus.CANCELLED, date);
        return deleted + " appointments deleted";
    }

    // B2 status flow
    public String updateStatus(UUID id, AppointmentStatus newStatus) {
        Optional<Appointment> existing = appointmentRepo.findById(id);
        if (existing.isEmpty()) {
            return "The appointment with that id does not exist";
        }
        Appointment appointment = existing.get();
        AppointmentStatus current = appointment.getStatus();
        boolean allowed = false;
        if (current == AppointmentStatus.SCHEDULED) {
            allowed = newStatus == AppointmentStatus.CONFIRMED || newStatus == AppointmentStatus.CANCELLED;
        } else if (current == AppointmentStatus.CONFIRMED) {
            allowed = newStatus == AppointmentStatus.COMPLETED || newStatus == AppointmentStatus.CANCELLED;
        }
        if (!allowed) {
            return "Cannot change status from " + current + " to " + newStatus;
        }
        appointment.setStatus(newStatus);
        appointmentRepo.save(appointment);
        return "Appointment status updated successfully";
    }

    // B3 reschedule
    public String reschedule(UUID id, LocalDate newDate) {
        Optional<Appointment> existing = appointmentRepo.findById(id);
        if (existing.isEmpty()) {
            return "The appointment with that id does not exist";
        }
        Appointment appointment = existing.get();
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            return "A COMPLETED appointment cannot be rescheduled";
        }
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            return "A CANCELLED appointment cannot be rescheduled";
        }
        if (newDate.isBefore(LocalDate.now())) {
            return "Appointment date cannot be in the past";
        }
        if (appointmentRepo.existsByDoctor_IdAndAppointmentDateAndStatusNot(
                appointment.getDoctor().getId(), newDate, AppointmentStatus.CANCELLED)) {
            return "Doctor is already booked on that date";
        }
        appointment.setAppointmentDate(newDate);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointmentRepo.save(appointment);
        return "Appointment rescheduled successfully";
    }

    // D1
    public List<Appointment> agenda(UUID doctorId, LocalDate date) {
        return appointmentRepo.findByDoctor_IdAndAppointmentDateAndStatusNotOrderByAppointmentDateAsc(
                doctorId, date, AppointmentStatus.CANCELLED);
    }

    // D2
    public List<Appointment> upcomingForPatient(String ssNumber) {
        List<AppointmentStatus> statuses = Arrays.asList(
                AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED);
        return appointmentRepo
                .findByPatient_SsNumberAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAsc(
                        ssNumber, LocalDate.now(), statuses);
    }

    public List<Object[]> workload(LocalDate start, LocalDate end) {
        return appointmentRepo.doctorWorkload(AppointmentStatus.COMPLETED, start, end);
    }
}
