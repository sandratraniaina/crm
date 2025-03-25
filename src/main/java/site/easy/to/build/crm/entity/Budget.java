package site.easy.to.build.crm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "budget")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "amount", nullable = false)
    @DecimalMin(value = "0", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt = LocalDate.now();

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    @JsonIgnore
    private User createdBy;
}