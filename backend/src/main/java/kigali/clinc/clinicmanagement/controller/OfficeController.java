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
import org.springframework.web.bind.annotation.RestController;

import kigali.clinc.clinicmanagement.domain.Office;
import kigali.clinc.clinicmanagement.service.OfficeService;

@RestController
@RequestMapping(value = "/api/offices")
public class OfficeController {

    @Autowired
    private OfficeService officeServes;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveOffice(@RequestBody Office office) {
        String msg = officeServes.saveOffice(office);
        if (msg.equalsIgnoreCase("Office is saved Successfully")) {
            return new ResponseEntity<>(msg, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<?> getAllOffices() {
        return new ResponseEntity<>(officeServes.findAll(), HttpStatus.OK);
    }

    // Quiz 2 A3
    @GetMapping("/free")
    public ResponseEntity<?> freeOffices() {
        return ResponseEntity.ok(officeServes.findFreeOffices());
    }

    @GetMapping("/busiest")
    public ResponseEntity<?> busiest() {
        Optional<Object[]> row = officeServes.findBusiestOffice();
        if (row.isEmpty()) {
            return ResponseEntity.ok("No appointments yet");
        }
        return ResponseEntity.ok(row.get());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getOfficeById(@PathVariable UUID id) {
        Optional<Office> office = officeServes.findById(id);
        if (office.isEmpty()) {
            return new ResponseEntity<>("Office not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(office.get(), HttpStatus.OK);
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateOffice(@PathVariable UUID id, @RequestBody Office office) {
        String msg = officeServes.updateOffice(id, office);
        if (msg.equalsIgnoreCase("Office is updated Successfully")) {
            return new ResponseEntity<>(msg, HttpStatus.OK);
        }
        if (msg.equalsIgnoreCase("Office not found")) {
            return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteOffice(@PathVariable UUID id) {
        String msg = officeServes.deleteOffice(id);
        if (msg.equalsIgnoreCase("Office is deleted Successfully")) {
            return new ResponseEntity<>(msg, HttpStatus.OK);
        }
        return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }
}
