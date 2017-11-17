package obp.dao;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KongJwtCredentialResponse {
	private String consumer_id;
	private String key;
	private String secret;
	private Long created_at;
	private String id;
}
