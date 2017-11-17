package obp.repo;


import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@NoRepositoryBean
public interface ReadOnlyRepository<T, ID extends Serializable>  extends CrudRepository<T, ID> {
    List<T> getByParentId(String parentId, Class<T> classt);
    List<T> getByParentIdAndAsofDate(String parentId, Date asOfDate, Class<T> classt);

    List<T> getByParentIdTopOne(String id, Class<T> thisType);

    List<T> getLocationByParentIdTopOne(String id, Class<T> thisType);

    List<T> getLocationByParentIdAndAsofDate(String id, Date daysPast, Class<T> thisType);

    List<T> getLocationByParentId(String id, Class<T> thisType);
}
