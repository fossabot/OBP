package obp.object;

import lombok.Data;
import lombok.EqualsAndHashCode;
import obp.neo4j.export.ElementNode;
import obp.neo4j.export.OrganizationNode;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@EqualsAndHashCode(callSuper=true)
@Document(collection = "organization_history")
public class OrganizationHistory extends Organization{
  public static String ENTITY_NAME;


  public OrganizationHistory() {
    this.ENTITY_NAME="organization_history";
  }


}
