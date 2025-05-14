package gr.jujuras.setlistplaylist.core.exceptions;


import lombok.Getter;

@Getter
public class ArtistNotFoundException extends RuntimeException{

    public ArtistNotFoundException(String message) {
        super(message);
    }
}
