package site.easy.to.build.crm.service.expense;

import site.easy.to.build.crm.entity.expense.LeadExpense;
import java.util.List;
import java.util.Optional;

public interface LeadExpenseService {

    // Create
    LeadExpense createLeadExpense(LeadExpense leadExpense);

    // Read
    Optional<LeadExpense> findById(Integer id);
    List<LeadExpense> findAll();
    List<LeadExpense> findByLeadId(Integer leadId);

    // Update
    LeadExpense updateLeadExpense(LeadExpense leadExpense);

    // Delete
    void deleteLeadExpense(Integer id);
}