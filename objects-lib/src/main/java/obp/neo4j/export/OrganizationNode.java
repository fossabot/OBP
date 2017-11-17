package obp.neo4j.export;

import lombok.Data;
import lombok.EqualsAndHashCode;
import obp.object.Organization;


@Data
@EqualsAndHashCode(callSuper=true)
public class OrganizationNode extends ElementNode {

	public OrganizationNode(Organization mongoObj){
		super(mongoObj);
	}
}
