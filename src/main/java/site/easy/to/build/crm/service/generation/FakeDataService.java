package site.easy.to.build.crm.service.generation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.github.javafaker.Faker;

import lombok.Data;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.entity.User;
import site.easy.to.build.crm.entity.expense.LeadExpense;
import site.easy.to.build.crm.entity.expense.TicketExpense;
import site.easy.to.build.crm.service.user.UserService;
import site.easy.to.build.crm.service.customer.CustomerService;
import site.easy.to.build.crm.service.lead.LeadService;
import site.easy.to.build.crm.service.ticket.TicketService;

@Service
@Data
public class FakeDataService {
    private final Faker faker;
    private final UserService userService;
    private final CustomerService customerService;
    private final LeadService leadService;
    private final TicketService ticketService;
    private final Random random;

    public FakeDataService(UserService userService, CustomerService customerService,
            LeadService leadService, TicketService ticketService) {
        this.faker = new Faker();
        this.userService = userService;
        this.customerService = customerService;
        this.leadService = leadService;
        this.ticketService = ticketService;
        this.random = new Random();
    }

    public User generateUser() {
        User user = new User();

        user.setUsername(faker.name().username());
        user.setEmail(faker.internet().emailAddress());
        user.setStatus("active");

        return user;
    }

    public Customer generateFakeCustomer() {
        Customer customer = new Customer();

        User randomUser = userService.getRandomUser();

        customer.setName(faker.name().fullName());
        customer.setEmail(faker.internet().emailAddress());
        customer.setCountry(faker.address().country());
        customer.setUser(randomUser);
        customer.setCreatedAt(LocalDateTime.now());

        customer.setPosition(faker.job().position());
        customer.setPhone(faker.phoneNumber().cellPhone());
        customer.setAddress(faker.address().streetAddress());
        customer.setCity(faker.address().city());
        customer.setState(faker.address().state());
        customer.setDescription(faker.lorem().sentence());
        customer.setTwitter("@" + faker.name().username());
        customer.setFacebook(faker.name().username());
        customer.setYoutube(faker.name().username() + "Channel");

        return customer;
    }

    public Lead generateFakeLead(Customer customer) {
        Lead lead = new Lead();

        User randomManager = userService.getRandomUser();
        User randomEmployee = userService.getRandomUser();

        lead.setName(faker.name().fullName());
        lead.setStatus(getRandomLeadStatus());
        lead.setManager(randomManager);
        lead.setEmployee(randomEmployee);
        lead.setCustomer(customer);

        lead.setPhone(faker.phoneNumber().cellPhone());

        return lead;
    }

    public LeadExpense generateFakeLeadExpense() {
        LeadExpense leadExpense = new LeadExpense();

        User randomUser = userService.getRandomUser();
        Customer randomCustomer = customerService.getRandomCustomer();
        Lead lead = leadService.save(generateFakeLead(randomCustomer));

        leadExpense.setAmount(BigDecimal.valueOf(faker.number().randomDouble(2, 10, 1000)));
        leadExpense.setCreatedBy(randomUser);
        leadExpense.setLead(lead);

        leadExpense.setDescription(faker.lorem().sentence());
        leadExpense.setCreatedAt(LocalDateTime.now());
        leadExpense.setExpenseDate(LocalDate.now());

        return leadExpense;
    }

    public Ticket generateFakeTicket(Customer customer) {
        Ticket ticket = new Ticket();

        User randomManager = userService.getRandomUser();
        User randomEmployee = userService.getRandomUser();

        ticket.setSubject(faker.lorem().sentence(3));
        ticket.setStatus(getRandomTicketStatus());
        ticket.setPriority(getRandomTicketPriority());
        ticket.setManager(randomManager);
        ticket.setEmployee(randomEmployee);
        ticket.setCustomer(customer);

        ticket.setDescription(faker.lorem().paragraph());

        return ticket;
    }

    public TicketExpense generateFakeTicketExpense() {
        TicketExpense ticketExpense = new TicketExpense();

        User randomUser = userService.getRandomUser();
        Customer randomCustomer = customerService.getRandomCustomer();
        Ticket ticket = ticketService.save(generateFakeTicket(randomCustomer));

        ticketExpense.setAmount(BigDecimal.valueOf(faker.number().randomDouble(2, 10, 1000)));
        ticketExpense.setCreatedBy(randomUser);
        ticketExpense.setTicket(ticket);

        ticketExpense.setDescription(faker.lorem().sentence());
        ticketExpense.setCreatedAt(LocalDateTime.now());
        ticketExpense.setExpenseDate(LocalDate.now());

        return ticketExpense;
    }

    public Budget generateFakeBudget() {
        Budget budget = new Budget();

        User randomUser = userService.getRandomUser();
        Customer randomCustomer = customerService.getRandomCustomer();

        budget.setAmount(BigDecimal.valueOf(faker.number().randomDouble(2, 100, 10000)));
        budget.setCreatedBy(randomUser);
        budget.setCustomer(randomCustomer);

        budget.setDescription(faker.lorem().sentence());
        budget.setCreatedAt(LocalDate.now());

        return budget;
    }

    private String getRandomLeadStatus() {
        String[] statuses = { "meeting-to-schedule", "scheduled", "archived", "success", "assign-to-sales" };
        return statuses[random.nextInt(statuses.length)];
    }

    private String getRandomTicketStatus() {
        String[] statuses = { "open", "assigned", "on-hold", "in-progress", "resolved", "closed", "reopened",
                "pending-customer-response", "escalated", "archived" };
        return statuses[random.nextInt(statuses.length)];
    }

    private String getRandomTicketPriority() {
        String[] priorities = { "low", "medium", "high", "closed", "urgent", "critical" };
        return priorities[random.nextInt(priorities.length)];
    }
}