package com.gasagency.controller;

import com.gasagency.model.CylinderPurchase;
import com.gasagency.model.ProductPurchase;
import com.gasagency.repository.CylinderPurchaseRepository;
import com.gasagency.repository.ProductPurchaseRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/purchase")
public class PurchaseController {

    @Autowired ProductPurchaseRepository productPurchaseRepo;
    @Autowired CylinderPurchaseRepository cylinderPurchaseRepo;

    // ==================== PRODUCT PURCHASE ====================

    @PostMapping("/product/add")
    public ResponseEntity<ProductPurchase> addProductPurchase(@RequestBody ProductPurchase p) {
        if (p.getPricePerUnit() > 0 && p.getQuantity() > 0) {
            p.setTotalAmount(p.getPricePerUnit() * p.getQuantity());
        }
        return ResponseEntity.ok(productPurchaseRepo.save(p));
    }

    @GetMapping("/product/all")
    public List<ProductPurchase> getAllProductPurchases() {
        return productPurchaseRepo.findAll();
    }

    @PutMapping("/product/update/{id}")
    public ResponseEntity<ProductPurchase> updateProductPurchase(
            @PathVariable int id, @RequestBody ProductPurchase updated) {
        return productPurchaseRepo.findById(id).map(p -> {
            p.setProductType(updated.getProductType());
            p.setQuantity(updated.getQuantity());
            p.setPurchaseDate(updated.getPurchaseDate());
            p.setPricePerUnit(updated.getPricePerUnit());
            p.setTotalAmount(updated.getPricePerUnit() * updated.getQuantity());
            return ResponseEntity.ok(productPurchaseRepo.save(p));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/product/delete/{id}")
    public ResponseEntity<String> deleteProductPurchase(@PathVariable int id) {
        productPurchaseRepo.deleteById(id);
        return ResponseEntity.ok("Deleted");
    }

    // ==================== CYLINDER PURCHASE ====================

    @PostMapping("/cylinder/add")
    public ResponseEntity<CylinderPurchase> addCylinderPurchase(@RequestBody CylinderPurchase c) {
        if (c.getPricePerUnit() > 0 && c.getQuantity() > 0) {
            c.setTotalAmount(c.getPricePerUnit() * c.getQuantity());
        }
        return ResponseEntity.ok(cylinderPurchaseRepo.save(c));
    }

    @GetMapping("/cylinder/all")
    public List<CylinderPurchase> getAllCylinderPurchases() {
        return cylinderPurchaseRepo.findAll();
    }

    @PutMapping("/cylinder/update/{id}")
    public ResponseEntity<CylinderPurchase> updateCylinderPurchase(
            @PathVariable int id, @RequestBody CylinderPurchase updated) {
        return cylinderPurchaseRepo.findById(id).map(c -> {
            c.setCylinderType(updated.getCylinderType());
            c.setQuantity(updated.getQuantity());
            c.setPurchaseDate(updated.getPurchaseDate());
            c.setPricePerUnit(updated.getPricePerUnit());
            c.setTotalAmount(updated.getPricePerUnit() * updated.getQuantity());
            return ResponseEntity.ok(cylinderPurchaseRepo.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/cylinder/delete/{id}")
    public ResponseEntity<String> deleteCylinderPurchase(@PathVariable int id) {
        cylinderPurchaseRepo.deleteById(id);
        return ResponseEntity.ok("Deleted");
    }
}