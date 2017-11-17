package obp.object;

import lombok.Data;
import lombok.EqualsAndHashCode;
import obp.neo4j.export.ElementNode;
import obp.neo4j.export.FacilityNode;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@EqualsAndHashCode(callSuper=true)
@Document(collection = "facility_history")
public class FacilityHistory extends Facility{
  public static String ENTITY_NAME;

  public FacilityHistory() {
    this.ENTITY_NAME="facility_history";
  }


}
