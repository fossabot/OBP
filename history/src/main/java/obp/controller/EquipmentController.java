package obp.controller;

import obp.object.EquipmentHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import obp.object.Equipment;
import obp.repo.EquipmentHistoryRepository;

@RestController
@RequestMapping("/history/equipment")
public class EquipmentController extends BaseHistoryController<EquipmentHistory> {
    @Autowired
    public EquipmentController(EquipmentHistoryRepository repo) {
        super(repo, EquipmentHistory.class);
    }
}
