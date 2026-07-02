package com.gasagency.controller;

import com.gasagency.model.CylinderSales;
import com.gasagency.model.ProductSales;
import com.gasagency.model.Cylinder;
import com.gasagency.model.Product;
import com.gasagency.model.BankDeposit;
import com.gasagency.repository.CylinderSalesRepository;
import com.gasagency.repository.ProductSalesRepository;
import com.gasagency.repository.CylinderRepository;
import com.gasagency.repository.ProductRepository;
import com.gasagency.repository.BankDepositRepository;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/sales")
public class SalesController {

    @Autowired CylinderSalesRepository cylinderSalesRepo;
    @Autowired ProductSalesRepository productSalesRepo;
    @Autowired CylinderRepository cylinderRepo;
    @Autowired ProductRepository productRepo;
    @Autowired BankDepositRepository bankDepositRepo;

    private void autoDepositOnline(double onlineAmount, String date, String remarks) {
        if (onlineAmount <= 0) return;
        BankDeposit deposit = new BankDeposit();
        deposit.setDate(date);
        deposit.setOnlineDeposit(onlineAmount);
        deposit.setCashDeposit(0);
        deposit.setTotalDeposit(onlineAmount);
        deposit.setRemarks(remarks);
        bankDepositRepo.save(deposit);
    }

    // ==================== CYLINDER SALES ====================

