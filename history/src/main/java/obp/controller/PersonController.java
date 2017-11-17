package obp.controller;

import obp.object.PersonHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import obp.object.Person;
import obp.repo.PersonHistoryRepository;

@RestController
@RequestMapping("/history/person")
public class PersonController extends BaseHistoryController<PersonHistory> {
    @Autowired
    public PersonController(PersonHistoryRepository repo) {
        super(repo,PersonHistory.class);
    }
}
