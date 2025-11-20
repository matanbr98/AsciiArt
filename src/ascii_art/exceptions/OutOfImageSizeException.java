package ascii_art.exceptions;

/**
 * An exception that is thrown when the image size is lower than the res.
 */
public class OutOfImageSizeException extends Exception {

    /**
     * Constructor for the exception.
     */
    public OutOfImageSizeException() {
        super("Did not change resolution due to exceeding boundaries.");
    }
}
