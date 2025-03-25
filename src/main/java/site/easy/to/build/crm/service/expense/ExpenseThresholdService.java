package site.easy.to.build.crm.service.expense;

import java.math.BigDecimal;

import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.expense.ExpenseThreshold;

public interface ExpenseThresholdService {
    BigDecimal getThresholdValue();

    ExpenseThreshold getThreshold();
    
    ExpenseThreshold updateThreshold(BigDecimal newValue);
    
    void initializeThresholdIfNotExists();

    public boolean isThresholdExceeded(Customer customer);

    public boolean isBudgetExceeded(Customer customer, BigDecimal newValue);
}
