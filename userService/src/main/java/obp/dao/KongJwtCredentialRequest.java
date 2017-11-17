package obp.dao;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KongJwtCredentialRequest {
	private String secret;
}
