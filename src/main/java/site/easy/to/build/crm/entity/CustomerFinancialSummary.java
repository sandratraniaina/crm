package site.easy.to.build.crm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import org.springframework.data.annotation.Immutable;

@Entity
@Table(name = "v_customer_financial_summary")
@Immutable
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CustomerFinancialSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @MapsId // Shares the customer_id as the primary key
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "total_budget", nullable = false)
    @DecimalMin(value = "0", message = "Total budget must be greater than or equal to 0")
    private BigDecimal totalBudget = BigDecimal.ZERO;

    @Column(name = "total_ticket_expense", nullable = false)
    @DecimalMin(value = "0", message = "Total ticket expense must be greater than or equal to 0")
    private BigDecimal totalTicketExpense = BigDecimal.ZERO;

    @Column(name = "total_lead_expense", nullable = false)
    @DecimalMin(value = "0", message = "Total lead expense must be greater than or equal to 0")
    private BigDecimal totalLeadExpense = BigDecimal.ZERO;

    @Column(name = "total_expense", nullable = false)
    @DecimalMin(value = "0", message = "Total expense must be greater than or equal to 0")
    private BigDecimal totalExpense = BigDecimal.ZERO;

    @Column(name = "remaining_budget", nullable = false)
    private BigDecimal remainingBudget = BigDecimal.ZERO;

    // Methods
    public boolean isBudgetExceeded(BigDecimal newAmount) {
        BigDecimal totalExpenses = totalExpense.add(newAmount);
        return totalExpenses.compareTo(totalBudget) > 0;
    }

    public boolean isThresholdExceeded(BigDecimal threshold) {
        BigDecimal thresholdAmount = totalBudget.multiply(threshold);
        
        return totalExpense.compareTo(thresholdAmount) > 0;
    }

    // Custom setters to update remaining_budget
    public void setTotalBudget(BigDecimal totalBudget) {
        this.totalBudget = totalBudget;
        updateRemainingBudget();
    }

    public void setTotalTicketExpense(BigDecimal totalTicketExpense) {
        this.totalTicketExpense = totalTicketExpense;
        updateRemainingBudget();
    }

    public void setTotalLeadExpense(BigDecimal totalLeadExpense) {
        this.totalLeadExpense = totalLeadExpense;
        updateRemainingBudget();
    }

    // Helper method to update remaining_budget
    private void updateRemainingBudget() {
        this.remainingBudget = totalBudget.subtract(totalTicketExpense.add(totalLeadExpense));
    }
}