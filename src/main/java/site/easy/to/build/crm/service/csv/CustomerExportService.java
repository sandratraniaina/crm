package site.easy.to.build.crm.service.csv;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.expense.LeadExpense;
import site.easy.to.build.crm.entity.expense.TicketExpense;
import site.easy.to.build.crm.service.budget.BudgetService;
import site.easy.to.build.crm.service.customer.CustomerService;
import site.easy.to.build.crm.service.expense.LeadExpenseService;
import site.easy.to.build.crm.service.expense.TicketExpenseService;

@Service
@Data
@RequiredArgsConstructor
public class CustomerExportService {
    private final CustomerService customerService;
    private final BudgetService budgetService;
    private final LeadExpenseService leadExpenseService;
    private final TicketExpenseService ticketExpenseService;

    public void getCsv(Integer customerId) {
        String data = generateCsvData(customerId);

        try (FileWriter writer = new FileWriter(
                "S:\\School\\S6\\Evaluation\\Projects\\crm\\.vscode\\data\\export.csv")) {
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String generateCsvData(Integer customerId) {
        Customer customer = customerService.findByCustomerId(customerId);

        List<Budget> budgets = budgetService.findByCustomerId(customerId);
        List<LeadExpense> leadExpenses = leadExpenseService.findByCustomerId(customerId);
        List<TicketExpense> ticketExpenses = ticketExpenseService.findByCustomerId(customerId);

        String data = getCustomerHeader();
        data += customerToCsvData(customer);
        data += getBudgetHeader();

        for (Budget budget : budgets) {
            data += budgetToCsvData(budget);
            data += "\n";
        }

        data += getLeadExpenseHeader();

        for (TicketExpense ticketExpense : ticketExpenses) {
            data += ticketExpenseToCsvData(ticketExpense);
        }
        for (LeadExpense leadExpense : leadExpenses) {
            data += leadExpenseToCsvData(leadExpense);
        }

        return data;
    }

    public String customerToCsvData(Customer customer) {
        return "copy_" + customer.getEmail() + "," + "copy_" + customer.getName() + "\n";
    }

    public String budgetToCsvData(Budget budget) {
        String email = budget.getCustomer().getEmail();
        return email + "," + budget.getAmount() + "\n";
    }

    public String leadExpenseToCsvData(LeadExpense leadExpense) {
        String email = leadExpense.getLead().getCustomer().getEmail();
        String name = leadExpense.getLead().getName();
        String type = "lead";
        String status = leadExpense.getLead().getStatus();
        String value = leadExpense.getAmount().toString();
        return email + "," + name + "," + type + "," + status + "," + value +"\n";
    }

    public String ticketExpenseToCsvData(TicketExpense ticketExpense) {
        String email = ticketExpense.getTicket().getCustomer().getEmail();
        String subject = ticketExpense.getTicket().getSubject();
        String type = "ticket";
        String status = ticketExpense.getTicket().getStatus();
        String value = ticketExpense.getAmount().toString();
        return email + "," + subject + "," + type + "," + status + "," + value + "\n";
    }

    public String getCustomerHeader() {
        return "customer_email,customer_name\n";
    }

    public String getLeadExpenseHeader() {
        return "customer_email,subject_or_name,type,status,expense\n";
    }

    public String getBudgetHeader() {
        return "customer_email,Budget\n";
    }
}
