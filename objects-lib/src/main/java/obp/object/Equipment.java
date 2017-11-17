package obp.object;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;
import obp.neo4j.export.ElementNode;
import obp.neo4j.export.EquipmentNode;


@Data
@EqualsAndHashCode(callSuper=true)
@Document
public class Equipment extends OBPBaseClass{
  public static String ENTITY_NAME; 
  
  private String function;
  private String status;
  private String countryOfOwnership;
  private String countryOfControl;
  
  public Equipment() {
    this.ENTITY_NAME="Equipment";
  }
  
  @Override
  public ElementNode createElementNode()
  {
    ElementNode newNode = new EquipmentNode(this);
    return newNode;
  }

}
