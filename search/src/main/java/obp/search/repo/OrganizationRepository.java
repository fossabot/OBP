package obp.search.repo;

import org.springframework.data.repository.Repository;

import obp.object.Organization;
import obp.object.Person;

public interface OrganizationRepository extends SearchableRepository<Organization>, Repository<Organization, String> {
}
