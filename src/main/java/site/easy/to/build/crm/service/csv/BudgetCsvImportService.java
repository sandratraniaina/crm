package site.easy.to.build.crm.service.csv;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.Data;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.exception.InvalidCsvFormatException;
import site.easy.to.build.crm.service.budget.BudgetService;
import site.easy.to.build.crm.service.customer.CustomerServiceImpl;

@Service
@Data
public class BudgetCsvImportService {
    private String filename;
    private Iterable<CSVRecord> budgetRecords;
    private List<Budget> customerBudgets = new ArrayList<>();
    private List<InvalidCsvFormatException> exceptions = new ArrayList<>();

    private CustomerCsvImportService customerCsvImportService;

    private final CustomerServiceImpl customerService;
    private final BudgetService customerBudgetService;

    /* ------------------------------ Constructors ------------------------------ */

    @Autowired
    public BudgetCsvImportService(CustomerServiceImpl customerService, BudgetService customerBudgetService) {
        this.customerService = customerService;
        this.customerBudgetService = customerBudgetService;
    }

    public BudgetCsvImportService(String filename,
            Iterable<CSVRecord> budgetRecords,
            CustomerCsvImportService customerCsvImportService,
            CustomerServiceImpl customerService,
            BudgetService customerBudgetService) {

        this.filename = filename;
        this.budgetRecords = budgetRecords;
        this.customerCsvImportService = customerCsvImportService;
        this.customerService = customerService;
        this.customerBudgetService = customerBudgetService;
    }

    /* --------------------------- Processing methods --------------------------- */

    private Customer getDbCustomer(Budget customerBudget) {
        String email = customerBudget.getCustomer().getEmail();
        return this.customerCsvImportService.getCustomerByEmail(email);
    }

    public void save() {
        if (!customerCsvImportService.hasError()) {
            List<Budget> budgets = new ArrayList<>();
            for (Budget customerBudget : customerBudgets) {
                Customer customer = getDbCustomer(customerBudget);
                LocalDate today = LocalDate.now();
                Budget budget = new Budget(null, customerBudget.getAmount(), "Default Description", today, customer, null);
                customerBudgetService.createBudget(budget);
            }
            this.setCustomerBudgets(budgets);
        }
    }

    public void processBudgetCsv() {
        int lineNumber = 1;
        for (CSVRecord budgetRecord : budgetRecords) {
            if (lineNumber > 1) { // Skip header
                customerBudgets.add(parseToCustomerBudget(budgetRecord, lineNumber));
            }
            lineNumber++;
        }
    }

    /* --------------------------- Validation methods --------------------------- */

    public boolean hasError() {
        return !exceptions.isEmpty();
    }

    /* ----------------------------- Parsing methods ---------------------------- */

    private Budget parseToCustomerBudget(CSVRecord budgetRecord, int lineNumber) {
        Budget customerBudget = new Budget();
        String email = budgetRecord.get(0);
        String strBudget = budgetRecord.get(1);

        if (!CsvValidationUtils.isValidEmail(email)) {
            exceptions.add(new InvalidCsvFormatException(filename, lineNumber, "Invalid email format provided"));
        }

        if (!customerCsvImportService.emailExists(email)) {
            exceptions.add(new InvalidCsvFormatException(filename, lineNumber,
                    "The customer with the provided email does not exist"));
        }

        double amount = CsvValidationUtils.parseAmount(strBudget, "0");
        if (!CsvValidationUtils.isValidAmount(strBudget)) {
            exceptions.add(new InvalidCsvFormatException(filename, lineNumber, "Invalid budget value provided"));
        }

        Customer customer = new Customer();
        customer.setEmail(email);

        LocalDate today = LocalDate.now();
        customerBudget.setCustomer(customer);
        customerBudget.setAmount(BigDecimal.valueOf(amount));
        customerBudget.setCreatedAt(today);

        return customerBudget;
    }
}