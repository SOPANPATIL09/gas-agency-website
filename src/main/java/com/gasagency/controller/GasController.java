package com.gasagency.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.itextpdf.layout.element.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.gasagency.model.Cylinder;
import com.gasagency.model.Employee;
import com.gasagency.model.Maintenance;
import com.gasagency.model.Sales;
import com.gasagency.repository.CylinderRepository;
import com.gasagency.repository.EmployeeRepository;
import com.gasagency.repository.MaintenanceRepository;
import com.gasagency.repository.SalesRepository;
import com.itextpdf.io.source.ByteArrayOutputStream;
//PDF Core
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;

//Document + Layout
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import jakarta.servlet.http.HttpSession;

import com.itextpdf.layout.element.Image;

//Image support
import com.itextpdf.io.image.ImageDataFactory;

//Spring Response
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
@RestController
@CrossOrigin(origins = "*")
public class GasController {

    @Autowired
    CylinderRepository cylinderRepo;

    @Autowired
    EmployeeRepository employeeRepo;

    @Autowired
    SalesRepository salesRepo;

    @Autowired
    MaintenanceRepository maintenanceRepo;

    // ================== LOGIN ==================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> user) {

        String username = user.get("username");
        String password = user.get("password");

        // ✅ Null check
        if(username == null || password == null){
            return ResponseEntity.badRequest().body("Missing fields");
        }

        // ✅ Trim (avoid space issues)
        username = username.trim();
        password = password.trim();

        // ✅ Check login
        if("admin".equals(username) && "Sudip@123".equals(password)){
            return ResponseEntity.ok("success");
        }

