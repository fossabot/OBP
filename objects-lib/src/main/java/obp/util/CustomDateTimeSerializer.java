package obp.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class CustomDateTimeSerializer extends JsonSerializer<Date> {
  
  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");

  @Override
  public void serialize(Date date, JsonGenerator gen, SerializerProvider sProvider)
      throws IOException, JsonProcessingException {
    
    String result = dateFormat.format(date);
    gen.writeString(result);
  }

}
