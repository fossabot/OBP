package obp.neo4j.repo;

import java.util.List;

import org.neo4j.ogm.model.Result;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import obp.neo4j.data.ElementNode;


public interface ElementRepository extends GraphRepository<ElementNode>{
	
	//MATCH (el:Equipment {mongoId:"5900c62b2a769fef31be9935"}) RETURN el
	@Query("MATCH (el:Equipment {mongoId:{0}}) RETURN el")
	List<ElementNode> findEquipmentByMongoId( String mongoId);
	@Query("MATCH (el:Event {mongoId:{0}}) RETURN el")
	List<ElementNode> findEventByMongoId( String mongoId);
	@Query("MATCH (el:Facility {mongoId:{0}}) RETURN el")
	List<ElementNode> findFacilityByMongoId( String mongoId);
	@Query("MATCH (el:Organization {mongoId:{0}}) RETURN el")
	List<ElementNode> findOrganizationByMongoId( String mongoId);
	@Query("MATCH (el:Person {mongoId:{0}}) RETURN el")
	List<ElementNode> findPersonByMongoId( String mongoId);
	// Clean Up Queries
	@Query("MATCH (n:Equipment) OPTIONAL MATCH (n)-[r]-() DELETE n, r")
	void removeAllEquipment();
	@Query("MATCH (n:Event) OPTIONAL MATCH (n)-[r]-() DELETE n, r")
	void removeAllEvent();
	@Query("MATCH (n:Facility) OPTIONAL MATCH (n)-[r]-() DELETE n, r")
	void removeAllFacility();
	@Query("MATCH (n:Organization) OPTIONAL MATCH (n)-[r]-() DELETE n, r")
	void removeAllOrganization();
	@Query("MATCH (n:Person) OPTIONAL MATCH (n)-[r]-() DELETE n, r")
	void removeAllPerson();
	@Query("match (p:Person) where p.name =~ {0} return p;")
	//@Query("MATCH p=()-[r:FOLLOWS]->() RETURN p LIMIT 25")
	List<ElementNode> getNode(String name);
	//Result getNode(String name);
	@Query("match (p1:Person),(p2:Person) where p1.mongoId={2} and p2.name={3} create (p1)-[:Owned_By]->(p2);")
	Result createOwnedByLinks(String objectType1, String objectType2, String node1, String node2);
	@Query("match (p1:Person),(p2:Person) where p1.mongoId={2} and p2.name={3} create (p1)-[:Housed_at]->(p2);")
	Result createHousedAtLinks(String objectType1, String objectType2, String node1, String node2);
	@Query("match (p1:Person),(p2:Person) where p1.mongoId={2} and p2.name={3} create (p1)-[:Participated_in]->(p2);")
	Result createParticipatedLinks(String objectType1, String objectType2, String node1, String node2);
	@Query("match (p1:Person),(p2:Person) where p1.mongoId={2} and p2.name={3} create (p1)-[:Hosted_at]->(p2);")
	Result createHostedAtLinks(String objectType1, String objectType2, String node1, String node2);
	@Query("match (p1:Person),(p2:Person) where p1.mongoId={2} and p2.name={3} create (p1)-[:Employed_By]->(p2);")
	Result createEmployedByLinks(String objectType1, String objectType2, String node1, String node2);
	@Query("match (p1:Person),(p2:Person) where p1.mongoId={2} and p2.name={3} create (p1)-[:Related_to]->(p2);")
	Result createRelatedToLinks(String objectType1, String objectType2, String node1, String node2);
	@Query("match (p1:Person),(p2:Person) where p1.mongoId={2} and p2.name={3} create (p1)-[:Associated_With]->(p2);")
	Result createAssociatedWithLinks(String objectType1, String objectType2, String node1, String node2);
	@Query("match (p1:Person),(p2:Person) where p1.mongoId={2} and p2.name={3} create (p1)-[:Commanded_By]->(p2);")
	Result createCommandsLinks(String objectType1, String objectType2, String node1, String node2);
	@Query("match (p1:Person),(o1:Organization) where p1.mongoId={2} and o1.name={3} create (p1)-[:Member_of]->(o1);")
	Result createMemberOfLinks(String objectType1, String objectType2, String node1, String node2);
	@Query("match (p1:Person),(p2:Person) where p1.mongoId={2} and p2.name={3} create (p1)-[:Present_At]->(p2);")
	Result createPresentAtLinks(String objectType1, String objectType2, String node1, String node2);
	@Query("match (p1:Person),(p2:Person) where p1.mongoId={2} and p2.name={3} create (p1)-[:Owns]->(p2);")
	Result createOwnsLinks(String objectType1, String objectType2, String node1, String node2);
	@Query("MATCH (n) WHERE EXISTS(n.mongoId) RETURN n;")
	List<ElementNode> findAllNodesWithMongoId();
//	@Query("MATCH (p1:Person)-[]-> (p2:Person) WHERE p1.mongoId={2} and exists(p1.mongoId) and exists(p2.mongoId) RETURN *;")
	@Query("MATCH (a:Person)-[r]->(n) where a.mongoId = {2} RETURN *;")
	Result findRelationBetweenTwoNodes(String objectType1, String objectType2, String node1, String node2);
	@Query("MATCH (a:Person)-[r]->(n) where exists(a.mongoId) RETURN *;")
	Result findNodeAndLinks(String id);
}
