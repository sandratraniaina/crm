package site.easy.to.build.crm.service.generation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.expense.LeadExpense;
import site.easy.to.build.crm.entity.expense.TicketExpense;

@Service
public class DataGenerationService {

    private final FakeDataService fakeDataService;

    @Autowired
    public DataGenerationService(FakeDataService fakeDataService) {
        this.fakeDataService = fakeDataService;
    }

    public List<Customer> generateCustomers(int count) {
        List<Customer> customers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            customers.add(fakeDataService.generateFakeCustomer());
        }
        return customers;
    }

    public List<Budget> generateBudgets(int count) {
        List<Budget> budgets = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            budgets.add(fakeDataService.generateFakeBudget());
        }
        return budgets;
    }

    public List<TicketExpense> generateTicketExpenses(int count) {
        List<TicketExpense> ticketExpenses = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ticketExpenses.add(fakeDataService.generateFakeTicketExpense());
        }
        return ticketExpenses;
    }

    public List<LeadExpense> generateLeadExpenses(int count) {
        List<LeadExpense> leadExpenses = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            leadExpenses.add(fakeDataService.generateFakeLeadExpense());
        }
        return leadExpenses;
    }
}