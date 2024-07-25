package talky.dietcontrol.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import talky.dietcontrol.exceptions.BadRequestException;
import talky.dietcontrol.exceptions.NotFoundException;
import talky.dietcontrol.model.dto.Error;


@RestControllerAdvice
@CrossOrigin(maxAge = 1440)
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({NotFoundException.class})
    protected ResponseEntity<Object> handleNotFound(Exception e) {
        return new ResponseEntity<>(new Error().code(404).message(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({BadRequestException.class})
    protected ResponseEntity<Object> handleBadRequest(Exception e) {
        return new ResponseEntity<>(new Error().code(400).message(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

}