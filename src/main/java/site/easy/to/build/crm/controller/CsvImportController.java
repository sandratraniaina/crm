package site.easy.to.build.crm.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import site.easy.to.build.crm.entity.User;
import site.easy.to.build.crm.exception.InvalidCsvFormatException;
import site.easy.to.build.crm.service.budget.BudgetService;
import site.easy.to.build.crm.service.csv.BudgetCsvImportService;
import site.easy.to.build.crm.service.csv.CustomerCsvImportService;
import site.easy.to.build.crm.service.csv.ExpenseCsvImportService;
import site.easy.to.build.crm.service.customer.CustomerServiceImpl;
import site.easy.to.build.crm.service.expense.LeadExpenseService;
import site.easy.to.build.crm.service.expense.TicketExpenseService;
import site.easy.to.build.crm.service.lead.LeadServiceImpl;
import site.easy.to.build.crm.service.ticket.TicketServiceImpl;
import site.easy.to.build.crm.service.user.UserServiceImpl;
import site.easy.to.build.crm.util.AuthenticationUtils;

@Controller
@RequiredArgsConstructor
public class CsvImportController {
    private final TransactionTemplate transactionTemplate;

    private final UserServiceImpl userService;
    private final CustomerServiceImpl customerService;
    private final LeadServiceImpl leadService;
    private final TicketServiceImpl ticketService;
    private final BudgetService customerBudgetService;
    private final LeadExpenseService leadExpenseService;
    private final TicketExpenseService ticketExpenseService;
    private final AuthenticationUtils authenticationUtils;

    @GetMapping("/csv-import")
    public String displayCsvImportForm() {
        return "csv-import/index";
    }

    @PostMapping("/csv-import")
    public String processCsv(
            @RequestParam("customerCsvFile") MultipartFile customerCsv,
            @RequestParam("budgetCsvFile") MultipartFile budgetCsv,
            @RequestParam("expenseCsvFile") MultipartFile expenseCsv,
            RedirectAttributes redirectAttributes,
            Authentication authentication) {

        int loggedInUserId = authenticationUtils.getLoggedInUserId(authentication);
        if (loggedInUserId == -1) {
            throw new IllegalStateException("No logged-in user found");
        }

        User createdBy = userService.findById(loggedInUserId);

        return transactionTemplate.execute(status -> {
            boolean hasError = false;
            List<InvalidCsvFormatException> exceptions = new ArrayList<>();
            String pageRedirect = "redirect:/csv-import";

            if (customerCsv.isEmpty() || budgetCsv.isEmpty() || expenseCsv.isEmpty()) {
                hasError = true;
                redirectAttributes.addFlashAttribute("error",
                        "One or more files are missing. Please select all files to upload.");
            } else {
                try (
                        Reader customerReader = new BufferedReader(
                                new InputStreamReader(customerCsv.getInputStream()));
                        Reader budgetReader = new BufferedReader(
                                new InputStreamReader(budgetCsv.getInputStream()));
                        Reader expenseReader = new BufferedReader(
                                new InputStreamReader(expenseCsv.getInputStream()))) {

                    Iterable<CSVRecord> customerRecords = CSVFormat.DEFAULT.parse(customerReader);
                    Iterable<CSVRecord> budgetRecords = CSVFormat.DEFAULT.parse(budgetReader);
                    Iterable<CSVRecord> expenseRecords = CSVFormat.DEFAULT.parse(expenseReader);

                    CustomerCsvImportService customerCsvImportService = new CustomerCsvImportService(
                            customerCsv.getOriginalFilename(),
                            customerRecords,
                            customerService);
                    customerCsvImportService.processCustomerCsv();

                    if (customerCsvImportService.hasError()) {
                        hasError = true;
                        exceptions.addAll(customerCsvImportService.getExceptions());
                    } else {
                        customerCsvImportService.save();
                    }

                    BudgetCsvImportService budgetCsvImportService = new BudgetCsvImportService(
                            budgetCsv.getOriginalFilename(),
                            budgetRecords,
                            customerCsvImportService,
                            customerService,
                            customerBudgetService);
                    budgetCsvImportService.processBudgetCsv();

                    if (budgetCsvImportService.hasError()) {
                        hasError = true;
                        exceptions.addAll(budgetCsvImportService.getExceptions());
                    } else {
                        budgetCsvImportService.save();
                    }

                    ExpenseCsvImportService expenseCsvImportService = new ExpenseCsvImportService(
                            expenseCsv.getOriginalFilename(),
                            expenseRecords,
                            customerCsvImportService,
                            userService,
                            customerService,
                            leadService,
                            ticketService,
                            leadExpenseService,
                            ticketExpenseService);
                    expenseCsvImportService.processCustomerCsv();

                    if (expenseCsvImportService.hasError()) {
                        hasError = true;
                        exceptions.addAll(expenseCsvImportService.getExceptions());
                    } else {
                        expenseCsvImportService.save(createdBy);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    hasError = true;
                }
            }

            if (hasError) {
                status.setRollbackOnly();
                redirectAttributes.addFlashAttribute("invalidFormatErrors", exceptions);
                redirectAttributes.addFlashAttribute("message", "Failed to process CSV files.");
            } else {
                redirectAttributes.addFlashAttribute("message", "CSV file uploaded and processed successfully!");
            }

            return pageRedirect;
        });
    }
}