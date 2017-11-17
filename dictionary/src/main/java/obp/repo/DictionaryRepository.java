package obp.repo;

import java.util.List;

import org.springframework.data.repository.Repository;

import obp.object.Dictionary;

public interface DictionaryRepository extends Repository<Dictionary, String> {

  abstract List<Dictionary> findAll() ;
  abstract List<Dictionary> findByClassesIgnoreCase(String classes);
  abstract List<Dictionary> findByDataTypeIgnoreCase(String dataType);
  abstract List<Dictionary> findByCoreTrue();
  abstract Dictionary findOne(String id);
  abstract boolean update(Dictionary object);
  abstract Dictionary save(Dictionary object);
  abstract Dictionary delete(String id);
}
