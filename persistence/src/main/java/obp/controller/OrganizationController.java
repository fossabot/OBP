package obp.controller;

import obp.object.OBPBaseClass;
import obp.repo.ObpBaseClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/organization")
public class OrganizationController extends BaseCrudController<OBPBaseClass> {
    @Autowired
    public OrganizationController(ObpBaseClassRepository repo) {
        super(repo);
    }
}
