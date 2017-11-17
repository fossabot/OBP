package obp.object;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import obp.neo4j.export.ElementNode;
import obp.neo4j.export.EventNode;
import obp.util.CustomDateSerializer;
import obp.util.CustomDateDeserializer;

@Data
@EqualsAndHashCode(callSuper=true)
@Document
public class Event extends OBPBaseClass{
  public static String ENTITY_NAME; 
  
  @JsonSerialize(using=CustomDateSerializer.class)
  @JsonDeserialize(using=CustomDateDeserializer.class)
  private Date startDate;
  @JsonSerialize(using=CustomDateSerializer.class)
  @JsonDeserialize(using=CustomDateDeserializer.class)
  private Date endDate;
  
  public Event() {
    this.ENTITY_NAME="Event";
  }
  
  @Override
  public ElementNode createElementNode()
  {
    ElementNode newNode = new EventNode(this);
    return newNode;
  }

}
