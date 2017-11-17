package obp.model;

import lombok.Data;

@Data
public class ObpStringValue implements ObpDataValue {
    private String value;

    public ObpStringValue() {
    }

    public ObpStringValue(String value) {
        this.value = value;
    }
}
