package site.easy.to.build.crm.repository.expense;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import site.easy.to.build.crm.entity.expense.LeadExpense;
import java.util.List;

public interface LeadExpenseRepository extends JpaRepository<LeadExpense, Integer> {
    
    List<LeadExpense> findByLeadLeadId(Integer leadId);

    @Query("SELECT le FROM LeadExpense le JOIN  le.lead l JOIN l.customer c WHERE c.customerId = :customerId")
    List<LeadExpense> findAllLeadExpensesByCustomerId(@Param("customerId") Integer customerId);
}