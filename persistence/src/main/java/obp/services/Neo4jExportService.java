package obp.services;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import obp.neo4j.export.ElementNode;
import obp.object.OBPBaseClass;

@Service
public class Neo4jExportService {
    private Map<String, String> env = System.getenv();
    private String theNeo4jServiceURI = env.getOrDefault("PERSISTENCE_NEO4J_BASE", "https://obp-05.esl.saic.com:8083");
    public String createNodeBeanName = "/create_node";
    public String updateNodeBeanName = "/update_node";
    public String deleteNodeBeanName = "/delete_node";


    public String modifyNode(OBPBaseClass mongoObj, String nameNeo4jBean, String jwt)
    {
        String service_call = theNeo4jServiceURI + nameNeo4jBean + "?jwt=" + jwt;

        ElementNode newNode = mongoObj.createElementNode();
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwt);

        HttpEntity<ElementNode> entity = new HttpEntity<>(newNode, headers);
        ResponseEntity<String> response = restTemplate.exchange(service_call, HttpMethod.POST, entity, String.class);
        return response.getBody();
    }

}
