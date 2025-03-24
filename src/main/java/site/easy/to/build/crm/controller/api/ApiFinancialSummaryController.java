package site.easy.to.build.crm.controller.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.easy.to.build.crm.dto.FinancialSummaryDTO;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.CustomerFinancialSummary;
import site.easy.to.build.crm.service.customer.CustomerService;
import site.easy.to.build.crm.service.summary.FinancialSummaryService;
import site.easy.to.build.crm.util.response.Response;
import site.easy.to.build.crm.util.response.ResponseUtil;

@RestController
@RequestMapping("/api/summary")
@PreAuthorize("hasRole('ROLE_MANAGER')")
@RequiredArgsConstructor
public class ApiFinancialSummaryController {

    private final FinancialSummaryService financialSummaryService;
    private final CustomerService customerService;

    @GetMapping("/financial-summary")
    public ResponseEntity<Response<FinancialSummaryDTO>> getFinancialSummary() {
        return ResponseUtil.sendResponse(HttpStatus.OK, true, "Financial summary retrieved successfully", financialSummaryService.getFinancialSummary());
    }

    @GetMapping("/customer-financial-summary")
    public ResponseEntity<Response<List<CustomerFinancialSummary>>> getCustomerFinancialSummary() {
        List<CustomerFinancialSummary> list = new ArrayList<>();
        List<Customer> customers = customerService.findAll();
        for (Customer customer : customers) {
            list.add(customer.getFinancialSummary());
        }
        return ResponseUtil.sendResponse(HttpStatus.OK, true, "Customer financial summary ", list);
    }
}