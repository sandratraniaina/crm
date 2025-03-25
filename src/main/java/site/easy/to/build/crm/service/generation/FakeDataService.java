package site.easy.to.build.crm.service.generation;

import org.springframework.stereotype.Service;

import com.github.javafaker.Faker;

import lombok.AllArgsConstructor;
import lombok.Data;
import site.easy.to.build.crm.entity.User;

@Service
@Data
@AllArgsConstructor
public class FakeDataService {
    private final Faker faker;

    public User generateUser() {
        User user = new User();

        user.setUsername(faker.name().username());
        user.setEmail(faker.internet().emailAddress());
        user.setStatus("active");

        return user;
    }
    
}
