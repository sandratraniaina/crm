package site.easy.to.build.crm.service.csv;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.Data;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.entity.User;
import site.easy.to.build.crm.entity.expense.LeadExpense;
import site.easy.to.build.crm.entity.expense.TicketExpense;
import site.easy.to.build.crm.exception.InvalidCsvFormatException;
import site.easy.to.build.crm.service.customer.CustomerServiceImpl;
import site.easy.to.build.crm.service.expense.LeadExpenseService;
import site.easy.to.build.crm.service.expense.TicketExpenseService;
import site.easy.to.build.crm.service.lead.LeadServiceImpl;
import site.easy.to.build.crm.service.ticket.TicketServiceImpl;
import site.easy.to.build.crm.service.user.UserServiceImpl;

@Service
@Data
public class ExpenseCsvImportService {
    private String filename;
    private Iterable<CSVRecord> expenseRecords;
    private List<LeadExpense> leadExpenses = new ArrayList<>();
    private List<TicketExpense> ticketExpenses = new ArrayList<>();
    private List<InvalidCsvFormatException> exceptions = new ArrayList<>();

    private CustomerCsvImportService customerCsvImportService;

    private final UserServiceImpl userService;
    private final CustomerServiceImpl customerService;
    private final LeadServiceImpl leadService;
    private final TicketServiceImpl ticketService;
    private final LeadExpenseService leadExpenseService;
    private final TicketExpenseService ticketExpenseService;

    /* ------------------------------ Constructors ------------------------------ */

    @Autowired
    public ExpenseCsvImportService(
            UserServiceImpl userService,
            CustomerServiceImpl customerService,
            LeadServiceImpl leadService,
            TicketServiceImpl ticketService,
            LeadExpenseService leadExpenseService,
            TicketExpenseService ticketExpenseService) {

        this.userService = userService;
        this.customerService = customerService;
        this.leadService = leadService;
        this.ticketService = ticketService;
        this.leadExpenseService = leadExpenseService;
        this.ticketExpenseService = ticketExpenseService;
    }

    public ExpenseCsvImportService(
            String filename,
            Iterable<CSVRecord> expenseRecords,
            CustomerCsvImportService customerCsvImportService,
            UserServiceImpl userService,
            CustomerServiceImpl customerService,
            LeadServiceImpl leadService,
            TicketServiceImpl ticketService,
            LeadExpenseService leadExpenseService,
            TicketExpenseService ticketExpenseService) {

        this.filename = filename;
        this.expenseRecords = expenseRecords;
        this.customerCsvImportService = customerCsvImportService;
        this.userService = userService;
        this.customerService = customerService;
        this.leadService = leadService;
        this.ticketService = ticketService;
        this.leadExpenseService = leadExpenseService;
        this.ticketExpenseService = ticketExpenseService;
    }

    /* --------------------------- Processing methods --------------------------- */

    public void save(User createdBy) {
        if (customerCsvImportService.hasError() || hasError()) {
            return;
        }

        List<LeadExpense> savedLeadExpenses = new ArrayList<>();
        List<TicketExpense> savedTicketExpenses = new ArrayList<>();

        for (LeadExpense leadExpense : leadExpenses) {
            leadExpense.setCreatedBy(createdBy);
            leadExpense = leadExpenseService.createLeadExpense(leadExpense);
            savedLeadExpenses.add(leadExpense);
        }

        for (TicketExpense ticketExpense : ticketExpenses) {
            ticketExpense.setCreatedBy(createdBy);
            ticketExpense = ticketExpenseService.createTicketExpense(ticketExpense);
            savedTicketExpenses.add(ticketExpense);
        }

        this.setLeadExpenses(savedLeadExpenses);
        this.setTicketExpenses(savedTicketExpenses);
    }

    public void processCustomerCsv() {
        int lineNumber = 1;
        for (CSVRecord expenseRecord : expenseRecords) {
            if (lineNumber > 1) {
                if (isLeadExpense(expenseRecord)) {
                    leadExpenses.add(parseToLeadExpense(expenseRecord, lineNumber));
                } else {
                    ticketExpenses.add(parseToTicketExpense(expenseRecord, lineNumber));
                }
            }
            lineNumber++;
        }
    }

    /* --------------------------- Validation methods --------------------------- */

    public boolean hasError() {
        return !exceptions.isEmpty();
    }

    private boolean isValidLeadStatus(String status) {
        List<String> validStatuses = Arrays.asList(
                "meeting-to-schedule",
                "scheduled",
                "archived",
                "success",
                "assign-to-sales");
        return status != null && !status.isEmpty() && validStatuses.contains(status.toLowerCase());
    }

