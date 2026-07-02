package com.gasagency.controller;

import com.gasagency.model.CscSales;
import com.gasagency.model.BankDeposit;
import com.gasagency.repository.CscSalesRepository;
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
@RequestMapping("/sales/csc")
public class CscSalesController {

    @Autowired
    CscSalesRepository cscSalesRepo;
    @Autowired
    BankDepositRepository bankDepositRepo;

    private void autoDepositOnline(double amount, String date, String remarks) {
        if (amount <= 0) return;
        BankDeposit d = new BankDeposit();
        d.setDate(date);
        d.setOnlineDeposit(amount);
        d.setCashDeposit(0);
        d.setTotalDeposit(amount);
        d.setRemarks(remarks);
        bankDepositRepo.save(d);
    }

    // ── ADD ──
    @PostMapping("/add")
    public ResponseEntity<CscSales> addCscSale(@RequestBody CscSales s) {
        // Price is entered manually — just auto-calculate total
        s.setCylinderTotal(s.getCylinderQty() * s.getCylinderPrice());
        s.setTotalAmount(s.getCylinderTotal());
        CscSales saved = cscSalesRepo.save(s);
        if (s.getOnlineReceived() > 0) {
            double price = s.getCylinderPrice();
            long onlineQty = price > 0 ? Math.round(s.getOnlineReceived() / price) : 0;
            boolean mixedPayment = s.getCashReceived() > 0;
            String remark = "CSC Sale - " + s.getCscCenterName() + " | "
                          + onlineQty + " " + s.getCylinderType()
                          + (mixedPayment
                              ? " (Online, of " + s.getCylinderQty() + " total)"
                              : " (Online)")
                          + " [Sale ID: " + saved.getId() + "]";
            autoDepositOnline(s.getOnlineReceived(), s.getDate(), remark);
        }
        return ResponseEntity.ok(saved);
    }

    // ── GET ALL ──
    @GetMapping("/all")
    public List<CscSales> getAllCscSales() {
        return cscSalesRepo.findAll();
    }

