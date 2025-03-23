package site.easy.to.build.crm.repository.expense;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import site.easy.to.build.crm.entity.expense.ExpenseThreshold;

@Repository
public interface ExpenseThresholdRepository extends JpaRepository<ExpenseThreshold, Integer> {
    // Since there's only one row, we can add a custom method to get it
    default ExpenseThreshold getThreshold() {
        return findAll().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Expense threshold not found"));
    }
}