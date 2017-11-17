package obp.repo;

import obp.security.SerializableUser;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<SerializableUser, String> {
  SerializableUser findByUsername(String username);
}
