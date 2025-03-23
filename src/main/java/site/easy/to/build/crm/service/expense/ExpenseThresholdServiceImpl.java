package site.easy.to.build.crm.service.expense;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import site.easy.to.build.crm.entity.expense.ExpenseThreshold;
import site.easy.to.build.crm.repository.expense.ExpenseThresholdRepository;

import java.math.BigDecimal;

@Service
public class ExpenseThresholdServiceImpl implements ExpenseThresholdService {
    
    private final ExpenseThresholdRepository thresholdRepository;
    
    @Autowired
    public ExpenseThresholdServiceImpl(ExpenseThresholdRepository thresholdRepository) {
        this.thresholdRepository = thresholdRepository;
    }
    
    @Override
    public BigDecimal getThresholdValue() {
        return thresholdRepository.getThreshold().getValue();
    }
    
    @Override
    @Transactional
    public ExpenseThreshold updateThreshold(BigDecimal newValue) {
        ExpenseThreshold threshold = thresholdRepository.getThreshold();
        threshold.setValue(newValue);
        return thresholdRepository.save(threshold);
    }
    
    @Override
    @Transactional
    public void initializeThresholdIfNotExists() {
        if (thresholdRepository.count() == 0) {
            ExpenseThreshold threshold = new ExpenseThreshold();
            threshold.setValue(BigDecimal.valueOf(0.8));
            thresholdRepository.save(threshold);
        }
    }
}
