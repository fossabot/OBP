package obp.neo4j.export;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import lombok.Data;
import obp.object.OBPBaseClass;

@Data
@JsonTypeInfo(include=As.WRAPPER_OBJECT, use=Id.NAME)
public abstract class ElementNode {
	 private String mongoId;
	 private String name;
	 private String type;

	 public ElementNode(OBPBaseClass mongoObj)
	 {
		 mongoId = mongoObj.getId();
		 name = mongoObj.getName();
		 type = mongoObj.getType();
	 }
}
