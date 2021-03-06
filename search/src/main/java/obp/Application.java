package obp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import obp.search.repo.BaseMongoRepository;

@SpringBootApplication
@EnableMongoRepositories(repositoryBaseClass = BaseMongoRepository.class)
public class Application implements CommandLineRunner {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
  }

}