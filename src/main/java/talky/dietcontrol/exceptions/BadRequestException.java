package talky.dietcontrol.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BadRequestException extends RuntimeException {
    public BadRequestException(String msg) {
        super(msg);
        log.error("Failed to process query. Status code: 400 BAD_REQUEST");

    }
}
