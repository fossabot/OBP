package obp.object;

import java.util.Date;
import java.util.Map;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
import obp.neo4j.export.ElementNode;
import obp.util.CustomDateTimeDeserializer;
import obp.util.CustomDateTimeSerializer;

@Data
public abstract class OBPBaseClass {
   public static String ENTITY_NAME; 
  
	 @Id private String id;
	 private String name;

	 @JsonSerialize(using=CustomDateTimeSerializer.class)
	 @JsonDeserialize(using=CustomDateTimeDeserializer.class)
	 private Date lastUpdated;
	 private String lastUpdatedBy;
	 private Location location;
	 private Map<String,Object> otherAttributes;
	 private String type;
	 public abstract ElementNode createElementNode();

}
