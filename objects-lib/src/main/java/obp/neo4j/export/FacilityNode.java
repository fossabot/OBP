package obp.neo4j.export;

import lombok.Data;
import lombok.EqualsAndHashCode;
import obp.object.Facility;


@Data
@EqualsAndHashCode(callSuper=true)
public class FacilityNode extends ElementNode {

	public FacilityNode(Facility mongoObj){
		super(mongoObj);
	}

}
