package obp.model;

import lombok.Data;

import java.util.Collection;

@Data
public class ObpPropertyDeclaration {
    private String id;
    private String name;
    private ObpDataType type;
    private Collection<String> attributeDeclarationIds;


    public ObpProperty createProperty() {
        return new ObpProperty(this.getId());
    }

    public void setValueFromString(ObpProperty property, String anInput) {
        ObpDataValue newValue = this.getType().newObpDataValue(anInput);
        property.getValue().setValue(newValue);
    }

    public boolean isValid() {
        if (getName() == null || getName().length() < 1) {
            return false;
        }
        if (getType() == null || !getType().isValid()) {
            return false;
        }
        return true;
    }

    public boolean validate(ObpProperty aProperty) {
        return true;
    }
}