    @PostMapping("/cylinder/add")
    public ResponseEntity<CylinderSales> addCylinderSale(@RequestBody CylinderSales s) {
        Cylinder cyl = cylinderRepo.findTopByTypeOrderByIdDesc(s.getCylinderType());
        if (cyl != null) {
            s.setCylinderPrice(cyl.getPrice());
            s.setCylinderTotal(s.getCylinderQty() * cyl.getPrice());
        }
        s.setTotalAmount(s.getCylinderTotal());
        CylinderSales saved = cylinderSalesRepo.save(s);
        if (s.getOnlineReceived() > 0) {
            double price = s.getCylinderPrice();
            long onlineQty = price > 0 ? Math.round(s.getOnlineReceived() / price) : 0;
            boolean mixedPayment = s.getCashReceived() > 0;
            String remark = "Cylinder Sale - " + onlineQty + " " + s.getCylinderType()
                          + (mixedPayment
                              ? " (Online, of " + s.getCylinderQty() + " total)"
                              : " (Online)")
                          + " [Sale ID: " + saved.getId() + "]";
            autoDepositOnline(s.getOnlineReceived(), s.getDate(), remark);
        }
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/cylinder/all")
    public List<CylinderSales> getAllCylinderSales() {
        return cylinderSalesRepo.findAll();
    }

    @PutMapping("/cylinder/update/{id}")
    public ResponseEntity<CylinderSales> updateCylinderSale(
            @PathVariable int id, @RequestBody CylinderSales updated) {
        return cylinderSalesRepo.findById(id).map(s -> {
            s.setDate(updated.getDate());
            s.setCylinderType(updated.getCylinderType());
            s.setCylinderQty(updated.getCylinderQty());
            s.setCashReceived(updated.getCashReceived());
            s.setOnlineReceived(updated.getOnlineReceived());
            Cylinder cyl = cylinderRepo.findTopByTypeOrderByIdDesc(updated.getCylinderType());
            if (cyl != null) {
                s.setCylinderPrice(cyl.getPrice());
                s.setCylinderTotal(updated.getCylinderQty() * cyl.getPrice());
                s.setTotalAmount(s.getCylinderTotal());
            }
            CylinderSales saved = cylinderSalesRepo.save(s);
            if (updated.getOnlineReceived() > 0) {
                double price = s.getCylinderPrice();
                long onlineQty = price > 0 ? Math.round(updated.getOnlineReceived() / price) : 0;
                boolean mixedPayment = updated.getCashReceived() > 0;
                String remark = "Cylinder Sale (Edit) - " + onlineQty
                    + " " + updated.getCylinderType()
                    + (mixedPayment
                        ? " (Online, of " + updated.getCylinderQty() + " total)"
                        : " (Online)")
                    + " [Sale ID: " + id + "]";
                autoDepositOnline(updated.getOnlineReceived(), updated.getDate(), remark);
            }
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }
    @DeleteMapping("/cylinder/delete/{id}")
    public ResponseEntity<String> deleteCylinderSale(@PathVariable int id) {
        cylinderSalesRepo.deleteById(id);
        return ResponseEntity.ok("Deleted");
    }

    // ==================== PRODUCT SALES ====================

    @PostMapping("/product/add")
    public ResponseEntity<ProductSales> addProductSale(@RequestBody ProductSales s) {
        Product prod = productRepo.findByProductName(s.getProductName());
        if (prod != null) {
            s.setProductPrice(prod.getPrice());
            s.setProductTotal(s.getProductQty() * prod.getPrice());
        }
        s.setTotalAmount(s.getProductTotal());
        ProductSales savedP = productSalesRepo.save(s);
        if (s.getOnlineReceived() > 0) {
            String remark = "Product Sale - " + s.getProductQty() + "x " + s.getProductName()
                          + " (Online) [Sale ID: " + savedP.getId() + "]";
            autoDepositOnline(s.getOnlineReceived(), s.getDate(), remark);
        }
        return ResponseEntity.ok(savedP);
    }

    @GetMapping("/product/all")
    public List<ProductSales> getAllProductSales() {
        return productSalesRepo.findAll();
    }

    @PutMapping("/product/update/{id}")
    public ResponseEntity<ProductSales> updateProductSale(
            @PathVariable int id, @RequestBody ProductSales updated) {
        return productSalesRepo.findById(id).map(s -> {
            s.setDate(updated.getDate());
            s.setProductName(updated.getProductName());
            s.setProductQty(updated.getProductQty());
            s.setCashReceived(updated.getCashReceived());
            s.setOnlineReceived(updated.getOnlineReceived());
            Product prod = productRepo.findByProductName(updated.getProductName());
            if (prod != null) {
                s.setProductPrice(prod.getPrice());
                s.setProductTotal(updated.getProductQty() * prod.getPrice());
                s.setTotalAmount(s.getProductTotal());
            }
            return ResponseEntity.ok(productSalesRepo.save(s));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/product/delete/{id}")
    public ResponseEntity<String> deleteProductSale(@PathVariable int id) {
        productSalesRepo.deleteById(id);
        return ResponseEntity.ok("Deleted");
    }

    // ==================== PDF: CYLINDER SALES ====================

    @GetMapping("/cylinder/pdf/{period}")
    public ResponseEntity<byte[]> cylinderSalesPDF(@PathVariable String period) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(new PdfDocument(new PdfWriter(out)), PageSize.A4);
        doc.setMargins(34f, 42.5f, 34f, 42.5f);

        DeviceRgb NAVY2 = new DeviceRgb(0x0a, 0x2e, 0x6e);
        DeviceRgb NAVY = new DeviceRgb(0x0d, 0x47, 0xa1);
        DeviceRgb WHITE = new DeviceRgb(255, 255, 255);
        DeviceRgb SKYBLUE = new DeviceRgb(0x90, 0xca, 0xf9);
        DeviceRgb GOLD = new DeviceRgb(0xff, 0xd5, 0x4f);
        DeviceRgb GRAYLIT = new DeviceRgb(0xf5, 0xf7, 0xfa);
        DeviceRgb GRAYLINE = new DeviceRgb(0xe0, 0xe7, 0xef);
        DeviceRgb BLUEMID = new DeviceRgb(0xbb, 0xde, 0xfb);
        float PW = PageSize.A4.getWidth() - 85f;

        buildHeader(doc, "Cylinder Sales Report", "Period: " + period, NAVY2, NAVY, WHITE, SKYBLUE, GOLD, PW);

        List<CylinderSales> records = cylinderSalesRepo.findAll().stream()
                .filter(s -> s.getDate() != null && s.getDate().startsWith(period))
                .collect(java.util.stream.Collectors.toList());

        doc.add(new Paragraph("Cylinder Sales Records")
                .setBold().setFontSize(13).setFontColor(NAVY).setMarginBottom(4));
        doc.add(new LineSeparator(new SolidLine(2f)).setStrokeColor(NAVY).setMarginBottom(6));

        Table table = new Table(new float[]{80f, 90f, 60f, 80f, 80f, 70f, 70f}).setWidth(PW);
        String[] headers = {"Date", "Type", "Qty", "Price (₹)", "Total (₹)", "Cash", "Online"};
        for (String h : headers) {
            table.addHeaderCell(new Cell()
                    .add(new Paragraph(h).setFontColor(WHITE).setBold().setFontSize(9))
                    .setBackgroundColor(NAVY).setBorder(Border.NO_BORDER).setPadding(8));
        }

        double grandTotal = 0, grandCash = 0, grandOnline = 0;
        int grandQty = 0;
        boolean alt = false;
        for (CylinderSales s : records) {
            DeviceRgb bg = alt ? GRAYLIT : WHITE;
            alt = !alt;
            table.addCell(pdfCell(s.getDate(), bg, TextAlignment.CENTER, GRAYLINE));
            table.addCell(pdfCell(s.getCylinderType(), bg, TextAlignment.CENTER, GRAYLINE));
            table.addCell(pdfCell(String.valueOf(s.getCylinderQty()), bg, TextAlignment.CENTER, GRAYLINE));
            table.addCell(pdfCell(fmt(s.getCylinderPrice()), bg, TextAlignment.RIGHT, GRAYLINE));
            table.addCell(pdfCell(fmt(s.getCylinderTotal()), bg, TextAlignment.RIGHT, GRAYLINE));
            table.addCell(pdfCell(fmt(s.getCashReceived()), bg, TextAlignment.RIGHT, GRAYLINE));
            table.addCell(pdfCell(fmt(s.getOnlineReceived()), bg, TextAlignment.RIGHT, GRAYLINE));
            grandTotal += s.getCylinderTotal();
            grandCash += s.getCashReceived();
            grandOnline += s.getOnlineReceived();
            grandQty += s.getCylinderQty();
        }
        // Totals row
        table.addCell(pdfCellBold("TOTAL", BLUEMID, TextAlignment.CENTER, NAVY));
        table.addCell(pdfCellBold("", BLUEMID, TextAlignment.CENTER, NAVY));
        table.addCell(pdfCellBold(String.valueOf(grandQty), BLUEMID, TextAlignment.CENTER, NAVY));
        table.addCell(pdfCellBold("", BLUEMID, TextAlignment.CENTER, NAVY));
        table.addCell(pdfCellBold(fmt(grandTotal), BLUEMID, TextAlignment.RIGHT, NAVY));
        table.addCell(pdfCellBold(fmt(grandCash), BLUEMID, TextAlignment.RIGHT, NAVY));
        table.addCell(pdfCellBold(fmt(grandOnline), BLUEMID, TextAlignment.RIGHT, NAVY));

        doc.add(table);
        buildFooter(doc, NAVY2, SKYBLUE, WHITE, PW);
        doc.close();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=CylinderSales_" + period + ".pdf")
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                .body(out.toByteArray());
    }

    // ==================== PDF: PRODUCT SALES ====================

    @GetMapping("/product/pdf/{period}")
    public ResponseEntity<byte[]> productSalesPDF(@PathVariable String period) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(new PdfDocument(new PdfWriter(out)), PageSize.A4);
        doc.setMargins(34f, 42.5f, 34f, 42.5f);

        DeviceRgb NAVY2 = new DeviceRgb(0x0a, 0x2e, 0x6e);
        DeviceRgb NAVY = new DeviceRgb(0x0d, 0x47, 0xa1);
        DeviceRgb WHITE = new DeviceRgb(255, 255, 255);
        DeviceRgb SKYBLUE = new DeviceRgb(0x90, 0xca, 0xf9);
        DeviceRgb GOLD = new DeviceRgb(0xff, 0xd5, 0x4f);
        DeviceRgb GRAYLIT = new DeviceRgb(0xf5, 0xf7, 0xfa);
        DeviceRgb GRAYLINE = new DeviceRgb(0xe0, 0xe7, 0xef);
        DeviceRgb ORANGE = new DeviceRgb(0xe6, 0x51, 0x00);
        DeviceRgb ORMID = new DeviceRgb(0xff, 0xe0, 0xb2);
        float PW = PageSize.A4.getWidth() - 85f;

        buildHeader(doc, "Product Sales Report", "Period: " + period, NAVY2, ORANGE, WHITE, SKYBLUE, GOLD, PW);

        List<ProductSales> records = productSalesRepo.findAll().stream()
                .filter(s -> s.getDate() != null && s.getDate().startsWith(period))
                .collect(java.util.stream.Collectors.toList());

        doc.add(new Paragraph("Product Sales Records")
                .setBold().setFontSize(13).setFontColor(ORANGE).setMarginBottom(4));
        doc.add(new LineSeparator(new SolidLine(2f)).setStrokeColor(ORANGE).setMarginBottom(6));

        Table table = new Table(new float[]{80f, 100f, 50f, 80f, 80f, 70f, 70f}).setWidth(PW);
        String[] headers = {"Date", "Product", "Qty", "Price (₹)", "Total (₹)", "Cash", "Online"};
        for (String h : headers) {
            table.addHeaderCell(new Cell()
                    .add(new Paragraph(h).setFontColor(WHITE).setBold().setFontSize(9))
                    .setBackgroundColor(ORANGE).setBorder(Border.NO_BORDER).setPadding(8));
        }

        double grandTotal = 0, grandCash = 0, grandOnline = 0;
        int grandQty = 0;
        boolean alt = false;
        for (ProductSales s : records) {
            DeviceRgb bg = alt ? GRAYLIT : WHITE;
            alt = !alt;
            table.addCell(pdfCell(s.getDate(), bg, TextAlignment.CENTER, GRAYLINE));
            table.addCell(pdfCell(s.getProductName(), bg, TextAlignment.LEFT, GRAYLINE));
            table.addCell(pdfCell(String.valueOf(s.getProductQty()), bg, TextAlignment.CENTER, GRAYLINE));
            table.addCell(pdfCell(fmt(s.getProductPrice()), bg, TextAlignment.RIGHT, GRAYLINE));
            table.addCell(pdfCell(fmt(s.getProductTotal()), bg, TextAlignment.RIGHT, GRAYLINE));
            table.addCell(pdfCell(fmt(s.getCashReceived()), bg, TextAlignment.RIGHT, GRAYLINE));
            table.addCell(pdfCell(fmt(s.getOnlineReceived()), bg, TextAlignment.RIGHT, GRAYLINE));
            grandTotal += s.getProductTotal();
            grandCash += s.getCashReceived();
            grandOnline += s.getOnlineReceived();
            grandQty += s.getProductQty();
        }
        table.addCell(pdfCellBold("TOTAL", ORMID, TextAlignment.CENTER, ORANGE));
        table.addCell(pdfCellBold("", ORMID, TextAlignment.CENTER, ORANGE));
        table.addCell(pdfCellBold(String.valueOf(grandQty), ORMID, TextAlignment.CENTER, ORANGE));
        table.addCell(pdfCellBold("", ORMID, TextAlignment.CENTER, ORANGE));
        table.addCell(pdfCellBold(fmt(grandTotal), ORMID, TextAlignment.RIGHT, ORANGE));
        table.addCell(pdfCellBold(fmt(grandCash), ORMID, TextAlignment.RIGHT, ORANGE));
        table.addCell(pdfCellBold(fmt(grandOnline), ORMID, TextAlignment.RIGHT, ORANGE));

        doc.add(table);
        buildFooter(doc, NAVY2, SKYBLUE, WHITE, PW);
        doc.close();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ProductSales_" + period + ".pdf")
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                .body(out.toByteArray());
    }

    // ==================== PDF HELPERS ====================

    private void buildHeader(Document doc, String title, String subtitle,
            DeviceRgb NAVY2, DeviceRgb ACCENT, DeviceRgb WHITE,
            DeviceRgb SKYBLUE, DeviceRgb GOLD, float PW) throws Exception {

        String logoPath = "src/main/resources/static/images/logo.png";
        Image logo = new Image(ImageDataFactory.create(logoPath)).setWidth(42).setHeight(42);

        Paragraph agencyName = new Paragraph("SUDIP HP GAS AGENCY")
                .setFontColor(WHITE).setBold().setFontSize(15).setMarginBottom(3).setMarginTop(0);
        Paragraph agencySub = new Paragraph("Authorized HP Gas Distributor  ·  Kurha, Maharashtra")
                .setFontColor(SKYBLUE).setFontSize(9).setMarginTop(0).setMarginBottom(0);

        Table nameBlock = new Table(new float[]{270f});
        nameBlock.addCell(new Cell().add(agencyName).setBorder(Border.NO_BORDER).setPadding(0).setPaddingLeft(8));
        nameBlock.addCell(new Cell().add(agencySub).setBorder(Border.NO_BORDER).setPadding(0).setPaddingLeft(8));

        Table leftInner = new Table(new float[]{50f, 270f});
        leftInner.addCell(new Cell().add(logo).setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE).setPadding(0));
        leftInner.addCell(new Cell().add(nameBlock).setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE).setPadding(0));

        Paragraph rtPara = new Paragraph(title.toUpperCase())
                .setFontColor(WHITE).setBold().setFontSize(11)
                .setTextAlignment(TextAlignment.RIGHT).setMarginBottom(4).setMarginTop(0);
        Paragraph rdPara = new Paragraph(subtitle)
                .setFontColor(GOLD).setBold().setFontSize(9)
                .setTextAlignment(TextAlignment.RIGHT).setMarginTop(0).setMarginBottom(0);

        Table rightInner = new Table(new float[]{150f});
        rightInner.addCell(new Cell().add(rtPara).setBorder(Border.NO_BORDER).setPadding(0));
        rightInner.addCell(new Cell().add(rdPara).setBorder(Border.NO_BORDER).setPadding(0));

        Table hdrOuter = new Table(new float[]{340f, 170f}).setWidth(PW);
        hdrOuter.addCell(new Cell().add(leftInner).setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE).setPadding(0));
        hdrOuter.addCell(new Cell().add(rightInner).setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE).setPadding(0));

        Table hdrBox = new Table(new float[]{PW}).setWidth(PW)
                .setBackgroundColor(NAVY2).setBorderRadius(new BorderRadius(8));
        hdrBox.addCell(new Cell().add(hdrOuter).setBorder(Border.NO_BORDER)
                .setPaddingTop(14).setPaddingBottom(14).setPaddingLeft(14).setPaddingRight(14));
        doc.add(hdrBox);
        doc.add(new Paragraph(" ").setFontSize(6));
    }

