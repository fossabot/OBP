package obp.security;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

@Data
@EqualsAndHashCode(callSuper=false)
@Document(collection = "user")
public class SerializableUser extends User implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
  public String id;
	
  private String client_secret;
  
  public SerializableUser() {
    super("notAValidUsername@!#", "notARealPassword", Collections.emptyList());
  }

  public SerializableUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
    super(username, password, authorities);
  }
}
