package site.easy.to.build.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import site.easy.to.build.crm.service.reset.DatabaseResetService;

@Controller
@RequestMapping("/employee/reset")
public class DatabaseResetController {

    private final DatabaseResetService databaseResetService;

    @Autowired
    public DatabaseResetController(DatabaseResetService databaseResetService) {
        this.databaseResetService = databaseResetService;
    }

    @GetMapping("/database")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public String showResetPage(Model model) {
        model.addAttribute("pageTitle", "Reset Database");
        return "reset/database-reset";
    }

    @PostMapping("/database")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public String resetDatabase(Model model) {
        try {
            databaseResetService.resetDatabaseExceptAuth();
            model.addAttribute("successMessage", "Database has been successfully reset, excluding authentication data.");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An error occurred while resetting the database: " + e.getMessage());
        }
        return "reset/database-reset";
    }
}