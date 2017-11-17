package obp.neo4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class ObpNeo4jServiceApplication implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(ObpNeo4jServiceApplication.class, args);
	}
	
	@Override
	  public void run(String... args) throws Exception {
	  }
}
