package site.easy.to.build.crm.service.budget;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.repository.BudgetRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;

    @Autowired
    public BudgetServiceImpl(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    @Override
    public Budget createBudget(Budget budget) {
        return budgetRepository.save(budget);
    }

    @Override
    public Optional<Budget> findById(Integer id) {
        return budgetRepository.findById(id);
    }

    @Override
    public List<Budget> findAll() {
        return budgetRepository.findAll();
    }

    @Override
    public List<Budget> findByCustomerId(Integer customerId) {
        return budgetRepository.findByCustomerCustomerId(customerId);
    }

    @Override
    public Budget updateBudget(Budget budget) {
        if (budget.getId() == null || !budgetRepository.existsById(budget.getId())) {
            throw new IllegalArgumentException("Budget with ID " + budget.getId() + " does not exist");
        }
        return budgetRepository.save(budget);
    }

    @Override
    public void deleteBudget(Integer id) {
        if (!budgetRepository.existsById(id)) {
            throw new IllegalArgumentException("Budget with ID " + id + " does not exist");
        }
        budgetRepository.deleteById(id);
    }
}