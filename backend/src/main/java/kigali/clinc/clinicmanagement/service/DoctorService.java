package kigali.clinc.clinicmanagement.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinc.clinicmanagement.domain.Doctor;
import kigali.clinc.clinicmanagement.domain.Office;
import kigali.clinc.clinicmanagement.domain.Specialization;
import kigali.clinc.clinicmanagement.repository.DoctorRepository;
import kigali.clinc.clinicmanagement.repository.OfficeRepository;
import kigali.clinc.clinicmanagement.repository.SpecializationRepository;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private OfficeRepository officeRepo;

    @Autowired
    private SpecializationRepository specializationRepo;

    public String saveDoctor(Doctor doctor) {
        if (doctor.getOffice() != null && doctor.getOffice().getId() != null) {
            Optional<Office> office = officeRepo.findById(doctor.getOffice().getId());
            if (office.isEmpty()) {
                return "Office not found";
            }
            List<Doctor> existing = doctorRepo.findAll();
            for (Doctor d : existing) {
                if (d.getOffice() != null && d.getOffice().getId().equals(office.get().getId())) {
                    return "That office already has a doctor";
                }
            }
            doctor.setOffice(office.get());
        }
        doctorRepo.save(doctor);
        return "Doctor is saved Successfully";
    }

    public List<Doctor> findAll() {
        return doctorRepo.findAll();
    }

    public Optional<Doctor> findById(UUID id) {
        return doctorRepo.findById(id);
    }

    public String updateDoctor(UUID id, Doctor doctor) {
        Optional<Doctor> existing = doctorRepo.findById(id);
        if (existing.isEmpty()) {
            return "Doctor not found";
        }
        Doctor dbDoctor = existing.get();
        dbDoctor.setFirstName(doctor.getFirstName());
        dbDoctor.setLastName(doctor.getLastName());
        dbDoctor.setDateOfBirth(doctor.getDateOfBirth());
        if (doctor.getOffice() != null && doctor.getOffice().getId() != null) {
            Optional<Office> office = officeRepo.findById(doctor.getOffice().getId());
            if (office.isEmpty()) {
                return "Office not found";
            }
            dbDoctor.setOffice(office.get());
        }
        doctorRepo.save(dbDoctor);
        return "Doctor is updated Successfully";
    }

    @Transactional
    public String deleteDoctor(UUID id) {
        Optional<Doctor> existing = doctorRepo.findById(id);
        if (existing.isEmpty()) {
            return "Doctor not found";
        }
        Doctor doctor = existing.get();
        Office office = doctor.getOffice();
        if (office != null) {
            doctor.setOffice(null);
            office.setDoctor(null);
            officeRepo.save(office);
            doctorRepo.save(doctor);
        }
        doctor.getSpecializations().clear();
        doctorRepo.save(doctor);
        doctorRepo.delete(doctor);
        return "Doctor is deleted Successfully";
    }

    // Quiz 2 A1
    @Transactional
    public String addSpecialization(UUID doctorId, UUID specializationId) {
        Optional<Doctor> doctorOpt = doctorRepo.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            return "The doctor with that id does not exist";
        }
        Optional<Specialization> specOpt = specializationRepo.findById(specializationId);
        if (specOpt.isEmpty()) {
            return "That specialization does not exist";
        }
        Doctor doctor = doctorOpt.get();
        Specialization spec = specOpt.get();
        if (doctor.getSpecializations().contains(spec)) {
            return "The doctor already has that specialization";
        }
        doctor.getSpecializations().add(spec);
        doctorRepo.save(doctor);
        return "Specialization added to the doctor";
    }

    // Quiz 2 A2
    @Transactional
    public String removeSpecialization(UUID doctorId, UUID specializationId) {
        Optional<Doctor> doctorOpt = doctorRepo.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            return "The doctor with that id does not exist";
        }
        Optional<Specialization> specOpt = specializationRepo.findById(specializationId);
        if (specOpt.isEmpty()) {
            return "That specialization does not exist";
        }
        Doctor doctor = doctorOpt.get();
        Specialization spec = specOpt.get();
        if (!doctor.getSpecializations().contains(spec)) {
            return "The doctor does not have that specialization";
        }
        doctor.getSpecializations().remove(spec);
        doctorRepo.save(doctor);
        return "Specialization removed from the doctor";
    }

    // Quiz 2 A4
    @Transactional
    public String moveOffice(UUID doctorId, int officeNumber) {
        Optional<Doctor> doctorOpt = doctorRepo.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            return "The doctor with that id does not exist";
        }
        Office office = officeRepo.findByOfficeNumber(officeNumber);
        if (office == null) {
            return "The office with that office number does not exist";
        }
        Doctor doctor = doctorOpt.get();
        if (doctor.getOffice() == null || !doctor.getOffice().getId().equals(office.getId())) {
            if (doctorRepo.existsByOffice_OfficeNumber(officeNumber)) {
                return "That office is taken by other doctor";
            }
        }
        doctor.setOffice(office);
        doctorRepo.save(doctor);
        return "Office changed successfully";
    }

    public List<Doctor> findBySpecializationName(String name) {
        return doctorRepo.findBySpecializationName(name);
    }

    public List<Doctor> findDoctorsWithoutOffice() {
        return doctorRepo.findDoctorsWithoutOffice();
    }

    public List<Doctor> searchByName(String name) {
        return doctorRepo.searchByName(name);
    }

    public boolean existsById(UUID id) {
        return doctorRepo.existsById(id);
    }
}
