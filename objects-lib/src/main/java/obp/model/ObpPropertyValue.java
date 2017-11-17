package obp.model;

import lombok.Data;

import java.util.Map;

@Data
public class ObpPropertyValue {
    private ObpDataValue value;
    private Map<String, ObpAttributeDeclaration> propertyAttributeDeclarations;
    private Map<String, ObpAttribute> propertyAttributes;
}
