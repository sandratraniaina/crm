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
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.User;
import site.easy.to.build.crm.entity.expense.LeadExpense;
import site.easy.to.build.crm.service.expense.LeadExpenseService;
import site.easy.to.build.crm.service.lead.LeadService;
import site.easy.to.build.crm.service.user.UserService;
import site.easy.to.build.crm.util.AuthenticationUtils;

import java.time.LocalDate;

@Controller
@RequestMapping("/leads")
@PreAuthorize("hasRole('ROLE_MANAGER')")
public class LeadExpenseController {

    private final LeadExpenseService leadExpenseService;
    private final LeadService leadService;
    private final UserService userService;
    private final AuthenticationUtils authenticationUtils;

    @Autowired
    public LeadExpenseController(LeadExpenseService leadExpenseService, LeadService leadService,
                                 UserService userService, AuthenticationUtils authenticationUtils,
                                 HttpSession session) {
        this.leadExpenseService = leadExpenseService;
        this.leadService = leadService;
        this.userService = userService;
        this.authenticationUtils = authenticationUtils;
    }

    @GetMapping("/{id}/expenses")
    public String showLeadExpenses(@PathVariable("id") Integer leadId, Model model) {
        Lead lead = leadService.findByLeadId(leadId);

        model.addAttribute("expenses", lead.getLeadExpenses());
        model.addAttribute("lead", lead);
        return "expenses/lead/list";
    }

    @GetMapping("/{id}/expenses/form")
    public String showExpenseForm(@PathVariable("id") Integer leadId,
                                  @RequestParam(value = "expenseId", required = false) Integer expenseId,
                                  Model model) {
        Lead lead = leadService.findByLeadId(leadId);

        LeadExpense expense;
        if (expenseId != null) {
            expense = leadExpenseService.findById(expenseId);
        } else {
            expense = new LeadExpense();
            expense.setLead(lead);
        }

        model.addAttribute("expense", expense);
        model.addAttribute("lead", lead);
        return "expenses/lead/form";
    }

    @PostMapping("/{id}/expenses/save")
    public String saveLeadExpense(@PathVariable("id") Integer leadId,
                                  @Valid @ModelAttribute("expense") LeadExpense expense,
                                  BindingResult bindingResult,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        if (bindingResult.hasErrors()) {
            Lead lead = leadService.findByLeadId(leadId);
            model.addAttribute("lead", lead);
            return "expenses/lead/form";
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

            Lead lead = leadService.findByLeadId(leadId);
            expense.setLead(lead);
            expense.setCreatedBy(createdBy);
            if (expense.getId() == null) { // New expense
                expense.setCreatedAt(LocalDate.now().atStartOfDay());
            }

            leadExpenseService.createLeadExpense(expense); // Use create for both save and update
            redirectAttributes.addFlashAttribute("successMessage",
                    "Lead expense " + (expense.getId() == null ? "created" : "updated") + " successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("expense", expense);
            return "redirect:/leads/" + leadId + "/expenses/form" +
                    (expense.getId() != null ? "?expenseId=" + expense.getId() : "");
        }

        return "redirect:/leads/" + leadId + "/expenses";
    }

    @GetMapping("/{id}/expenses/{expenseId}/delete")
    public String deleteLeadExpense(@PathVariable("id") Integer leadId,
                                    @PathVariable("expenseId") Integer expenseId,
                                    RedirectAttributes redirectAttributes) {
        try {
            leadExpenseService.deleteLeadExpense(expenseId);
            redirectAttributes.addFlashAttribute("successMessage", "Lead expense deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting lead expense: " + e.getMessage());
        }

        return "redirect:/leads/" + leadId + "/expenses";
    }
}
