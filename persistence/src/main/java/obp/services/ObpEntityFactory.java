package obp.services;

import obp.model.*;
import obp.repo.ObpEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

@Repository
public class ObpEntityFactory {

    @Autowired
    private ObpPropertyDeclarationFactory pdFactory;

    @Autowired
    private ObpEntityRepository entityRepo;

    @Autowired
    private EntityValidator entityValidator;

    @Autowired
    private ObpPropertyFactory propertyFactory;

    /**
     * This does NOT persist the entity to the DB.
     * @param entityDeclaration
     * @return fully instantiated entity
     */
    public ObpEntity createFullEntity(ObpEntityDeclaration entityDeclaration) {
        ObpEntity entity = new ObpEntity(entityDeclaration.getId());
        for(String pdId : entityDeclaration.getPropertyDeclarationIds()) {
            ObpPropertyDeclaration currentPropertyDeclaration = pdFactory.getPropertyDeclarationById(pdId);
            propertyFactory.ensurePropertyExists(entity, currentPropertyDeclaration);
        }
        return entity;
    }

    /**
     * This does NOT persist the entity to the DB.
     * @param ed
     * @return
     */
    public ObpEntity createEntity(ObpEntityDeclaration ed) {
        return new ObpEntity(ed.getId());
    }

    /**
     * This does NOT persist the entity to the DB.
     * @param entity
     * @param propertyName
     * @param valueAsString
     */
    public void addProperty(ObpEntity entity, String propertyName, String valueAsString) {
        ObpPropertyDeclaration propDeclaration = this.pdFactory.getPropertyDeclarationNamed(propertyName);
        Assert.notNull(propDeclaration, "Property Declaration " + propertyName + " was not found");
        ObpProperty property = new ObpProperty(propDeclaration.getId());
        propDeclaration.setValueFromString(property, valueAsString);
        entity.addProperty(property);
    }

    public ObpEntity save(ObpEntity entity) {
        if(this.entityValidator.validate(entity)) {
            return this.entityRepo.save(entity);
        } else {
            throw new IllegalStateException("entity not valid");
        }
    }

}
