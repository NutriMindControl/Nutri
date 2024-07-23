package talky.dietcontrol.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

/**
 * Error
 */
@Validated
@Data
public class Error {
    @JsonProperty("code")
    private Integer code = null;

    @JsonProperty("message")
    private String message = null;

    public Error code(Integer code) {
        this.code = code;
        return this;
    }


    public Error message(String message) {
        this.message = message;
        return this;
    }
}
