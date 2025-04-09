package site.easy.to.build.crm.repository.expense;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import site.easy.to.build.crm.entity.expense.TicketExpense;
import java.util.List;

public interface TicketExpenseRepository extends JpaRepository<TicketExpense, Integer> {

    List<TicketExpense> findByTicketTicketId(Integer ticketId);

    @Query("SELECT te FROM TicketExpense te JOIN  te.ticket t JOIN t.customer c WHERE c.customerId = :customerId")
    List<TicketExpense> findAllTicketExpenseByCustomerId(@Param("customerId") Integer customerId);
}