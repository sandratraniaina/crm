package site.easy.to.build.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.easy.to.build.crm.entity.Budget;
import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Integer> {
    
    List<Budget> findByCustomerCustomerId(Integer customerId);
}