package obp.object;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;
import obp.neo4j.export.ElementNode;
import obp.neo4j.export.OrganizationNode;

@Data
@EqualsAndHashCode(callSuper=true)
@Document
public class Organization extends OBPBaseClass{
  public static String ENTITY_NAME; 
  

  private String function;
  
  public Organization() {
    this.ENTITY_NAME="organization";
  }
  
  @Override
  public ElementNode createElementNode()
  {
    ElementNode newNode = new OrganizationNode(this);
    return newNode;
  }

}
