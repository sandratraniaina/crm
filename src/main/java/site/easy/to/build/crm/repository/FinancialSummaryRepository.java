package site.easy.to.build.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import site.easy.to.build.crm.dto.FinancialSummaryDTO;

public interface FinancialSummaryRepository extends JpaRepository<FinancialSummaryDTO, Integer> {
    @Query(value = "SELECT * FROM v_financial_summary", nativeQuery = true)
    FinancialSummaryDTO getFinancialSummary();
}