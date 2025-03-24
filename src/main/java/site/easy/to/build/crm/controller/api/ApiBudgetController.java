package site.easy.to.build.crm.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.service.budget.BudgetService;
import site.easy.to.build.crm.service.customer.CustomerService;
import site.easy.to.build.crm.util.response.Response;
import site.easy.to.build.crm.util.response.ResponseUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@PreAuthorize("hasRole('ROLE_MANAGER')")
public class ApiBudgetController {

    private final BudgetService budgetService;
    private final CustomerService customerService;

    @Autowired
    public ApiBudgetController(BudgetService budgetService, CustomerService customerService) {
        this.budgetService = budgetService;
        this.customerService = customerService;
    }

    @GetMapping("/budgets")
    public ResponseEntity<Response<List<Budget>>> getCustomerBudgets() {
        List<Budget> budgets = budgetService.findAll();
        return ResponseUtil.sendResponse(HttpStatus.OK, true, "Customer budgets retrieved successfully", budgets);
    }

    // GET /api/customers/{customerId}/budget
    @GetMapping("/{customerId}/budget")
    public ResponseEntity<Response<Map<String, Object>>> getCustomerBudgets(@PathVariable("customerId") Integer customerId) {
        Customer customer = customerService.findByCustomerId(customerId);
        if (customer == null) {
            return ResponseUtil.sendResponse(HttpStatus.NOT_FOUND, false, "Customer not found", null);
        }
        List<Budget> budgets = budgetService.findByCustomerId(customerId);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("customer", customer);
        responseData.put("budgets", budgets);
        return ResponseUtil.sendResponse(HttpStatus.OK, true, "Customer budgets retrieved successfully", responseData);
    }

    @PutMapping("/{customerId}/budget/{budgetId}")
    public ResponseEntity<Response<Map<String, Boolean>>> updateCustomerBudget(
            @PathVariable("customerId") Integer customerId,
            @PathVariable("budgetId") Integer budgetId,
            @RequestBody Budget updatedBudget) {
        Budget budget = budgetService.findById(budgetId).orElseThrow();
        if (budget == null || !budget.getCustomer().getCustomerId().equals(customerId)) {
            return ResponseUtil.sendResponse(HttpStatus.NOT_FOUND, false, "Budget not found", null);
        }
        budget.setAmount(updatedBudget.getAmount());
        budget.setDescription(updatedBudget.getDescription());
        budgetService.createBudget(budget);
        return ResponseUtil.sendResponse(HttpStatus.OK, true, "Budget updated successfully", Map.of("success", true));
    }

    @DeleteMapping("/{customerId}/budget/{budgetId}")
    public ResponseEntity<Response<Map<String, Boolean>>> deleteCustomerBudget(
            @PathVariable("customerId") Integer customerId,
            @PathVariable("budgetId") Integer budgetId) {
        Budget budget = budgetService.findById(budgetId).orElseThrow();
        if (budget == null || !budget.getCustomer().getCustomerId().equals(customerId)) {
            return ResponseUtil.sendResponse(HttpStatus.NOT_FOUND, false, "Budget not found", null);
        }
        budgetService.deleteBudget(budgetId);
        return ResponseUtil.sendResponse(HttpStatus.OK, true, "Budget deleted successfully", Map.of("success", true));
    }
}