package cristianorocchi.graffitiplatform.exceptions;



import java.util.HashMap;
import java.util.Map;

public class ErrorsPayload {
    private String message;
    private Map<String, String> errors = new HashMap<>();

    public ErrorsPayload(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void addError(String field, String errorMessage) {
        this.errors.put(field, errorMessage);
    }
}

