package obp.services;

import obp.model.ObpEntity;
import obp.model.ObpEntityDeclaration;
import obp.model.ObpProperty;
import obp.model.ObpPropertyDeclaration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class EntityValidator {

    @Autowired
    private ObpEntityDeclarationFactory edFactory;

    @Autowired
    private ObpPropertyDeclarationFactory pdFactory;

    public boolean validate(ObpEntity entity) {
        if(! entity.isValid()) {
            return false;
        } // otherwise, continue...

        if(! this.ensureAllPropertiesExist(entity)) { return false; }
        // otherwise, continue...

        if(! this.allPropertiesValid(entity)) { return false; }
        // otherwise, continue...

        // else, ...
        return true;
    }

    private boolean ensureAllPropertiesExist(ObpEntity entity) {
        ObpEntityDeclaration ed = this.edFactory.getEntityDeclarationById(entity.getEntityDeclarationId());
        Collection<ObpProperty> properties = entity.getProperties();
        Map<String, ObpProperty> propsById = this.makePropertiesByPropDeclarationId(properties);
        for (String propertyDeclarationId : ed.getPropertyDeclarationIds()) {
            ObpProperty current = propsById.get(propertyDeclarationId);
            if (current == null) {
                return false;
            }
        }
        // else, all were found
        return true;
    }

    private boolean allPropertiesValid(ObpEntity entity) {
        Map<String, ObpPropertyDeclaration> propDeclarationsById
                = this.createPropertDeclarationsByPropDeclarationId(entity);
        for(ObpProperty prop : entity.getProperties()) {
            ObpPropertyDeclaration propertyDeclaration = propDeclarationsById.get(prop.getPropertyDeclarationId());
            if(! propertyDeclaration.validate(prop)) {
                return false;
            }
        }
        // otherwise, they're all good
        return true;
    }

    private Map<String, ObpProperty> makePropertiesByPropDeclarationId(Collection<ObpProperty> properties) {
        Map<String, ObpProperty> group = new HashMap<>(properties.size());
        for(ObpProperty prop : properties) {
            group.put(prop.getPropertyDeclarationId(), prop);
        }
        return group;
    }


    private Map<String, ObpPropertyDeclaration> createPropertDeclarationsByPropDeclarationId(ObpEntity entity) {
        ObpEntityDeclaration ed = this.edFactory.getEntityDeclarationById(entity.getEntityDeclarationId());
        Collection<String> propDeclarationIds = ed.getPropertyDeclarationIds();
        Map<String, ObpPropertyDeclaration> propDeclarationsById = new HashMap<>(propDeclarationIds.size());
        for(String propDeclarationId : propDeclarationIds) {
            ObpPropertyDeclaration pd = this.pdFactory.getPropertyDeclarationById(propDeclarationId);
            propDeclarationsById.put(pd.getId(), pd);
        }
        return propDeclarationsById;
    }
}
