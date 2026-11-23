package kigali.clinc.clinicmanagement.controller;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinc.clinicmanagement.domain.Doctor;
import kigali.clinc.clinicmanagement.service.AppointmentService;
import kigali.clinc.clinicmanagement.service.DoctorService;

@RestController
@RequestMapping(value = "/api/doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveDoctor(@RequestBody Doctor doctor) {
        String msg = doctorService.saveDoctor(doctor);
        if (msg.equalsIgnoreCase("Doctor is saved Successfully")) {
            return new ResponseEntity<>(msg, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<?> getAllDoctors() {
        return new ResponseEntity<>(doctorService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String name) {
        return ResponseEntity.ok(doctorService.searchByName(name));
    }

    @GetMapping("/workload")
    public ResponseEntity<?> workload(@RequestParam String start, @RequestParam String end) {
        return ResponseEntity.ok(appointmentService.workload(
                LocalDate.parse(start), LocalDate.parse(end)));
    }

    @GetMapping("/by-specialization")
    public ResponseEntity<?> bySpecialization(@RequestParam String name) {
        return ResponseEntity.ok(doctorService.findBySpecializationName(name));
    }

    @GetMapping("/without-office")
    public ResponseEntity<?> withoutOffice() {
        return ResponseEntity.ok(doctorService.findDoctorsWithoutOffice());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getDoctorById(@PathVariable UUID id) {
        Optional<Doctor> doctor = doctorService.findById(id);
        if (doctor.isEmpty()) {
            return new ResponseEntity<>("Doctor not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(doctor.get(), HttpStatus.OK);
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateDoctor(@PathVariable UUID id, @RequestBody Doctor doctor) {
        String msg = doctorService.updateDoctor(id, doctor);
        if (msg.equalsIgnoreCase("Doctor is updated Successfully")) {
            return new ResponseEntity<>(msg, HttpStatus.OK);
        }
        if (msg.equalsIgnoreCase("Doctor not found")) {
            return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteDoctor(@PathVariable UUID id) {
        String msg = doctorService.deleteDoctor(id);
        if (msg.equalsIgnoreCase("Doctor is deleted Successfully")) {
            return new ResponseEntity<>(msg, HttpStatus.OK);
        }
        return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }

    // Quiz 2 A1
    @PutMapping("/{doctorId}/specializations/{specializationId}")
    public ResponseEntity<?> addSpecialization(@PathVariable UUID doctorId,
                                               @PathVariable UUID specializationId) {
        String msg = doctorService.addSpecialization(doctorId, specializationId);
        if (msg.equalsIgnoreCase("Specialization added to the doctor")) {
            return new ResponseEntity<>(msg, HttpStatus.OK);
        }
        if (msg.equalsIgnoreCase("The doctor already has that specialization")) {
            return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }

    // Quiz 2 A2
    @DeleteMapping("/{doctorId}/specializations/{specializationId}")
    public ResponseEntity<?> removeSpecialization(@PathVariable UUID doctorId,
                                                  @PathVariable UUID specializationId) {
        String msg = doctorService.removeSpecialization(doctorId, specializationId);
        if (msg.equalsIgnoreCase("Specialization removed from the doctor")) {
            return new ResponseEntity<>(msg, HttpStatus.OK);
        }
        if (msg.equalsIgnoreCase("The doctor does not have that specialization")) {
            return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }

    // Quiz 2 A4
    @PatchMapping("/{doctorId}/office")
    public ResponseEntity<?> moveOffice(@PathVariable UUID doctorId,
                                        @RequestParam int officeNumber) {
        String msg = doctorService.moveOffice(doctorId, officeNumber);
        if (msg.equalsIgnoreCase("Office changed successfully")) {
            return new ResponseEntity<>(msg, HttpStatus.OK);
        }
        if (msg.equalsIgnoreCase("That office is taken by other doctor")) {
            return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }

    // Quiz 2 D1
    @GetMapping("/{doctorId}/agenda")
    public ResponseEntity<?> agenda(@PathVariable UUID doctorId, @RequestParam String date) {
        if (!doctorService.existsById(doctorId)) {
            return new ResponseEntity<>("The doctor with that id does not exist", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(appointmentService.agenda(doctorId, LocalDate.parse(date)));
    }
}
