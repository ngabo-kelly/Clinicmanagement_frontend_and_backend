package kigali.clinc.clinicmanagement;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.domain.Doctor;
import kigali.clinc.clinicmanagement.domain.Office;
import kigali.clinc.clinicmanagement.domain.Patient;
import kigali.clinc.clinicmanagement.domain.Prescription;
import kigali.clinc.clinicmanagement.domain.Specialization;
import kigali.clinc.clinicmanagement.repository.AppointmentRepository;
import kigali.clinc.clinicmanagement.repository.DoctorRepository;
import kigali.clinc.clinicmanagement.repository.OfficeRepository;
import kigali.clinc.clinicmanagement.repository.PatientRepository;
import kigali.clinc.clinicmanagement.repository.PrescriptionRepository;
import kigali.clinc.clinicmanagement.repository.SpecializationRepository;

/**
 * Quiz 2 seed: 4 offices (104 free), 3 doctors with offices,
 * 5 patients with ssNumber, future appointments + completed ones.
 */
@Component
public class DataLoader implements CommandLineRunner {

    private final OfficeRepository officeRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final SpecializationRepository specializationRepository;
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;

    public DataLoader(OfficeRepository officeRepository,
                      DoctorRepository doctorRepository,
                      PatientRepository patientRepository,
                      SpecializationRepository specializationRepository,
                      AppointmentRepository appointmentRepository,
                      PrescriptionRepository prescriptionRepository) {
        this.officeRepository = officeRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.specializationRepository = specializationRepository;
        this.appointmentRepository = appointmentRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (officeRepository.count() > 0
                || doctorRepository.count() > 0
                || patientRepository.count() > 0
                || specializationRepository.count() > 0
                || appointmentRepository.count() > 0
                || prescriptionRepository.count() > 0) {
            return;
        }

        Specialization cardio = saveSpec("Cardiology");
        Specialization pediatrics = saveSpec("Pediatrics");
        Specialization derm = saveSpec("Dermatology");
        saveSpec("Neurology");

        Office o101 = saveOffice("Room 101", 101);
        Office o102 = saveOffice("Room 102", 102);
        Office o103 = saveOffice("Room 103", 103);
        saveOffice("Room 104", 104); // free

        Doctor alice = saveDoctor("Alice", "Mugisha", LocalDate.of(1990, 1, 15), o101);
        Doctor eric = saveDoctor("Eric", "Nkusi", LocalDate.of(1988, 6, 20), o102);
        Doctor claire = saveDoctor("Claire", "Uwase", LocalDate.of(1992, 3, 8), o103);

        alice.getSpecializations().add(cardio);
        alice.getSpecializations().add(derm);
        doctorRepository.save(alice);
        eric.getSpecializations().add(pediatrics); // only one specialization
        doctorRepository.save(eric);
        claire.getSpecializations().add(cardio);
        doctorRepository.save(claire);

        Patient main = savePatient("SS-1001", "Aline", "Uwase", LocalDate.of(1995, 4, 12));
        savePatient("SS-1002", "Claudine", "Uwase", LocalDate.of(1998, 8, 21));
        savePatient("SS-1003", "Paul", "Habimana", LocalDate.of(2000, 1, 15));
        savePatient("SS-1004", "Diane", "Mukamana", LocalDate.of(1997, 9, 3));
        Patient p5 = savePatient("SS-1005", "Kevin", "Niyonsenga", LocalDate.of(1999, 11, 11));

        // 3 future appointments for main patient
        Appointment a1 = saveAppt(LocalDate.of(2026, 10, 20), "Check-up", AppointmentStatus.SCHEDULED, main, alice);
        saveAppt(LocalDate.of(2026, 10, 22), "Follow-up", AppointmentStatus.CONFIRMED, main, alice);
        saveAppt(LocalDate.of(2026, 10, 30), "Review", AppointmentStatus.SCHEDULED, main, eric);

        // same doctor same day CANCELLED
        saveAppt(LocalDate.of(2026, 10, 20), "Cancelled slot", AppointmentStatus.CANCELLED, p5, alice);

        // at least 2 COMPLETED for prescriptions
        Appointment done1 = saveAppt(LocalDate.of(2026, 9, 10), "Past consult", AppointmentStatus.COMPLETED, main, alice);
        Appointment done2 = saveAppt(LocalDate.of(2026, 9, 15), "Past fever", AppointmentStatus.COMPLETED, p5, eric);

        Prescription pr1 = new Prescription();
        pr1.setMedicineName("Amoxicillin");
        pr1.setDosage("1 tablet, 3 times a day");
        pr1.setDurationDays(7);
        pr1.setIssuedDate(LocalDate.of(2026, 9, 10));
        pr1.setAppointment(done1);
        prescriptionRepository.save(pr1);

        Prescription pr2 = new Prescription();
        pr2.setMedicineName("Paracetamol");
        pr2.setDosage("1 tablet when needed");
        pr2.setDurationDays(3);
        pr2.setIssuedDate(LocalDate.of(2026, 9, 15));
        pr2.setAppointment(done2);
        prescriptionRepository.save(pr2);

        // silence unused warning for a1 if any
        if (a1.getId() == null) {
            return;
        }
    }

    private Specialization saveSpec(String name) {
        Specialization s = new Specialization();
        s.setName(name);
        return specializationRepository.save(s);
    }

    private Office saveOffice(String name, int number) {
        Office o = new Office();
        o.setName(name);
        o.setOfficeNumber(number);
        return officeRepository.save(o);
    }

    private Doctor saveDoctor(String first, String last, LocalDate dob, Office office) {
        Doctor d = new Doctor();
        d.setFirstName(first);
        d.setLastName(last);
        d.setDateOfBirth(dob);
        d.setOffice(office);
        return doctorRepository.save(d);
    }

    private Patient savePatient(String ss, String first, String last, LocalDate dob) {
        Patient p = new Patient();
        p.setSsNumber(ss);
        p.setFirstName(first);
        p.setLastName(last);
        p.setDateOfBirth(dob);
        return patientRepository.save(p);
    }

    private Appointment saveAppt(LocalDate date, String reason, AppointmentStatus status,
                                 Patient patient, Doctor doctor) {
        Appointment a = new Appointment();
        a.setAppointmentDate(date);
        a.setReason(reason);
        a.setStatus(status);
        a.setPatient(patient);
        a.setDoctor(doctor);
        return appointmentRepository.save(a);
    }
}
