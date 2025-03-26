package site.easy.to.build.crm.service.expense;

import site.easy.to.build.crm.entity.expense.LeadExpense;
import java.util.List;

public interface LeadExpenseService {

    // Create
    LeadExpense createLeadExpense(LeadExpense leadExpense);

    // Read
    LeadExpense findById(Integer id);
    List<LeadExpense> findAll();
    List<LeadExpense> findByLeadId(Integer leadId);
    
    List<LeadExpense> findByCustomerId(Integer leadId);

    // Update
    LeadExpense updateLeadExpense(LeadExpense leadExpense);

    // Delete
    void deleteLeadExpense(Integer id);
}