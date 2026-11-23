package kigali.clinc.clinicmanagement.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import kigali.clinc.clinicmanagement.domain.Office;
import kigali.clinc.clinicmanagement.repository.OfficeRepository;

@Service
public class OfficeService {

    @Autowired
    private OfficeRepository officeRepo;

    public String saveOffice(Office office) {
        if (officeRepo.findByOfficeNumber(office.getOfficeNumber()) != null) {
            return "Office with that number " + office.getOfficeNumber() + " already exists";
        }
        officeRepo.save(office);
        return "Office is saved Successfully";
    }

    public List<Office> findAll() {
        return officeRepo.findAll();
    }

    public Optional<Office> findById(UUID id) {
        return officeRepo.findById(id);
    }

    public String updateOffice(UUID id, Office office) {
        Optional<Office> existing = officeRepo.findById(id);
        if (existing.isEmpty()) {
            return "Office not found";
        }
        Office dbOffice = existing.get();
        Office sameNumber = officeRepo.findByOfficeNumber(office.getOfficeNumber());
        if (sameNumber != null && !sameNumber.getId().equals(id)) {
            return "Office with that number " + office.getOfficeNumber() + " already exists";
        }
        dbOffice.setName(office.getName());
        dbOffice.setOfficeNumber(office.getOfficeNumber());
        officeRepo.save(dbOffice);
        return "Office is updated Successfully";
    }

    public String deleteOffice(UUID id) {
        if (!officeRepo.existsById(id)) {
            return "Office not found";
        }
        officeRepo.deleteById(id);
        return "Office is deleted Successfully";
    }

    // Quiz 2 A3
    public List<Office> findFreeOffices() {
        return officeRepo.findFreeOffices();
    }

    public Optional<Object[]> findBusiestOffice() {
        List<Object[]> rows = officeRepo.findBusiestOffice(PageRequest.of(0, 1));
        if (rows.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(rows.get(0));
    }

    public long countFreeOffices() {
        return officeRepo.findFreeOffices().size();
    }
}
