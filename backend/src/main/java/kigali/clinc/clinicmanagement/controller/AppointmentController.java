package kigali.clinc.clinicmanagement.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.service.AppointmentService;

@RestController
@RequestMapping(value = "/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveAppointment(@RequestBody Appointment appointment) {
        String msg = appointmentService.saveAppointment(appointment);
        if (msg.equalsIgnoreCase("Appointment is saved Successfully")) {
            return new ResponseEntity<>(msg, HttpStatus.CREATED);
        }
        if (msg.equalsIgnoreCase("Appointment date cannot be in the past")) {
            return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
        }
        if (msg.equalsIgnoreCase("Doctor is already booked on that date")) {
            return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
        }
        if (msg.equalsIgnoreCase("Patient not found") || msg.equalsIgnoreCase("Doctor not found")) {
            return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<?> getAllAppointments() {
        return new ResponseEntity<>(appointmentService.findAll(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getAppointmentById(@PathVariable UUID id) {
        Optional<Appointment> appointment = appointmentService.findById(id);
        if (appointment.isEmpty()) {
            return new ResponseEntity<>("Appointment not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(appointment.get(), HttpStatus.OK);
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateAppointment(@PathVariable UUID id, @RequestBody Appointment appointment) {
        String msg = appointmentService.updateAppointment(id, appointment);
        if (msg.equalsIgnoreCase("Appointment is updated Successfully")) {
            return new ResponseEntity<>(msg, HttpStatus.OK);
        }
        return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteAppointment(@PathVariable UUID id) {
        String msg = appointmentService.deleteAppointment(id);
        if (msg.equalsIgnoreCase("Appointment is deleted Successfully")) {
            return new ResponseEntity<>(msg, HttpStatus.OK);
        }
        if (msg.equalsIgnoreCase("That appointment has prescriptions, delete them first")) {
            return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/by-status")
    public ResponseEntity<?> byStatus(@RequestParam AppointmentStatus status) {
        return ResponseEntity.ok(appointmentService.findByStatus(status));
    }

    @GetMapping("/between")
    public ResponseEntity<?> between(@RequestParam String start, @RequestParam String end) {
        return ResponseEntity.ok(appointmentService.findBetween(
                LocalDate.parse(start), LocalDate.parse(end)));
    }

    @GetMapping("/stats/by-status")
    public ResponseEntity<?> statsByStatus() {
        return ResponseEntity.ok(appointmentService.statsByStatus());
    }

    @PatchMapping("/cancel-day")
    public ResponseEntity<?> cancelDay(@RequestParam UUID doctorId, @RequestParam String date) {
        return ResponseEntity.ok(appointmentService.cancelDay(doctorId, LocalDate.parse(date)));
    }

    @GetMapping("/page")
    public ResponseEntity<?> page(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "appointmentDate,asc") String sort) {
        String[] parts = sort.split(",");
        Sort.Direction direction = parts.length > 1 && parts[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, parts[0]));
        return ResponseEntity.ok(appointmentService.findPage(pageable));
    }

    @DeleteMapping("/cancelled-before")
    public ResponseEntity<?> cancelledBefore(@RequestParam String date) {
        return ResponseEntity.ok(appointmentService.deleteCancelledBefore(LocalDate.parse(date)));
    }

    // Quiz 2 B2
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable UUID id, @RequestParam AppointmentStatus status) {
        String msg = appointmentService.updateStatus(id, status);
        if (msg.equalsIgnoreCase("Appointment status updated successfully")) {
            return new ResponseEntity<>(msg, HttpStatus.OK);
        }
        if (msg.equalsIgnoreCase("The appointment with that id does not exist")) {
            return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
    }

    // Quiz 2 B3
    @PatchMapping("/{id}/reschedule")
    public ResponseEntity<?> reschedule(@PathVariable UUID id, @RequestParam String newDate) {
        String msg = appointmentService.reschedule(id, LocalDate.parse(newDate));
        if (msg.equalsIgnoreCase("Appointment rescheduled successfully")) {
            return new ResponseEntity<>(msg, HttpStatus.OK);
        }
        if (msg.equalsIgnoreCase("The appointment with that id does not exist")) {
            return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        }
        if (msg.equalsIgnoreCase("Doctor is already booked on that date")) {
            return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
    }
}
