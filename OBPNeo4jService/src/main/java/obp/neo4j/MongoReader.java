package obp.neo4j;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import obp.neo4j.data.*;

public class MongoReader {
	private ObjectMapper mapperMongoObj;

	public MongoReader() {
		mapperMongoObj = new ObjectMapper();
    }
	
	public ElementNode parseElementNode( String str_Json)
	{
		ElementNode result = null;
		try {
			result = mapperMongoObj.readValue(str_Json, ElementNode.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}
