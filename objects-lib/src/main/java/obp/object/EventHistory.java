package obp.object;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import obp.neo4j.export.ElementNode;
import obp.neo4j.export.EventNode;
import obp.util.CustomDateDeserializer;
import obp.util.CustomDateSerializer;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=true)
@Document(collection = "event_history")
public class EventHistory extends Event{
  public static String ENTITY_NAME;


  public EventHistory() {
    this.ENTITY_NAME="event_history";
  }
  


}
