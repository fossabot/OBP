package obp.neo4j;

import java.util.List;

import org.neo4j.ogm.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import obp.neo4j.data.ElementNode;
import obp.neo4j.repo.ElementRepository;

@Service
public class NodeService {
	
	@Autowired ElementRepository elementRep;

	// MATCH ()-[r]-() DELETE r
	// MATCH (n) DELETE n
	// MATCH (n:Facility) OPTIONAL MATCH (n)-[r]-() DELETE n, r
	public void cleanRepo(){
		elementRep.removeAllEquipment();
		elementRep.removeAllEvent();
		elementRep.removeAllFacility();
		elementRep.removeAllOrganization();
		elementRep.removeAllPerson();
	}

	public String saveNode(ElementNode my_node)
	{
		String serviceResult = "";
		
		if (my_node != null)
		{
			try
			{
				elementRep.save(my_node);
				serviceResult = "success";
			}
			catch(Exception ex)
			{
				serviceResult = "Error: Cannot save Neo4j object.";
			}
		}
		else
		{
			serviceResult = "Error: Cannot read Neo4j object.";
		}
		
		return serviceResult;
	}

	public String updateNode(ElementNode nodeFromMongo)
	{
		String serviceResult = "";
		
		if (nodeFromMongo != null)
		{
			String typeName = nodeFromMongo.getClass().getSimpleName();
			List<ElementNode> my_list = findElementByMongoId(typeName, nodeFromMongo.getMongoId());
			
			
			if ((my_list != null) && (my_list.size() > 0)) 
			{
				// Get The first node and update it. It should not multiple nodes with same mongoId
				ElementNode myNode = my_list.get(0);
				// Set Neo4j Id from existing object to new one and save it
				nodeFromMongo.setId(myNode.getId());
				try
				{
					elementRep.save(nodeFromMongo);
					serviceResult = "success";
				}
				catch(Exception ex)
				{
					serviceResult = "Error: Cannot update Neo4j object.";
				}
			}
			else
				serviceResult = "Error: Cannot update Neo4j object.";
		}
		else
		{
			serviceResult = "Error: Cannot read Neo4j object.";
		}
		
		return serviceResult;
	}
	
	public String deleteNode(ElementNode nodeFromMongo)
	{
		String serviceResult = "";
		
		if (nodeFromMongo != null)
		{
			String typeName = nodeFromMongo.getClass().getSimpleName();
			List<ElementNode> my_list = findElementByMongoId(typeName, nodeFromMongo.getMongoId());
			
			
			if ((my_list != null) && (my_list.size() > 0))
			{
				try	
				{
					for(ElementNode myNode : my_list)
					{
						elementRep.delete(myNode);
					}
					serviceResult = "success";
				}
				catch(Exception ex)
				{
					serviceResult = "Error: Cannot delete Neo4j object.";
				}
			}
			else
				serviceResult = "Error: Cannot delete Neo4j object.";
		}
		else
		{
			serviceResult = "Error: Cannot read Neo4j object.";
		}

		return serviceResult;
	}
	
	public List<ElementNode> findElementByMongoId(String className, String mongoId){
		if((className == null) || className.isEmpty())
			return null;
		if((mongoId == null) || mongoId.isEmpty())
			return null;
		if (className.equals("EquipmentNode"))
			return elementRep.findEquipmentByMongoId(mongoId);
		else if (className.equals("EventNode"))
				return elementRep.findEventByMongoId(mongoId);
		else if (className.equals("FacilityNode"))
			return elementRep.findFacilityByMongoId(mongoId);
		else if (className.equals("OrganizationNode"))
			return elementRep.findOrganizationByMongoId(mongoId);
		else if (className.equals("PersonNode"))
			return elementRep.findPersonByMongoId(mongoId);
		else return null;
 	}
	public List<ElementNode> getNode(String name){
		return elementRep.getNode(name);
	}
	public Result createLinks(String objectType1, String objectType2, String node1, String node2, String relationship){
		if("Owned By (Owns)".equalsIgnoreCase(relationship)){
			return elementRep.createOwnedByLinks(objectType1, objectType2, node1, node2);
		}else if("Housed  at (Houses)".equalsIgnoreCase(relationship)){ 
			return elementRep.createHousedAtLinks(objectType1, objectType2, node1, node2);
		}else if("Participated in (Had Participants)".equalsIgnoreCase(relationship)){ 
			return elementRep.createParticipatedLinks(objectType1, objectType2, node1, node2);
		}else if("Hosted at (Hosted)".equalsIgnoreCase(relationship)){ 
			return elementRep.createHostedAtLinks(objectType1, objectType2, node1, node2);
		}else if("Employed By (Employs)".equalsIgnoreCase(relationship)){ 
			return elementRep.createEmployedByLinks(objectType1, objectType2, node1, node2);
		}else if("Related to (Related to)".equalsIgnoreCase(relationship)){ 
			return elementRep.createRelatedToLinks(objectType1, objectType2, node1, node2);
		}else if("Associated With (Associated With)".equalsIgnoreCase(relationship)){ 
			return elementRep.createAssociatedWithLinks(objectType1, objectType2, node1, node2);
		}else if("Commanded By (Commands)".equalsIgnoreCase(relationship)){ 
			return elementRep.createCommandsLinks(objectType1, objectType2, node1, node2);
		}else if("Member of (Has Member of)".equalsIgnoreCase(relationship)){ 
			return elementRep.createMemberOfLinks(objectType1, objectType2, node1, node2);
		}else if("Present At (Was attended by)".equalsIgnoreCase(relationship)){
			return elementRep.createPresentAtLinks(objectType1, objectType2, node1, node2);
		}else if("Owns (Owned by)".equalsIgnoreCase(relationship)){
			return elementRep.createOwnsLinks(objectType1, objectType2, node1, node2);
		}
		return null;
	}
	public List<ElementNode> findAllNodesWithMongoId(){
		return elementRep.findAllNodesWithMongoId();
	}
	public Result findRelationBetweenTwoNodes(String objectType1, String objectType2, String node1, String node2){
		return elementRep.findRelationBetweenTwoNodes(objectType1, objectType2, node1, node2);
	}

	public Result findNodeAndLinks(String id) {
		return elementRep.findNodeAndLinks(id);
	}
}
