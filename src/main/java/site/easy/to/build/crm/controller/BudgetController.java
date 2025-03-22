package site.easy.to.build.crm.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.User;
import site.easy.to.build.crm.service.budget.BudgetService;
import site.easy.to.build.crm.service.customer.CustomerService;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/customers")
public class BudgetController {

    private final BudgetService budgetService;
    private final CustomerService customerService;
    private final HttpSession session;

    @Autowired
    public BudgetController(BudgetService budgetService, CustomerService customerService, HttpSession session) {
        this.budgetService = budgetService;
        this.customerService = customerService;
        this.session = session;
    }

    // GET /customers/{id}/budgets - Show budgets for a specific customer
    @GetMapping("/{id}/budgets")
    public String showBudgetsByCustomer(@PathVariable("id") Integer customerId, Model model) {
        List<Budget> budgets = budgetService.findByCustomerId(customerId);
        Customer customer = customerService.findByCustomerId(customerId);
        
        model.addAttribute("budgets", budgets);
        model.addAttribute("customer", customer);
        return "budgets/list"; // Thymeleaf template: budgets/list.html
    }

    // GET /customers/budgets - Show all budgets (across all customers)
    @GetMapping("/budgets")
    public String showAllBudgets(Model model) {
        List<Budget> budgets = budgetService.findAll();
        model.addAttribute("budgets", budgets);
        return "budgets/all"; // Thymeleaf template: budgets/all.html
    }

    // GET /customers/{id}/budgets/create - Show form to create a new budget
    @GetMapping("/{id}/budgets/create")
    public String showCreateBudgetForm(@PathVariable("id") Integer customerId, Model model) {
        Customer customer = customerService.findByCustomerId(customerId);
        
        Budget budget = new Budget();
        budget.setCustomer(customer);
        
        model.addAttribute("budget", budget);
        model.addAttribute("customer", customer);
        return "budgets/create"; // Thymeleaf template: budgets/create.html
    }

    // POST /customers/{id}/budgets/create - Process budget creation
    @PostMapping("/{id}/budgets/create")
    public String createBudget(@PathVariable("id") Integer customerId, 
                              @ModelAttribute("budget") Budget budget, 
                              Authentication authentication) {
        // Get the currently logged-in user from session (set in CrmUserDetails)
        Integer loggedInUserId = (Integer) session.getAttribute("loggedInUserId");
        if (loggedInUserId == null) {
            throw new IllegalStateException("No logged-in user found");
        }

        // Fetch the User entity (assuming a UserService exists)
        User createdBy = new User(); // Placeholder; replace with actual UserService call
        createdBy.setId(loggedInUserId); // Set the ID from session

        // Set budget properties
        Customer customer = customerService.findByCustomerId(customerId);
        budget.setCustomer(customer);
        budget.setCreatedBy(createdBy);
        budget.setCreatedAt(LocalDate.now()); // Ensure created_at is set

        // Save the budget
        budgetService.createBudget(budget);

        // Redirect to the customer's budgets list
        return "redirect:/customers/" + customerId + "/budgets";
    }
}