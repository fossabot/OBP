package obp.repo;

import java.beans.PropertyDescriptor;
import java.io.Serializable;

import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

import com.mongodb.WriteResult;

public class BaseMongoRepository<T, ID extends Serializable> extends SimpleMongoRepository<T, ID> {

  private MongoEntityInformation<T, ID> entityInformation;
  private MongoOperations operations;

  public BaseMongoRepository(MongoEntityInformation<T, ID> metadata, MongoOperations mongoOperations) {
    super(metadata, mongoOperations);
    this.entityInformation = metadata;
    this.operations = mongoOperations;
  }
 
  public boolean update(T thing) {
    Query identifyingQuery = getIdQueryFor(thing);
    Update update = updateOfNonNullFields(thing);
    WriteResult writeResult = operations.updateFirst(identifyingQuery, update, thing.getClass());
    return writeResult.isUpdateOfExisting();
  }

  private Query getIdQueryFor(T thing) {
    return new Query(Criteria.where(entityInformation.getIdAttribute()).is(entityInformation.getId(thing)));
  }

  private Update updateOfNonNullFields(T thing) {
    PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(thing.getClass());
    Update update = new Update();
    for (PropertyDescriptor pd : propertyDescriptors) {
      if (pd.getName().equals("class")) {
        continue;
      }
      Object value = null;
      try {
        value = pd.getReadMethod().invoke(thing);
      } catch (Exception e) {
        e.printStackTrace();
        //we need a better way to use the bean spec
      }
      if (value != null) {
        update.set(pd.getName(), value);
      }
    }
    return update;
  }
  
}
