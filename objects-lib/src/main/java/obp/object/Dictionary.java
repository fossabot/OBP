package obp.object;

import java.util.ArrayList;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document
public class Dictionary {
  @Id private String id;
  private String field;
  private ArrayList<String> classes;
  private String dataType;
  private boolean core;
  private boolean required;
  private Integer order;
  private Boolean enabled;
}
