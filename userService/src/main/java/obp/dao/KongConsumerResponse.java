package obp.dao;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KongConsumerResponse {
	private String custom_id;
	private String username;
	private Long created_at;
	private String id;
}
