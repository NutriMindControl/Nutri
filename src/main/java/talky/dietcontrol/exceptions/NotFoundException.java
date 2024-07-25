package talky.dietcontrol.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotFoundException extends RuntimeException {
    public NotFoundException(String msg) {
        super(msg);
        log.error("Failed to process query. Status code: 404 NOT_FOUND");
    }
}
