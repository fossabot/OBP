package obp.neo4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.response.model.RelationshipModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import obp.neo4j.data.ElementNode;
import obp.neo4j.data.vis.VisEdge;
import obp.neo4j.data.vis.VisNode;
import obp.neo4j.data.vis.VisRequest;
import obp.neo4j.data.vis.VisResponse;

// {"EquipmentNode":["{\"mongoId\":\"5900c6542a769fef31be9937\",\"name\":\"Mexico\",\"type\":\"F-22\"}"]}
//{"EquipmentNode":["{\"mongoId\":\"5900c62b2a769fef31be9935\",\"name\":\"Ohio\",\"type\":\"F-16\"}","{\"mongoId\":\"5901fac9b7112b132e60ef71\",\"name\":\"Ohio\",\"type\":\"Ship\"}"],
//    "PersonNode":["{\"mongoId\":\"59284c50b8b97c3f89674787\",\"name\":\"Ohio\",\"role\":\"Unknown\"}"]}
// {"EquipmentNode":{"mongoId":"5900c62b2a769fef31be9935","name":"Ohio","type":"F-16"}}
//{"PersonNode":{"mongoId":"59284c50b8b97c3f89674787","name":"Ohio","role":"Unknown"}}

@RestController
public class Neo4jServiceController {

    @Autowired NodeService nodeService;
    @PreAuthorize("#oauth2.hasScope('ROLE_RWCD')")
    @RequestMapping(value = "/get_node/{name1}/{name2}", method = RequestMethod.GET)
    public @ResponseBody VisResponse getNode(@PathVariable("name1") String name1, @PathVariable("name2") String name2,
    		HttpServletRequest request, HttpServletResponse response){
		request.setAttribute("Content-Type", "application/json");
		response.setHeader("Content-Type", "application/json");
		List<ElementNode> nodes = null;
		Result result = null;
		VisResponse visResponse = new VisResponse();
		visResponse.setNodes(new ArrayList<VisNode>());
		visResponse.setEdges(new ArrayList<VisEdge>());
		
		if(StringUtils.isNotEmpty(name1) && StringUtils.equalsIgnoreCase(name2, "undefined")){
			nodes = nodeService.getNode(name1);
			if(nodes.size()>0){
				for(ElementNode node : nodes){
					visResponse.getNodes().add(createVisNode(node.getMongoId(), node.getName(), "Person", 1,null,null));
				}
			}
		}else if(!StringUtils.equalsIgnoreCase(name1, "undefined") && !StringUtils.equalsIgnoreCase(name2, "undefined")){
			result = nodeService.findRelationBetweenTwoNodes("Person", "Person", name1, name2);
			populateGraph(visResponse, nodes, result, 1);
		}
		return visResponse;
    }

    @PreAuthorize("#oauth2.hasScope('ROLE_RWCD')")
    @RequestMapping(value = "/create_links", method = RequestMethod.POST)
    public @ResponseBody Result createLinks(@RequestBody VisRequest visRequest, 
    		HttpServletRequest request, HttpServletResponse response){
		request.setAttribute("Content-Type", "application/json");
		response.setHeader("Content-Type", "application/json");
		
		Result result = null;
		
		if(StringUtils.isNotEmpty(visRequest.getObject1())&&StringUtils.isNotEmpty(visRequest.getObject2())&&
				StringUtils.isNotEmpty(visRequest.getNode1())&&StringUtils.isNotEmpty(visRequest.getNode2())){
			result = nodeService.createLinks(visRequest.getObject1(),visRequest.getObject2(),visRequest.getNode1(),visRequest.getNode2(), visRequest.getRelationship());
		}
		return result;
    }

    @PreAuthorize("#oauth2.hasScope('ROLE_RWCD')")
    @RequestMapping(value = "/create_node", method = RequestMethod.POST)
    public String createNode(@RequestBody ElementNode my_node){
        return  nodeService.saveNode(my_node);
    }

    @PreAuthorize("#oauth2.hasScope('ROLE_RWO')")
    @RequestMapping(value = "/update_node", method = RequestMethod.POST)
    public String updateNode(@RequestBody ElementNode my_node){
        return nodeService.updateNode(my_node);
    }

    @PreAuthorize("#oauth2.hasScope('ROLE_RWCD')")
    @RequestMapping(value = "/delete_node", method = RequestMethod.POST)
    public String deleteNode(@RequestBody ElementNode my_node){
        return nodeService.deleteNode(my_node);
    }

