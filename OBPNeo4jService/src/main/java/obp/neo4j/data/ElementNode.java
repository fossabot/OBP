package obp.neo4j.data;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import lombok.Data;

@Data
@JsonTypeInfo(include=As.WRAPPER_OBJECT, use=Id.NAME)
@NodeEntity()
public abstract class ElementNode {
	 @GraphId
     protected Long id;
	 @Property(name="mongoId")
	 protected String mongoId;
	 @Property(name="name")
	 protected String name;
	@Property(name="type")
	protected String type;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getMongoId() {
		return mongoId;
	}
	public void setMongoId(String mongoId) {
		this.mongoId = mongoId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getCategory()
	{
		String category = "Unknown";
		if(this instanceof EquipmentNode)
		{
			category = "Equipment";
		}
		else if(this instanceof EventNode)
		{
			category = "Event";
		}
		else if(this instanceof FacilityNode)
		{
			category = "Facility";
		}
		else if(this instanceof OrganizationNode)
		{
			category = "Organization";
		}
		else if(this instanceof PersonNode)
		{
			category = "Person";
		}
		return category;
	}

	public static final String UNKNOWN_URL = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7d/Question_opening-closing.svg/78px-Question_opening-closing.svg.png";
	public static final String EQUIPMENT_URL = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e9/Swiss_Army_Knive_opened.jpeg/220px-Swiss_Army_Knive_opened.jpeg";
	public static final String EVENT_URL = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/dd/Birthday_candles.jpg/220px-Birthday_candles.jpg";
	public static final String FACILITY_URL = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/34/Kansas_City_Arrowhead_Stadium.jpg/220px-Kansas_City_Arrowhead_Stadium.jpg";
	public static final String ORGANIZATION_URL = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5b/Society.svg/200px-Society.svg.png";
	public static final String PERSON_URL = "https://upload.wikimedia.org/wikipedia/commons/thumb/9/94/Stick_Figure.svg/170px-Stick_Figure.svg.png";

	public String getCategoryImageURL()
	{
		String categoryImage = UNKNOWN_URL;
		if(this instanceof EquipmentNode)
		{
			categoryImage = EQUIPMENT_URL;
		}
		else if(this instanceof EventNode)
		{
			categoryImage = EVENT_URL;
		}
		else if(this instanceof FacilityNode)
		{
			categoryImage = FACILITY_URL;
		}
		else if(this instanceof OrganizationNode)
		{
			categoryImage = ORGANIZATION_URL;
		}
		else if(this instanceof PersonNode)
		{
			categoryImage = PERSON_URL;
		}
		return categoryImage;
	}
}
