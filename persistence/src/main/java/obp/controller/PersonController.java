package obp.controller;

import obp.object.OBPBaseClass;
import obp.repo.ObpBaseClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/person")
public class PersonController extends BaseCrudController<OBPBaseClass> {
    @Autowired
    public PersonController(ObpBaseClassRepository repo) {
        super(repo);
    }
}
