package site.easy.to.build.crm.exception;

import lombok.Getter;

@Getter
public class InvalidCsvFormatException extends Exception {
    private final String filename;
    private final int lineNumber;
    private final String errorMessage;

    public InvalidCsvFormatException(String filename, int lineNumber, String errorMessage) {
        super("Invalid CSV format in file: " + filename + ", line: " + lineNumber + ". " + errorMessage);
        this.filename = filename;
        this.lineNumber = lineNumber;
        this.errorMessage = errorMessage;
    }
}