    private void buildFooter(Document doc, DeviceRgb NAVY2, DeviceRgb SKYBLUE,
            DeviceRgb WHITE, float PW) throws Exception {
        String logoPath = "src/main/resources/static/images/logo.png";
        Image flogo = new Image(ImageDataFactory.create(logoPath)).setWidth(24).setHeight(24);

        Table footerLeft = new Table(new float[]{32f, 260f});
        footerLeft.addCell(new Cell().add(flogo).setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE).setPadding(0));
        footerLeft.addCell(new Cell()
                .add(new Paragraph("Sudip HP Gas Agency").setFontColor(WHITE).setBold().setFontSize(10)
                        .setMarginBottom(2).setMarginTop(0))
                .add(new Paragraph("Kurha, Maharashtra  ·  +91 9579916599  ·  Authorized HP Gas Distributor")
                        .setFontColor(SKYBLUE).setFontSize(8).setMarginTop(0))
                .setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setPadding(0).setPaddingLeft(8));

        Paragraph footerRight = new Paragraph("System-generated report.\nFor queries contact the agency office.")
                .setFontColor(SKYBLUE).setFontSize(8).setTextAlignment(TextAlignment.RIGHT);

        Table footerOuter = new Table(new float[]{340f, 170f}).setWidth(PW);
        footerOuter.addCell(new Cell().add(footerLeft).setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE).setPadding(0));
        footerOuter.addCell(new Cell().add(footerRight).setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE).setPadding(0));

        Table footerBox = new Table(new float[]{PW}).setWidth(PW)
                .setBackgroundColor(NAVY2).setBorderRadius(new BorderRadius(8));
        footerBox.addCell(new Cell().add(footerOuter).setBorder(Border.NO_BORDER)
                .setPaddingTop(12).setPaddingBottom(12).setPaddingLeft(14).setPaddingRight(14));
        doc.add(new Paragraph(" ").setFontSize(5));
        doc.add(footerBox);
    }

    private Cell pdfCell(String text, DeviceRgb bg, TextAlignment align, DeviceRgb lineColor) {
        return new Cell().add(new Paragraph(text == null ? "" : text).setFontSize(9).setTextAlignment(align))
                .setBackgroundColor(bg).setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(lineColor, 0.5f))
                .setPaddingTop(7).setPaddingBottom(7).setPaddingLeft(8).setPaddingRight(8);
    }

    private Cell pdfCellBold(String text, DeviceRgb bg, TextAlignment align, DeviceRgb lineColor) {
        return new Cell().add(new Paragraph(text == null ? "" : text).setFontSize(9).setBold().setTextAlignment(align))
                .setBackgroundColor(bg).setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(lineColor, 0.5f))
                .setPaddingTop(8).setPaddingBottom(8).setPaddingLeft(8).setPaddingRight(8);
    }

    private String fmt(double v) {
        return String.format("%,.2f", v);
    }
}