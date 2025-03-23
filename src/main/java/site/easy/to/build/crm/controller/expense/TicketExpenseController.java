package site.easy.to.build.crm.controller.expense;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.entity.User;
import site.easy.to.build.crm.entity.expense.TicketExpense;
import site.easy.to.build.crm.service.expense.ExpenseThresholdService;
import site.easy.to.build.crm.service.expense.TicketExpenseService;
import site.easy.to.build.crm.service.ticket.TicketService;
import site.easy.to.build.crm.service.user.UserService;
import site.easy.to.build.crm.util.AuthenticationUtils;

import java.time.LocalDate;

@Controller
@RequestMapping("/tickets")
@PreAuthorize("hasRole('ROLE_MANAGER')")
public class TicketExpenseController {

    private final TicketExpenseService ticketExpenseService;
    private final TicketService ticketService;
    private final UserService userService;
    private final AuthenticationUtils authenticationUtils;
    private final ExpenseThresholdService expenseThresholdService;

    @Autowired
    public TicketExpenseController(TicketExpenseService ticketExpenseService, TicketService ticketService,
            UserService userService, AuthenticationUtils authenticationUtils,
            HttpSession session, ExpenseThresholdService expenseThresholdService) {
        this.ticketExpenseService = ticketExpenseService;
        this.ticketService = ticketService;
        this.userService = userService;
        this.authenticationUtils = authenticationUtils;
        this.expenseThresholdService = expenseThresholdService;
    }

    @GetMapping("/{id}/expenses")
    public String showTicketExpenses(@PathVariable("id") Integer ticketId, Model model) {
        Ticket ticket = ticketService.findByTicketId(ticketId);

        model.addAttribute("expenses", ticket.getTicketExpenses());
        model.addAttribute("ticket", ticket);
        return "expenses/ticket/list";
    }

    @GetMapping("/{id}/expenses/form")
    public String showExpenseForm(@PathVariable("id") Integer ticketId,
            @RequestParam(value = "expenseId", required = false) Integer expenseId,
            Model model) {
        Ticket ticket = ticketService.findByTicketId(ticketId);

        Customer customer = ticket.getCustomer();
        model.addAttribute("warning", expenseThresholdService.isThresholdExceeded(customer));

        TicketExpense expense;
        if (expenseId != null) {
            expense = ticketExpenseService.findById(expenseId);
        } else {
            expense = new TicketExpense();
            expense.setTicket(ticket);
        }

        model.addAttribute("expense", expense);
        model.addAttribute("ticket", ticket);
        return "expenses/ticket/form";
    }

    @PostMapping("/{id}/expenses/save")
    public String saveTicketExpense(@PathVariable("id") Integer ticketId,
            @Valid @ModelAttribute("expense") TicketExpense expense,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            @RequestParam(name = "confirm", required = false) Boolean confirm,
            Model model) {
        if (bindingResult.hasErrors()) {
            Ticket ticket = ticketService.findByTicketId(ticketId);
            model.addAttribute("ticket", ticket);
            return "expenses/ticket/form";
        }

        try {
            int loggedInUserId = authenticationUtils.getLoggedInUserId(authentication);
            if (loggedInUserId == -1) {
                throw new IllegalStateException("No logged-in user found");
            }

            User createdBy = userService.findById(loggedInUserId);
            if (createdBy == null) {
                throw new IllegalStateException("Logged-in user not found in database");
            }

            Ticket ticket = ticketService.findByTicketId(ticketId);
            expense.setTicket(ticket);
            expense.setCreatedBy(createdBy);
            if (expense.getId() == null) { // New expense
                expense.setCreatedAt(LocalDate.now().atStartOfDay());
            }

            boolean excedeedBudget = expenseThresholdService.isBudgetExceeded(expense.getTicket().getCustomer(),
                    expense.getAmount());

            if (excedeedBudget && confirm == null) {
                // IMplement somethign
                model.addAttribute("ticket", ticket);
                model.addAttribute("expense", expense);
                model.addAttribute("confirmationMessage", "Customer budget will be exceeded, are you sure to confirm?");
                return "expenses/ticket/form";
            }

            ticketExpenseService.createTicketExpense(expense); // Use create for both save and update
            redirectAttributes.addFlashAttribute("successMessage",
                    "Ticket expense " + (expense.getId() == null ? "created" : "updated") + " successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("expense", expense);
            return "redirect:/tickets/" + ticketId + "/expenses/form" +
                    (expense.getId() != null ? "?expenseId=" + expense.getId() : "");
        }

        return "redirect:/tickets/" + ticketId + "/expenses";
    }

    @GetMapping("/{id}/expenses/{expenseId}/delete")
    public String deleteTicketExpense(@PathVariable("id") Integer ticketId,
            @PathVariable("expenseId") Integer expenseId,
            RedirectAttributes redirectAttributes) {
        try {
            ticketExpenseService.deleteTicketExpense(expenseId);
            redirectAttributes.addFlashAttribute("successMessage", "Ticket expense deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting ticket expense: " + e.getMessage());
        }

        return "redirect:/tickets/" + ticketId + "/expenses";
    }
}