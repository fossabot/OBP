package obp.repo;

import obp.model.ObpPropertyDeclaration;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ObpPropertyDeclarationRepository
        extends CrudRepository<ObpPropertyDeclaration, String> {

    List<ObpPropertyDeclaration> findByName(String name);
}
