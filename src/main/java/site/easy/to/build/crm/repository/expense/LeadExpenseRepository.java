package site.easy.to.build.crm.repository.expense;

import org.springframework.data.jpa.repository.JpaRepository;
import site.easy.to.build.crm.entity.expense.LeadExpense;
import java.util.List;

public interface LeadExpenseRepository extends JpaRepository<LeadExpense, Integer> {
    
    List<LeadExpense> findByLeadLeadId(Integer leadId);
}