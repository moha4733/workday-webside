package dk.tommer.workday.Service;

import dk.tommer.workday.entity.WorkLog;
import dk.tommer.workday.entity.WorkLogStatus;
import dk.tommer.workday.repository.WorkLogRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PdfService {

    @Autowired
    private WorkLogRepository workLogRepository;

    public byte[] generateApprovedWorkLogsPdf(LocalDate startDate, LocalDate endDate, Long userId) throws IOException {
        List<WorkLog> approvedWorkLogs;
        
        if (userId != null) {
            approvedWorkLogs = workLogRepository.findByUser_IdAndStatusAndDateBetweenOrderByDateAsc(
                userId, WorkLogStatus.APPROVED, startDate, endDate);
        } else {
            approvedWorkLogs = workLogRepository.findByStatusAndDateBetweenOrderByDateAsc(
                WorkLogStatus.APPROVED, startDate, endDate);
        }

        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            PDRectangle rect = page.getMediaBox();
            float margin = 50;
            float yPosition = rect.getUpperRightY() - margin;

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                
                // Try to add logo if exists
                try {
                    ClassPathResource logoRes = new ClassPathResource("static/images/logo.png");
                    if (logoRes.exists()) {
                        try (InputStream is = logoRes.getInputStream()) {
                            byte[] imageBytes = is.readAllBytes();
                            // Note: PDFBox 2.0.30 doesn't have createFromByteArray, using simpler approach
                            // For now, we'll skip logo and focus on better formatting
                        }
                    }
                } catch (Exception ignore) {}

                // Header with background
                contentStream.setLineWidth(1);
                contentStream.setStrokingColor(0.2f, 0.4f, 0.8f);
                contentStream.setNonStrokingColor(0.9f, 0.95f, 1.0f);
                contentStream.addRect(margin - 10, yPosition - 40, rect.getWidth() - 2 * margin + 20, 50);
                contentStream.fillAndStroke();
                
                // Title
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
                contentStream.setNonStrokingColor(0.1f, 0.1f, 0.1f);
                contentStream.newLineAtOffset(margin, yPosition - 15);
                contentStream.showText("GODKENDTE TIMER RAPPORT");
                contentStream.endText();
                
                // Subtitle with date range
                yPosition -= 60;
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.setNonStrokingColor(0.4f, 0.4f, 0.4f);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Periode: " + startDate.format(DateTimeFormatter.ofPattern("dd. MMMM yyyy")) + 
                                 " - " + endDate.format(DateTimeFormatter.ofPattern("dd. MMMM yyyy")));
                contentStream.endText();
                
                // Generated date
                yPosition -= 20;
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                contentStream.setNonStrokingColor(0.6f, 0.6f, 0.6f);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Genereret: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd. MMMM yyyy 'kl.' HH:mm")));
                contentStream.endText();
                
                yPosition -= 40;
                
                if (approvedWorkLogs.isEmpty()) {
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA, 14);
                    contentStream.setNonStrokingColor(0.5f, 0.5f, 0.5f);
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText("Ingen godkendte timer fundet i den valgte periode.");
                    contentStream.endText();
                } else {
                    // Summary box
                    double grandTotal = approvedWorkLogs.stream().mapToDouble(WorkLog::getHours).sum();
                    Map<String, List<WorkLog>> workLogsByUser = approvedWorkLogs.stream()
                        .collect(Collectors.groupingBy(wl -> wl.getUser().getName()));
                    
                    // Summary header
                    contentStream.setLineWidth(1);
                    contentStream.setStrokingColor(0.2f, 0.4f, 0.8f);
                    contentStream.setNonStrokingColor(0.95f, 0.98f, 1.0f);
                    contentStream.addRect(margin - 5, yPosition - 25, rect.getWidth() - 2 * margin + 10, 30);
                    contentStream.fillAndStroke();
                    
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                    contentStream.setNonStrokingColor(0.1f, 0.1f, 0.1f);
                    contentStream.newLineAtOffset(margin, yPosition - 8);
                    contentStream.showText("OVERSIGT");
                    contentStream.endText();
                    
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    contentStream.setNonStrokingColor(0.2f, 0.4f, 0.8f);
                    contentStream.newLineAtOffset(rect.getWidth() - margin - 120, yPosition - 8);
                    contentStream.showText("Samlet total: " + String.format("%.1f", grandTotal) + " timer");
                    contentStream.endText();
                    
                    yPosition -= 40;
                    
                    // Process each user
                    for (Map.Entry<String, List<WorkLog>> entry : workLogsByUser.entrySet()) {
                        String userName = entry.getKey();
                        List<WorkLog> userWorkLogs = entry.getValue();
                        
                        // Check if we need a new page
                        if (yPosition < 200) {
                            contentStream.close();
                            page = new PDPage(PDRectangle.A4);
                            document.addPage(page);
                            rect = page.getMediaBox();
                            yPosition = rect.getUpperRightY() - margin;
                            PDPageContentStream newContentStream = new PDPageContentStream(document, page);
                            contentStream = newContentStream;
                        }
                        
                        // User section header
                        contentStream.setLineWidth(0.5f);
                        contentStream.setStrokingColor(0.7f, 0.7f, 0.7f);
                        contentStream.moveTo(margin, yPosition);
                        contentStream.lineTo(rect.getWidth() - margin, yPosition);
                        contentStream.stroke();
                        
                        yPosition -= 25;
                        contentStream.beginText();
                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                        contentStream.setNonStrokingColor(0.2f, 0.4f, 0.8f);
                        contentStream.newLineAtOffset(margin, yPosition);
                        contentStream.showText(userName.toUpperCase());
                        contentStream.endText();
                        
                        // User total
                        double userTotal = userWorkLogs.stream().mapToDouble(WorkLog::getHours).sum();
                        contentStream.beginText();
                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                        contentStream.setNonStrokingColor(0.5f, 0.5f, 0.5f);
                        contentStream.newLineAtOffset(margin + 200, yPosition);
                        contentStream.showText(String.format("%.1f", userTotal) + " timer");
                        contentStream.endText();
                        
                        yPosition -= 35;
                        
                        // Table headers
                        contentStream.setNonStrokingColor(0.9f, 0.9f, 0.9f);
                        contentStream.addRect(margin, yPosition - 15, rect.getWidth() - 2 * margin, 20);
                        contentStream.fill();
                        
                        contentStream.beginText();
                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
                        contentStream.setNonStrokingColor(0.3f, 0.3f, 0.3f);
                        contentStream.newLineAtOffset(margin + 10, yPosition - 5);
                        contentStream.showText("DATO");
                        contentStream.endText();
                        
                        contentStream.beginText();
                        contentStream.newLineAtOffset(margin + 100, yPosition - 5);
                        contentStream.showText("PROJEKT");
                        contentStream.endText();
                        
                        contentStream.beginText();
                        contentStream.newLineAtOffset(margin + 300, yPosition - 5);
                        contentStream.showText("TIMER");
                        contentStream.endText();
                        
                        yPosition -= 25;
                        
                        // Work log entries with alternating row colors
                        int rowNumber = 0;
                        for (WorkLog workLog : userWorkLogs) {
                            if (yPosition < 100) {
                                PDPageContentStream oldContentStream = contentStream;
                                contentStream.close();
                                page = new PDPage(PDRectangle.A4);
                                document.addPage(page);
                                rect = page.getMediaBox();
                                yPosition = rect.getUpperRightY() - margin;
                                PDPageContentStream newContentStream = new PDPageContentStream(document, page);
                                contentStream = newContentStream;
                            }
                            
                            // Alternating row background
                            if (rowNumber % 2 == 0) {
                                contentStream.setNonStrokingColor(0.98f, 0.98f, 0.98f);
                                contentStream.addRect(margin, yPosition - 12, rect.getWidth() - 2 * margin, 15);
                                contentStream.fill();
                            }
                            
                            contentStream.beginText();
                            contentStream.setFont(PDType1Font.HELVETICA, 10);
                            contentStream.setNonStrokingColor(0.3f, 0.3f, 0.3f);
                            contentStream.newLineAtOffset(margin + 10, yPosition);
                            contentStream.showText(workLog.getDate().format(DateTimeFormatter.ofPattern("dd. MMMM yyyy")));
                            contentStream.endText();
                            
                            contentStream.beginText();
                            contentStream.newLineAtOffset(margin + 100, yPosition);
                            contentStream.showText(workLog.getProject().getName());
                            contentStream.endText();
                            
                            contentStream.beginText();
                            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
                            contentStream.newLineAtOffset(margin + 300, yPosition);
                            contentStream.showText(String.format("%.1f", workLog.getHours()));
                            contentStream.endText();
                            
                            yPosition -= 18;
                            rowNumber++;
                        }
                        
                        yPosition -= 20;
                    }
                    
                    // Footer
                    yPosition = margin + 30;
                    contentStream.setLineWidth(0.5f);
                    contentStream.setStrokingColor(0.7f, 0.7f, 0.7f);
                    contentStream.moveTo(margin, yPosition);
                    contentStream.lineTo(rect.getWidth() - margin, yPosition);
                    contentStream.stroke();
                    
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA, 8);
                    contentStream.setNonStrokingColor(0.6f, 0.6f, 0.6f);
                    contentStream.newLineAtOffset(margin, yPosition - 15);
                    contentStream.showText("Workday System - Automatisk genereret rapport");
                    contentStream.endText();
                }
            }
            
            document.save(outputStream);
            return outputStream.toByteArray();
        }
    }
}
