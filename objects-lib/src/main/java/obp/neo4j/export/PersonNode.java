package obp.neo4j.export;

import com.fasterxml.jackson.annotation.JsonRootName;

import lombok.Data;
import lombok.EqualsAndHashCode;
import obp.object.Person;


@Data
@EqualsAndHashCode(callSuper=true)
@JsonRootName(value = "Person")
public class PersonNode extends ElementNode {

	public PersonNode(Person mongoObj){
		super(mongoObj);
	}
}
