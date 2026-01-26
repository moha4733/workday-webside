package dk.tommer.workday.controller.admin;

import dk.tommer.workday.entity.MaterialOrder;
import dk.tommer.workday.entity.MaterialStatus;
import dk.tommer.workday.entity.Company;
import dk.tommer.workday.repository.MaterialOrderRepository;
import dk.tommer.workday.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.core.io.ClassPathResource;

import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {
    @Autowired
    private MaterialOrderRepository materialOrderRepository;
    @Autowired
    private CompanyRepository companyRepository;

    @GetMapping
    public String listOrders(Model model) {
        List<MaterialOrder> pending = materialOrderRepository.findByStatusOrderByCreatedAtDesc(MaterialStatus.PENDING);
        List<MaterialOrder> all = materialOrderRepository.findAllByOrderByCreatedAtDesc();
        model.addAttribute("pendingOrders", pending);
        model.addAttribute("allOrders", all);
        return "admin-orders";
    }

    @PostMapping("/{id}/approve")
    public String approveOrder(@PathVariable Long id) {
        MaterialOrder order = materialOrderRepository.findById(id).orElseThrow();
        order.setStatus(MaterialStatus.APPROVED);
        materialOrderRepository.save(order);
        return "redirect:/admin/orders";
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> exportOrderPdf(@PathVariable Long id) throws Exception {
        MaterialOrder order = materialOrderRepository.findById(id).orElseThrow();
        Company company = companyRepository.findFirstByOrderByIdAsc().orElse(new Company());

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            PDRectangle rect = page.getMediaBox();
            float margin = 50;

            PDPageContentStream cs = new PDPageContentStream(doc, page);

            // Try to draw logo if exists at static/images/logo.png
            try {
                ClassPathResource logoRes = new ClassPathResource("static/images/logo.png");
                if (logoRes.exists()) {
                    try (InputStream is = logoRes.getInputStream()) {
                        PDImageXObject img = PDImageXObject.createFromByteArray(doc, is.readAllBytes(), "logo");
                        float imgWidth = 120;
                        float imgHeight = img.getHeight() * (imgWidth / img.getWidth());
                        cs.drawImage(img, rect.getLowerLeftX() + margin, rect.getUpperRightY() - margin - imgHeight, imgWidth, imgHeight);
                    }
                }
            } catch (Exception ignore) {}

            // Header text
            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA_BOLD, 18);
            cs.newLineAtOffset(margin, rect.getUpperRightY() - margin - 20);
            String header = (company.getCompanyName() != null && !company.getCompanyName().isBlank())
                    ? company.getCompanyName() + " – Materialeliste"
                    : "Materialeliste";
            cs.showText(header);
            cs.endText();

            float y = rect.getUpperRightY() - margin - 60;

            // Order details
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            cs.setFont(PDType1Font.HELVETICA, 12);
            y = writeLine(cs, margin, y, "Bestilling ID: " + order.getId());
            y = writeLine(cs, margin, y, "Oprettet: " + (order.getCreatedAt() != null ? fmt.format(order.getCreatedAt()) : "-"));
            y = writeLine(cs, margin, y, "Status: " + (order.getStatus() != null ? order.getStatus().name() : "PENDING"));
            y = writeLine(cs, margin, y, "Bruger: " + (order.getUser() != null ? order.getUser().getEmail() : "Ukendt"));

            y = writeLine(cs, margin, y, "");
            cs.setFont(PDType1Font.HELVETICA_BOLD, 14);
            y = writeLine(cs, margin, y, "Materialer/Detaljer:");
            cs.setFont(PDType1Font.HELVETICA, 12);
            y = writeParagraph(cs, margin, y, order.getDescription() != null ? order.getDescription() : "Ingen beskrivelse");

            cs.close();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            byte[] bytes = baos.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "materialeliste-" + order.getId() + ".pdf");
            return ResponseEntity.ok().headers(headers).body(bytes);
        }
    }

    private float writeLine(PDPageContentStream cs, float x, float y, String text) throws Exception {
        cs.beginText();
        cs.newLineAtOffset(x, y);
        cs.showText(text != null ? text : "");
        cs.endText();
        return y - 18;
    }

    private float writeParagraph(PDPageContentStream cs, float x, float y, String text) throws Exception {
        if (text == null) text = "";
        int maxCharsPerLine = 90;
        for (int i = 0; i < text.length(); i += maxCharsPerLine) {
            int end = Math.min(text.length(), i + maxCharsPerLine);
            y = writeLine(cs, x, y, text.substring(i, end));
        }
        return y;
    }
}
