package site.easy.to.build.crm.service.csv;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.Data;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.User;
import site.easy.to.build.crm.exception.InvalidCsvFormatException;
import site.easy.to.build.crm.service.customer.CustomerServiceImpl;

@Service
@Data
public class CustomerCsvImportService {
    private String filename;
    private Iterable<CSVRecord> customerRecords;
    private List<Customer> customers = new ArrayList<>();
    private List<InvalidCsvFormatException> exceptions = new ArrayList<>();

    private final CustomerServiceImpl customerService;

    /* ------------------------------ Constructors ------------------------------ */

    @Autowired
    public CustomerCsvImportService(CustomerServiceImpl customerService) {
        this.customerService = customerService;
    }

    public CustomerCsvImportService(
            String filename,
            Iterable<CSVRecord> customerRecords,
            CustomerServiceImpl customerService) {
        this.filename = filename;
        this.customerRecords = customerRecords;
        this.customerService = customerService;
    }

    /* --------------------------- Processing methods --------------------------- */

    public void save() {
        if (hasError()) {
            return;
        }

        List<Customer> savedCustomers = new ArrayList<>();
        User user = new User();
        user.setId(52);

        for (Customer customer : this.getCustomers()) {
            customer.setUser(user);
            customer = customerService.save(customer);
            savedCustomers.add(customer);
        }

        this.setCustomers(savedCustomers);
    }

    public Customer getCustomerByEmail(String email) {
        return this.customerService.findByEmail(email);
    }

    public void processCustomerCsv() {
        int lineNumber = 0;
        for (CSVRecord customerRecord : customerRecords) {
            if (lineNumber > 0) { // Skip header
                customers.add(parseCsvRecord(customerRecord, lineNumber));
            }
            lineNumber++;
        }
    }

    /* --------------------------- Validation methods --------------------------- */

    public boolean hasError() {
        return !exceptions.isEmpty();
    }

    public boolean emailExists(String email) {
        return email != null && !email.isEmpty() &&
                customers.stream().anyMatch(c -> c.getEmail().equalsIgnoreCase(email));
    }

    /* ----------------------------- Parsing methods ---------------------------- */

    private Customer parseCsvRecord(CSVRecord customerRecord, int lineNumber) {
        Customer customer = new Customer();
        String email = customerRecord.get(0);
        String name = customerRecord.get(1);

        if (!CsvValidationUtils.isValidEmail(email)) {
            exceptions.add(new InvalidCsvFormatException(filename, lineNumber, "Invalid email format provided"));
        }

        customer.setEmail(email);
        customer.setName(name);

        // Fillers
        customer.setPosition("Default Position");
        customer.setPhone("####");
        customer.setAddress("Default Address");
        customer.setCity("Default City");
        customer.setState("Default State");
        customer.setCountry("Default Country");
        customer.setDescription("Default Description");
        customer.setTwitter("Default Twitter");
        customer.setFacebook("Default Facebook");
        customer.setYoutube("Default YouTube");
        customer.setCreatedAt(LocalDateTime.now());

        return customer;
    }

    /* ----------------------------- Utility methods ---------------------------- */

    public List<String> getAllEmails() {
        return customers.stream()
                .map(Customer::getEmail)
                .toList();
    }
}