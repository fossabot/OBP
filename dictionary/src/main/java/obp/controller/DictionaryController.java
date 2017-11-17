package obp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import obp.object.Dictionary;
import obp.repo.DictionaryRepository;
import obp.service.CoreOrderService;

@RestController
@CrossOrigin
@RequestMapping("/dictionary")
public class DictionaryController {
    protected final DictionaryRepository repo;
    private final CoreOrderService coreOrderService;

    @Autowired
    public DictionaryController(DictionaryRepository repo, CoreOrderService coreOrderService) {
        this.coreOrderService = coreOrderService;
        this.repo = repo;
    }

    @PreAuthorize("#oauth2.hasScope('ROLE_RO')")
    @RequestMapping(method = RequestMethod.GET)
    public List<Dictionary> findAll() {
        List<Dictionary> all = repo.findAll();
        return all;
    }

    /**
     * finds an object based on unique id
     *
     * @param id id of object
     * @return object specified by id
     */
    @PreAuthorize("#oauth2.hasScope('ROLE_RO')")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Dictionary get(@PathVariable String id) {
        Dictionary one  = repo.findOne(id);
        return one;
    }


    /**
     * finds attributes of an object based on object type
     *
     * @param classType id of object
     * @return object specified by id
     */
    @PreAuthorize("#oauth2.hasScope('ROLE_RO')")
    @RequestMapping(value = "/find/{classType}", method = RequestMethod.GET)
    public List<Dictionary> findByClass(@PathVariable String classType) {

        return repo.findByClassesIgnoreCase(classType);
    }

    @PreAuthorize("#oauth2.hasScope('ROLE_RO')")
    @RequestMapping(value = "/findByDataType/{dataType}", method = RequestMethod.GET)
    public List<Dictionary> findByDataType(@PathVariable String dataType) {

        return repo.findByDataTypeIgnoreCase(dataType);
    }

    /**
     * finds an object's core properties
     *
     * @return object specified by id
     */
    @PreAuthorize("#oauth2.hasScope('ROLE_RO')")
    @RequestMapping(value = "/core", method = RequestMethod.GET)
    public List<String> findAllCore() {

        List<Dictionary> dList = repo.findByCoreTrue();
        return coreOrderService.order(dList);
    }

    /**
     * saves an object to store when the field hasn't been made before this save is only called on an insert path
     * @param object object to save
     * @return object persisted, including any info generated by the database
     */
    @PreAuthorize("#oauth2.hasScope('ROLE_RWCD')")
    @RequestMapping(method = RequestMethod.POST)
    public Dictionary save(@RequestBody Dictionary object) {
        if(object.getEnabled()) {
            List<Dictionary> allOfThem = this.findAll();
            for (Dictionary dictionary : allOfThem) {
                if (dictionary.getField().equalsIgnoreCase(object.getField())) {
                    return dictionary;
                }
            }
        }
        return repo.save(object);
    }

    /**
     * updates any fields of an object that are not null
     * @param object the collection of all objects, with modified fields non null
     * @return true if object updated, false if not
     */
    @PreAuthorize("#oauth2.hasScope('ROLE_RWO')")
    @RequestMapping(method = RequestMethod.PUT)
    public boolean update(@RequestBody List<Dictionary> object) {
        boolean successful = true;
        for (Dictionary d : object) {
            if (!repo.update(d)) {
                successful = false;
            }
        }
        return successful; 
    }

    /**
     * deletes an object
     * @param id id of object to delete
     * @return returns the object deleted
     */
    @PreAuthorize("#oauth2.hasScope('ROLE_RWCD')")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Dictionary delete(@PathVariable String id) {

        Dictionary deleted = get(id);
        repo.delete(id);
        return deleted;
    }
}