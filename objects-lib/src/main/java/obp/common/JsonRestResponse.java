package obp.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public class JsonRestResponse implements Serializable {
	private static final long serialVersionUID = -5267644744055485556L;
	
	private int code;
	private String message = "";
	
	@Setter(AccessLevel.NONE)
	private List<Object> data;
	
	public void setCode(HttpStatus htppCode) {
		this.code = htppCode.value();
	}
	
	public void setData(Object data) {
		this.data.add(data);
	}
	
	private JsonRestResponse(){}
	
	public static final JsonRestResponse getInstance(){
		JsonRestResponse jsonRestResponse = new JsonRestResponse();
		jsonRestResponse.message = "";
		jsonRestResponse.data = new ArrayList<Object>();
		return jsonRestResponse;
	}
}
