package obp.search.repo;

import org.springframework.data.repository.Repository;

import obp.object.Equipment;
import obp.object.Organization;
import obp.search.repo.BaseMongoRepository;

public interface EquipmentRepository extends SearchableRepository<Equipment>, Repository<Equipment, String> {
}
