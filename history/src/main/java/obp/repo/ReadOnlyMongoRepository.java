package obp.repo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;

public class ReadOnlyMongoRepository<T, ID extends Serializable> extends SimpleMongoRepository<T, ID> implements ReadOnlyRepository<T, ID> {

  private MongoEntityInformation<T, ID> entityInformation;
  private MongoOperations operations;
  private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");


  public ReadOnlyMongoRepository(MongoEntityInformation<T, ID> metadata, MongoOperations mongoOperations) {
    super(metadata, mongoOperations);
    this.entityInformation = metadata;
    this.operations = mongoOperations;
  }


  private Query getIdQueryFor(T thing) {
    return new Query(Criteria.where(entityInformation.getIdAttribute()).is(entityInformation.getId(thing)));
  }


  public List<T> getByParentId(String parentId, Class<T> thisThing){

    BasicDBObject obj = new BasicDBObject("parentId", new ObjectId(parentId));

    BasicDBObject sortObject= new BasicDBObject("ingestDate",-1);
    BasicQuery bq = new BasicQuery(obj);
    bq.setSortObject(sortObject);
    return operations.find(bq, thisThing);
  }

  public List<T> getByParentIdAndAsofDate(String parentId, Date asOfDate, Class<T> thisThing){

    BasicDBObject obj = new BasicDBObject("parentId", new ObjectId(parentId)).append("ingestDate", new BasicDBObject("$lt", new Date()).
            append("$gte", asOfDate) );

    BasicDBObject sortObject= new BasicDBObject("ingestDate",-1);
    BasicQuery bq = new BasicQuery(obj);
    bq.setSortObject(sortObject);
    return operations.find(bq, thisThing);

  }

  public List<T> getByParentIdTopOne(String parentId, Class<T> thisThing){
    BasicDBObject obj = new BasicDBObject("parentId", new ObjectId(parentId));
    BasicDBObject sortObject= new BasicDBObject("ingestDate",-1);
    BasicQuery bq = new BasicQuery(obj);
    bq.setSortObject(sortObject);
    bq.limit(1);
    return operations.find(bq, thisThing);
  }

  //{"location.lat":{$ne:null},"location.lon":{$ne:null}}
  @Override
  public List<T> getLocationByParentIdTopOne(String parentId, Class<T> thisThing) {
    BasicDBObject obj = new BasicDBObject("parentId", new ObjectId(parentId));
    obj.append("location.lat",new BasicDBObject("$ne", null));
    obj.append("location.lon",new BasicDBObject("$ne", null));
    BasicDBObject sortObject= new BasicDBObject("ingestDate",-1);
    BasicQuery bq = new BasicQuery(obj);
    bq.setSortObject(sortObject);
    bq.limit(1);
    return operations.find(bq, thisThing);
  }

  @Override
  public List<T> getLocationByParentIdAndAsofDate(String parentId, Date asOfDate, Class<T> thisType) {
    BasicDBObject obj = new BasicDBObject("parentId", new ObjectId(parentId)).append("ingestDate", new BasicDBObject("$lt", new Date()).
            append("$gte", asOfDate) );
    obj.append("location.lat",new BasicDBObject("$ne", null));
    obj.append("location.lon",new BasicDBObject("$ne", null));
    BasicDBObject sortObject= new BasicDBObject("ingestDate",-1);
    BasicQuery bq = new BasicQuery(obj);
    bq.setSortObject(sortObject);
    //List<T> listLoc = operations.getCollection(operations.getCollectionName(thisType)).distinct("location",bq.getQueryObject());

    //GroupOperation groupOperation = getGroupOperation();


    //return operations.aggregate(Aggregation.newAggregation(groupOperation),thisType, thisType).getMappedResults();

    return operations.find(bq, thisType);
  }
  private GroupOperation getGroupOperation() {
    return group("location")
            .first("location").as("location")
            .max("lastUpdated").as("lastUpdated")
            .last("id").as("id")
            .last("lastUpdatedBy").as("lastUpdatedBy");
  }


  @Override
  public List<T> getLocationByParentId(String parentId, Class<T> thisType) {
    BasicDBObject obj = new BasicDBObject("parentId", new ObjectId(parentId));
    obj.append("location.lat",new BasicDBObject("$ne", null));
    obj.append("location.lon",new BasicDBObject("$ne", null));
    BasicDBObject sortObject= new BasicDBObject("ingestDate",-1);
    BasicQuery bq = new BasicQuery(obj);
    bq.setSortObject(sortObject);
    //return operations.getCollection(operations.getCollectionName(thisType)).distinct("location",bq.getQueryObject());
    return operations.find(bq, thisType);
  }

}
