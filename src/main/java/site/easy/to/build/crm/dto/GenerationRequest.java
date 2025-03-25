package site.easy.to.build.crm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
class GenerationRequest {
    private int customerCount;
    private int budgetCount;
    private int ticketExpenseCount;
    private int leadExpenseCount;
}