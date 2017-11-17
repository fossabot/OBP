package obp.services;

import obp.model.ObpEntity;
import obp.model.ObpProperty;
import obp.model.ObpPropertyDeclaration;
import org.springframework.stereotype.Repository;

@Repository
public class ObpPropertyFactory {
    public ObpProperty ensurePropertyExists(ObpEntity entity, ObpPropertyDeclaration propertyDeclaration) {
        ObpProperty currentProperty = entity.getPropertyOrNullByPropertyDeclarationId(propertyDeclaration.getId());
        if(currentProperty == null) {
            currentProperty = new ObpProperty(propertyDeclaration);
            entity.addProperty(currentProperty);
        }
        return currentProperty;
    }
}
