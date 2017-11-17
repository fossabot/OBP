package obp.neo4j;

import java.util.Map;

import org.neo4j.ogm.session.SessionFactory;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableNeo4jRepositories({ "obp.neo4j"}) // "BOOT-INF.classes.obp.neo4j" })//basePackages = "obp.neo4j_import")       
@EnableTransactionManagement
@EntityScan ({"obp.neo4j"}) //, "BOOT-INF.classes.obp.neo4j" } ) 
public class ApplicationConfig  {
    
    @Bean 
    public org.neo4j.ogm.config.Configuration getConfiguration() {
        Map<String, String> env = System.getenv();
       // Next line for OBP Neo4j Server
        //String neo4jUrl = env.getOrDefault("NEO4J_PROTOCOL", "http://") + env.getOrDefault("NEO4J_USER", "neo4j") + ":" + env.getOrDefault("NEO4J_PASS", "gr8Times!") + "@" + env.getOrDefault("NEO4J_HOST", "192.168.119.3") + ":" + env.getOrDefault("NEO4J_HTTP_PORT", "7474");
       //  Next line for local host
       String neo4jUrl = env.getOrDefault("NEO4J_PROTOCOL", "http://") + env.getOrDefault("NEO4J_USER", "neo4j") + ":" + env.getOrDefault("NEO4J_PASS", "gr8Times!") + "@" + env.getOrDefault("NEO4J_HOST", "localhost") + ":" + env.getOrDefault("NEO4J_HTTP_PORT", "7474");

        System.out.println("NEO4JURL: "+neo4jUrl);
        
        org.neo4j.ogm.config.Configuration config = new org.neo4j.ogm.config.Configuration();
        
        config
            .driverConfiguration()
            .setDriverClassName
            ("org.neo4j.ogm.drivers.http.driver.HttpDriver")
            .setURI(neo4jUrl);
        return config;
    }

    @Bean
    public SessionFactory getSessionFactory() {
        return new SessionFactory(getConfiguration(), "obp.neo4j"); // "BOOT-INF.classes.obp.neo4j");

    }
    
    @Bean
    public Neo4jTransactionManager transactionManager() {
    	return new Neo4jTransactionManager(getSessionFactory());
    }
    
}

