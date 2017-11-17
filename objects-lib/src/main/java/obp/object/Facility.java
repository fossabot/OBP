package obp.object;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;
import obp.neo4j.export.ElementNode;
import obp.neo4j.export.FacilityNode;

@Data
@EqualsAndHashCode(callSuper=true)
@Document
public class Facility extends OBPBaseClass{
  public static String ENTITY_NAME; 

  private String function;
  private String status;
  private String countryOfOwnership;
  private String countryOfControl;
  
  public Facility() {
    this.ENTITY_NAME="Facility";
  }
  
  @Override
  public ElementNode createElementNode()
  {
    ElementNode newNode = new  FacilityNode(this);
    return newNode;
  }

}
