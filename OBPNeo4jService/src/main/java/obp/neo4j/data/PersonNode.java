package obp.neo4j.data;

import org.neo4j.ogm.annotation.NodeEntity;

import com.fasterxml.jackson.annotation.JsonRootName;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@JsonRootName(value = "Person")
@NodeEntity(label = "Person")
public class PersonNode extends ElementNode {
}
