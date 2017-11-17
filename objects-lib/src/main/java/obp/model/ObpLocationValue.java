package obp.model;

import lombok.Data;
import obp.object.Location;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.StringTokenizer;

@Data
public class ObpLocationValue implements ObpDataValue {
    private Location value;

    /**
     * This is used by Mongo.  It is open for use in the general API as well.
     */
    public ObpLocationValue() {}

    public ObpLocationValue(String anInput) {
        //Format: ##.######,##.######
        StringTokenizer tokenizer = new StringTokenizer(anInput, ",");
        String latitude = tokenizer.nextToken();
        String longitude = tokenizer.nextToken();
        DecimalFormat decimalFormatter = new DecimalFormat("##.######");
        try {
            value = new Location(decimalFormatter.parse(latitude), decimalFormatter.parse(longitude));
        } catch (ParseException ex) {
            //TODO: Stop eating the exception
        }
    }
}
