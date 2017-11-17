
package obp.neo4j.data;

import org.neo4j.ogm.annotation.NodeEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@NodeEntity(label = "Equipment")
public class EquipmentNode extends ElementNode {
}
