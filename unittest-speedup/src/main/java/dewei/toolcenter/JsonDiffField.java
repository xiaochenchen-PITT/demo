package dewei.toolcenter;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JsonDiffField {

    private String jsonPath;

    private String leftValue;

    private String rightValue;
}
