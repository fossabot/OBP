package obp.services;

import obp.model.ObpEntityDeclaration;
import obp.model.ObpPropertyDeclaration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EntityDeclarationValidator {

    @Autowired
    private ObpPropertyDeclarationFactory pdFactory;

    public boolean validate(ObpEntityDeclaration entityDeclaration) {
        if(! entityDeclaration.isValid()) {
            return false;
        } // otherwise, continue...

        for (String propertyDeclarationId : entityDeclaration.getPropertyDeclarationIds()) {
            ObpPropertyDeclaration pd = this.pdFactory.getPropertyDeclarationById(propertyDeclarationId);
            if (! pd.isValid()) {
                return false;
            }
        }

        // else, ...
        return true;
    }
}
