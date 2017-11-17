package obp.controller;

import obp.object.EventHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import obp.object.Event;
import obp.repo.EventHistoryRepository;

@RestController
@RequestMapping("/history/event")
public class EventController extends BaseHistoryController<EventHistory> {
    @Autowired
    public EventController(EventHistoryRepository repo) {
        super(repo,EventHistory.class);
    }
}
