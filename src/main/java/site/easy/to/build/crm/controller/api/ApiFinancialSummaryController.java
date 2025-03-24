package site.easy.to.build.crm.controller.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.easy.to.build.crm.dto.FinancialSummaryDTO;
import site.easy.to.build.crm.service.summary.FinancialSummaryService;
import site.easy.to.build.crm.util.response.Response;
import site.easy.to.build.crm.util.response.ResponseUtil;

@RestController
@RequestMapping("/api/summary")
@PreAuthorize("hasRole('ROLE_MANAGER')")
@RequiredArgsConstructor
public class ApiFinancialSummaryController {

    private final FinancialSummaryService financialSummaryService;

    @GetMapping("/financial-summary")
    public ResponseEntity<Response<FinancialSummaryDTO>> getFinancialSummary() {
        return ResponseUtil.sendResponse(HttpStatus.OK, true, "Financial summary retrieved successfully", financialSummaryService.getFinancialSummary());
    }
}