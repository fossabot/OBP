package obp.search.repo;

import org.springframework.data.repository.Repository;

import obp.object.Person;

public interface PersonRepository extends SearchableRepository<Person>, Repository<Person, String> {
}
