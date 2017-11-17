package obp.object;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@EqualsAndHashCode(callSuper=true)
@Document(collection = "equipment_history")
public class EquipmentHistory extends Equipment {
    public static String ENTITY_NAME;

    public EquipmentHistory() {
        this.ENTITY_NAME="equipment_history";
    }
}
