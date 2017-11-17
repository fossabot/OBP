package obp.search.repo;

import org.springframework.data.repository.Repository;

import obp.object.Facility;
import obp.object.Organization;

public interface FacilityRepository extends SearchableRepository<Facility>, Repository<Facility, String> {
}
