package obp.services;

import obp.model.ObpDataType;
import obp.model.ObpPropertyDeclaration;
import obp.repo.ObpPropertyDeclarationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Repository
public class ObpPropertyDeclarationFactory {

    @Autowired
    private ObpPropertyDeclarationRepository pdRepo;

    private Map<String, ObpPropertyDeclaration> propertyDeclarationsByName;

    public Collection<ObpPropertyDeclaration> getPropertyDeclarations() {
        return getPropertyDeclarationsByName().values();
    }

    public Map<String, ObpPropertyDeclaration> getPropertyDeclarationsByName() {
        // For customer demos, this needs real-time or near-real-time information.
        // However, for production use, this data is not updated frequently and can
        // be cached with infrequent updates.
        Iterable<ObpPropertyDeclaration> pdIterable = this.pdRepo.findAll();
        Map<String, ObpPropertyDeclaration> pds = new HashMap<>();
        Iterator<ObpPropertyDeclaration> edIterator = pdIterable.iterator();
        while(edIterator.hasNext()) {
            ObpPropertyDeclaration pd = edIterator.next();
            pds.put(pd.getName(), pd);
        }
        this.propertyDeclarationsByName = pds;
        return propertyDeclarationsByName;
    }

    public ObpPropertyDeclaration getPropertyDeclarationById(String id) {

        for(ObpPropertyDeclaration pd : this.getPropertyDeclarationsByName().values()) {
            if(pd.getId().equals(id)) { return pd; }
        }
        // if none was found, then...
        throw new IllegalStateException("id " + id + " not found!");
    }

    public ObpPropertyDeclaration getPropertyDeclarationNamed(String name) {
        ObpPropertyDeclaration propertyDeclaration = getPropertyDeclarationsByName().get(name);
        return propertyDeclaration;
    }

    public ObpPropertyDeclaration createPropertyDeclaration(String name, ObpDataType dataType) {
        ObpPropertyDeclaration propertyDeclaration = getPropertyDeclarationsByName().get(name);
        if (propertyDeclaration != null) {
            throw new IllegalStateException("Property Declaration " + name + " already exists");
        } //else, continue
        propertyDeclaration = new ObpPropertyDeclaration();
        propertyDeclaration.setName(name);
        propertyDeclaration.setType(dataType);
        this.pdRepo.save(propertyDeclaration);
        // update the in-memory data and retrieve new version with ID and any other DB instantiated data
        Map<String, ObpPropertyDeclaration> newGroup = this.getPropertyDeclarationsByName();
        return newGroup.get(name);
    }

    public ObpPropertyDeclaration setPropertyDeclarationType(String propertyDeclarationId, ObpDataType type) {
        ObpPropertyDeclaration propertyDeclaration = getPropertyDeclarationById(propertyDeclarationId);
        if (propertyDeclaration == null) {
            throw new IllegalStateException("Can't find Property Declaration " + propertyDeclarationId);
        } //else, continue
        propertyDeclaration.setType(type);
        ObpPropertyDeclaration newVersion = this.pdRepo.save(propertyDeclaration);
        // update the in-memory data
        this.getPropertyDeclarationsByName();
        return newVersion;
    }

}