package obp.dao;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class AuthenticationResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@NonNull private String access_token;
  private String token_type;
  private long expires_in;
}
