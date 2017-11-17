package obp.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WelcomeController {

	// inject via application.properties
	@Value("${welcome.message:test}")
	private String message = "Hello World";

	@RequestMapping("/")
	public String welcome(Map<String, Object> model) {
		model.put("message", this.message);
		return "welcome";
	}
	@RequestMapping("/consumer")
	public String getConsumer(Map<String, Object> model) {
		model.put("message", this.message);
		return "consumer";
	}
//	@RequestMapping("/getMessages")
//	public String getMessages(Map<String,Object> model){
//      Properties props = new Properties();
//      props.put("bootstrap.servers", "192.168.119.30:9092");
//      props.put("group.id", "test-consumer-group");
//      props.put("enable.auto.commit", "true");
//      props.put("auto.commit.interval.ms", "1000");
//      props.put("session.timeout.ms", "30000");
//      props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//      props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//      KafkaConsumer<String, String> consumer = new KafkaConsumer
//         <String, String>(props);
//      
//      consumer.subscribe(Arrays.asList("test100"));
////      System.out.println("Subscribed to topic " + "test100");
////      while (true) {
////      	}
//      ConsumerRecords<String, String> records = consumer.poll(100);
//      for (ConsumerRecord<String, String> record : records){
//         System.out.printf("offset = %d, key = %s, value = %s\n", 
//         record.offset(), record.key(), record.value());
//      }
//	return message;
//	}
}