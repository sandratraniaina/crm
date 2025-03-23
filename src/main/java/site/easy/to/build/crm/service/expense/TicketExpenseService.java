package site.easy.to.build.crm.service.expense;

import site.easy.to.build.crm.entity.expense.TicketExpense;
import java.util.List;
import java.util.Optional;

public interface TicketExpenseService {

    // Create
    TicketExpense createTicketExpense(TicketExpense ticketExpense);

    // Read
    Optional<TicketExpense> findById(Integer id);
    List<TicketExpense> findAll();
    List<TicketExpense> findByTicketId(Integer ticketId);

    // Update
    TicketExpense updateTicketExpense(TicketExpense ticketExpense);

    // Delete
    void deleteTicketExpense(Integer id);
}