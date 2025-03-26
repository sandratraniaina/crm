package site.easy.to.build.crm.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import site.easy.to.build.crm.entity.expense.ExpenseThreshold;
import site.easy.to.build.crm.service.expense.ExpenseThresholdService;
import site.easy.to.build.crm.util.response.Response;
import site.easy.to.build.crm.util.response.ResponseUtil;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api")
@PreAuthorize("hasRole('ROLE_MANAGER')")
public class ApiExpenseThresholdController {

    private final ExpenseThresholdService expenseThresholdService;

    @Autowired
    public ApiExpenseThresholdController(ExpenseThresholdService expenseThresholdService) {
        this.expenseThresholdService = expenseThresholdService;
    }

    // GET /api/expense-threshold
    @GetMapping("/expense-threshold")
    public ResponseEntity<Response<Map<String, ExpenseThreshold>>> getExpenseThreshold() {
        ExpenseThreshold threshold = expenseThresholdService.getThreshold();
        Map<String, ExpenseThreshold> responseData = Map.of("threshold", threshold);
        return ResponseUtil.sendResponse(HttpStatus.OK, true, "Expense threshold retrieved successfully", responseData);
    }

    // POST /api/expense-threshold/update
    @PostMapping("/expense-threshold/update")
    public ResponseEntity<Response<Map<String, Boolean>>> updateExpenseThreshold(
            @RequestBody Map<String, Double> request) {
        Double thresholdValue = request.get("threshold");
                
        if (thresholdValue == null) {
            return ResponseUtil.sendResponse(HttpStatus.BAD_REQUEST, false, "Threshold value is required", null);
        }
        
        expenseThresholdService.updateThreshold(BigDecimal.valueOf(thresholdValue));
        return ResponseUtil.sendResponse(HttpStatus.OK, true, "Expense threshold updated successfully",
                Map.of("success", true));
    }
}
