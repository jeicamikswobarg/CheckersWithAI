package exception;

public class FieldTakenException extends Throwable {

    @Override
    public String getMessage() {
        return "Field already taken";
    }
}
