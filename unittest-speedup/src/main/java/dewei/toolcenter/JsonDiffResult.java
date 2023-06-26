package dewei.toolcenter;

import lombok.Data;

import java.util.List;

@Data
public class JsonDiffResult {

    private Boolean isEqual;

    private List<JsonDiffField> diffFields;


}
