package site.easy.to.build.crm.entity.expense;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.entity.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "ticket_expense")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TicketExpense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "amount", nullable = false)
    @DecimalMin(value = "0", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate = LocalDate.now();

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    @JsonIgnore
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;
}