package obp.services;

import obp.model.ObpDataType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SlimEntities {
    @RequestMapping(value = "/dataTypes", method = RequestMethod.GET)
    public List<ObpDataType> getAllDataTypes() {
        return ObpDataType.getValues();
    }
}
