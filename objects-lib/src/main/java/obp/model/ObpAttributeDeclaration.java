package obp.model;

import lombok.Data;

@Data
public class ObpAttributeDeclaration {
    private String attributeName;
    private ObpDataFillType fillType;

    public boolean isValid() {
        return true;
    }
}
