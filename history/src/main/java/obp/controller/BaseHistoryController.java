package obp.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import obp.services.HistoryServiceImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import obp.repo.ReadOnlyRepository;

@CrossOrigin
@RequestMapping
public abstract class BaseHistoryController<T> {
    
    private Map<String, String> env = System.getenv();


    private HistoryServiceImpl<T> historyService;
    
    BaseHistoryController(ReadOnlyRepository<T, String> repo, Class<T> thisType) {
        historyService= new HistoryServiceImpl(repo, thisType);
    }



    /**
     * finds an object based on unique id of the entity parent
     *
     * @param id
     *          id of object
     * @param timerange
     *          timerange is a enum that represents chucks of time to return
     * @return object specified by id history
     */
    @PreAuthorize("#oauth2.hasScope('ROLE_RO')")
    @RequestMapping(value = "/{id}",  params = { "timerange"}, method = RequestMethod.GET)
    public List<T> get(@PathVariable String id,@RequestParam("timerange") String timerange) {
        if(timerange !=null && !timerange.isEmpty())
            return historyService.getByParentId(id,timerange);
        else
            return historyService.getByParentId(id);
    }
    /**
     * finds an object based on unique id of the entity parent
     *
     * @param id
     *          id of object
     * @return object specified by id history
     */
    @PreAuthorize("#oauth2.hasScope('ROLE_RO')")
    @RequestMapping(value = "/{id}",  method = RequestMethod.GET)
    public List<T> get(@PathVariable String id) {
            return historyService.getByParentId(id);
    }

    protected String extractJwt(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        String jwt = "";

        if (bearer != null) {
            String[] split = bearer.split(" ");
            if (split.length >= 2) {
                jwt = split[1];
            }
        }

        return jwt;
    }

    /**
     * finds location specific properties on unique id of the entity parent
     *
     * @param id
     *          id of object
     * @return object specified by id
     */
    @PreAuthorize("#oauth2.hasScope('ROLE_RO')")
    @RequestMapping(value = "/{id}/location",  params = { "timerange"}, method = RequestMethod.GET)
    public List<T> getLocation(@PathVariable String id,@RequestParam("timerange") String timerange) {
        if(timerange !=null && !timerange.isEmpty())
            return historyService.getLocationByParentId(id,timerange);
        else
            return historyService.getLocationByParentId(id);
    }

    /**
     * finds an object based on unique id of the entity parent
     *
     * @param id
     *          id of object
     * @return object specified by id history
     */
    @PreAuthorize("#oauth2.hasScope('ROLE_RO')")
    @RequestMapping(value = "/{id}/location",  method = RequestMethod.GET)
    public List<T> getLocation(@PathVariable String id) {
        return historyService.getLocationByParentId(id);
    }

}