        return ResponseEntity.status(401).body("fail");
    }
    // ================== CYLINDER ==================
    @PostMapping("/addCylinder")
    public Cylinder addCylinder(@RequestBody Cylinder c) {
        return cylinderRepo.save(c);
    }

    @GetMapping("/getCylinder")
    public List<Cylinder> getCylinder() {
        return cylinderRepo.findAll();
    }

    // ================== EMPLOYEE ==================
    @PostMapping("/addEmployee")
    public Employee addEmployee(@RequestBody Employee e) {
        return employeeRepo.save(e);
    }

    @GetMapping("/getEmployee")
    public List<Employee> getEmployee() {
        return employeeRepo.findAll();
    }

    // ================== SALES ==================
    @PostMapping("/addSales")
    public Sales addSales(@RequestBody Sales s) {
        return salesRepo.save(s);
    }

    @GetMapping("/getSales")
    public List<Sales> getSales() {
        return salesRepo.findAll();
    }

    @DeleteMapping("/deleteSales/{id}")
    public String deleteSales(@PathVariable int id) {
        salesRepo.deleteById(id);
        return "Deleted";
    }

    // ================== MAINTENANCE ==================
    @PostMapping("/addMaintenance")
    public Maintenance addMaintenance(@RequestBody Maintenance m) {
        return maintenanceRepo.save(m);
    }

    @GetMapping("/getMaintenance")
    public List<Maintenance> getMaintenance() {
        return maintenanceRepo.findAll();
    }

    // ================== DATE-WISE TOTAL ==================
    @GetMapping("/total/{date}")
    public Map<String, Object> getTotal(@PathVariable String date) {

        double domesticPrice = 0, commercialPrice = 0, smallPrice = 0;

        for (Cylinder c : cylinderRepo.findAll()) {
            if (c.getType().equalsIgnoreCase("domestic")) {
				domesticPrice = c.getPrice();
			}
            if (c.getType().equalsIgnoreCase("commercial")) {
				commercialPrice = c.getPrice();
			}
            if (c.getType().equalsIgnoreCase("5kg")) {
				smallPrice = c.getPrice();
			}
        }

        int totalQty = 0;
        double totalAmount = 0;

        for (Sales s : salesRepo.findAll()) {
            if (s.getDate().equals(date)) {

                int d = s.getDomestic();
                int c = s.getCommercial();
                int sm = s.getSmall();

                totalQty += d + c + sm;

                totalAmount += (d * domesticPrice)
                             + (c * commercialPrice)
                             + (sm * smallPrice);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("quantity", totalQty);
        result.put("amount", totalAmount);

        return result;
    }

    // ================== MONTHLY SALES ==================
    @GetMapping("/monthlyAmount/{month}")
    public Map<String, Object> getMonthlyAmount(@PathVariable String month) {

        double domesticPrice = 0, commercialPrice = 0, smallPrice = 0;

        for (Cylinder c : cylinderRepo.findAll()) {
            if (c.getType().equalsIgnoreCase("domestic")) {
				domesticPrice = c.getPrice();
			}
            if (c.getType().equalsIgnoreCase("commercial")) {
				commercialPrice = c.getPrice();
			}
            if (c.getType().equalsIgnoreCase("5kg")) {
				smallPrice = c.getPrice();
			}
        }

        int totalQty = 0;
        double totalAmount = 0;

        for (Sales s : salesRepo.findAll()) {
            if (s.getDate().startsWith(month)) {

                totalQty += s.getDomestic() + s.getCommercial() + s.getSmall();

                totalAmount += (s.getDomestic() * domesticPrice)
                             + (s.getCommercial() * commercialPrice)
                             + (s.getSmall() * smallPrice);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("quantity", totalQty);
        result.put("amount", totalAmount);

        return result;
    }

    // ================== MAINTENANCE TOTAL ==================
    @GetMapping("/maintenance/total/{date}")
    public double getMaintenanceTotal(@PathVariable String date) {
        double total = 0;

        for (Maintenance m : maintenanceRepo.findAll()) {
            if (m.getDate().equals(date)) {
                total += m.getFuel() + m.getOtherExpense()
                       + m.getEmployeePayment() + m.getBankDeposit();
            }
        }
        return total;
    }

    @GetMapping("/maintenance/month/{month}")
    public double getMonthlyMaintenance(@PathVariable String month) {
        double total = 0;

        for (Maintenance m : maintenanceRepo.findAll()) {
            if (m.getDate().startsWith(month)) {
                total += m.getFuel() + m.getOtherExpense()
                       + m.getEmployeePayment() + m.getBankDeposit();
            }
        }
        return total;
    }

    // ================== TOTAL SALES ==================
    @GetMapping("/sales/total")
    public Map<String, Object> getSalesTotal() {

        double domesticPrice = 0, commercialPrice = 0, smallPrice = 0;

        for (Cylinder c : cylinderRepo.findAll()) {
            if (c.getType().equalsIgnoreCase("domestic")) {
				domesticPrice = c.getPrice();
			}
            if (c.getType().equalsIgnoreCase("commercial")) {
				commercialPrice = c.getPrice();
			}
            if (c.getType().equalsIgnoreCase("5kg")) {
				smallPrice = c.getPrice();
			}
        }

        int totalQty = 0;
        double totalAmount = 0;

        for (Sales s : salesRepo.findAll()) {

            int d = s.getDomestic();
            int c = s.getCommercial();
            int sm = s.getSmall();

            totalQty += d + c + sm;

            totalAmount += (d * domesticPrice)
                         + (c * commercialPrice)
                         + (sm * smallPrice);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("quantity", totalQty);
        result.put("amount", totalAmount);

        return result;
    }

    // ================== DAILY REPORT ==================
    @GetMapping("/report/daily/{date}")
    public ResponseEntity<String> dailyReport(@PathVariable String date) {

        StringBuilder sb = new StringBuilder();

        sb.append("Sudip HP Gas Agency\n");
        sb.append("Daily Report\n");
        sb.append("Date: ").append(date).append("\n\n");

        sb.append("Date,Domestic,Commercial,5kg,Total\n");

        double domesticPrice = 0, commercialPrice = 0, smallPrice = 0;

        for (Cylinder c : cylinderRepo.findAll()) {
            if (c.getType().equalsIgnoreCase("domestic")) {
				domesticPrice = c.getPrice();
			}
            if (c.getType().equalsIgnoreCase("commercial")) {
				commercialPrice = c.getPrice();
			}
            if (c.getType().equalsIgnoreCase("5kg")) {
				smallPrice = c.getPrice();
			}
        }

        int totalQty = 0;
        double totalAmount = 0;
        double totalMaintenance = 0;

        for (Sales s : salesRepo.findAll()) {
            if (s.getDate().equals(date)) {

                int total = s.getDomestic() + s.getCommercial() + s.getSmall();
                totalQty += total;

                totalAmount += (s.getDomestic() * domesticPrice)
                             + (s.getCommercial() * commercialPrice)
                             + (s.getSmall() * smallPrice);

                sb.append(s.getDate()).append(",")
                  .append(s.getDomestic()).append(",")
                  .append(s.getCommercial()).append(",")
                  .append(s.getSmall()).append(",")
                  .append(total).append("\n");
            }
        }

        for (Maintenance m : maintenanceRepo.findAll()) {
            if (m.getDate().equals(date)) {
                totalMaintenance += m.getFuel()
                                  + m.getOtherExpense()
                                  + m.getEmployeePayment()
                                  + m.getBankDeposit();
            }
        }

        sb.append("\n--------------------------------\n");
        sb.append("Total Quantity: ").append(totalQty).append("\n");
        sb.append("Total Sales Amount: Rs. ").append(totalAmount).append("\n");
        sb.append("Total Maintenance: Rs. ").append(totalMaintenance).append("\n");
        sb.append("Profit: Rs. ").append(totalAmount - totalMaintenance).append("\n");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=daily_report.csv")
                .body(sb.toString());
    }

    // ================== MONTHLY REPORT ==================
    @GetMapping("/report/month/{month}")
    public ResponseEntity<String> monthlyReport(@PathVariable String month) {

        StringBuilder sb = new StringBuilder();

        sb.append("Sudip HP Gas Agency\n");
        sb.append("Monthly Report\n");
        sb.append("Month: ").append(month).append("\n\n");

        double domesticPrice = 0, commercialPrice = 0, smallPrice = 0;

        for (Cylinder c : cylinderRepo.findAll()) {
            if (c.getType().equalsIgnoreCase("domestic")) {
				domesticPrice = c.getPrice();
			}
            if (c.getType().equalsIgnoreCase("commercial")) {
				commercialPrice = c.getPrice();
			}
            if (c.getType().equalsIgnoreCase("5kg")) {
				smallPrice = c.getPrice();
			}
        }

        int totalQty = 0;
        double totalAmount = 0;
        double totalMaintenance = 0;

        for (Sales s : salesRepo.findAll()) {
            if (s.getDate().startsWith(month)) {

                totalQty += s.getDomestic() + s.getCommercial() + s.getSmall();

                totalAmount += (s.getDomestic() * domesticPrice)
                             + (s.getCommercial() * commercialPrice)
                             + (s.getSmall() * smallPrice);
            }
        }

        for (Maintenance m : maintenanceRepo.findAll()) {
            if (m.getDate().startsWith(month)) {
                totalMaintenance += m.getFuel()
                                  + m.getOtherExpense()
                                  + m.getEmployeePayment()
                                  + m.getBankDeposit();
            }
        }

        sb.append("Total Quantity: ").append(totalQty).append("\n");
        sb.append("Total Sales Amount: Rs. ").append(totalAmount).append("\n");
        sb.append("Total Maintenance: Rs. ").append(totalMaintenance).append("\n");
        

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=monthly_report.csv")
                .body(sb.toString());
    }@GetMapping("/report/year/{year}")
    public ResponseEntity<String> yearlyReport(@PathVariable String year) {

        StringBuilder sb = new StringBuilder();

        sb.append("Sudip HP Gas Agency\n");
        sb.append("Yearly Report\n");
        sb.append("Year: ").append(year).append("\n\n");

        double domesticPrice = 0, commercialPrice = 0, smallPrice = 0;

        for (Cylinder c : cylinderRepo.findAll()) {
            if (c.getType().equalsIgnoreCase("domestic")) {
				domesticPrice = c.getPrice();
			}
            if (c.getType().equalsIgnoreCase("commercial")) {
				commercialPrice = c.getPrice();
			}
            if (c.getType().equalsIgnoreCase("5kg")) {
				smallPrice = c.getPrice();
			}
        }

        int totalQty = 0;
        double totalAmount = 0;
        double totalMaintenance = 0;

        for (Sales s : salesRepo.findAll()) {
            if (s.getDate().startsWith(year)) {

                totalQty += s.getDomestic() + s.getCommercial() + s.getSmall();

                totalAmount += (s.getDomestic() * domesticPrice)
                             + (s.getCommercial() * commercialPrice)
                             + (s.getSmall() * smallPrice);
            }
        }

        for (Maintenance m : maintenanceRepo.findAll()) {
            if (m.getDate().startsWith(year)) {
                totalMaintenance += m.getFuel()
                                  + m.getOtherExpense()
                                  + m.getEmployeePayment()
                                  + m.getBankDeposit();
            }
        }

        sb.append("Total Quantity: ").append(totalQty).append("\n");
        sb.append("Total Sales Amount: Rs. ").append(totalAmount).append("\n");
        sb.append("Total Maintenance: Rs. ").append(totalMaintenance).append("\n");
        

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=yearly_report.csv")
                .body(sb.toString());
    }
    @GetMapping("/report/pdf/{date}")
    public ResponseEntity<byte[]> generatePDF(@PathVariable String date) throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // 🔥 LOGO (PUT IMAGE IN: src/main/resources/static/images/logo.png)
        String logoPath = "src/main/resources/static/images/logo.png";
        Image logo = new Image(ImageDataFactory.create(logoPath)).scaleToFit(100, 100);
        document.add(logo);

        // TITLE
        document.add(new Paragraph("Sudip HP Gas Agency")
                .setBold().setFontSize(18));

        document.add(new Paragraph("Daily Report - " + date));
        document.add(new Paragraph(" "));

        // 🔥 GET PRICE MAP
        double domesticPrice = 0, commercialPrice = 0, smallPrice = 0;

        for (Cylinder c : cylinderRepo.findAll()) {
            if (c.getType().equalsIgnoreCase("domestic")) domesticPrice = c.getPrice();
            if (c.getType().equalsIgnoreCase("commercial")) commercialPrice = c.getPrice();
            if (c.getType().equalsIgnoreCase("5kg")) smallPrice = c.getPrice();
        }

        int domestic = 0, commercial = 0, small = 0;

        for (Sales s : salesRepo.findAll()) {
            if (s.getDate().equals(date)) {
                domestic += s.getDomestic();
                commercial += s.getCommercial();
                small += s.getSmall();
            }
        }

        double domesticAmount = domestic * domesticPrice;
        double commercialAmount = commercial * commercialPrice;
        double smallAmount = small * smallPrice;

        double totalAmount = domesticAmount + commercialAmount + smallAmount;

        // 🔥 SALES TABLE
        Table salesTable = new Table(4);
        salesTable.addCell("Type");
        salesTable.addCell("Qty");
        salesTable.addCell("Price");
        salesTable.addCell("Amount");

        salesTable.addCell("Domestic");
        salesTable.addCell(String.valueOf(domestic));
        salesTable.addCell(String.valueOf(domesticPrice));
        salesTable.addCell("₹ " + domesticAmount);

        salesTable.addCell("Commercial");
        salesTable.addCell(String.valueOf(commercial));
        salesTable.addCell(String.valueOf(commercialPrice));
        salesTable.addCell("₹ " + commercialAmount);

        salesTable.addCell("5kg");
        salesTable.addCell(String.valueOf(small));
        salesTable.addCell(String.valueOf(smallPrice));
        salesTable.addCell("₹ " + smallAmount);

        document.add(new Paragraph("Sales Summary"));
        document.add(salesTable);

        document.add(new Paragraph("Total Sales Amount: ₹ " + totalAmount));
        document.add(new Paragraph(" "));

        // 🔥 MAINTENANCE
        double fuel = 0, other = 0, emp = 0, bank = 0;

        for (Maintenance m : maintenanceRepo.findAll()) {
            if (m.getDate().equals(date)) {
                fuel += m.getFuel();
                other += m.getOtherExpense();
                emp += m.getEmployeePayment();
                bank += m.getBankDeposit();
            }
        }

        // 🔥 MAINTENANCE TABLE
        Table mTable = new Table(2);

        mTable.addCell("Type");
        mTable.addCell("Amount");

        mTable.addCell("Fuel");
        mTable.addCell("₹ " + fuel);

        mTable.addCell("Other Expense");
        mTable.addCell("₹ " + other);

        mTable.addCell("Employee Payment");
        mTable.addCell("₹ " + emp);

        mTable.addCell("Bank Deposit");
        mTable.addCell("₹ " + bank);

        document.add(new Paragraph("Maintenance Summary"));
        document.add(mTable);
        double totalMaintenance = fuel + other + emp + bank;
        document.add(new Paragraph("\nTotal Maintenance Amount: ₹ " + totalMaintenance));
        document.close();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=Gas_Report.pdf")
                .body(out.toByteArray());
    }@GetMapping("/report/pdf/month/{month}")
    public ResponseEntity<byte[]> monthlyPDF(@PathVariable String month) throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfDocument pdf = new PdfDocument(new PdfWriter(out));
        Document doc = new Document(pdf);

        // LOGO
        String logoPath = "src/main/resources/static/images/logo.png";
        Image logo = new Image(ImageDataFactory.create(logoPath)).scaleToFit(100, 100);
        doc.add(logo);

        doc.add(new Paragraph("Sudip HP Gas Agency").setBold().setFontSize(18));
        doc.add(new Paragraph("Monthly Report - " + month));
        doc.add(new Paragraph(" "));

        // GET PRICES
        double dp = 0, cp = 0, sp = 0;

        for (Cylinder c : cylinderRepo.findAll()) {
            if (c.getType().equalsIgnoreCase("domestic")) dp = c.getPrice();
            if (c.getType().equalsIgnoreCase("commercial")) cp = c.getPrice();
            if (c.getType().equalsIgnoreCase("5kg")) sp = c.getPrice();
        }

        int d = 0, c = 0, s = 0;

        for (Sales sale : salesRepo.findAll()) {
            if (sale.getDate().startsWith(month)) {
                d += sale.getDomestic();
                c += sale.getCommercial();
                s += sale.getSmall();
            }
        }

        double da = d * dp;
        double ca = c * cp;
        double sa = s * sp;
        double total = da + ca + sa;

        // SALES TABLE
        Table table = new Table(4);
        table.addCell("Type");
        table.addCell("Qty");
        table.addCell("Price");
        table.addCell("Amount");

        table.addCell("Domestic"); table.addCell(d+""); table.addCell(dp+""); table.addCell("₹ "+da);
        table.addCell("Commercial"); table.addCell(c+""); table.addCell(cp+""); table.addCell("₹ "+ca);
        table.addCell("5kg"); table.addCell(s+""); table.addCell(sp+""); table.addCell("₹ "+sa);

        doc.add(new Paragraph("Sales Summary"));
        doc.add(table);
        doc.add(new Paragraph("Total Sales Amount: ₹ " + total));
        doc.add(new Paragraph(" "));

        // MAINTENANCE
        double fuel=0, other=0, emp=0, bank=0;

        for (Maintenance m : maintenanceRepo.findAll()) {
            if (m.getDate().startsWith(month)) {
                fuel += m.getFuel();
                other += m.getOtherExpense();
                emp += m.getEmployeePayment();
                bank += m.getBankDeposit();
            }
        }

        Table mt = new Table(2);
        mt.addCell("Type"); mt.addCell("Amount");

        mt.addCell("Fuel"); mt.addCell("₹ "+fuel);
        mt.addCell("Other Expense"); mt.addCell("₹ "+other);
        mt.addCell("Employee Payment"); mt.addCell("₹ "+emp);
        mt.addCell("Bank Deposit"); mt.addCell("₹ "+bank);
     
        doc.add(new Paragraph("Maintenance Summary"));
        doc.add(mt);
        double totalMaintenance = fuel + other + emp+ bank;
        doc.add(new Paragraph("\nTotal Maintenance Amount: ₹ " + totalMaintenance));

        doc.close();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=monthly.pdf")
                .body(out.toByteArray());
    }@GetMapping("/report/pdf/year/{year}")
    public ResponseEntity<byte[]> yearlyPDF(@PathVariable String year) throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfDocument pdf = new PdfDocument(new PdfWriter(out));
        Document doc = new Document(pdf);

        // LOGO
        String logoPath = "src/main/resources/static/images/logo.png";
        Image logo = new Image(ImageDataFactory.create(logoPath)).scaleToFit(100, 100);
        doc.add(logo);

        doc.add(new Paragraph("Sudip HP Gas Agency").setBold().setFontSize(18));
        doc.add(new Paragraph("Yearly Report - " + year));
        doc.add(new Paragraph(" "));

        // 🔥 GET PRICES
        double dp=0, cp=0, sp=0;

        for (Cylinder c : cylinderRepo.findAll()) {
            if (c.getType().equalsIgnoreCase("domestic")) dp = c.getPrice();
            if (c.getType().equalsIgnoreCase("commercial")) cp = c.getPrice();
            if (c.getType().equalsIgnoreCase("5kg")) sp = c.getPrice();
        }

        int d=0, c=0, s=0;

        for (Sales sale : salesRepo.findAll()) {
            if (sale.getDate().startsWith(year)) {
                d += sale.getDomestic();
                c += sale.getCommercial();
                s += sale.getSmall();
            }
        }

        double da = d * dp;
        double ca = c * cp;
        double sa = s * sp;
        double total = da + ca + sa;

        // 🔥 SALES TABLE
        Table table = new Table(4);
        table.addCell("Type");
        table.addCell("Qty");
        table.addCell("Price");
        table.addCell("Amount");

        table.addCell("Domestic"); table.addCell(d+""); table.addCell(dp+""); table.addCell("₹ "+da);
        table.addCell("Commercial"); table.addCell(c+""); table.addCell(cp+""); table.addCell("₹ "+ca);
        table.addCell("5kg"); table.addCell(s+""); table.addCell(sp+""); table.addCell("₹ "+sa);

        doc.add(new Paragraph("Sales Summary"));
        doc.add(table);
        doc.add(new Paragraph("Total Sales Amount: ₹ " + total));
        doc.add(new Paragraph(" "));

        // 🔥 MAINTENANCE (ADDED NOW)
        double fuel=0, other=0, emp=0, bank=0;

        for (Maintenance m : maintenanceRepo.findAll()) {
            if (m.getDate().startsWith(year)) {
                fuel += m.getFuel();
                other += m.getOtherExpense();
                emp += m.getEmployeePayment();
                bank += m.getBankDeposit();
            }
        }

        // 🔥 MAINTENANCE TABLE
        Table mt = new Table(2);
        mt.addCell("Type");
        mt.addCell("Amount");

        mt.addCell("Fuel"); mt.addCell("₹ "+fuel);
        mt.addCell("Other Expense"); mt.addCell("₹ "+other);
        mt.addCell("Employee Payment"); mt.addCell("₹ "+emp);
        mt.addCell("Bank Deposit"); mt.addCell("₹ "+bank);

        doc.add(new Paragraph("Maintenance Summary"));
        doc.add(mt);
        double totalMaintenance = fuel + other + emp + bank;

        doc.add(new Paragraph("\nTotal Maintenance Amount: ₹ " + totalMaintenance));
        
        doc.close();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=yearly.pdf")
                .body(out.toByteArray());
    }
    @Transactional
    @DeleteMapping("/deleteEmployee/{name}")
    public ResponseEntity<String> delete(@PathVariable String name){

        employeeRepo.deleteByName(name);

        return ResponseEntity.ok("Deleted");
    }

    @PutMapping("/updatePrice")
    public void update(@RequestBody Cylinder c){

        Cylinder existing = cylinderRepo
            .findTopByTypeOrderByDateDesc(c.getType());

        existing.setPrice(c.getPrice());

        cylinderRepo.save(existing);
    }
    @PutMapping("/updateEmployee")
    public void updateEmployee(@RequestBody Employee emp){

        Employee existing = employeeRepo.findByName(emp.getName());

        existing.setSalary(emp.getSalary());

        employeeRepo.save(existing);
    }
    
    
    
}