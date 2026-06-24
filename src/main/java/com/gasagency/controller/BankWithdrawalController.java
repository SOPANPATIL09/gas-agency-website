package com.gasagency.controller;

import com.gasagency.model.BankWithdrawal;
import com.gasagency.repository.BankWithdrawalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/bankwithdrawal")
public class BankWithdrawalController {

    @Autowired
    BankWithdrawalRepository bankWithdrawalRepo;

    @PostMapping("/add")
    public ResponseEntity<BankWithdrawal> addWithdrawal(@RequestBody BankWithdrawal w) {
        w.setTotalWithdrawal(w.getOnlineWithdrawal() + w.getCashWithdrawal());
        return ResponseEntity.ok(bankWithdrawalRepo.save(w));
    }

    @GetMapping("/all")
    public List<BankWithdrawal> getAllWithdrawals() {
        return bankWithdrawalRepo.findAll();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BankWithdrawal> updateWithdrawal(
            @PathVariable int id, @RequestBody BankWithdrawal updated) {
        return bankWithdrawalRepo.findById(id).map(w -> {
            w.setDate(updated.getDate());
            w.setOnlineWithdrawal(updated.getOnlineWithdrawal());
            w.setCashWithdrawal(updated.getCashWithdrawal());
            w.setTotalWithdrawal(updated.getOnlineWithdrawal() + updated.getCashWithdrawal());
            w.setRemarks(updated.getRemarks());
            return ResponseEntity.ok(bankWithdrawalRepo.save(w));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteWithdrawal(@PathVariable int id) {
        bankWithdrawalRepo.deleteById(id);
        return ResponseEntity.ok("Deleted");
    }

    @GetMapping("/total")
    public ResponseEntity<Map<String, Object>> getTotals() {
        List<BankWithdrawal> all = bankWithdrawalRepo.findAll();
        double totalOnline = all.stream().mapToDouble(BankWithdrawal::getOnlineWithdrawal).sum();
        double totalCash   = all.stream().mapToDouble(BankWithdrawal::getCashWithdrawal).sum();
        double grandTotal  = all.stream().mapToDouble(BankWithdrawal::getTotalWithdrawal).sum();
        Map<String, Object> result = new HashMap<>();
        result.put("totalOnline", totalOnline);
        result.put("totalCash",   totalCash);
        result.put("grandTotal",  grandTotal);
        result.put("count",       all.size());
        return ResponseEntity.ok(result);
    }
}