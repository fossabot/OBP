package obp.repo;

import obp.model.ObpEntityDeclaration;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ObpEntityDeclarationRepository
        extends CrudRepository<ObpEntityDeclaration, String> {

    List<ObpEntityDeclaration> findByName(String name);
}
