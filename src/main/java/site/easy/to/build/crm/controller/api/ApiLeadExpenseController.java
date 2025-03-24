package site.easy.to.build.crm.controller.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.expense.LeadExpense;
import site.easy.to.build.crm.service.expense.LeadExpenseService;
import site.easy.to.build.crm.service.lead.LeadService;
import site.easy.to.build.crm.util.response.Response;
import site.easy.to.build.crm.util.response.ResponseUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leads")
@PreAuthorize("hasRole('ROLE_MANAGER')")
@RequiredArgsConstructor
public class ApiLeadExpenseController {

    private final LeadExpenseService leadExpenseService;
    private final LeadService leadService;

    @GetMapping("/expenses")
    public ResponseEntity<Response<List<LeadExpense>>> getLeadExpenses() {
        return ResponseUtil.sendResponse(HttpStatus.OK, true, "Lead expenses fetch successfully", leadExpenseService.findAll());
    }

        @GetMapping
    public ResponseEntity<Response<List<Lead>>> getTickets() {
        return ResponseUtil.sendResponse(HttpStatus.OK, true, "Ticket fetchet successfully", leadService.findAll());
    }

    // GET /api/leads/{leadId}/expenses
    @GetMapping("/{leadId}/expenses")
    public ResponseEntity<Response<Map<String, Object>>> getLeadExpenses(@PathVariable("leadId") Integer leadId) {
        Lead lead = leadService.findByLeadId(leadId);
        List<LeadExpense> expenses = leadExpenseService.findByLeadId(leadId);
        Map<String, Object> responseData = new HashMap<>();

        responseData.put("lead", lead);
        responseData.put("expenses", expenses);
        return ResponseUtil.sendResponse(HttpStatus.OK, true, "Lead expenses retrieved successfully", responseData);
    }

    @PutMapping("/{leadId}/expenses/{expenseId}")
    public ResponseEntity<Response<Map<String, Boolean>>> updateLeadExpense(
            @PathVariable("leadId") Integer leadId,
            @PathVariable("expenseId") Integer expenseId,
            @RequestBody LeadExpense updatedExpense) {
        LeadExpense expense = leadExpenseService.findById(expenseId);
        if (expense == null || expense.getLead().getLeadId() != leadId) {
            return ResponseUtil.sendResponse(HttpStatus.NOT_FOUND, false, "Lead expense not found", null);
        }
        expense.setAmount(updatedExpense.getAmount());
        expense.setDescription(updatedExpense.getDescription());
        expense.setExpenseDate(updatedExpense.getExpenseDate());
        leadExpenseService.createLeadExpense(expense);
        return ResponseUtil.sendResponse(HttpStatus.OK, true, "Lead expense updated successfully", Map.of("success", true));
    }

    @DeleteMapping("/{leadId}/expenses/{expenseId}")
    public ResponseEntity<Response<Map<String, Boolean>>> deleteLeadExpense(
            @PathVariable("leadId") Integer leadId,
            @PathVariable("expenseId") Integer expenseId) {
        LeadExpense expense = leadExpenseService.findById(expenseId);
        if (expense == null || expense.getLead().getLeadId() != leadId) {
            return ResponseUtil.sendResponse(HttpStatus.NOT_FOUND, false, "Lead expense not found", null);
        }
        leadExpenseService.deleteLeadExpense(expenseId);
        return ResponseUtil.sendResponse(HttpStatus.OK, true, "Lead expense deleted successfully", Map.of("success", true));
    }
}