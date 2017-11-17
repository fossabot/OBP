package obp.services;

import obp.model.ObpEntityDeclaration;
import obp.model.ObpSlimEntityDeclaration;
import obp.repo.ObpEntityDeclarationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ObpEntityDeclarationFactory {

    @Autowired
    private ObpEntityDeclarationRepository edRepo;

    @Autowired
    private ObpPropertyDeclarationFactory pdFactory;

    @Autowired
    private EntityDeclarationValidator entityDeclarationValidator;

    private Map<String, ObpEntityDeclaration> entityDeclarations;

    public Collection<ObpEntityDeclaration> getEntityDeclarationList() {
        return getEntityDeclarations().values();
    }

    public Map<String, ObpEntityDeclaration> getEntityDeclarations() {
        // For customer demos, this needs real-time or near-real-time information.
        // However, for production use, this data is not updated frequently and can
        // be cached with infrequent updates.
        Iterable<ObpEntityDeclaration> edIterable = this.edRepo.findAll();
        Map<String, ObpEntityDeclaration> eds = new HashMap<>();
        Iterator<ObpEntityDeclaration> edIterator = edIterable.iterator();
        while(edIterator.hasNext()) {
            ObpEntityDeclaration ed = edIterator.next();
            eds.put(ed.getName(), ed);
        }
        this.entityDeclarations = eds;
        return entityDeclarations;
    }

    public List<ObpSlimEntityDeclaration> getSlimEntityDeclarations() {
        List<ObpSlimEntityDeclaration> result = new ArrayList<>();
        Iterator<ObpEntityDeclaration> entityDeclarationIterator = getEntityDeclarations().values().iterator();
        while (entityDeclarationIterator.hasNext()) {
            ObpEntityDeclaration current = entityDeclarationIterator.next();
            result.add(new ObpSlimEntityDeclaration(current));
        }
        return result;
    }

    /**
     * If not already created (by name) this will persist a new name to the DB.
      * @param name
     * @return
     */
    public ObpEntityDeclaration getOrCreateEntityDeclaration(String name) {
        ObpEntityDeclaration entityDeclaration = getEntityDeclarationByName(name);
        if (entityDeclaration == null) {
            entityDeclaration = createEntityDeclaration(name);
        }
        return entityDeclaration;
    }

    public ObpEntityDeclaration getEntityDeclarationByName(String name) {
        ObpEntityDeclaration entityDeclaration = getEntityDeclarations().get(name);
        return entityDeclaration;
    }

    public ObpEntityDeclaration getEntityDeclarationById(String id) {
        for(ObpEntityDeclaration ed : this.getEntityDeclarations().values()) {
            if( id.equals(ed.getId())) {
                return ed;
            }
        }
        // else, none was found
        throw new IllegalStateException(id + " was not found");
    }
    private synchronized ObpEntityDeclaration createEntityDeclaration(String name) {
        ObpEntityDeclaration entityDeclaration = getEntityDeclarations().get(name);
        if (entityDeclaration == null) {
            entityDeclaration = new ObpEntityDeclaration(name);
            if(this.entityDeclarationValidator.validate(entityDeclaration)) {
                ObpEntityDeclaration newVersion = this.edRepo.save(entityDeclaration);
                this.getEntityDeclarations().put(name, newVersion);
                return newVersion;
            } else {
                throw new IllegalStateException("entity declaration not valid");
            }
        } else {
            throw new IllegalStateException(name + " already exists");
        }
    }

    /**
     * This persists the association to the DB.
     * @param anEntityDeclaration
     * @param aPropertyDeclarationId
     * @return
     */
    public ObpEntityDeclaration associate(ObpEntityDeclaration anEntityDeclaration, String aPropertyDeclarationId) {
        anEntityDeclaration.addPropertyDeclaration(aPropertyDeclarationId);
        if(this.entityDeclarationValidator.validate(anEntityDeclaration)) {
            ObpEntityDeclaration newVersion = this.edRepo.save(anEntityDeclaration);
            this.entityDeclarations.put(newVersion.getName(), newVersion);
            return newVersion;
        } else {
            throw new IllegalStateException("entity declaration not valid");
        }
    }

}
