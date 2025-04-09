package site.easy.to.build.crm.service.summary;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.dto.FinancialSummaryDTO;
import site.easy.to.build.crm.repository.FinancialSummaryRepository;

@Service
@RequiredArgsConstructor
public class FinancialSummaryServiceImpl implements FinancialSummaryService {
    
    private final FinancialSummaryRepository financialSummaryRepository;
    
    @Override
    public FinancialSummaryDTO getFinancialSummary() {
        return financialSummaryRepository.getFinancialSummary();
    }
}