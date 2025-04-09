package site.easy.to.build.crm.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid; // Add this import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; // Add this import
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.User;
import site.easy.to.build.crm.service.budget.BudgetService;
import site.easy.to.build.crm.service.customer.CustomerService;
import site.easy.to.build.crm.service.user.UserService;
import site.easy.to.build.crm.util.AuthenticationUtils;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/customers")
@PreAuthorize("hasRole('ROLE_MANAGER')")
public class BudgetController {

    private final BudgetService budgetService;
    private final CustomerService customerService;
    private final UserService userService;
    private final AuthenticationUtils authenticationUtils;

    @Autowired
    public BudgetController(BudgetService budgetService, CustomerService customerService,
            UserService userService, AuthenticationUtils authenticationUtils,
            HttpSession session) {
        this.budgetService = budgetService;
        this.customerService = customerService;
        this.userService = userService;
        this.authenticationUtils = authenticationUtils;
    }

    @GetMapping("/{id}/budgets")
    public String showBudgetsByCustomer(@PathVariable("id") Integer customerId, Model model) {
        List<Budget> budgets = budgetService.findByCustomerId(customerId);
        Customer customer = customerService.findByCustomerId(customerId);

        model.addAttribute("budgets", budgets);
        model.addAttribute("customer", customer);
        return "budgets/list";
    }

    @GetMapping("/budgets")
    public String showAllBudgets(Model model) {
        List<Budget> budgets = budgetService.findAll();
        model.addAttribute("budgets", budgets);
        return "budgets/all";
    }

    @GetMapping("/{id}/budgets/create")
    public String showCreateBudgetForm(@PathVariable("id") Integer customerId, Model model) {
        Customer customer = customerService.findByCustomerId(customerId);

        Budget budget = new Budget();
        budget.setCustomer(customer);

        model.addAttribute("budget", budget);
        model.addAttribute("customer", customer);
        return "budgets/create";
    }

    @PostMapping("/{id}/budgets/create")
    public String createBudget(@PathVariable("id") Integer customerId,
            @Valid @ModelAttribute("budget") Budget budget, // Add @Valid here
            BindingResult bindingResult, // Add BindingResult to capture validation errors
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) { // Add Model for returning to the form
        // Check for validation errors first
        if (bindingResult.hasErrors()) {
            // Add customer to the model so the form can render again
            Customer customer = customerService.findByCustomerId(customerId);
            model.addAttribute("customer", customer);
            return "budgets/create"; // Return to the form with field errors
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

            Customer customer = customerService.findByCustomerId(customerId);
            budget.setCustomer(customer);
            budget.setCreatedBy(createdBy);
            budget.setCreatedAt(LocalDate.now());

            budgetService.createBudget(budget);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("budget", budget);
            return "redirect:/customers/" + customerId + "/budgets/create";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Budget added successfully");
        return "redirect:/customers/" + customerId + "/budgets";
    }

    @GetMapping("/{id}/budgets/{budgetId}/delete")
    public String deleteBudget(@PathVariable("id") Integer customerId, @PathVariable("budgetId") Integer budgetId,
            RedirectAttributes redirectAttributes) {
        try {
            budgetService.deleteBudget(budgetId);
            redirectAttributes.addFlashAttribute("successMessage", "Budget deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "There was an error while deleting the budget");
        }

        return "redirect:/customers/" + customerId + "/budgets";
    }
}