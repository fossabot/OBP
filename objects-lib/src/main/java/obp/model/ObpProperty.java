package obp.model;

import lombok.Data;

@Data
public class ObpProperty {
    private String propertyDeclarationId;
    private ObpPropertyValue value;

    public ObpProperty() {
    }

    public ObpProperty(ObpPropertyDeclaration aPropertyDeclaration) {
        setPropertyDeclarationId(aPropertyDeclaration.getId());
        //setValue(aPropertyDeclaration.getDefaultValue());
    }

    public ObpPropertyValue getValue() {
        if (value == null) {
            value = new ObpPropertyValue();
        }
        return value;
    }


    public ObpProperty(String propertyDeclarationId) {
        this.propertyDeclarationId = propertyDeclarationId;
    }

}
