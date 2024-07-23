package talky.dietcontrol.exceptions;

public class InvalidMediaTypeException extends RuntimeException {
    public InvalidMediaTypeException(String type) {
        super("Unsupported/invalid media type: " + type);
    }
}
