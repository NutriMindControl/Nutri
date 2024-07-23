package talky.dietcontrol.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;

@Data

public class StepDto   {
  @JsonProperty("media_id")
  private Long mediaId = null;

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("step_num")
  private Integer stepNum = null;

  @JsonProperty("wait_time_mins")
  private Integer waitTimeMins = null;

}
