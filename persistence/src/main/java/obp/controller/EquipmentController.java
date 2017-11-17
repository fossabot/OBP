package obp.controller;

import obp.object.OBPBaseClass;
import obp.repo.ObpBaseClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/equipment")
public class EquipmentController extends BaseCrudController<OBPBaseClass> {
    @Autowired
    public EquipmentController(ObpBaseClassRepository repo) {
        super(repo);
    }
}
