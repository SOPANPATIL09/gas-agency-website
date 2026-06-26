package com.gasagency.controller;

import com.gasagency.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired SalesRepository salesRepo;
    @Autowired CylinderSalesRepository cylinderSalesRepo;
    @Autowired ProductSalesRepository productSalesRepo;
    @Autowired MaintenanceRepository maintenanceRepo;
    @Autowired BankDepositRepository bankDepositRepo;
    @Autowired EmployeeRepository employeeRepo;
    @Autowired ProductPurchaseRepository productPurchaseRepo;
    @Autowired CylinderPurchaseRepository cylinderPurchaseRepo;

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        Map<String, Object> summary = new HashMap<>();

        // Cylinder Sales totals
        double cylSalesTotal = cylinderSalesRepo.findAll().stream()
                .mapToDouble(s -> s.getCylinderTotal()).sum();
        long cylSalesCount = cylinderSalesRepo.count();
        long cylSalesQty = cylinderSalesRepo.findAll().stream()
                .mapToLong(s -> s.getCylinderQty()).sum();

        // Product Sales totals
        double prodSalesTotal = productSalesRepo.findAll().stream()
                .mapToDouble(s -> s.getProductTotal()).sum();
        long prodSalesCount = productSalesRepo.count();

        // Legacy Sales (old combined table)
        double legacyCylTotal = salesRepo.findAll().stream()
                .mapToDouble(s -> s.getCylinderTotal()).sum();
        double legacyProdTotal = salesRepo.findAll().stream()
                .mapToDouble(s -> s.getProductTotal()).sum();

        // Maintenance
        double maintenanceTotal = maintenanceRepo.findAll().stream()
                .mapToDouble(m -> m.getFuel() + m.getOtherExpense() + m.getEmployeePayment()).sum();
        long maintenanceCount = maintenanceRepo.count();

        // Bank Deposits
        double bankDepositTotal = bankDepositRepo.findAll().stream()
                .mapToDouble(d -> d.getTotalDeposit()).sum();
        double bankOnlineTotal = bankDepositRepo.findAll().stream()
                .mapToDouble(d -> d.getOnlineDeposit()).sum();
        double bankCashTotal = bankDepositRepo.findAll().stream()
                .mapToDouble(d -> d.getCashDeposit()).sum();
        long bankDepositCount = bankDepositRepo.count();

        // Product Purchases
        double productPurchaseTotal = productPurchaseRepo.findAll().stream()
                .mapToDouble(p -> p.getTotalAmount()).sum();
        long productPurchaseCount = productPurchaseRepo.count();

        // Cylinder Purchases
        double cylinderPurchaseTotal = cylinderPurchaseRepo.findAll().stream()
                .mapToDouble(p -> p.getTotalAmount()).sum();
        long cylinderPurchaseCount = cylinderPurchaseRepo.count();

        // Employees
        long employeeCount = employeeRepo.count();

        summary.put("cylinderSalesTotal", cylSalesTotal + legacyCylTotal);
        summary.put("cylinderSalesCount", cylSalesCount);
        summary.put("cylinderSalesQty", cylSalesQty);
        summary.put("productSalesTotal", prodSalesTotal + legacyProdTotal);
        summary.put("productSalesCount", prodSalesCount);
        summary.put("maintenanceTotal", maintenanceTotal);
        summary.put("maintenanceCount", maintenanceCount);
        summary.put("bankDepositTotal", bankDepositTotal);
        summary.put("bankDepositOnline", bankOnlineTotal);
        summary.put("bankDepositCash", bankCashTotal);
        summary.put("bankDepositCount", bankDepositCount);
        summary.put("productPurchaseTotal", productPurchaseTotal);
        summary.put("productPurchaseCount", productPurchaseCount);
        summary.put("cylinderPurchaseTotal", cylinderPurchaseTotal);
        summary.put("cylinderPurchaseCount", cylinderPurchaseCount);
        summary.put("employeeCount", employeeCount);
        summary.put("totalSales", cylSalesTotal + prodSalesTotal + legacyCylTotal + legacyProdTotal);

        return ResponseEntity.ok(summary);
    }
}