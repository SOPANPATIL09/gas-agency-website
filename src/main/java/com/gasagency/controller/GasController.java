package com.gasagency.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.gasagency.model.Cylinder;
import com.gasagency.model.Employee;
import com.gasagency.model.Maintenance;
import com.gasagency.model.Sales;
import com.gasagency.repository.CylinderRepository;
import com.gasagency.repository.EmployeeRepository;
import com.gasagency.repository.MaintenanceRepository;
import com.gasagency.repository.SalesRepository;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.BorderRadius;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.gasagency.model.Product;
import com.gasagency.model.NewConnection;
import com.gasagency.repository.ProductRepository;
import com.gasagency.repository.NewConnectionRepository;
@RestController
@CrossOrigin(origins = "*")
public class GasController {
	
	
	@Autowired ProductRepository productRepo;
	@Autowired NewConnectionRepository newConnectionRepo;
    @Autowired CylinderRepository    cylinderRepo;
    @Autowired EmployeeRepository    employeeRepo;
    @Autowired SalesRepository       salesRepo;
    @Autowired MaintenanceRepository maintenanceRepo;

    // ==================== LOGIN ====================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> user) {
        String username = user.get("username");
        String password = user.get("password");
        if (username == null || password == null)
            return ResponseEntity.badRequest().body("Missing fields");
        username = username.trim();
        password = password.trim();
        if ("admin".equals(username) && "Sudip@123".equals(password))
            return ResponseEntity.ok("success");
        return ResponseEntity.status(401).body("fail");
    }
 // ==================== PRODUCT ====================

    @PostMapping("/addProduct")
    public Product addProduct(@RequestBody Product p) {
        return productRepo.save(p);
    }

    @GetMapping("/getProducts")
    public List<Product> getProducts() {
        return productRepo.findAll();
    }
 // ==================== NEW CONNECTION ====================

    @PostMapping("/addConnection")
    public NewConnection addConnection(@RequestBody NewConnection n) {
        return newConnectionRepo.save(n);
    }

    @GetMapping("/getConnection")
    public List<NewConnection> getConnection() {
        return newConnectionRepo.findAll();
    }
    // ==================== CYLINDER ====================
    @PostMapping("/addCylinder")
    public Cylinder addCylinder(@RequestBody Cylinder c) {
        return cylinderRepo.save(c);
    }

    @GetMapping("/getCylinder")
    public List<Cylinder> getCylinder() {
        return cylinderRepo.findAll();
    }

    @PutMapping("/updatePrice")
    public void updatePrice(@RequestBody Cylinder c) {
        Cylinder existing = cylinderRepo.findTopByTypeOrderByDateDesc(c.getType());
        existing.setPrice(c.getPrice());
        cylinderRepo.save(existing);
    }

    // ==================== EMPLOYEE ====================
    @PostMapping("/addEmployee")
    public Employee addEmployee(@RequestBody Employee e) {
        return employeeRepo.save(e);
    }

    @GetMapping("/getEmployee")
    public List<Employee> getEmployee() {
        return employeeRepo.findAll();
    }

    @PutMapping("/updateEmployee")
    public void updateEmployee(@RequestBody Employee emp) {
        Employee existing = employeeRepo.findByName(emp.getName());
        existing.setSalary(emp.getSalary());
        employeeRepo.save(existing);
    }

    @Transactional
    @DeleteMapping("/deleteEmployee/{name}")
    public ResponseEntity<String> deleteEmployee(@PathVariable String name) {
        employeeRepo.deleteByName(name);
        return ResponseEntity.ok("Deleted");
    }

    // ==================== SALES ====================
    @PostMapping("/addSales")
    public Sales addSales(@RequestBody Sales s) {

        if(s.getProductName() != null) {
            Product p = productRepo.findByProductName(s.getProductName());

            if(p != null) {
                s.setProductPrice(p.getPrice());
            }
        }

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

    // ==================== MAINTENANCE ====================
    @PostMapping("/addMaintenance")
    public Maintenance addMaintenance(@RequestBody Maintenance m) {
        return maintenanceRepo.save(m);
    }

    @GetMapping("/getMaintenance")
    public List<Maintenance> getMaintenance() {
        return maintenanceRepo.findAll();
    }

    // ==================== DATE-WISE TOTAL ====================
    @GetMapping("/total/{date}")
    public Map<String, Object> getTotal(@PathVariable String date) {
        double[] prices = getPrices();
        int totalQty = 0;
        double totalAmount = 0;
        for (Sales s : salesRepo.findAll()) {
            if (s.getDate().equals(date)) {
                totalQty   += s.getDomestic() + s.getCommercial() + s.getSmall();
                totalAmount += (s.getDomestic()   * prices[0])
                             + (s.getCommercial() * prices[1])
                             + (s.getSmall()      * prices[2]);
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("quantity", totalQty);
        result.put("amount",   totalAmount);
        return result;
    }

    // ==================== MONTHLY SALES ====================
    @GetMapping("/monthlyAmount/{month}")
    public Map<String, Object> getMonthlyAmount(@PathVariable String month) {
        double[] prices = getPrices();
        int totalQty = 0;
        double totalAmount = 0;
        for (Sales s : salesRepo.findAll()) {
            if (s.getDate().startsWith(month)) {
                totalQty   += s.getDomestic() + s.getCommercial() + s.getSmall();
                totalAmount += (s.getDomestic()   * prices[0])
                             + (s.getCommercial() * prices[1])
                             + (s.getSmall()      * prices[2]);
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("quantity", totalQty);
        result.put("amount",   totalAmount);
        return result;
    }

    // ==================== MAINTENANCE TOTAL ====================
    @GetMapping("/maintenance/total/{date}")
    public double getMaintenanceTotal(@PathVariable String date) {
        double total = 0;
        for (Maintenance m : maintenanceRepo.findAll())
            if (m.getDate().equals(date))
                total += m.getFuel() + m.getOtherExpense()
                       + m.getEmployeePayment() + m.getBankDeposit();
        return total;
    }

    @GetMapping("/maintenance/month/{month}")
    public double getMonthlyMaintenance(@PathVariable String month) {
        double total = 0;
        for (Maintenance m : maintenanceRepo.findAll())
            if (m.getDate().startsWith(month))
                total += m.getFuel() + m.getOtherExpense()
                       + m.getEmployeePayment() + m.getBankDeposit();
        return total;
    }

    // ==================== TOTAL SALES ====================
    @GetMapping("/sales/total")
    public Map<String, Object> getSalesTotal() {
        double[] prices = getPrices();
        int totalQty = 0;
        double totalAmount = 0;
        for (Sales s : salesRepo.findAll()) {
            totalQty   += s.getDomestic() + s.getCommercial() + s.getSmall();
            totalAmount += (s.getDomestic()   * prices[0])
                         + (s.getCommercial() * prices[1])
                         + (s.getSmall()      * prices[2]);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("quantity", totalQty);
        result.put("amount",   totalAmount);
        return result;
    }

    // ==================== CSV REPORTS ====================
    @GetMapping("/report/daily/{date}")
    public ResponseEntity<String> dailyReport(@PathVariable String date) {
        double[] prices = getPrices();
        StringBuilder sb = new StringBuilder();
        sb.append("Sudip HP Gas Agency\nDaily Report\nDate: ").append(date).append("\n\n");
        sb.append("Date,Domestic,Commercial,5kg,Total\n");
        int totalQty = 0;
        double totalAmount = 0, totalMaintenance = 0;
        for (Sales s : salesRepo.findAll()) {
            if (s.getDate().equals(date)) {
                int total = s.getDomestic() + s.getCommercial() + s.getSmall();
                totalQty += total;
                totalAmount += (s.getDomestic() * prices[0])
                             + (s.getCommercial() * prices[1])
                             + (s.getSmall() * prices[2]);
                sb.append(s.getDate()).append(",")
                  .append(s.getDomestic()).append(",")
                  .append(s.getCommercial()).append(",")
                  .append(s.getSmall()).append(",")
                  .append(total).append("\n");
            }
        }
        for (Maintenance m : maintenanceRepo.findAll())
            if (m.getDate().equals(date))
                totalMaintenance += m.getFuel() + m.getOtherExpense()
                                  + m.getEmployeePayment() + m.getBankDeposit();
        sb.append("\n--------------------------------\n");
        sb.append("Total Quantity: ").append(totalQty).append("\n");
        sb.append("Total Sales Amount: Rs. ").append(totalAmount).append("\n");
        sb.append("Total Maintenance: Rs. ").append(totalMaintenance).append("\n");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=daily_report.csv")
                .body(sb.toString());
    }

    @GetMapping("/report/month/{month}")
    public ResponseEntity<String> monthlyReport(@PathVariable String month) {
        double[] prices = getPrices();
        StringBuilder sb = new StringBuilder();
        sb.append("Sudip HP Gas Agency\nMonthly Report\nMonth: ").append(month).append("\n\n");
        int totalQty = 0;
        double totalAmount = 0, totalMaintenance = 0;
        for (Sales s : salesRepo.findAll()) {
            if (s.getDate().startsWith(month)) {
                totalQty   += s.getDomestic() + s.getCommercial() + s.getSmall();
                totalAmount += (s.getDomestic() * prices[0])
                             + (s.getCommercial() * prices[1])
                             + (s.getSmall() * prices[2]);
            }
        }
        for (Maintenance m : maintenanceRepo.findAll())
            if (m.getDate().startsWith(month))
                totalMaintenance += m.getFuel() + m.getOtherExpense()
                                  + m.getEmployeePayment() + m.getBankDeposit();
        sb.append("Total Quantity: ").append(totalQty).append("\n");
        sb.append("Total Sales Amount: Rs. ").append(totalAmount).append("\n");
        sb.append("Total Maintenance: Rs. ").append(totalMaintenance).append("\n");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=monthly_report.csv")
                .body(sb.toString());
    }

    @GetMapping("/report/year/{year}")
    public ResponseEntity<String> yearlyReport(@PathVariable String year) {
        double[] prices = getPrices();
        StringBuilder sb = new StringBuilder();
        sb.append("Sudip HP Gas Agency\nYearly Report\nYear: ").append(year).append("\n\n");
        int totalQty = 0;
        double totalAmount = 0, totalMaintenance = 0;
        for (Sales s : salesRepo.findAll()) {
            if (s.getDate().startsWith(year)) {
                totalQty   += s.getDomestic() + s.getCommercial() + s.getSmall();
                totalAmount += (s.getDomestic() * prices[0])
                             + (s.getCommercial() * prices[1])
                             + (s.getSmall() * prices[2]);
            }
        }
        for (Maintenance m : maintenanceRepo.findAll())
            if (m.getDate().startsWith(year))
                totalMaintenance += m.getFuel() + m.getOtherExpense()
                                  + m.getEmployeePayment() + m.getBankDeposit();
        sb.append("Total Quantity: ").append(totalQty).append("\n");
        sb.append("Total Sales Amount: Rs. ").append(totalAmount).append("\n");
        sb.append("Total Maintenance: Rs. ").append(totalMaintenance).append("\n");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=yearly_report.csv")
                .body(sb.toString());
    }

    // ==================== PDF REPORTS ====================
    @GetMapping("/report/pdf/{date}")
    public ResponseEntity<byte[]> generatePDF(@PathVariable String date) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(new PdfDocument(new PdfWriter(out)), PageSize.A4);
        doc.setMargins(34f, 42.5f, 34f, 42.5f);
        buildPDF(doc, "Daily", "Date: " + date, date, null);
        doc.close();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Gas_Daily_" + date + ".pdf")
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                .body(out.toByteArray());
    }

    @GetMapping("/report/pdf/month/{month}")
    public ResponseEntity<byte[]> monthlyPDF(@PathVariable String month) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(new PdfDocument(new PdfWriter(out)), PageSize.A4);
        doc.setMargins(34f, 42.5f, 34f, 42.5f);
        buildPDF(doc, "Monthly", "Month: " + month, null, month);
        doc.close();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Gas_Monthly_" + month + ".pdf")
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                .body(out.toByteArray());
    }

    @GetMapping("/report/pdf/year/{year}")
    public ResponseEntity<byte[]> yearlyPDF(@PathVariable String year) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(new PdfDocument(new PdfWriter(out)), PageSize.A4);
        doc.setMargins(34f, 42.5f, 34f, 42.5f);
        buildPDF(doc, "Yearly", "Year: " + year, null, year);
        doc.close();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Gas_Yearly_" + year + ".pdf")
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                .body(out.toByteArray());
    }

    // ==================== HELPER: GET PRICES ====================
    private double[] getPrices() {
        double dp = 0, cp = 0, sp = 0;
        for (Cylinder c : cylinderRepo.findAll()) {
            if (c.getType().equalsIgnoreCase("domestic"))   dp = c.getPrice();
            if (c.getType().equalsIgnoreCase("commercial")) cp = c.getPrice();
            if (c.getType().equalsIgnoreCase("5kg"))        sp = c.getPrice();
        }
        return new double[]{dp, cp, sp};
    }

    // ==================== HELPER: BUILD PDF ====================
    private void buildPDF(Document doc, String rtype, String period,
                          String date, String prefix) throws Exception {

        // ── COLORS ──
        DeviceRgb NAVY2    = new DeviceRgb(0x0a, 0x2e, 0x6e);
        DeviceRgb NAVY     = new DeviceRgb(0x0d, 0x47, 0xa1);
        DeviceRgb BLUELIT  = new DeviceRgb(0xe3, 0xf0, 0xff);
        DeviceRgb BLUEMID  = new DeviceRgb(0xbb, 0xde, 0xfb);
        DeviceRgb ORANGE   = new DeviceRgb(0xe6, 0x51, 0x00);
        DeviceRgb ORLIT    = new DeviceRgb(0xff, 0xf8, 0xe1);
        DeviceRgb ORMID    = new DeviceRgb(0xff, 0xe0, 0xb2);
        DeviceRgb GRAY     = new DeviceRgb(0x54, 0x6e, 0x7a);
        DeviceRgb GRAYLIT  = new DeviceRgb(0xf5, 0xf7, 0xfa);
        DeviceRgb GRAYLINE = new DeviceRgb(0xe0, 0xe7, 0xef);
        DeviceRgb WHITE    = new DeviceRgb(255, 255, 255);
        DeviceRgb GOLD     = new DeviceRgb(0xff, 0xd5, 0x4f);
        DeviceRgb SKYBLUE  = new DeviceRgb(0x90, 0xca, 0xf9);

        float PW = PageSize.A4.getWidth() - 85f; // ~510pt usable width

        // ── LOGO ──
        String logoPath = "src/main/resources/static/images/logo.png";
        Image logo = new Image(ImageDataFactory.create(logoPath)).setWidth(42).setHeight(42);

        // ── HEADER LEFT: logo + agency name ──
        Paragraph agencyName = new Paragraph("SUDIP HP GAS AGENCY")
                .setFontColor(WHITE).setBold().setFontSize(15)
                .setMarginBottom(3).setMarginTop(0);
        Paragraph agencySub = new Paragraph("Authorized HP Gas Distributor  ·  Kurha, Maharashtra")
                .setFontColor(SKYBLUE).setFontSize(9).setMarginTop(0).setMarginBottom(0);

        Table nameBlock = new Table(new float[]{270f});
        nameBlock.addCell(new Cell().add(agencyName).setBorder(Border.NO_BORDER).setPadding(0).setPaddingLeft(8));
        nameBlock.addCell(new Cell().add(agencySub) .setBorder(Border.NO_BORDER).setPadding(0).setPaddingLeft(8));

        Table leftInner = new Table(new float[]{50f, 270f});
        leftInner.addCell(new Cell().add(logo).setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE).setPadding(0));
        leftInner.addCell(new Cell().add(nameBlock).setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE).setPadding(0));

        // ── HEADER RIGHT: report type + period ──
        Paragraph rtPara = new Paragraph(rtype.toUpperCase() + " REPORT")
                .setFontColor(WHITE).setBold().setFontSize(13)
                .setTextAlignment(TextAlignment.RIGHT).setMarginBottom(4).setMarginTop(0);
        Paragraph rdPara = new Paragraph(period)
                .setFontColor(GOLD).setBold().setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT).setMarginTop(0).setMarginBottom(0);

        Table rightInner = new Table(new float[]{150f});
        rightInner.addCell(new Cell().add(rtPara).setBorder(Border.NO_BORDER).setPadding(0));
        rightInner.addCell(new Cell().add(rdPara).setBorder(Border.NO_BORDER).setPadding(0));

        // ── HEADER OUTER: 340 + 170 = 510pt ──
        Table hdrOuter = new Table(new float[]{340f, 170f}).setWidth(PW);
        hdrOuter.addCell(new Cell().add(leftInner).setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE).setPadding(0));
        hdrOuter.addCell(new Cell().add(rightInner).setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE).setPadding(0));

        Table hdrBox = new Table(new float[]{PW}).setWidth(PW)
                .setBackgroundColor(NAVY2).setBorderRadius(new BorderRadius(8));
        hdrBox.addCell(new Cell().add(hdrOuter).setBorder(Border.NO_BORDER)
                .setPaddingTop(14).setPaddingBottom(14)
                .setPaddingLeft(14).setPaddingRight(14));
        doc.add(hdrBox);
        doc.add(new Paragraph(" ").setFontSize(4));

        // ── INFO BAR ──
        String genTime = new java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a")
                .format(new java.util.Date());
        float cw3 = PW / 3f;
        Table info = new Table(new float[]{cw3, cw3, cw3}).setWidth(PW);
        String[][] infoCells = {
            {"Location:  Kurha, Maharashtra", "LEFT"},
            {"Phone:  +91 9579916599",        "CENTER"},
            {"Generated:  " + genTime,        "RIGHT"},
        };
        for (String[] ic : infoCells) {
            TextAlignment ta = ic[1].equals("CENTER") ? TextAlignment.CENTER
                             : ic[1].equals("RIGHT")  ? TextAlignment.RIGHT
                             : TextAlignment.LEFT;
            info.addCell(new Cell()
                    .add(new Paragraph(ic[0]).setFontColor(GRAY).setFontSize(9).setTextAlignment(ta))
                    .setBackgroundColor(GRAYLIT).setBorder(Border.NO_BORDER)
                    .setPaddingTop(7).setPaddingBottom(7)
                    .setPaddingLeft(10).setPaddingRight(10));
        }
        doc.add(info);
        doc.add(new Paragraph(" ").setFontSize(5));

        // ── PRICES ──
        double[] prices = getPrices();
        double dp = prices[0], cp = prices[1], sp = prices[2];

        // ── SALES TOTALS ──
        int dom = 0, com = 0, sml = 0;
        for (Sales s : salesRepo.findAll()) {
            boolean match = (date   != null && s.getDate().equals(date))
                         || (prefix != null && s.getDate().startsWith(prefix));
            if (match) {
                dom += s.getDomestic();
                com += s.getCommercial();
                sml += s.getSmall();
            }
        }
        double dAmt = dom * dp, cAmt = com * cp, sAmt = sml * sp;
        double totalSales = dAmt + cAmt + sAmt;

        // ── MAINTENANCE TOTALS ──
        double fuel = 0, other = 0, emp = 0, bank = 0;
        for (Maintenance m : maintenanceRepo.findAll()) {
            boolean match = (date   != null && m.getDate().equals(date))
                         || (prefix != null && m.getDate().startsWith(prefix));
            if (match) {
                fuel  += m.getFuel();
                other += m.getOtherExpense();
                emp   += m.getEmployeePayment();
                bank  += m.getBankDeposit();
            }
        }
        double totalMaint = fuel + other + emp + bank;

        // ── 2 SUMMARY CARDS (no profit) ──
        float GAP = 10f;
        float CW2 = (PW - GAP) / 2f;

        Table salesCard = makeCard("TOTAL SALES",    "#0d47a1", totalSales,
                                   "Revenue from cylinder sales", BLUELIT, CW2, GRAY);
        Table maintCard = makeCard("TOTAL EXPENSES", "#e65100", totalMaint,
                                   "Maintenance & operations",    ORLIT,   CW2, GRAY);

        Table cardsRow = new Table(new float[]{CW2, GAP, CW2}).setWidth(PW);
        cardsRow.addCell(new Cell().add(salesCard).setBorder(Border.NO_BORDER).setPadding(0));
        cardsRow.addCell(new Cell().setBorder(Border.NO_BORDER).setPadding(0));
        cardsRow.addCell(new Cell().add(maintCard).setBorder(Border.NO_BORDER).setPadding(0));
        doc.add(cardsRow);
        doc.add(new Paragraph(" ").setFontSize(5));

        // ── SALES TABLE ──
        doc.add(new Paragraph("Sales Summary")
                .setBold().setFontSize(12).setFontColor(NAVY).setMarginBottom(3));
        doc.add(new LineSeparator(new SolidLine(2f)).setStrokeColor(NAVY).setMarginBottom(5));

        Table st = new Table(new float[]{170f, 90f, 120f, 130f}).setWidth(PW);
        for (String h : new String[]{"Cylinder Type", "Qty Sold", "Unit Price (Rs.)", "Amount (Rs.)"}) {
            TextAlignment ta = h.equals("Cylinder Type") ? TextAlignment.LEFT
                             : h.equals("Qty Sold")      ? TextAlignment.CENTER
                             : TextAlignment.RIGHT;
            st.addHeaderCell(new Cell()
                    .add(new Paragraph(h).setFontColor(WHITE).setBold().setFontSize(10).setTextAlignment(ta))
                    .setBackgroundColor(NAVY).setBorder(Border.NO_BORDER)
                    .setPaddingTop(10).setPaddingBottom(10).setPaddingLeft(12).setPaddingRight(12));
        }
        Object[][] srows = {
            {"Domestic",   dom, dp, dAmt},
            {"Commercial", com, cp, cAmt},
            {"5 kg",       sml, sp, sAmt},
        };
        boolean alt = false;
        for (Object[] row : srows) {
            DeviceRgb bg = alt ? GRAYLIT : WHITE; alt = !alt;
            st.addCell(pdfCell(row[0].toString(), bg, TextAlignment.LEFT,   false, GRAYLINE));
            st.addCell(pdfCell(row[1].toString(), bg, TextAlignment.CENTER, false, GRAYLINE));
            st.addCell(pdfCell(String.format("%,.2f", ((Number) row[2]).doubleValue()), bg, TextAlignment.RIGHT, false, GRAYLINE));
            st.addCell(pdfCell(String.format("%,.2f", ((Number) row[3]).doubleValue()), bg, TextAlignment.RIGHT, true,  GRAYLINE));
        }
        st.addCell(pdfCell("TOTAL",                            BLUEMID, TextAlignment.LEFT,   true,  NAVY));
        st.addCell(pdfCell(String.valueOf(dom + com + sml),    BLUEMID, TextAlignment.CENTER, true,  NAVY));
        st.addCell(pdfCell("",                                 BLUEMID, TextAlignment.RIGHT,  false, NAVY));
        st.addCell(pdfCell(String.format("%,.2f", totalSales), BLUEMID, TextAlignment.RIGHT,  true,  NAVY));
        doc.add(st);
        doc.add(new Paragraph(" ").setFontSize(5));

        // ── MAINTENANCE TABLE ──
        doc.add(new Paragraph("Maintenance & Expense Summary")
                .setBold().setFontSize(12).setFontColor(ORANGE).setMarginBottom(3));
        doc.add(new LineSeparator(new SolidLine(2f)).setStrokeColor(ORANGE).setMarginBottom(5));

        Table mt = new Table(new float[]{170f, 170f, 170f}).setWidth(PW);
        for (String h : new String[]{"Expense Type", "Amount (Rs.)", "Remarks"}) {
            TextAlignment ta = h.equals("Amount (Rs.)") ? TextAlignment.RIGHT : TextAlignment.LEFT;
            mt.addHeaderCell(new Cell()
                    .add(new Paragraph(h).setFontColor(WHITE).setBold().setFontSize(10).setTextAlignment(ta))
                    .setBackgroundColor(ORANGE).setBorder(Border.NO_BORDER)
                    .setPaddingTop(10).setPaddingBottom(10).setPaddingLeft(12).setPaddingRight(12));
        }
        String[][] mrows = {
            {"Fuel",             String.format("%,.2f", fuel),  "Petrol / Diesel cost"},
            {"Other Expense",    String.format("%,.2f", other), "Miscellaneous"},
            {"Employee Payment", String.format("%,.2f", emp),   "Salary / Wages"},
            {"Bank Deposit",     String.format("%,.2f", bank),  "Deposited to bank"},
        };
        alt = false;
        for (String[] row : mrows) {
            DeviceRgb bg = alt ? ORLIT : WHITE; alt = !alt;
            mt.addCell(pdfCell(row[0], bg, TextAlignment.LEFT,  false, GRAYLINE));
            mt.addCell(pdfCell(row[1], bg, TextAlignment.RIGHT, true,  GRAYLINE));
            mt.addCell(pdfCell(row[2], bg, TextAlignment.LEFT,  false, GRAYLINE));
        }
        mt.addCell(pdfCell("TOTAL EXPENSES",                      ORMID, TextAlignment.LEFT,  true,  ORANGE));
        mt.addCell(pdfCell(String.format("%,.2f", totalMaint),    ORMID, TextAlignment.RIGHT, true,  ORANGE));
        mt.addCell(pdfCell("",                                    ORMID, TextAlignment.LEFT,  false, ORANGE));
        doc.add(mt);
        doc.add(new Paragraph(" ").setFontSize(5));

        // ── FOOTER ──
        Image flogo = new Image(ImageDataFactory.create(logoPath)).setWidth(24).setHeight(24);

        Table footerLeft = new Table(new float[]{32f, 260f});
        footerLeft.addCell(new Cell().add(flogo).setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE).setPadding(0));
        footerLeft.addCell(new Cell()
                .add(new Paragraph("Sudip HP Gas Agency")
                        .setFontColor(WHITE).setBold().setFontSize(10)
                        .setMarginBottom(2).setMarginTop(0))
                .add(new Paragraph("Kurha, Maharashtra  ·  +91 9579916599  ·  Authorized HP Gas Distributor")
                        .setFontColor(SKYBLUE).setFontSize(8).setMarginTop(0))
                .setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setPadding(0).setPaddingLeft(8));

        Paragraph footerRight = new Paragraph(
                "System-generated report.\nFor queries contact the agency office.")
                .setFontColor(SKYBLUE).setFontSize(8)
                .setTextAlignment(TextAlignment.RIGHT);

        Table footerOuter = new Table(new float[]{340f, 170f}).setWidth(PW);
        footerOuter.addCell(new Cell().add(footerLeft).setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE).setPadding(0));
        footerOuter.addCell(new Cell().add(footerRight).setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE).setPadding(0));

        Table footerBox = new Table(new float[]{PW}).setWidth(PW)
                .setBackgroundColor(NAVY2).setBorderRadius(new BorderRadius(8));
        footerBox.addCell(new Cell().add(footerOuter).setBorder(Border.NO_BORDER)
                .setPaddingTop(12).setPaddingBottom(12)
                .setPaddingLeft(14).setPaddingRight(14));
        doc.add(footerBox);
    }

    // ==================== HELPER: CARD ====================
    private Table makeCard(String title, String titleColor, double value,
                           String sub, DeviceRgb bg, float cw, DeviceRgb gray) {
        DeviceRgb tc = new DeviceRgb(
                Integer.parseInt(titleColor.substring(1, 3), 16),
                Integer.parseInt(titleColor.substring(3, 5), 16),
                Integer.parseInt(titleColor.substring(5, 7), 16));

        Table inner = new Table(new float[]{cw - 24f});
        inner.addCell(new Cell()
                .add(new Paragraph(title).setBold().setFontSize(9).setFontColor(tc)
                        .setMarginBottom(4).setMarginTop(0))
                .add(new Paragraph(String.format("Rs. %,.2f", value)).setBold().setFontSize(19)
                        .setFontColor(tc).setMarginBottom(4).setMarginTop(0))
                .add(new Paragraph(sub).setFontSize(8).setFontColor(gray).setMarginTop(0))
                .setBorder(Border.NO_BORDER).setPadding(0));

        Table wrap = new Table(new float[]{cw});
        wrap.addCell(new Cell().add(inner)
                .setBackgroundColor(bg).setBorder(Border.NO_BORDER)
                .setBorderRadius(new BorderRadius(6))
                .setPaddingTop(14).setPaddingBottom(14)
                .setPaddingLeft(14).setPaddingRight(12));
        return wrap;
    }

    // ==================== HELPER: TABLE CELL ====================
    private Cell pdfCell(String text, DeviceRgb bg, TextAlignment align,
            boolean bold, DeviceRgb lineColor) {

Paragraph p = new Paragraph(text)
   .setFontSize(10)
   .setTextAlignment(align);

if (bold) p.setBold();  // ✅ no argument

return new Cell()
   .add(p)
   .setBackgroundColor(bg)
   .setBorder(Border.NO_BORDER)
   .setBorderBottom(new SolidBorder(lineColor, 0.5f))
   .setPaddingTop(9)
   .setPaddingBottom(9)
   .setPaddingLeft(12)
   .setPaddingRight(12);
}

} // ← END OF CLASS