    @PreAuthorize("#oauth2.hasScope('ROLE_RWCD')")
    @RequestMapping(value = "/bulk_import", method = RequestMethod.POST)
    public String bulkImport(@RequestBody postToNeo4JService myNodesObj){

        // Clean Up Repo
        nodeService.cleanRepo();

        int errorCount = 0;
        for(ElementNode my_node : myNodesObj.myNodeList)
        {
            if(!nodeService.saveNode(my_node).equals("success")){
                errorCount++;
            }
        }
        return Integer.toString(errorCount);
    }

    @RequestMapping(value = "/displaySelectedNode", method = RequestMethod.POST)
    public VisResponse getDisplaySelectedNode(@RequestBody VisRequest visRequest)
    {
		List<ElementNode> nodes = null;
		Result result = null;
		VisResponse visResponse = new VisResponse();
		visResponse.setNodes(new ArrayList<VisNode>());
		visResponse.setEdges(new ArrayList<VisEdge>());

		if(StringUtils.isNotEmpty(visRequest.getId())){
			result = nodeService.findRelationBetweenTwoNodes("Person", "Person", visRequest.getId(), "A");
			populateGraph(visResponse, nodes, result, 1);
		}else{
			result = nodeService.findNodeAndLinks(visRequest.getId());
			populateGraph(visResponse, nodes, result, 1);
		}
		return visResponse;
    }

    @RequestMapping(value = "/visualizeSpecificNode", method = RequestMethod.POST)
    public VisResponse visualizeSpecificNode(@RequestBody VisRequest visRequest)
    {
		VisResponse visResponse = new VisResponse();
		visResponse.setNodes(new ArrayList<VisNode>());
		visResponse.setEdges(new ArrayList<VisEdge>());

    	List<ElementNode> elementNodes = 
    			nodeService.findElementByMongoId(visRequest.getClassName(), visRequest.getId());
    	
    	if(elementNodes.size()!=1)
    	{
    		System.out.println( "element not found");
    	}
    	else
    	{
    		ElementNode elementNode = elementNodes.get(0);
    		
    		VisNode visNode = 
    				createVisNode(
    						elementNode.getMongoId(),
    						elementNode.getName(),
							elementNode.getCategory(),
    						0,
    			    		null,
							elementNode.getCategoryImageURL());

    		//TODO implement more details - for now load temp data as well
    		addTemporaryVisualData(visResponse,visNode,visRequest.getClassName(),visRequest.getDepth());
    	}
    	return visResponse;
    }
    
    @RequestMapping(value = "/visualizeAllNodes", method = RequestMethod.POST)
    public @ResponseBody VisResponse visualizeAllNodes(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response)
    {
		request.setAttribute("Content-Type", "application/json");
		response.setHeader("Content-Type", "application/json");
		VisResponse visResponse = new VisResponse();
		visResponse.setEdges(new ArrayList<VisEdge>());
		visResponse.setNodes(new ArrayList<VisNode>());
		
		//TODO implement
		addTemporaryVisualData(visResponse,null,null,5);
    	
    	return visResponse;
    }
    
    private VisNode createVisNode(
    		String id,
    		String label,
    		String category,
    		Integer level,
    		String imageURL,
    		String categoryImageURL)
    {
    	VisNode visNode = new VisNode();
    	visNode.setId(id);
    	visNode.setLabel(label);
    	visNode.setCategory(category);
    	visNode.setLevel(level);
    	visNode.setImageURL(imageURL);
    	visNode.setCategoryImageURL(categoryImageURL);
    	return visNode;
    }

    private VisEdge createVisEdge(
    		String fromId,
    		String toId,
    		String label)
    {
    	VisEdge visEdge = new VisEdge();
    	visEdge.setFromId(fromId);
    	visEdge.setToId(toId);
    	visEdge.setLabel(label);
    	return visEdge;
    }
    
    private VisEdge createVisEdge(
    		VisNode fromNode,
    		VisNode toNode,
    		String label)
    {
    	return createVisEdge(fromNode.getId(),toNode.getId(),label);
    }
    
