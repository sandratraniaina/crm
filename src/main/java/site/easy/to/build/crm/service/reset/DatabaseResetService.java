package site.easy.to.build.crm.service.reset;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabaseResetService {

    private final EntityManager entityManager;

    @Autowired
    public DatabaseResetService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public void resetDatabaseExceptAuth() {
        // List of tables to clear (excluding auth-related tables)
        String[] tablesToClear = {
            "contract_settings", "customer", "email_template", "employee", "file",
            "google_drive_file", "lead_action", "lead_settings", "ticket_settings",
            "trigger_contract", "trigger_lead", "trigger_ticket", "user_profile"
        };

        // Disable foreign key checks to avoid constraint violations
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();

        // Truncate each table to delete data and reset auto-increment
        for (String table : tablesToClear) {
            String fullTableName = "crm." + table;
            entityManager.createNativeQuery("TRUNCATE TABLE " + fullTableName).executeUpdate();
        }

        // Re-enable foreign key checks
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
    }
}
