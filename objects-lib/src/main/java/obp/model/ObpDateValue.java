package obp.model;

import lombok.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class ObpDateValue implements ObpDataValue {
    private static SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
    private Date value;

    /**
     * This is used by Mongo.  It is open for use in the general API as well.
     */
    public ObpDateValue() {}

    public ObpDateValue(String anInput) {
        try {
            this.value = formatter.parse(anInput);
        } catch (ParseException e) {
            throw new IllegalStateException(value + " is not it the right format", e);
        }
    }
}
