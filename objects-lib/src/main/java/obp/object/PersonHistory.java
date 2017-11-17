package obp.object;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import obp.neo4j.export.ElementNode;
import obp.neo4j.export.PersonNode;
import obp.util.CustomDateDeserializer;
import obp.util.CustomDateSerializer;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=true)
@Document(collection = "person_history")
public class PersonHistory extends Person{

  public PersonHistory() {
    this.ENTITY_NAME="person_history";
  }


}
