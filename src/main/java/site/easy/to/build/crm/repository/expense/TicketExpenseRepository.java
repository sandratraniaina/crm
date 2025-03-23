package site.easy.to.build.crm.repository.expense;

import org.springframework.data.jpa.repository.JpaRepository;
import site.easy.to.build.crm.entity.expense.TicketExpense;
import java.util.List;

public interface TicketExpenseRepository extends JpaRepository<TicketExpense, Integer> {
    
    List<TicketExpense> findByTicketTicketId(Integer ticketId);
}