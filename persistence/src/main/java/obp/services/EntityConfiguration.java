package obp.services;

import obp.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/entityConfiguration/")
public class EntityConfiguration {
    @Autowired
    private ObpEntityDeclarationFactory edFactory;
    @Autowired
    private ObpPropertyDeclarationFactory pdFactory;
    @Autowired
    private ObpEntityFactory entityFactory;


    @RequestMapping(value = "/associate/{anEntityDeclarationName},{aPropertyDeclarationId}", method = RequestMethod.PUT)
    private ObpEntityDeclaration associate(@PathVariable String anEntityDeclarationName, @PathVariable String aPropertyDeclarationId) {
        ObpEntityDeclaration anEntityDeclaration = edFactory.getEntityDeclarationByName(anEntityDeclarationName);
        edFactory.associate(anEntityDeclaration, aPropertyDeclarationId);
        return anEntityDeclaration;
    }
    @RequestMapping(value = "/propertyDeclaration/create/{propertyName},{dataType}", method = RequestMethod.PUT)
    private ObpPropertyDeclaration createPropertyDeclaration(@PathVariable String propertyName, @PathVariable String dataType) {
        ObpPropertyDeclaration result = pdFactory.createPropertyDeclaration(propertyName, ObpDataType.getForName(dataType));

        return result;
    }
    @RequestMapping(value = "/dataType/all", method = RequestMethod.GET)
    private Collection<ObpDataType> getDataTypes() {
        return ObpDataType.getValues();
    }
    @RequestMapping(value = "/dataType/{dataTypeName}", method = RequestMethod.GET)
    private ObpDataType getDataType(@PathVariable String dataTypeName) {
        return ObpDataType.getForName(dataTypeName);
    }
    @RequestMapping(value = "/propertyDeclaration/all", method = RequestMethod.GET)
    private Collection<ObpPropertyDeclaration> getPropertyDeclarations() {
        return pdFactory.getPropertyDeclarations();
    }

    @RequestMapping(value = "/all/", method = RequestMethod.GET)
    private Iterable<ObpEntityDeclaration> getEntityDeclarations() {
        return edFactory.getEntityDeclarationList();
    }

    @RequestMapping(value = "/slimEntities/", method = RequestMethod.GET)
    public List<ObpSlimEntityDeclaration> getAllSlimEntities() {
        return edFactory.getSlimEntityDeclarations();
    }

    @RequestMapping(value = "/entityDeclaration/create/{name}", method = RequestMethod.PUT)
    public ObpEntityDeclaration createEntityDeclaration(@PathVariable String name) {
        ObpEntityDeclaration entityDeclaration = edFactory.getOrCreateEntityDeclaration(name);
        return entityDeclaration;
    }
    @RequestMapping(value = "/entity/create/{name}", method = RequestMethod.GET)
    public ObpEntity createEntity(@PathVariable String name) {
        ObpEntityDeclaration entityDeclaration = edFactory.getEntityDeclarationByName(name);
        ObpEntity newEntity = entityFactory.createFullEntity(entityDeclaration);

        return newEntity;
    }
}
