package kigali.clinc.clinicmanagement.controller;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinc.clinicmanagement.domain.Patient;
import kigali.clinc.clinicmanagement.service.AppointmentService;
import kigali.clinc.clinicmanagement.service.PatientService;

@RestController
@RequestMapping(value = "/api/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> savePatient(@RequestBody Patient patient) {
        String msg = patientService.savePatient(patient);
        if (msg.equalsIgnoreCase("Patient is saved Successfully")) {
            return new ResponseEntity<>(msg, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<?> getAllPatients() {
        return new ResponseEntity<>(patientService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/by-last-name")
    public ResponseEntity<?> byLastName(@RequestParam String lastName) {
        return ResponseEntity.ok(patientService.findByLastName(lastName));
    }

    @GetMapping("/frequent")
    public ResponseEntity<?> frequent(@RequestParam long min) {
        return ResponseEntity.ok(patientService.findFrequentPatients(min));
    }

    @GetMapping("/of-doctor/{doctorId}")
    public ResponseEntity<?> ofDoctor(@PathVariable UUID doctorId) {
        if (!patientService.doctorExists(doctorId)) {
            return new ResponseEntity<>("The doctor with that id does not exist", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(patientService.findPatientsOfDoctor(doctorId));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getPatientById(@PathVariable UUID id) {
        Optional<Patient> patient = patientService.findById(id);
        if (patient.isEmpty()) {
            return new ResponseEntity<>("Patient not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(patient.get(), HttpStatus.OK);
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updatePatient(@PathVariable UUID id, @RequestBody Patient patient) {
        String msg = patientService.updatePatient(id, patient);
        if (msg.equalsIgnoreCase("Patient is updated Successfully")) {
            return new ResponseEntity<>(msg, HttpStatus.OK);
        }
        return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deletePatient(@PathVariable UUID id) {
        String msg = patientService.deletePatient(id);
        if (msg.equalsIgnoreCase("Patient is deleted Successfully")) {
            return new ResponseEntity<>(msg, HttpStatus.OK);
        }
        return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }

    // Quiz 2 D2
    @GetMapping("/{ssNumber}/upcoming")
    public ResponseEntity<?> upcoming(@PathVariable String ssNumber) {
        if (!patientService.existsBySsNumber(ssNumber)) {
            return new ResponseEntity<>("That patient with that ssnumber does not exist",
                    HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(appointmentService.upcomingForPatient(ssNumber));
    }
}
