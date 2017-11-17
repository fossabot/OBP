package obp.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;

@Data
public class ObpEntity {
    private String id;
    private String entityDeclarationId;
    private Collection<ObpProperty> properties = new ArrayList<ObpProperty>();

    public ObpEntity(String entityDeclarationId) {
        this.entityDeclarationId = entityDeclarationId;
    }

    public ObpProperty getPropertyByPropertyDeclarationId(String propertyDeclarationId) {
        ObpProperty property = getPropertyOrNullByPropertyDeclarationId(propertyDeclarationId);
        if (property == null) {
            throw new IllegalStateException(propertyDeclarationId + " was not found");
        }
        return property;
    }

    public ObpProperty getPropertyOrNullByPropertyDeclarationId(String propertyDeclarationId) {
        for(ObpProperty prop : this.properties) {
            if(propertyDeclarationId.equals(prop.getPropertyDeclarationId())) {
                return prop;
            }
        }
        return null;
    }

    public boolean addProperty(ObpProperty aProperty) {
        return getProperties().add(aProperty);
    }

    public boolean isValid() {
        return true;
    }
}
