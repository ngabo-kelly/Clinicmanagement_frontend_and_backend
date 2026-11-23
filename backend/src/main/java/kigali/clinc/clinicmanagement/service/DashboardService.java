package kigali.clinc.clinicmanagement.service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.repository.AppointmentRepository;
import kigali.clinc.clinicmanagement.repository.DoctorRepository;
import kigali.clinc.clinicmanagement.repository.PatientRepository;
import kigali.clinc.clinicmanagement.repository.PrescriptionRepository;

@Service
public class DashboardService {

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private OfficeService officeService;

    @Autowired
    private AppointmentRepository appointmentRepo;

    @Autowired
    private PrescriptionRepository prescriptionRepo;

    public Map<String, Long> summary() {
        Map<String, Long> map = new LinkedHashMap<>();
        map.put("patients", patientRepo.count());
        map.put("doctors", doctorRepo.count());
        map.put("freeOffices", officeService.countFreeOffices());
        map.put("appointmentsToday", appointmentRepo.countByAppointmentDate(LocalDate.now()));
        map.put("scheduled", appointmentRepo.countByStatus(AppointmentStatus.SCHEDULED));
        map.put("prescriptions", prescriptionRepo.count());
        return map;
    }
}
