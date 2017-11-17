package obp.search.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import obp.neo4j.export.ElementNode;
import obp.object.*;
import obp.search.repo.SearchableRepository;

@RestController
@CrossOrigin
public class SearchAll {

    private Collection<SearchableRepository> repos;
    
    private Map<String, String> env = System.getenv();
    private String NEO4J_BASE = env.getOrDefault("SEARCH_NEO4J_BASE", "https://obp-05.esl.saic.com:8083");
    private String theNeo4jServiceURI = NEO4J_BASE + "/bulk_import";

    @Autowired MongoOperations mongoOperation;
    @Autowired
    public SearchAll(Collection<SearchableRepository> repos) {
        this.repos = repos;
    }

    @PreAuthorize("#oauth2.hasScope('ROLE_RWO')")
    @RequestMapping(value = {"/export_all", ""}, method = RequestMethod.PUT)
    public String loadAllRecordsToNeo4j() {

        RestTemplate restTemplate = new RestTemplate(); 
        List<ElementNode> myNodeList = new ArrayList<ElementNode>();
        postToNeo4JService requestObj = new postToNeo4JService();
        for (SearchableRepository repo : repos) {
            Class genericType = GenericTypeResolver.resolveTypeArgument(repo.getClass(), SearchableRepository.class);
            //String typeName = genericType.getSimpleName();
            List<OBPBaseClass> myList = mongoOperation.findAll(genericType);

            // Build Neo4j nodes
            for(OBPBaseClass mongoObj : myList){
                ElementNode myNode = mongoObj.createElementNode();
                myNodeList.add(myNode);
            }
        }

        requestObj.myNodeList = myNodeList;
        String response = restTemplate.postForObject(theNeo4jServiceURI, requestObj, String.class);
        response = "Send to Neo4j " + Integer.toString(myNodeList.size()) + " objects. Failed: " + response +".";
        return response;
    }
}
