package obp.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;


@Controller
public class KafkaController {
    @PreAuthorize("#oauth2.hasScope('ROLE_RO')")
	@RequestMapping(value="/getTestMessage", method=RequestMethod.GET)
	public @ResponseBody KafkaJson getTestMessage(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response){
        request.setAttribute("Content-Type", "application/json");
		response.setHeader("Content-Type", "application/json");
		KafkaJson json = new KafkaJson();

		json.setStatus("This is a Test Message to verify the REST Service.");
		return json;
	}	
	
    @PreAuthorize("#oauth2.hasScope('ROLE_RO')")
	@RequestMapping(value="/getTopics", method=RequestMethod.GET)
	public @ResponseBody KafkaJson getTopics(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response){
        request.setAttribute("Content-Type", "application/json");
		response.setHeader("Content-Type", "application/json");
		KafkaJson json = new KafkaJson();

		List<String> allTopics = this.getAllTopics();
		json.setTopics(allTopics);
		json.setStatus("All Topics Displayed");
		return json;
	}	

    @PreAuthorize("#oauth2.hasScope('ROLE_RO')")
	@RequestMapping(value="/createTopic/{topic}", method=RequestMethod.POST)
	public @ResponseBody KafkaJson createTopic(Map<String, Object> model, @PathVariable("topic") String topic, 
											   HttpServletRequest request, HttpServletResponse response){
		KafkaJson json = new KafkaJson();
		request.setAttribute("Content-Type", "application/json");
		response.setHeader("Content-Type", "application/json");
		if(StringUtils.isNotEmpty(topic)){
		    String zookeeperConnect = "192.168.119.30:2181";
		    int sessionTimeoutMs = 10 * 1000;
		    int connectionTimeoutMs = 8 * 1000;

		    int partitions = 1;
		    int replication = 1;
		    Properties topicConfig = new Properties(); // add per-topic configurations settings here
		    
		    ZkClient zkClient = new ZkClient(
		        zookeeperConnect,
		        sessionTimeoutMs,
		        connectionTimeoutMs,
		        ZKStringSerializer$.MODULE$);

		    // Security for Kafka was added in Kafka 0.9.0.0
		    boolean isSecureKafkaCluster = false;

		    try{
			    ZkUtils zkUtils = new ZkUtils(zkClient, new ZkConnection(zookeeperConnect), isSecureKafkaCluster);
			    AdminUtils.createTopic(zkUtils, topic, partitions, replication, topicConfig, RackAwareMode.Enforced$.MODULE$);
			    System.out.println("Successfully created a new Topic \""+ topic + "\"");
				json.setStatus("Successfully created a new Topic "+ topic);
		    }finally{
			    zkClient.close();
		    }
		}
		List<String> allTopics = this.getAllTopics();
		json.setTopics(allTopics);
		return json;		
	}	
    @PreAuthorize("#oauth2.hasScope('ROLE_RO')")
	@RequestMapping(value="/subscribeTopic/{topic}", method=RequestMethod.POST)
	public @ResponseBody KafkaJson subscribeTopic(Map<String, Object> model, @PathVariable("topic") String topic, 
				HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute("Content-Type", "application/json");
		response.setHeader("Content-Type", "application/json");
		KafkaJson json = new KafkaJson();
        Properties configProperties = new Properties();
        KafkaConsumer<String, String> consumer = null ;
        		
        configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.119.30:9092");
        configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-group");
        configProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, "simple");

        consumer = new KafkaConsumer<String, String>(configProperties);
        try{
            consumer.subscribe(Arrays.asList(topic));
            System.out.println("Successfully subscribted to Topic: " + topic);
			json.setStatus("Successfully subscribted to Topic: " + topic);
    		json.setTopics(Arrays.asList(topic));
        }catch(Exception e){
        	System.out.println(e.getMessage());
        }finally{
    		consumer.close();
        }
		
		//Spawn the thread for subscription to start listening to incoming messages
	    LinkedHashMap<String, String> messages = new LinkedHashMap<String, String>();		
        ConsumerThread consumerRunnable = new ConsumerThread(topic, "test-consumer-group", messages);
        consumerRunnable.start();
        System.out.println("New Kafka Client Spawned.. ");
        consumerRunnable.getKafkaConsumer().wakeup();
        try {
			consumerRunnable.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return json;
	}

