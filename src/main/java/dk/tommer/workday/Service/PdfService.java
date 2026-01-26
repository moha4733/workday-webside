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
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

            // Simple PDF generation without complex try-with-resources issues
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            
            try {
                // Title
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Godkendte Timer Rapport");
                contentStream.endText();
                
                yPosition -= 30;
                
                // Date range
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Periode: " + startDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + 
                                 " til " + endDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                contentStream.endText();
                
                yPosition -= 40;
                
                if (approvedWorkLogs.isEmpty()) {
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText("Ingen godkendte timer fundet i den valgte periode.");
                    contentStream.endText();
                } else {
                    // Group by user
                    Map<String, List<WorkLog>> workLogsByUser = approvedWorkLogs.stream()
                        .collect(Collectors.groupingBy(wl -> wl.getUser().getName()));
                    
                    for (Map.Entry<String, List<WorkLog>> entry : workLogsByUser.entrySet()) {
                        String userName = entry.getKey();
                        List<WorkLog> userWorkLogs = entry.getValue();
                        
                        // User name header
                        yPosition -= 25;
                        contentStream.beginText();
                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                        contentStream.newLineAtOffset(margin, yPosition);
                        contentStream.showText("Medarbejder: " + userName);
                        contentStream.endText();
                        
                        yPosition -= 20;
                        
                        // Table headers
                        contentStream.beginText();
                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
                        contentStream.newLineAtOffset(margin, yPosition);
                        contentStream.showText("Dato");
                        contentStream.endText();
                        
                        contentStream.beginText();
                        contentStream.newLineAtOffset(margin + 80, yPosition);
                        contentStream.showText("Projekt");
                        contentStream.endText();
                        
                        contentStream.beginText();
                        contentStream.newLineAtOffset(margin + 200, yPosition);
                        contentStream.showText("Timer");
                        contentStream.endText();
                        
                        yPosition -= 15;
                        
                        // Work log entries
                        double totalHours = 0;
                        for (WorkLog workLog : userWorkLogs) {
                            if (yPosition < 100) {
                                // Close current stream and create new page
                                contentStream.close();
                                page = new PDPage(PDRectangle.A4);
                                document.addPage(page);
                                rect = page.getMediaBox();
                                yPosition = rect.getUpperRightY() - margin;
                                contentStream = new PDPageContentStream(document, page);
                            }
                            
                            contentStream.beginText();
                            contentStream.setFont(PDType1Font.HELVETICA, 10);
                            contentStream.newLineAtOffset(margin, yPosition);
                            contentStream.showText(workLog.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                            contentStream.endText();
                            
                            contentStream.beginText();
                            contentStream.newLineAtOffset(margin + 80, yPosition);
                            contentStream.showText(workLog.getProject().getName());
                            contentStream.endText();
                            
                            contentStream.beginText();
                            contentStream.newLineAtOffset(margin + 200, yPosition);
                            contentStream.showText(String.valueOf(workLog.getHours()));
                            contentStream.endText();
                            
                            totalHours += workLog.getHours();
                            yPosition -= 15;
                        }
                        
                        // Total for user
                        yPosition -= 10;
                        contentStream.beginText();
                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                        contentStream.newLineAtOffset(margin, yPosition);
                        contentStream.showText("Total timer for " + userName + ": " + String.format("%.1f", totalHours));
                        contentStream.endText();
                        
                        yPosition -= 30;
                    }
                    
                    // Grand total
                    double grandTotal = approvedWorkLogs.stream()
                        .mapToDouble(WorkLog::getHours)
                        .sum();
                    
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText("Samlet total: " + String.format("%.1f", grandTotal) + " timer");
                    contentStream.endText();
                }
            } finally {
                contentStream.close();
            }
            
            document.save(outputStream);
            return outputStream.toByteArray();
        }
    }
}
