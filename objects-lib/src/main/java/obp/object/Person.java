package obp.object;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
import lombok.EqualsAndHashCode;
import obp.neo4j.export.ElementNode;
import obp.neo4j.export.PersonNode;
import obp.util.CustomDateDeserializer;
import obp.util.CustomDateSerializer;

@Data
@EqualsAndHashCode(callSuper=true)
@Document
public class Person extends OBPBaseClass{
  private Integer age;
  @JsonSerialize(using=CustomDateSerializer.class)
  @JsonDeserialize(using=CustomDateDeserializer.class)
  private Date dateOfBirth;
  private String height;
  private String countryOfOwnership;
  private String countryOfControl;
  
  public Person() {
    this.ENTITY_NAME="person";
  }
  
  @Override
  public ElementNode createElementNode()
  {
    ElementNode newNode = new PersonNode(this);
    return newNode;
  }

}
