package com.gasagency.controller;

import com.gasagency.model.CustomerDue;
import com.gasagency.repository.CustomerDueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/due")
public class CustomerDueController {

    @Autowired
    CustomerDueRepository dueRepo;

    @PostMapping("/add")
    public ResponseEntity<CustomerDue> addDue(@RequestBody CustomerDue due) {
        return ResponseEntity.ok(dueRepo.save(due));
    }

    @GetMapping("/all")
    public List<CustomerDue> getAllDues() {
        return dueRepo.findAll();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CustomerDue> updateDue(@PathVariable int id, @RequestBody CustomerDue updated) {
        return dueRepo.findById(id).map(d -> {
            d.setCustomerName(updated.getCustomerName());
            d.setDate(updated.getDate());
            d.setCylinderType(updated.getCylinderType());
            d.setTotalAmount(updated.getTotalAmount());
            d.setPaidAmount(updated.getPaidAmount());
            d.setRemarks(updated.getRemarks());
            return ResponseEntity.ok(dueRepo.save(d));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteDue(@PathVariable int id) {
        dueRepo.deleteById(id);
        return ResponseEntity.ok("Deleted");
    }
}