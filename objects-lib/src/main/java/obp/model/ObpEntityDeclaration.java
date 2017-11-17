package obp.model;


import lombok.Data;
import org.springframework.util.Assert;

import java.util.*;

@Data
public class ObpEntityDeclaration {
    private String id;
    private String name;

    /**
     * This is only used to translate prop declarations to and from Mongo.  There is probably a
     * more elegant way to translate this to Mongo without exposing it to the API.
     */
    private Collection<String> propertyDeclarationIds;

    public Collection<String> getPropertyDeclarationIds() {
        if (propertyDeclarationIds == null) {
            propertyDeclarationIds = new HashSet<String>();
        }
        return propertyDeclarationIds;
    }

    public ObpEntityDeclaration(String name) {
        Assert.notNull(name, "Name cannot be null");
        setName(name.trim());
    }


    public void addPropertyDeclaration(String aPropertyDeclarationId) {
        this.getPropertyDeclarationIds().add(aPropertyDeclarationId);
    }

    public boolean isValid() {
        if (getName() == null || getName().length() < 1) {
            return false;
        }
        // else, ...
        return true;
    }

}