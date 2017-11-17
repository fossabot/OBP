package obp.neo4j.export;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import lombok.Data;
import lombok.EqualsAndHashCode;
import obp.object.Equipment;

@Data
@EqualsAndHashCode(callSuper=true)
@JsonTypeInfo(include=As.WRAPPER_OBJECT, use=Id.NAME)
public class EquipmentNode extends ElementNode {

	
	public EquipmentNode(Equipment mongoObj){
		super(mongoObj);

	}
}
