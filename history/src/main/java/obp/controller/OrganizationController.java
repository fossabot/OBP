package obp.controller;

import obp.object.OrganizationHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import obp.object.Organization;
import obp.repo.OrganizationHistoryRepository;

@RestController
@RequestMapping("/history/organization")
public class OrganizationController extends BaseHistoryController<OrganizationHistory> {
    @Autowired
    public OrganizationController(OrganizationHistoryRepository repo) {
        super(repo,OrganizationHistory.class);
    }
}
