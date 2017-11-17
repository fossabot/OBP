package obp.controller;

import obp.object.FacilityHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import obp.object.Facility;
import obp.repo.FacilityHistoryRepository;

@RestController
@RequestMapping("/history/facility")
public class FacilityController extends BaseHistoryController<FacilityHistory> {
    @Autowired
    public FacilityController(FacilityHistoryRepository repo) {
        super(repo,FacilityHistory.class);
    }
}
