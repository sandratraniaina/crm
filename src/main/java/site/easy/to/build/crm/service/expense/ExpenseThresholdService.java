package site.easy.to.build.crm.service.expense;

import java.math.BigDecimal;

import site.easy.to.build.crm.entity.expense.ExpenseThreshold;

public interface ExpenseThresholdService {
    BigDecimal getThresholdValue();
    
    ExpenseThreshold updateThreshold(BigDecimal newValue);
    
    void initializeThresholdIfNotExists();
}
