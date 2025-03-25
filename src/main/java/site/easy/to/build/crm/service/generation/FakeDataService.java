package site.easy.to.build.crm.service.generation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.github.javafaker.Faker;

import lombok.AllArgsConstructor;
import lombok.Data;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.User;
import site.easy.to.build.crm.service.user.UserService;

@Service
@Data
@AllArgsConstructor
public class FakeDataService {
    private final Faker faker;
    private final UserService userService;

    private final Random random = new Random();

    public User generateUser() {
        User user = new User();

        user.setUsername(faker.name().username());
        user.setEmail(faker.internet().emailAddress());
        user.setStatus("active");

        return user;
    }

    public Customer generateFakeCustomer() {
        Customer customer = new Customer();

        List<User> users = userService.findAll();
        if (users.isEmpty()) {
            throw new IllegalStateException("No users available in the database");
        }
        User randomUser = users.get(random.nextInt(users.size()));
        
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
}
