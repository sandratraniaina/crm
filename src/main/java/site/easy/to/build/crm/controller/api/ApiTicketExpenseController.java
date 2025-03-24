package site.easy.to.build.crm.controller.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import site.easy.to.build.crm.entity.expense.TicketExpense;
import site.easy.to.build.crm.service.expense.TicketExpenseService;
import site.easy.to.build.crm.service.ticket.TicketService;
import site.easy.to.build.crm.util.response.Response;
import site.easy.to.build.crm.util.response.ResponseUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
@PreAuthorize("hasRole('ROLE_MANAGER')")
@RequiredArgsConstructor
public class ApiTicketExpenseController {

    private final TicketExpenseService ticketExpenseService;
    private final TicketService ticketService;

    @GetMapping("/expenses")
    public ResponseEntity<Response<List<TicketExpense>>> getTicketExpenses() {
        return ResponseUtil.sendResponse(HttpStatus.OK, true, "Ticket expenses retrieved successfully", ticketExpenseService.findAll());
    }

    @GetMapping("/{ticketId}/expenses")
    public ResponseEntity<Response<Map<String, Object>>> getTicketExpenses(@PathVariable("ticketId") Integer ticketId) {
        List<TicketExpense> expenses = ticketExpenseService.findByTicketId(ticketId);
        Map<String, Object> responseData = new HashMap<>();
        
        responseData.put("ticket", ticketService.findByTicketId(ticketId));
        responseData.put("expenses", expenses);
        return ResponseUtil.sendResponse(HttpStatus.OK, true, "Ticket expenses retrieved successfully", responseData);
    }

    @PutMapping("/{ticketId}/expenses/{expenseId}")
    public ResponseEntity<Response<Map<String, Boolean>>> updateTicketExpense(
            @PathVariable("ticketId") Integer ticketId,
            @PathVariable("expenseId") Integer expenseId,
            @RequestBody TicketExpense updatedExpense) {
        TicketExpense expense = ticketExpenseService.findById(expenseId);
        if (expense == null || expense.getTicket().getTicketId() != ticketId) {
            return ResponseUtil.sendResponse(HttpStatus.NOT_FOUND, false, "Ticket expense not found", null);
        }
        expense.setAmount(updatedExpense.getAmount());
        expense.setDescription(updatedExpense.getDescription());
        expense.setExpenseDate(updatedExpense.getExpenseDate());
        ticketExpenseService.createTicketExpense(expense);
        return ResponseUtil.sendResponse(HttpStatus.OK, true, "Ticket expense updated successfully", Map.of("success", true));
    }

    @DeleteMapping("/{ticketId}/expenses/{expenseId}")
    public ResponseEntity<Response<Map<String, Boolean>>> deleteTicketExpense(
            @PathVariable("ticketId") Integer ticketId,
            @PathVariable("expenseId") Integer expenseId) {
        TicketExpense expense = ticketExpenseService.findById(expenseId);
        if (expense == null || expense.getTicket().getTicketId() != ticketId) {
            return ResponseUtil.sendResponse(HttpStatus.NOT_FOUND, false, "Ticket expense not found", null);
        }
        ticketExpenseService.deleteTicketExpense(expenseId);
        return ResponseUtil.sendResponse(HttpStatus.OK, true, "Ticket expense deleted successfully", Map.of("success", true));
    }
}