    @SuppressWarnings("unchecked")
	private void populateGraph(
    		VisResponse visResponse, 
    		List<ElementNode> elementNodes, Result result,
    		Integer depth) {
    	if(elementNodes !=null && elementNodes.size()>0){
        	for(ElementNode node : elementNodes){
        		visResponse.getNodes().add(createVisNode(node.getMongoId(), node.getName(),"Person", 1,null,ElementNode.PERSON_URL));
        	}
    	}
    	if(result!=null){
	    	Iterator<?> i = result.iterator();
	    	while(i.hasNext()){
	    		LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) i.next();
	    		ElementNode node1 = null;
	    		ElementNode node2 = null;
	    		RelationshipModel r = null;
	    		for(String key : map.keySet()){
	    			if("r".equalsIgnoreCase(key)){
	    				//relationship
	    				r = (RelationshipModel) map.get(key);
	    				if(node1!=null && node2!=null){
		    	    		VisNode fromNode = new VisNode(node1.getMongoId(), node1.getName(),1);
		    	    		VisNode toNode = new VisNode(node2.getMongoId(), node2.getName(),1);
		    		    	visResponse.getEdges().add(this.createVisEdge(fromNode, toNode, r.getType()));
		    				System.out.println("Link created between "+ node1.getName() + " and " + node2.getName());
	    				}
	    			}else{
	    				//node
	    				if("a".equalsIgnoreCase(key)){
	    					if(map.get(key) instanceof ElementNode){
				    			node1 = (ElementNode) map.get(key);
				    			if(!alreadyExists(node1,visResponse.getNodes())){
				    				System.out.println("Added node: "+node1.getName()+" [MongoId: "+node1.getMongoId()+"]");
					        		visResponse.getNodes().add(createVisNode(node1.getMongoId(), node1.getName(),"Person", 1,null,ElementNode.PERSON_URL));
				    			}
	    					}
	    				}else if("b".equalsIgnoreCase(key) || "n".equalsIgnoreCase(key)){
	    					if(map.get(key) instanceof ElementNode){
				    			node2 = (ElementNode) map.get(key);
				    			if(!alreadyExists(node2,visResponse.getNodes())){
				    				System.out.println("Added node: "+node2.getName()+" [MongoId: "+node2.getMongoId()+"]");
					        		visResponse.getNodes().add(createVisNode(node2.getMongoId(), node2.getName(),"Person", 1,null,ElementNode.PERSON_URL));
				    			}
	    					}
	    				}
	    			}
	    		}
	    	}
    	}
    }

    private boolean alreadyExists(ElementNode p, Collection<VisNode> nodes) {
    	for(VisNode node : nodes){
    		if(p.getId()!=null && node.getId()!=null){
        		if(node.getId().equalsIgnoreCase(p.getMongoId())){
        			return true;
        		}
    		}
    	}
		return false;
	}

	@Deprecated
    private void addTemporaryVisualData(
    		VisResponse visResponse, 
    		VisNode primaryVisNode, 
    		String primaryClassName, 
    		Integer depth)
    {
    	if(primaryVisNode!=null)
    	{
    	    visResponse.getNodes().add(primaryVisNode);
    	}
    	
    	if(depth==null || depth >=1)
    	{
    		VisNode equipment = createVisNode("Equipment","Tank","Equipment",1,"https://upload.wikimedia.org/wikipedia/commons/thumb/5/56/XM1202_MCS.jpg/220px-XM1202_MCS.jpg",ElementNode.EQUIPMENT_URL);
	    	visResponse.getNodes().add(equipment);

    		if(primaryVisNode!=null)
        	{
	    	    visResponse.getEdges().add(
	    	    			createVisEdge(primaryVisNode,equipment,"Unknown Relationship"));
        	}
    		
    		if(depth==null || depth >= 2)
    		{
		    	VisNode event = createVisNode("Event","Event","Event",2,null, ElementNode.EVENT_URL);
		    	visResponse.getNodes().add(event);
		    	
		    	VisNode organization = createVisNode("Organization","SAIC","Organization",2,"https://www.chaffeybreeze.com/logos/science-applications-intl-logo.jpg",ElementNode.ORGANIZATION_URL);
		    	visResponse.getNodes().add(organization);
		    	
		    	VisNode person = createVisNode("Person","Person","Person",2,null,ElementNode.PERSON_URL);
		    	visResponse.getNodes().add(person);
		    	
		    	VisNode facility = createVisNode("Facility","Facility","Facility",2,null,ElementNode.FACILITY_URL);
		    	visResponse.getNodes().add(facility);
		    	
		    	// Edges for this level
		    	visResponse.getEdges().add(
		    			createVisEdge(equipment,organization,"Owned By (Owns)"));
		    	visResponse.getEdges().add(
		    			createVisEdge(equipment,person,"Owned By (Owns)"));
		    	visResponse.getEdges().add(
		    			createVisEdge(equipment,facility,"Housed  at (Houses)"));
		    	visResponse.getEdges().add(
		    			createVisEdge(equipment,event,"Participated in (Had Participants)"));
		    	visResponse.getEdges().add(
		    			createVisEdge(event,person,"Hosted by (Hosted)"));
		    	visResponse.getEdges().add(
		    			createVisEdge(event,organization,"Hosted by (Hosted)"));
		    	visResponse.getEdges().add(
		    			createVisEdge(event,facility,"Hosted at (Hosted)"));
		    	visResponse.getEdges().add(
		    			createVisEdge(organization,facility,"Owns (Owned by)"));
		    	visResponse.getEdges().add(
		    			createVisEdge(person,organization,"Employed By (Employs)"));
		    	visResponse.getEdges().add(
		    			createVisEdge(person,organization,"Associated With (Associated With)"));
		    	visResponse.getEdges().add(
		    			createVisEdge(person,facility,"Owns (Owned by)"));
    		
		    	if(depth==null || depth >= 3)
		    	{
		        	VisNode person2 = createVisNode("Person2","Fisherman","Person",3,"https://upload.wikimedia.org/wikipedia/commons/thumb/6/6d/Fisherman_and_his_catch_Seychelles.jpg/250px-Fisherman_and_his_catch_Seychelles.jpg",ElementNode.PERSON_URL);
		        	visResponse.getNodes().add(person2);
		        	
		        	VisNode person3 = createVisNode("Person3","Person3","Person",3,null,ElementNode.PERSON_URL);
		        	visResponse.getNodes().add(person3);
		        	
		        	VisNode person4 = createVisNode("Person4","Person4","Person",3,null,ElementNode.PERSON_URL);
		        	visResponse.getNodes().add(person4);
		        	
		        	VisNode organization2 = createVisNode("Organization2","USDA","Organization",3,"https://www.usda.gov/themes/usda/img/usda-symbol.svg",ElementNode.ORGANIZATION_URL);
		        	visResponse.getNodes().add(organization2);
		        	
		        	VisNode organization3 = createVisNode("Organization3","Organization3","Organization",3,null,ElementNode.ORGANIZATION_URL);
		        	visResponse.getNodes().add(organization3);
		        	
		        	VisNode event2 = createVisNode("Event2","Event2","Event",3,null,ElementNode.EVENT_URL);
		        	visResponse.getNodes().add(event2);

			    	// Edges for this level
			    	visResponse.getEdges().add(
			    			createVisEdge(person,person2,"Associated With (Associated With)"));
			    	visResponse.getEdges().add(
			    			createVisEdge(person,person3,"Related to (Related to)"));
			    	visResponse.getEdges().add(
			    			createVisEdge(person,person4,"Commanded By (Commands)"));
			    	visResponse.getEdges().add(
			    			createVisEdge(person,organization2,"Commanded By *(Commands)"));
			    	visResponse.getEdges().add(
			    			createVisEdge(person,organization3,"Member of (Has Member of)"));
			    	visResponse.getEdges().add(
			    			createVisEdge(person,event2,"Present At (Was attended by)"));
			    	
			    	if(depth==null || depth >= 4)
			    	{	
			    		VisNode previousNode = person4;
			    		if(depth==null)
			    		{
			    			depth = 10;
			    		}
			    		for(int x=4; x<=depth; x++)
			    		{
			    			VisNode tempNode1 = createVisNode("FakeNode"+x,"FakeNode"+x,"Unknown",x,null,ElementNode.UNKNOWN_URL);
			    			visResponse.getNodes().add(tempNode1);

			    			VisNode tempNode2 = createVisNode("AnotherFakeNode"+x,"AnotherFakeNode"+x,"Unknown",x,null,ElementNode.UNKNOWN_URL);
			    			visResponse.getNodes().add(tempNode2);

					    	// Edges for this level
					    	visResponse.getEdges().add(
					    			createVisEdge(previousNode,tempNode1,"Unknown Relationship"));
					    	visResponse.getEdges().add(
					    			createVisEdge(previousNode,tempNode2,"Unknown Relationship"));
			    			
					    	previousNode = tempNode1;
			    		}
			    	}
		    	}
    		}
    	}    	
    }
}

