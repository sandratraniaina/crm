package site.easy.to.build.crm.service.generation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.expense.LeadExpense;
import site.easy.to.build.crm.entity.expense.TicketExpense;
import site.easy.to.build.crm.service.budget.BudgetService;
import site.easy.to.build.crm.service.customer.CustomerService;
import site.easy.to.build.crm.service.expense.LeadExpenseService;
import site.easy.to.build.crm.service.expense.TicketExpenseService;

@Service
public class DataGenerationService {

    private final FakeDataService fakeDataService;
    private final CustomerService customerService;
    private final BudgetService budgetService;
    private final TicketExpenseService ticketExpenseService;
    private final LeadExpenseService leadExpenseService;

    @Autowired
    public DataGenerationService(FakeDataService fakeDataService, CustomerService customerService,
                                 BudgetService budgetService, TicketExpenseService ticketExpenseService,
                                 LeadExpenseService leadExpenseService) {
        this.fakeDataService = fakeDataService;
        this.customerService = customerService;
        this.budgetService = budgetService;
        this.ticketExpenseService = ticketExpenseService;
        this.leadExpenseService = leadExpenseService;
    }

    public List<Customer> generateCustomers(int count) {
        List<Customer> customers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            customers.add(fakeDataService.generateFakeCustomer());
        }
        return customers;
    }

    public void saveCustomers(List<Customer> customers) {
        for (Customer customer : customers) {
            customerService.save(customer);
        }
    }

    public List<Budget> generateBudgets(int count) {
        List<Budget> budgets = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            budgets.add(fakeDataService.generateFakeBudget());
        }
        return budgets;
    }

    public void saveBudgets(List<Budget> budgets) {
        for (Budget budget : budgets) {
            budgetService.createBudget(budget);
        }
    }

    public List<TicketExpense> generateTicketExpenses(int count) {
        List<TicketExpense> ticketExpenses = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ticketExpenses.add(fakeDataService.generateFakeTicketExpense());
        }
        return ticketExpenses;
    }

    public void saveTicketExpenses(List<TicketExpense> ticketExpenses) {
        for (TicketExpense ticketExpense : ticketExpenses) {
            ticketExpenseService.createTicketExpense(ticketExpense);
        }
    }

    public List<LeadExpense> generateLeadExpenses(int count) {
        List<LeadExpense> leadExpenses = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            leadExpenses.add(fakeDataService.generateFakeLeadExpense());
        }
        return leadExpenses;
    }

    public void saveLeadExpenses(List<LeadExpense> leadExpenses) {
        for (LeadExpense leadExpense : leadExpenses) {
            leadExpenseService.createLeadExpense(leadExpense);
        }
    }
}