package obp.dao;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KongConsumerRequest {
	private String username;
	private String custom_id;
}
