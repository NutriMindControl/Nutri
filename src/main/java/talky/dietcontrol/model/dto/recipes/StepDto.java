package talky.dietcontrol.model.dto.recipes;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

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
