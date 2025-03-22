package site.easy.to.build.crm.service.budget;

import site.easy.to.build.crm.entity.Budget;
import java.util.List;
import java.util.Optional;

public interface BudgetService {

    // Create
    Budget createBudget(Budget budget);

    // Read
    Optional<Budget> findById(Integer id);
    List<Budget> findAll();
    List<Budget> findByCustomerId(Integer customerId);

    // Update
    Budget updateBudget(Budget budget);

    // Delete
    void deleteBudget(Integer id);
}