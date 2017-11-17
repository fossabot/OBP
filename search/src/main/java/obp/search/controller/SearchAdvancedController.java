package obp.search.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;
import obp.common.JsonRestResponse;
import obp.search.repo.SearchableRepository;
import obp.service.SearchHelperService;

@RestController
@CrossOrigin
public class SearchAdvancedController {
    private Collection<SearchableRepository> repos;
    private SearchHelperService searchHelperService;
    private MongoTemplate mongoTemplate;

    @Autowired
    public SearchAdvancedController(Collection<SearchableRepository> repos, 
            SearchHelperService searchHelperService,
            MongoTemplate mongoTemplate) {
        this.repos = repos;
        this.searchHelperService = searchHelperService;
        this.mongoTemplate = mongoTemplate;
    }

    /**
     *  Search using AND condition
     * @param searchTerms JSON pairs Example:"{ "Name": "Ohio", "Type": "F-16" }";
     * @return
     */
    @PreAuthorize("#oauth2.hasScope('ROLE_RO')")
    @RequestMapping(value = {"/search_advanced/{searchTerms}", ""}, method = RequestMethod.GET)
    public JsonRestResponse searchAdvanced(@PathVariable String searchTerms) {
    	JsonRestResponse restResponse = JsonRestResponse.getInstance();
        //String strInput = "{ \"Name\": \"Ohio\", \"Type\": \"F-16\" }";
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> myInputMap = null;
        try {
            myInputMap = mapper.readValue(searchTerms, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
    		restResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR);
    		restResponse.setMessage("System Error");
    		return restResponse;
        }

        Map<String, Object> returnMap = new HashMap<>();
        if (searchTerms == null || searchTerms.isEmpty() || myInputMap.isEmpty()) {
    		restResponse.setCode(HttpStatus.OK);
    		restResponse.setData(returnMap);
    		return restResponse;
        }

        List<Criteria> criterias = new ArrayList<>();
        try {
			for (Map.Entry<String, Object> entry : myInputMap.entrySet()) {
				criterias.add(searchHelperService.getProperCriteria(entry.getKey(), entry.getValue()));
			}
			Criteria andCriteria = new Criteria();
			andCriteria.andOperator(criterias.toArray(new Criteria[criterias.size()]));
			Query query = new Query(andCriteria);
			List<Object> myList = null;
			for (SearchableRepository repo : repos) {
				Class genericType = GenericTypeResolver.resolveTypeArgument(repo.getClass(),
						SearchableRepository.class);
				String typeName = genericType.getSimpleName();
				myList = mongoTemplate.find(query, genericType);
				returnMap.put(typeName, myList);
			} 
		} catch (Exception e) {
            e.printStackTrace();
    		restResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR);
    		restResponse.setMessage("System Error");
    		return restResponse;
		}
		restResponse.setCode(HttpStatus.OK);
		restResponse.setData(returnMap);
		return restResponse;
    }

    @PreAuthorize("#oauth2.hasScope('ROLE_RO')")
    @RequestMapping(value = {"/search_loc/{searchTerms}", ""}, method = RequestMethod.GET)
    public JsonRestResponse searchLocation(@PathVariable String searchTerms) {
    	JsonRestResponse restResponse = JsonRestResponse.getInstance();
        String strInput = "{ \"lat\": \"39.94166\", \"lon\": \"-94.830246\" }";
        ObjectMapper mapper = new ObjectMapper();

        Map<String, String> myInputMap = null;
        try
        {
            myInputMap = mapper.readValue(strInput, Map.class);
        } 
        catch (Exception e) {
            e.printStackTrace();
    		restResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR);
    		restResponse.setMessage("System Error");
    		return restResponse;
        }

        Map<String, Object> returnMap = new HashMap<>();
        if (searchTerms == null || searchTerms.isEmpty() || myInputMap.isEmpty()) {
    		restResponse.setCode(HttpStatus.OK);
    		restResponse.setData(returnMap);
    		return restResponse;
        }

        List<Object> myList = null;
        Point location = new Point(-73.99171, 40.738868);
        //NearQuery query = NearQuery.near(location).maxDistance(new Distance(10, Metrics.MILES));
        try {
			Query myLocQuery = new Query(Criteria.where("location").near(location).maxDistance(10000));

			for (SearchableRepository repo : repos) {
			    Class genericType = GenericTypeResolver.resolveTypeArgument(repo.getClass(), SearchableRepository.class);
			    String typeName = genericType.getSimpleName();
			    myList = mongoTemplate.find(myLocQuery, genericType);
			    returnMap.put(typeName, myList);
			}
		} catch (Exception e) {
			e.printStackTrace();
    		restResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR);
    		restResponse.setMessage(e.getMessage());
    		return restResponse;
		}

        myList = null;
        
		restResponse.setCode(HttpStatus.OK);
		restResponse.setData(returnMap);
		return restResponse;
    }

    /**  Search using logical JSON query string
     *
     * 	  Search examples: {"$and":[{"Type":"Ship"}, {"$or":[{"Name":"Richmond"},{"Name":"Ohio"}]}]}
     *							{"$or":[{"Name":"Ohio"},{"Type": "F-16"}]}
     *							{"$and":[{"Name":"Ohio"},{"Type": "F-16"}]}
     * @param searchTerms JSON logical JSON query string
     * @return
     */
    @PreAuthorize("#oauth2.hasScope('ROLE_RO')")
    @RequestMapping(value = {"/search_logic/{searchTerms}", ""}, method = RequestMethod.GET)
    public JsonRestResponse searchLogic(@PathVariable String searchTerms) {
    	JsonRestResponse restResponse = JsonRestResponse.getInstance();
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> returnMap = new HashMap<>();
        if (searchTerms == null || searchTerms.isEmpty() ) {
    		restResponse.setCode(HttpStatus.OK);
    		restResponse.setData(returnMap);
    		return restResponse;
        }

        BasicQuery my_query = new BasicQuery(searchTerms);

        List<Object> myList = null;

        try {
			for (SearchableRepository repo : repos) {
				Class genericType = GenericTypeResolver.resolveTypeArgument(repo.getClass(),
						SearchableRepository.class);
				String typeName = genericType.getSimpleName();
				myList = mongoTemplate.find(my_query, genericType);
				returnMap.put(typeName, myList);
			} 
		} catch (Exception e) {
            e.printStackTrace();
    		restResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR);
    		restResponse.setMessage(e.getMessage());
    		return restResponse;
		}
		restResponse.setCode(HttpStatus.OK);
		restResponse.setData(returnMap);
		return restResponse;
    }

}
