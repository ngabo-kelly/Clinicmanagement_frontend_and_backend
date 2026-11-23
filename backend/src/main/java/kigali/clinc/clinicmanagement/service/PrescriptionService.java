package kigali.clinc.clinicmanagement.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.domain.Prescription;
import kigali.clinc.clinicmanagement.repository.AppointmentRepository;
import kigali.clinc.clinicmanagement.repository.PrescriptionRepository;

@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    // C1
    public String savePrescription(UUID appointmentId, Prescription prescription) {
        Optional<Appointment> appointmentOpt = appointmentRepo.findById(appointmentId);
        if (appointmentOpt.isEmpty()) {
            return "The appointment with that id does not exist";
        }
        Appointment appointment = appointmentOpt.get();
        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            return "Prescriptions are only for COMPLETED appointments";
        }
        prescription.setAppointment(appointment);
        prescription.setIssuedDate(LocalDate.now());
        prescriptionRepo.save(prescription);
        return "Prescription saved successfully";
    }

    // C2
    public List<Prescription> findByAppointment(UUID appointmentId) {
        return prescriptionRepo.findByAppointment_Id(appointmentId);
    }

    public boolean appointmentExists(UUID appointmentId) {
        return appointmentRepo.existsById(appointmentId);
    }

    // C3
    public List<Prescription> findByPatientSsNumber(String ssNumber) {
        return prescriptionRepo.findByPatientSsNumber(ssNumber);
    }

    // C4 update
    public String updatePrescription(UUID id, Prescription body) {
        Optional<Prescription> existing = prescriptionRepo.findById(id);
        if (existing.isEmpty()) {
            return "The prescription with that id does not exist";
        }
        Prescription db = existing.get();
        db.setMedicineName(body.getMedicineName());
        db.setDosage(body.getDosage());
        db.setDurationDays(body.getDurationDays());
        prescriptionRepo.save(db);
        return "Prescription updated successfully";
    }

    // C4 delete
    public String deletePrescription(UUID id) {
        if (!prescriptionRepo.existsById(id)) {
            return "The prescription with that id does not exist";
        }
        prescriptionRepo.deleteById(id);
        return "Prescription deleted successfully";
    }

    public List<Object[]> topMedicines() {
        return prescriptionRepo.topMedicines();
    }

    public long count() {
        return prescriptionRepo.count();
    }
}
