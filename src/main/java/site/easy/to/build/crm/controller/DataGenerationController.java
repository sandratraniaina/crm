package site.easy.to.build.crm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import site.easy.to.build.crm.service.generation.DataGenerationService;

import site.easy.to.build.crm.dto.GenerationRequest;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.expense.LeadExpense;
import site.easy.to.build.crm.entity.expense.TicketExpense;

@Controller
@RequestMapping("/data-generation")
@PreAuthorize("hasRole('ROLE_MANAGER')")
public class DataGenerationController {

    private final DataGenerationService dataGenerationService;

    @Autowired
    public DataGenerationController(DataGenerationService dataGenerationService) {
        this.dataGenerationService = dataGenerationService;
    }

    @GetMapping
    public String showDataGenerationForm(Model model) {
        model.addAttribute("generationRequest", new GenerationRequest());
        return "data-generation/generate";
    }

    @PostMapping
    public String generateData(@ModelAttribute("generationRequest") GenerationRequest generationRequest,
                              RedirectAttributes redirectAttributes) {
        try {
            if (generationRequest.getCustomerCount() > 0) {
                List<Customer> customers = dataGenerationService.generateCustomers(generationRequest.getCustomerCount());
                dataGenerationService.saveCustomers(customers);
            }
            if (generationRequest.getBudgetCount() > 0) {
                List<Budget> budgets = dataGenerationService.generateBudgets(generationRequest.getBudgetCount());
                dataGenerationService.saveBudgets(budgets);
            }
            if (generationRequest.getTicketExpenseCount() > 0) {
                List<TicketExpense> ticketExpenses = dataGenerationService.generateTicketExpenses(generationRequest.getTicketExpenseCount());
                dataGenerationService.saveTicketExpenses(ticketExpenses);
            }
            if (generationRequest.getLeadExpenseCount() > 0) {
                List<LeadExpense> leadExpenses = dataGenerationService.generateLeadExpenses(generationRequest.getLeadExpenseCount());
                dataGenerationService.saveLeadExpenses(leadExpenses);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Data generated and saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error during data generation or saving: " + e.getMessage());
        }
        return "redirect:/data-generation";
    }
}