    private boolean isValidTicketStatus(String status) {
        List<String> validStatuses = Arrays.asList("open",
                "assigned",
                "on-hold",
                "in-progress",
                "resolved",
                "closed",
                "reopened",
                "pending-customer-response",
                "escalated",
                "archived");
        return status != null && !status.isEmpty() && validStatuses.contains(status.toLowerCase());
    }

    /* ----------------------------- Parsing methods ---------------------------- */

    private LeadExpense parseToLeadExpense(CSVRecord expenseRecord, int lineNumber) {
        LeadExpense leadExpense = new LeadExpense();
        String email = expenseRecord.get(0);
        String label = expenseRecord.get(1);
        String status = expenseRecord.get(3);
        double amount = CsvValidationUtils.parseAmount(expenseRecord.get(4), "0");

        status = "meeting-to-schedule";

        if (!isValidLeadStatus(status)) {
            exceptions.add(new InvalidCsvFormatException(filename, lineNumber, "Invalid status value provided"));
        }

        validateCommonFields(expenseRecord, lineNumber);
        populateExpense(leadExpense, label, amount, status, email);
        return leadExpense;
    }

    private TicketExpense parseToTicketExpense(CSVRecord expenseRecord, int lineNumber) {
        TicketExpense ticketExpense = new TicketExpense();
        String email = expenseRecord.get(0);
        String label = expenseRecord.get(1);
        String status = expenseRecord.get(3);
        double amount = CsvValidationUtils.parseAmount(expenseRecord.get(4), "0");

        status = "open";

        if (!isValidTicketStatus(status)) {
            exceptions.add(new InvalidCsvFormatException(filename, lineNumber, "Invalid status value provided"));
        }

        validateCommonFields(expenseRecord, lineNumber);
        populateExpense(ticketExpense, label, amount, status, email);
        return ticketExpense;
    }

    /* ----------------------------- Helper methods ----------------------------- */

    private void validateCommonFields(CSVRecord expenseRecord, int lineNumber) {
        String email = expenseRecord.get(0);
        if (!CsvValidationUtils.isValidEmail(email)) {
            exceptions.add(new InvalidCsvFormatException(filename, lineNumber, "Invalid email format provided"));
        }

        if (!customerCsvImportService.emailExists(email)) {
            exceptions.add(new InvalidCsvFormatException(filename, lineNumber,
                    "The customer with the provided email does not exist"));
        }

        if (!CsvValidationUtils.isValidAmount(expenseRecord.get(4))) {
            exceptions.add(new InvalidCsvFormatException(filename, lineNumber, "Invalid amount value provided"));
        }
    }

    private void populateExpense(Object expense, String label, double amount, String status, String email) {
        User user = new User();
        Customer customer = new Customer();
        customer.setEmail(email);

        if (!customerCsvImportService.hasError()) {
            user = userService.findByUsername("aina").get(0);
            customer = customerService.findByEmail(email);  
        }

        if (expense instanceof LeadExpense leadExpense) {
            Lead lead = new Lead();
            lead.setName(label);
            lead.setStatus(status);
            lead.setPhone("Number");
            lead.setCreatedAt(LocalDateTime.now());
            lead.setCustomer(customer);
            lead.setManager(user);
            lead.setEmployee(user);

            if (!customerCsvImportService.hasError() && !hasError()) {
                lead.setCustomer(customer);
                lead = leadService.save(lead);
            }

            leadExpense.setDescription("Expense for " + label);
            leadExpense.setAmount(BigDecimal.valueOf(amount));
            leadExpense.setExpenseDate(LocalDate.now());
            leadExpense.setLead(lead);
        } else if (expense instanceof TicketExpense ticketExpense) {
            Ticket ticket = new Ticket();
            ticket.setSubject(label);
            ticket.setDescription("Default Description");
            ticket.setStatus(status);
            ticket.setPriority("low");
            ticket.setCreatedAt(LocalDateTime.now());
            ticket.setCustomer(customer);
            ticket.setManager(user);
            ticket.setEmployee(user);

            if (!customerCsvImportService.hasError() && !hasError()) {
                ticket.setCustomer(customer);
                ticket = ticketService.save(ticket);
            }

            ticketExpense.setDescription("Expense for " + label);
            ticketExpense.setAmount(BigDecimal.valueOf(amount));
            ticketExpense.setExpenseDate(LocalDate.now());
            ticketExpense.setTicket(ticket);
        }
    }

    private boolean isLeadExpense(CSVRecord expenseRecord) {
        return expenseRecord.get(2).equalsIgnoreCase("lead");
    }
}