package gr.jujuras.setlistplaylist.core.exceptions;

/**
 * Exception thrown when external API calls fail.
 * This includes Spotify, Setlist.fm, MusicBrainz, or any other third-party API.
 */
public class ExternalApiException extends RuntimeException {

    /**
     * Constructs a new ExternalApiException with the specified detail message.
     *
     * @param message the detail message
     */
    public ExternalApiException(String message) {
        super(message);
    }

    /**
     * Constructs a new ExternalApiException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public ExternalApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
