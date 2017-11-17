package obp.object;

import lombok.Data;

@Data
public class Location {
  private Float lon;
  private Float lat;


  public Location() {}

    public Location(Float latitude, Float longitude) {
        lon = longitude;
        lat = latitude;
    }

    public Location(Number latitude, Number longitude) {
        lon = new Float(longitude.floatValue());
        lat = new Float(latitude.floatValue());
    }
}
