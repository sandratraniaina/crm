package site.easy.to.build.crm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialSummaryDTO {
    private BigDecimal totalBudget;
    private BigDecimal ticketExpenses;
    private BigDecimal leadExpenses;
}