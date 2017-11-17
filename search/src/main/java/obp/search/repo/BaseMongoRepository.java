package obp.search.repo;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

public class BaseMongoRepository<T, ID extends Serializable> extends SimpleMongoRepository<T, ID> {

  private MongoEntityInformation<T, ID> entityInformation;
  private MongoOperations operations;

  public BaseMongoRepository(MongoEntityInformation<T, ID> metadata, MongoOperations mongoOperations) {
    super(metadata, mongoOperations);
    this.entityInformation = metadata;
    this.operations = mongoOperations;
  }
 
  
}
