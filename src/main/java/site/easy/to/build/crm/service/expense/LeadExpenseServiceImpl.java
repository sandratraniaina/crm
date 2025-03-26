package site.easy.to.build.crm.service.expense;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import site.easy.to.build.crm.entity.expense.LeadExpense;
import site.easy.to.build.crm.repository.expense.LeadExpenseRepository;

import java.util.List;
import java.util.Optional;

@Service
public class LeadExpenseServiceImpl implements LeadExpenseService {

    private final LeadExpenseRepository leadExpenseRepository;

    @Autowired
    public LeadExpenseServiceImpl(LeadExpenseRepository leadExpenseRepository) {
        this.leadExpenseRepository = leadExpenseRepository;
    }

    @Override
    public LeadExpense createLeadExpense(LeadExpense leadExpense) {
        return leadExpenseRepository.save(leadExpense);
    }

    @Override
    public LeadExpense findById(Integer id) {
        Optional<LeadExpense> optionalLeadExpense = leadExpenseRepository.findById(id);
        return optionalLeadExpense.orElseThrow(() -> new RuntimeException("Lead expense not found:" + id));
    }

    @Override
    public List<LeadExpense> findAll() {
        return leadExpenseRepository.findAll();
    }

    @Override
    public List<LeadExpense> findByLeadId(Integer leadId) {
        return leadExpenseRepository.findByLeadLeadId(leadId);
    }

    @Override
    public LeadExpense updateLeadExpense(LeadExpense leadExpense) {
        if (!leadExpenseRepository.existsById(leadExpense.getId())) {
            throw new IllegalArgumentException("LeadExpense with ID " + leadExpense.getId() + " does not exist.");
        }
        return leadExpenseRepository.save(leadExpense);
    }

    @Override
    public void deleteLeadExpense(Integer id) {
        if (!leadExpenseRepository.existsById(id)) {
            throw new IllegalArgumentException("LeadExpense with ID " + id + " does not exist.");
        }
        leadExpenseRepository.deleteById(id);
    }

    @Override
    public List<LeadExpense> findByCustomerId(Integer customerId) {
        return leadExpenseRepository.findAllLeadExpensesByCustomerId(customerId);
    }
}