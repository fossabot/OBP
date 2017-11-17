package obp.neo4j.export;

import lombok.Data;
import lombok.EqualsAndHashCode;
import obp.object.Event;


@Data
@EqualsAndHashCode(callSuper=true)
public class EventNode extends ElementNode {
	private String type;
	
	public EventNode(Event mongoObj){
		super(mongoObj);

	}
}
