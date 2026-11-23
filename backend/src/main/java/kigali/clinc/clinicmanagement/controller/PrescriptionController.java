package kigali.clinc.clinicmanagement.controller;

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

import kigali.clinc.clinicmanagement.domain.Prescription;
import kigali.clinc.clinicmanagement.service.PrescriptionService;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    // C1
    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> save(@RequestParam UUID appointmentId,
                                  @RequestBody Prescription prescription) {
        String msg = prescriptionService.savePrescription(appointmentId, prescription);
        if (msg.equalsIgnoreCase("Prescription saved successfully")) {
            return new ResponseEntity<>(msg, HttpStatus.CREATED);
        }
        if (msg.equalsIgnoreCase("The appointment with that id does not exist")) {
            return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
    }

    // C2
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<?> byAppointment(@PathVariable UUID appointmentId) {
        if (!prescriptionService.appointmentExists(appointmentId)) {
            return new ResponseEntity<>("The appointment with that id does not exist",
                    HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(prescriptionService.findByAppointment(appointmentId));
    }

    // C3
    @GetMapping("/patient/{ssNumber}")
    public ResponseEntity<?> byPatient(@PathVariable String ssNumber) {
        return ResponseEntity.ok(prescriptionService.findByPatientSsNumber(ssNumber));
    }

    // Bonus
    @GetMapping("/stats/top-medicines")
    public ResponseEntity<?> topMedicines() {
        return ResponseEntity.ok(prescriptionService.topMedicines());
    }

    // C4 update
    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody Prescription body) {
        String msg = prescriptionService.updatePrescription(id, body);
        if (msg.equalsIgnoreCase("Prescription updated successfully")) {
            return new ResponseEntity<>(msg, HttpStatus.OK);
        }
        return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }

    // C4 delete
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        String msg = prescriptionService.deletePrescription(id);
        if (msg.equalsIgnoreCase("Prescription deleted successfully")) {
            return new ResponseEntity<>(msg, HttpStatus.OK);
        }
        return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }
}
