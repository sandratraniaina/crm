package site.easy.to.build.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import site.easy.to.build.crm.dto.FinancialSummaryDTO;
import site.easy.to.build.crm.entity.expense.TicketExpense;

@Repository
public interface FinancialSummaryRepository extends JpaRepository<TicketExpense, Integer> {

    @Query("SELECT new site.easy.to.build.crm.dto.FinancialSummaryDTO(" +
            "CAST(COALESCE((SELECT SUM(b.amount) FROM Budget b), 0) AS BigDecimal), " +
            "CAST(COALESCE((SELECT SUM(te.amount) FROM TicketExpense te WHERE te.ticket.ticketId IS NOT NULL), 0) AS BigDecimal), "
            +
            "CAST(COALESCE((SELECT SUM(le.amount) FROM LeadExpense le WHERE le.lead.leadId IS NOT NULL), 0) AS BigDecimal))")
    FinancialSummaryDTO getFinancialSummary();
}