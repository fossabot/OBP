package obp.search.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import obp.common.JsonRestResponse;
import obp.search.repo.SearchableRepository;
import obp.service.SearchHelperService;

@RestController
@RequestMapping("/search")
@CrossOrigin
public class SearchController {
    private Collection<SearchableRepository<?>> repos;
    private SearchHelperService searchHelperService;
    private MongoTemplate mongoTemplate;

    @Autowired
    public SearchController(Collection<SearchableRepository<?>> repos, 
            SearchHelperService searchHelperService,
            MongoTemplate mongoTemplate) {
        this.repos = repos;
        this.searchHelperService = searchHelperService;
        this.mongoTemplate = mongoTemplate;
    }

    @PreAuthorize("#oauth2.hasScope('ROLE_RO')")
    @RequestMapping(value = {"/{searchTerms}", ""}, method = RequestMethod.GET)
    public JsonRestResponse searchAll(@PathVariable String searchTerms, HttpServletRequest request) {
    	JsonRestResponse restResponse = JsonRestResponse.getInstance();
        Map<String, Object> returnMap = new HashMap<>();
        Query query = searchHelperService.createOrDateQuery(searchTerms);
        String datelessSearchTerms = searchHelperService.removeDates(searchTerms);
        TextCriteria textCriteria = null;
        
        if (datelessSearchTerms != null && !datelessSearchTerms.isEmpty()) {
            textCriteria = allOrCriteria(datelessSearchTerms);
        } 
        
        List<TextCriteria> dateTextCriterias = new ArrayList<>();
        for(String dateText : searchHelperService.allDates(searchTerms)) {
            dateTextCriterias.add(allOrCriteria("\"" + dateText + "\""));
        }
        
        try {
			for (SearchableRepository<?> repo : repos) {
			    Class<?> genericType = GenericTypeResolver.resolveTypeArgument(repo.getClass(), SearchableRepository.class);
			    Collection<Object> collection = new ArrayList<>();
			    
			    if (query != null) {
			        collection.addAll(mongoTemplate.find(query, genericType));
			    }
			    
			    if (textCriteria != null) {
			        collection.addAll(repo.findAllBy(textCriteria));
			    }
			    
			    for(TextCriteria dateTextCriteria : dateTextCriterias) {
			        collection.addAll(repo.findAllBy(dateTextCriteria));
			    }
			    
			    returnMap.put(genericType.getSimpleName(), collection);
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

    /**
     * Creates a search criteria as if there is an or between every word
     *
     * @param searchTerms terms to search on
     * @return text criteria object with terms stuffed inside
     */
    private TextCriteria allOrCriteria(String searchTerms) {
        String[] tokens = searchTerms.split(" ");
        return TextCriteria.forDefaultLanguage().matchingAny(tokens);
    }
}
