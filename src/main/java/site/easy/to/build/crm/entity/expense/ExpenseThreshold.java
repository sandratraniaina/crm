package site.easy.to.build.crm.entity.expense;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "expense_threshold")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseThreshold {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "value", nullable = false, precision = 3, scale = 2)
    private BigDecimal value = BigDecimal.valueOf(0.8);
}