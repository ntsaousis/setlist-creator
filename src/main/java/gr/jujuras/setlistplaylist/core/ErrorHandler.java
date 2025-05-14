package gr.jujuras.setlistplaylist.core;


import gr.jujuras.setlistplaylist.core.exceptions.ArtistNotFoundException;

import gr.jujuras.setlistplaylist.core.exceptions.ExternalApiException;
import gr.jujuras.setlistplaylist.core.exceptions.SetNotFoundException;
import gr.jujuras.setlistplaylist.dto.ResponseMessageErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice 
public class ErrorHandler {

    @ExceptionHandler({ArtistNotFoundException.class})
    public ResponseEntity<ResponseMessageErrorDTO> handleArtistNotFound(ArtistNotFoundException ex, WebRequest request) {
        return new ResponseEntity<>(new ResponseMessageErrorDTO(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({SetNotFoundException.class})
    public ResponseEntity<ResponseMessageErrorDTO> handleSetNotFound(SetNotFoundException ex, WebRequest request) {
        return new ResponseEntity<>(new ResponseMessageErrorDTO(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ExternalApiException.class})
    public ResponseEntity<ResponseMessageErrorDTO> handleSetNotFound(ExternalApiException ex, WebRequest request) {
        return new ResponseEntity<>(new ResponseMessageErrorDTO(ex.getMessage()), HttpStatus.NOT_FOUND);
    }


}
