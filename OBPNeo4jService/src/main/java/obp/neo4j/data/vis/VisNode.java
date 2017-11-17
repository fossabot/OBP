package obp.neo4j.data.vis;

import lombok.Data;

@Data
public class VisNode 
{
	private String id;
	private String label;
	private Integer level;
	private String category;
	private String categoryImageURL;
	private String imageURL;
	
	public VisNode(){}
	public VisNode(String id, String label, Integer level){
		this.id = id;
		this.label = label;
		this.level = level;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getCategoryImageURL() {
		return categoryImageURL;
	}
	public void setCategoryImageURL(String categoryImageURL) {
		this.categoryImageURL = categoryImageURL;
	}
	public String getImageURL() {
		return imageURL;
	}
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}
}
