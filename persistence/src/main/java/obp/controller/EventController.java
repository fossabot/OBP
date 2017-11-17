package obp.controller;

import obp.object.OBPBaseClass;
import obp.repo.ObpBaseClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/event")
public class EventController extends BaseCrudController<OBPBaseClass> {
    @Autowired
    public EventController(ObpBaseClassRepository repo) {
        super(repo);
    }
}
