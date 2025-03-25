package site.easy.to.build.crm.service.csv;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CsvValidationUtils {
    private CsvValidationUtils() {
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static double parseAmount(String amount, String defaultValue) {
        if (amount == null || amount.isEmpty()) {
            return Double.parseDouble(defaultValue);
        }
        try {
            return Double.parseDouble(amount.replace(",", "."));
        } catch (NumberFormatException e) {
            return Double.parseDouble(defaultValue);
        }
    }

    public static boolean isValidAmount(String amount) {
        if (amount == null || amount.isEmpty()) {
            return false;
        }
        try {
            double numericAmount = Double.parseDouble(amount.replace(",", "."));
            return numericAmount > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}