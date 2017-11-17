package obp.util;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;


public class CustomDateTimeDeserializer extends JsonDeserializer<Date> {
  
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss.SSS");

  @Override
  public Date deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
    LocalDate localDate = null;
    Date date = null;
    if (parser.getCurrentToken().equals(JsonToken.VALUE_STRING)) {
        localDate = LocalDate.parse(parser.getText().toString(), formatter);
        date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    return date;
  }
}
