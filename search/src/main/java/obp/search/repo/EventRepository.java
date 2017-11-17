package obp.search.repo;

import org.springframework.data.repository.Repository;

import obp.object.Event;
import obp.object.Organization;

public interface EventRepository extends SearchableRepository<Event>, Repository<Event, String> {
}
