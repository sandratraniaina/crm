package site.easy.to.build.crm.controller;

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
    public String exportCustomer(@PathVariable("id") Integer customerId, RedirectAttributes redirectAttributes) {
        // Call export service in here.
        try {
            customerExportService.getCsv(customerId);
            redirectAttributes.addFlashAttribute("message", "CSV exported successfully");
            return "redirect:/employee/customer/manager/all-customers";
        } catch (Exception e) {
            redirectAttributes.addAttribute("error", "There was an error");
            return "redirect:/employee/customer/manager/all-customers";
        }

    }
}
