package obp.search.repo;

import java.util.List;

import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SearchableRepository<T> {

  abstract List<T> findAllBy(TextCriteria criteria);
}
