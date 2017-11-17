package obp.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface UpdateableRepository<T, ID extends Serializable> extends CrudRepository<T, ID> {
  boolean update(T thing);
}
