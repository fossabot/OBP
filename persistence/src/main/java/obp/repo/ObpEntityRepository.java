package obp.repo;

import obp.model.ObpEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ObpEntityRepository
        extends CrudRepository<ObpEntity, String> {

    List<ObpEntity> findByEntityDeclarationId(String edId);

}
