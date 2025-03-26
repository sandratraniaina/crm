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

    private static final String NEW_DATA_PREFIX = "\"copy";
    private static final String DATA_SEPARATOR = "\",\"";

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

        StringBuilder data = new StringBuilder();
        data.append(getCustomerHeader());
        data.append(customerToCsvData(customer));
        data.append("\n");

        data.append(getBudgetHeader());
        for (Budget budget : budgets) {
            data.append(budgetToCsvData(budget));
        }

        data.append("\n");

        data.append(getLeadExpenseHeader());
        for (TicketExpense ticketExpense : ticketExpenses) {
            data.append(ticketExpenseToCsvData(ticketExpense));
        }

        for (LeadExpense leadExpense : leadExpenses) {
            data.append(leadExpenseToCsvData(leadExpense));
        }

        return data.toString();
    }

    public String customerToCsvData(Customer customer) {
        return NEW_DATA_PREFIX + customer.getEmail() + "\"," + NEW_DATA_PREFIX + customer.getName() + "\"\n";
    }

    public String budgetToCsvData(Budget budget) {
        String email = budget.getCustomer().getEmail();
        return email + ",\"" + budget.getAmount() + "\"\n";
    }

    public String leadExpenseToCsvData(LeadExpense leadExpense) {
        String email = leadExpense.getLead().getCustomer().getEmail();
        String name = leadExpense.getLead().getName();
        String type = "lead";
        String status = leadExpense.getLead().getStatus();
        String value = leadExpense.getAmount().toString();
        return NEW_DATA_PREFIX + email + DATA_SEPARATOR + name + DATA_SEPARATOR + type + DATA_SEPARATOR + status + DATA_SEPARATOR + value + "\"\n";
    }

    public String ticketExpenseToCsvData(TicketExpense ticketExpense) {
        String email = ticketExpense.getTicket().getCustomer().getEmail();
        String subject = ticketExpense.getTicket().getSubject();
        String type = "ticket";
        String status = ticketExpense.getTicket().getStatus();
        String value = ticketExpense.getAmount().toString();
        return NEW_DATA_PREFIX + email + DATA_SEPARATOR + subject + DATA_SEPARATOR + type + DATA_SEPARATOR + status + DATA_SEPARATOR + value + "\"\n";
    }

    public String getCustomerHeader() {
        return "\"customer_email\",\"customer_name\"\n";
    }

    public String getLeadExpenseHeader() {
        return "\"customer_email\",\"subject_or_name\",\"type\",\"status\",\"expense\"\n";
    }

    public String getBudgetHeader() {
        return "\"customer_email\",\"Budget\"\n";
    }
}
