package pathfinder;

/**
 * Thrown when a duplicate key is encountered where it isn't allowed.
 */
public class DuplicateKeyException extends RuntimeException {

    public DuplicateKeyException(String message) {
        super(message);
    }
}
