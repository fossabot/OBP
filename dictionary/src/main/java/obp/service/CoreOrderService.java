package obp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import obp.object.Dictionary;

@Service
public class CoreOrderService {
   

    public List<String> order(List<Dictionary> coreFields) {
        List<Dictionary> coreFieldsCopy = new ArrayList<>(coreFields);
        
        List<String> orderedFields = coreFieldsCopy
                .stream()
                .sorted(CoreOrderService::fieldComparator)
                .map(field -> field.getField())
                .collect(Collectors.toList());
        
        return orderedFields;
    }
    
    private static int fieldComparator(Dictionary field1, Dictionary field2) {
        
        int index1 = -1;
        int index2 = -1;
        
        if (field1.getOrder() != null) {
            index1 = field1.getOrder();
        }
        
        if (field2.getOrder() != null) {
            index2 = field2.getOrder();
        }
        
        // if field not explicitly defined, default to alphabetical
        if (index1 == -1 && index2 == -1) {
            return field1.getField().compareTo(field2.getField());
        }
        
        if (index1 != -1 && index2 != -1) {
            int result = index1 - index2;
            if (result == 0) {
                result = field1.getField().compareTo(field2.getField());
            }
            return result;
        }
        
        if (index1 != -1 && index2 == -1) {
            return -1;
        }
        
        
        return 1;
    }
}
