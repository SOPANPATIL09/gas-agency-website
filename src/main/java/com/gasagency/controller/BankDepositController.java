package com.gasagency.controller;

import com.gasagency.model.BankDeposit;
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
@RequestMapping("/bankdeposit")
public class BankDepositController {

    @Autowired BankDepositRepository bankDepositRepo;

    @PostMapping("/add")
    public ResponseEntity<BankDeposit> addDeposit(@RequestBody BankDeposit d) {
        d.setTotalDeposit(d.getOnlineDeposit() + d.getCashDeposit());
        return ResponseEntity.ok(bankDepositRepo.save(d));
    }

    @GetMapping("/all")
    public List<BankDeposit> getAllDeposits() {
        return bankDepositRepo.findAll();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BankDeposit> updateDeposit(
            @PathVariable int id, @RequestBody BankDeposit updated) {
        return bankDepositRepo.findById(id).map(d -> {
            d.setDate(updated.getDate());
            d.setOnlineDeposit(updated.getOnlineDeposit());
            d.setCashDeposit(updated.getCashDeposit());
            d.setTotalDeposit(updated.getOnlineDeposit() + updated.getCashDeposit());
            d.setRemarks(updated.getRemarks());
            return ResponseEntity.ok(bankDepositRepo.save(d));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteDeposit(@PathVariable int id) {
        bankDepositRepo.deleteById(id);
        return ResponseEntity.ok("Deleted");
    }

    @GetMapping("/total")
    public ResponseEntity<java.util.Map<String, Object>> getTotals() {
        List<BankDeposit> all = bankDepositRepo.findAll();
        double totalOnline = all.stream().mapToDouble(BankDeposit::getOnlineDeposit).sum();
        double totalCash = all.stream().mapToDouble(BankDeposit::getCashDeposit).sum();
        double grandTotal = all.stream().mapToDouble(BankDeposit::getTotalDeposit).sum();
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("totalOnline", totalOnline);
        result.put("totalCash", totalCash);
        result.put("grandTotal", grandTotal);
        result.put("count", all.size());
        return ResponseEntity.ok(result);
    }

    // ==================== PDF ====================

    @GetMapping("/pdf/{period}")
    public ResponseEntity<byte[]> bankDepositPDF(@PathVariable String period) throws Exception {
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
        DeviceRgb GREEN = new DeviceRgb(0x1b, 0x5e, 0x20);
        DeviceRgb GREENMID = new DeviceRgb(0xa5, 0xd6, 0xa7);
        float PW = PageSize.A4.getWidth() - 85f;

        // Header
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
        Paragraph rtPara = new Paragraph("BANK DEPOSIT REPORT")
                .setFontColor(WHITE).setBold().setFontSize(11)
                .setTextAlignment(TextAlignment.RIGHT).setMarginBottom(4).setMarginTop(0);
        Paragraph rdPara = new Paragraph("Period: " + period)
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

        // Table
        doc.add(new Paragraph("Bank Deposit Records")
                .setBold().setFontSize(13).setFontColor(NAVY).setMarginBottom(4));
        doc.add(new LineSeparator(new SolidLine(2f)).setStrokeColor(NAVY).setMarginBottom(6));

        List<BankDeposit> records = bankDepositRepo.findAll().stream()
                .filter(d -> d.getDate() != null && d.getDate().startsWith(period))
                .collect(java.util.stream.Collectors.toList());

        Table table = new Table(new float[]{90f, 120f, 100f, 120f, 80f}).setWidth(PW);
        String[] headers = {"Date", "Online Deposit (₹)", "Cash Deposit (₹)", "Total (₹)", "Remarks"};
        for (String h : headers) {
            table.addHeaderCell(new Cell()
                    .add(new Paragraph(h).setFontColor(WHITE).setBold().setFontSize(9))
                    .setBackgroundColor(NAVY).setBorder(Border.NO_BORDER).setPadding(8));
        }

        double totalOnline = 0, totalCash = 0, grandTotal = 0;
        boolean alt = false;
        for (BankDeposit d : records) {
            DeviceRgb bg = alt ? GRAYLIT : WHITE;
            alt = !alt;
            table.addCell(pdfCell(d.getDate(), bg, TextAlignment.CENTER, GRAYLINE));
            table.addCell(pdfCell(fmt(d.getOnlineDeposit()), bg, TextAlignment.RIGHT, GRAYLINE));
            table.addCell(pdfCell(fmt(d.getCashDeposit()), bg, TextAlignment.RIGHT, GRAYLINE));
            table.addCell(pdfCell(fmt(d.getTotalDeposit()), bg, TextAlignment.RIGHT, GRAYLINE));
            table.addCell(pdfCell(d.getRemarks() != null ? d.getRemarks() : "—", bg, TextAlignment.LEFT, GRAYLINE));
            totalOnline += d.getOnlineDeposit();
            totalCash += d.getCashDeposit();
            grandTotal += d.getTotalDeposit();
        }
        table.addCell(pdfCellBold("TOTAL", GREENMID, TextAlignment.CENTER, GREEN));
        table.addCell(pdfCellBold(fmt(totalOnline), GREENMID, TextAlignment.RIGHT, GREEN));
        table.addCell(pdfCellBold(fmt(totalCash), GREENMID, TextAlignment.RIGHT, GREEN));
        table.addCell(pdfCellBold(fmt(grandTotal), GREENMID, TextAlignment.RIGHT, GREEN));
        table.addCell(pdfCellBold("", GREENMID, TextAlignment.LEFT, GREEN));

        doc.add(table);

        // Footer
        Image flogo = new Image(ImageDataFactory.create(logoPath)).setWidth(24).setHeight(24);
        Table footerLeft = new Table(new float[]{32f, 260f});
        footerLeft.addCell(new Cell().add(flogo).setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE).setPadding(0));
        footerLeft.addCell(new Cell()
                .add(new Paragraph("Sudip HP Gas Agency").setFontColor(WHITE).setBold().setFontSize(10)
                        .setMarginBottom(2).setMarginTop(0))
                .add(new Paragraph("Kurha, Maharashtra  ·  +91 9579916599").setFontColor(SKYBLUE).setFontSize(8).setMarginTop(0))
                .setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE).setPadding(0).setPaddingLeft(8));
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

        doc.close();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=BankDeposit_" + period + ".pdf")
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                .body(out.toByteArray());
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

    private String fmt(double v) { return String.format("%,.2f", v); }
}