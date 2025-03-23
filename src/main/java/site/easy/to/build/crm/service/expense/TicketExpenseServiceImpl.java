package site.easy.to.build.crm.service.expense;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.entity.expense.TicketExpense;
import site.easy.to.build.crm.repository.expense.TicketExpenseRepository;

import java.util.List;

@Service
public class TicketExpenseServiceImpl implements TicketExpenseService {

    private final TicketExpenseRepository ticketExpenseRepository;

    @Autowired
    public TicketExpenseServiceImpl(TicketExpenseRepository ticketExpenseRepository) {
        this.ticketExpenseRepository = ticketExpenseRepository;
    }

    @Override
    public TicketExpense createTicketExpense(TicketExpense ticketExpense) {
        return ticketExpenseRepository.save(ticketExpense);
    }

    @Override
    public TicketExpense findById(Integer id) {
        return ticketExpenseRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Expense not found"));
    }

    @Override
    public List<TicketExpense> findAll() {
        return ticketExpenseRepository.findAll();
    }

    @Override
    public List<TicketExpense> findByTicketId(Integer ticketId) {
        return ticketExpenseRepository.findByTicketTicketId(ticketId);
    }

    @Override
    public TicketExpense updateTicketExpense(TicketExpense ticketExpense) {
        if (!ticketExpenseRepository.existsById(ticketExpense.getId())) {
            throw new IllegalArgumentException("TicketExpense with ID " + ticketExpense.getId() + " does not exist.");
        }
        return ticketExpenseRepository.save(ticketExpense);
    }

    @Override
    public void deleteTicketExpense(Integer id) {
        if (!ticketExpenseRepository.existsById(id)) {
            throw new IllegalArgumentException("TicketExpense with ID " + id + " does not exist.");
        }
        ticketExpenseRepository.deleteById(id);
    }
}