    // ── UPDATE ──
    @PutMapping("/update/{id}")
    public ResponseEntity<CscSales> updateCscSale(@PathVariable int id, @RequestBody CscSales updated) {
        return cscSalesRepo.findById(id).map(s -> {
            s.setDate(updated.getDate());
            s.setCscCenterName(updated.getCscCenterName());
            s.setCylinderType(updated.getCylinderType());
            s.setCylinderQty(updated.getCylinderQty());
            s.setCylinderPrice(updated.getCylinderPrice());
            s.setCylinderTotal(updated.getCylinderQty() * updated.getCylinderPrice());
            s.setTotalAmount(s.getCylinderTotal());
            s.setCashReceived(updated.getCashReceived());
            s.setOnlineReceived(updated.getOnlineReceived());
            CscSales saved = cscSalesRepo.save(s);
            if (updated.getOnlineReceived() > 0) {
                double price = updated.getCylinderPrice();
                long onlineQty = price > 0 ? Math.round(updated.getOnlineReceived() / price) : 0;
                boolean mixedPayment = updated.getCashReceived() > 0;
                String remark = "CSC Sale (Edit) - " + updated.getCscCenterName()
                    + " | " + onlineQty + " " + updated.getCylinderType()
                    + (mixedPayment
                        ? " (Online, of " + updated.getCylinderQty() + " total)"
                        : " (Online)")
                    + " [Sale ID: " + id + "]";
                autoDepositOnline(updated.getOnlineReceived(), updated.getDate(), remark);
            }
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    // ── DELETE ──
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCscSale(@PathVariable int id) {
        cscSalesRepo.deleteById(id);
        return ResponseEntity.ok("Deleted");
    }

    // ── PDF ──
    @GetMapping("/pdf/{period}")
    public ResponseEntity<byte[]> cscSalesPDF(@PathVariable String period) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(new PdfDocument(new PdfWriter(out)), PageSize.A4);
        doc.setMargins(34f, 42.5f, 34f, 42.5f);

        DeviceRgb NAVY2    = new DeviceRgb(0x0a, 0x2e, 0x6e);
        DeviceRgb TEAL     = new DeviceRgb(0x00, 0x89, 0x7b);
        DeviceRgb WHITE    = new DeviceRgb(255, 255, 255);
        DeviceRgb SKYBLUE  = new DeviceRgb(0x90, 0xca, 0xf9);
        DeviceRgb GOLD     = new DeviceRgb(0xff, 0xd5, 0x4f);
        DeviceRgb GRAYLIT  = new DeviceRgb(0xf5, 0xf7, 0xfa);
        DeviceRgb GRAYLINE = new DeviceRgb(0xe0, 0xe7, 0xef);
        DeviceRgb TEALMID  = new DeviceRgb(0xb2, 0xdf, 0xdb);
        float PW = PageSize.A4.getWidth() - 85f;

        buildHeader(doc, "CSC Sales Report", "Period: " + period, NAVY2, TEAL, WHITE, SKYBLUE, GOLD, PW);

        List<CscSales> records = cscSalesRepo.findAll().stream()
                .filter(s -> s.getDate() != null && s.getDate().startsWith(period))
                .collect(java.util.stream.Collectors.toList());

        doc.add(new Paragraph("CSC Center Sales Records")
                .setBold().setFontSize(13).setFontColor(TEAL).setMarginBottom(4));
        doc.add(new LineSeparator(new SolidLine(2f)).setStrokeColor(TEAL).setMarginBottom(6));

        Table table = new Table(new float[]{65f, 110f, 65f, 55f, 70f, 70f, 60f, 60f}).setWidth(PW);
        String[] headers = {"Date", "CSC Center", "Type", "Qty", "Price (₹)", "Total (₹)", "Cash", "Online"};
        for (String h : headers) {
            table.addHeaderCell(new Cell()
                    .add(new Paragraph(h).setFontColor(WHITE).setBold().setFontSize(9))
                    .setBackgroundColor(TEAL).setBorder(Border.NO_BORDER).setPadding(8));
        }

        double grandTotal = 0, grandCash = 0, grandOnline = 0;
        int grandQty = 0;
        boolean alt = false;
        for (CscSales s : records) {
            DeviceRgb bg = alt ? GRAYLIT : WHITE;
            alt = !alt;
            table.addCell(pdfCell(s.getDate(),                        bg, TextAlignment.CENTER, GRAYLINE));
            table.addCell(pdfCell(s.getCscCenterName(),               bg, TextAlignment.LEFT,   GRAYLINE));
            table.addCell(pdfCell(s.getCylinderType(),                bg, TextAlignment.CENTER, GRAYLINE));
            table.addCell(pdfCell(String.valueOf(s.getCylinderQty()), bg, TextAlignment.CENTER, GRAYLINE));
            table.addCell(pdfCell(fmt(s.getCylinderPrice()),          bg, TextAlignment.RIGHT,  GRAYLINE));
            table.addCell(pdfCell(fmt(s.getCylinderTotal()),          bg, TextAlignment.RIGHT,  GRAYLINE));
            table.addCell(pdfCell(fmt(s.getCashReceived()),           bg, TextAlignment.RIGHT,  GRAYLINE));
            table.addCell(pdfCell(fmt(s.getOnlineReceived()),         bg, TextAlignment.RIGHT,  GRAYLINE));
            grandTotal  += s.getCylinderTotal();
            grandCash   += s.getCashReceived();
            grandOnline += s.getOnlineReceived();
            grandQty    += s.getCylinderQty();
        }
        table.addCell(pdfCellBold("TOTAL",                  TEALMID, TextAlignment.CENTER, TEAL));
        table.addCell(pdfCellBold("",                        TEALMID, TextAlignment.CENTER, TEAL));
        table.addCell(pdfCellBold("",                        TEALMID, TextAlignment.CENTER, TEAL));
        table.addCell(pdfCellBold(String.valueOf(grandQty),  TEALMID, TextAlignment.CENTER, TEAL));
        table.addCell(pdfCellBold("",                        TEALMID, TextAlignment.CENTER, TEAL));
        table.addCell(pdfCellBold(fmt(grandTotal),           TEALMID, TextAlignment.RIGHT,  TEAL));
        table.addCell(pdfCellBold(fmt(grandCash),            TEALMID, TextAlignment.RIGHT,  TEAL));
        table.addCell(pdfCellBold(fmt(grandOnline),          TEALMID, TextAlignment.RIGHT,  TEAL));

        doc.add(table);
        buildFooter(doc, NAVY2, SKYBLUE, WHITE, PW);
        doc.close();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=CscSales_" + period + ".pdf")
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                .body(out.toByteArray());
    }

    // ── PDF HELPERS ──

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