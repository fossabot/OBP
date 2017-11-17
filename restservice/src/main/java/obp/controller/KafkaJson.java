package obp.controller;

import java.util.LinkedHashMap;
import java.util.List;

public class KafkaJson {
	private List<String> topics; 
	private LinkedHashMap<String, String> messages;
	private String status = "";
	public List<String> getTopics() {
		return topics;
	}
	public void setTopics(List<String> topics) {
		this.topics = topics;
	}
	public LinkedHashMap<String, String> getMessages() {
		return messages;
	}
	public void setMessages(LinkedHashMap<String, String> messages) {
		this.messages = messages;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}