    @PreAuthorize("#oauth2.hasScope('ROLE_RO')")
	@RequestMapping(value="/publishMessage/{topic}/{message}", method=RequestMethod.POST)
	public @ResponseBody KafkaJson getPublish(Map<String, Object> model, @PathVariable("topic") String topic, 
											  @PathVariable("message") String message, HttpServletRequest request, 
											  HttpServletResponse response) {
		request.setAttribute("Content-Type", "application/json");
		response.setHeader("Content-Type", "application/json");
		KafkaJson json = new KafkaJson();
        LinkedHashMap<String, String> messageByTopic = new LinkedHashMap<String, String>();

		Properties props = new Properties();
		KafkaProducer<String, String> producer = null;
		props.put("bootstrap.servers", "192.168.119.30:9092");
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");

		try{
			 producer = new KafkaProducer<String, String>(props);
			 
			 if(producer!=null){
				 ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, message);
				producer.send(record);
				messageByTopic.put(topic, message);
				json.setTopics(Arrays.asList(topic));
				json.setMessages(messageByTopic);
				json.setStatus("Successfully published to Topic: " + topic);
			}
		}finally{
			producer.close();
		}
		model.put("message", message);
		return json;
	}
    @PreAuthorize("#oauth2.hasScope('ROLE_RO')")
	@RequestMapping(value="/getMessages/{topic}", method=RequestMethod.GET)
	public @ResponseBody KafkaJson getMessage(Map<String, Object> model, @PathVariable("topic") String topic, 
											  HttpServletRequest request, HttpServletResponse response) {
		KafkaJson json = new KafkaJson();
		Properties props = new Properties();
		props.put("bootstrap.servers", "192.168.119.30:9092");
		props.put("group.id", "test-consumer-group");
		props.put("enable.auto.commit", "true");
		props.put("auto.commit.interval.ms", "1000");
		props.put("session.timeout.ms", "30000");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

	    LinkedHashMap<String, String> messages = new LinkedHashMap<String, String>();		
        ConsumerThread consumerRunnable = new ConsumerThread(topic, "test-consumer-group", messages);
        consumerRunnable.start();
        System.out.println("New Kafka Client Spawned.. ");
        consumerRunnable.getKafkaConsumer().wakeup();
        System.out.println("Stopping consumer .....");
        try {
			consumerRunnable.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		json.setMessages(messages);
		return json;
	}
    private static class ConsumerThread extends Thread{
        private String topicName;
        private String groupId;
        private KafkaConsumer<String,String> kafkaConsumer;
        private LinkedHashMap<String, String> messages;
        
        public ConsumerThread(String topicName, String groupId, LinkedHashMap<String, String> messages){
            this.topicName = topicName;
            this.groupId = groupId;
            this.messages = messages;
        }
        public void run() {
            Properties configProperties = new Properties();
            configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.119.30:9092");
            configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
            configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
            configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            configProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, "simple");

            //Figure out where to start processing messages from
            kafkaConsumer = new KafkaConsumer<String, String>(configProperties);
            kafkaConsumer.subscribe(Arrays.asList(topicName));
            //Start processing messages
            try {
                while (true) {
                    ConsumerRecords<String, String> records = kafkaConsumer.poll(100);
                    for (ConsumerRecord<String, String> record : records){
                        System.out.println("New Message Received on Topic " + topicName + ": " + record.value());
                    	messages.put(topicName, record.value());
                    }
                    	
                }
            }catch(WakeupException ex){
                System.out.println("Exception caught " + ex.getMessage());
            }finally{
                kafkaConsumer.close();
                System.out.println("After closing KafkaConsumer");
            }
        }
        public KafkaConsumer<String,String> getKafkaConsumer(){
           return this.kafkaConsumer;
        }
    }
	private List<String> getAllTopics() {
		List<String> allTopics = new ArrayList<String>();
        Properties configProperties = new Properties();
        KafkaConsumer<String, String> consumer = null ;
        		
        configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.119.30:9092");
        configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-group");
        configProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, "simple");

        consumer = new KafkaConsumer<String, String>(configProperties);
        Map<String,List<PartitionInfo>> topics = consumer.listTopics();
        for(String str : topics.keySet()){
        	allTopics.add(topics.get(str).get(0).toString());
        }
        System.out.println("Topics: " + topics.size());
        consumer.close();
		return allTopics;
	}
}