package site.easy.to.build.crm.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import site.easy.to.build.crm.service.csv.CustomerExportService;
import site.easy.to.build.crm.service.customer.CustomerService;

@Controller
@RequestMapping("/customers")
@Data
@RequiredArgsConstructor
public class CustomerDuplicationController {
    private final CustomerExportService customerExportService;
    private final CustomerService customerService;
    private final Gson gson = new Gson();

    @GetMapping("/{id}/export")
    public ResponseEntity<byte[]> exportCustomer(@PathVariable("id") Integer customerId,
            RedirectAttributes redirectAttributes) {
        try {
            // Generate CSV data as a string
            String csvData = customerExportService.generateCsvData(customerId);
            byte[] csvBytes = csvData.getBytes();

            // Set headers for file download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "customer_export_" + customerId + ".csv");
            headers.setContentLength(csvBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(csvBytes);
        } catch (Exception e) {
            redirectAttributes.addAttribute("error", "There was an error exporting the CSV");
            return ResponseEntity.status(500).body(null); // Or handle error differently
        }
    }
}