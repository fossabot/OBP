package obp.controller;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;

@CrossOrigin
@RestController
@RequestMapping(value = "/")
public class OptionsController {
  private Collection<String> options;
  private RequestMappingInfoHandlerMapping _mapping;

  @Autowired
  public OptionsController(RequestMappingInfoHandlerMapping mapping) {
    Set<String> set = new HashSet<>();
    _mapping = mapping;
    _mapping.getHandlerMethods()
            .keySet()
            .forEach(key -> set.addAll(key.getPatternsCondition().getPatterns()
                    .stream()
                    .filter(pattern -> pattern.split("/").length == 2)
                    .map(s -> s.substring(1))
                    .collect(Collectors.toList())));
    set.remove("error");
    set.remove("search");
    set.remove("all");
    set.remove("dictionary");
    options = set;
  }

  @RequestMapping(method = RequestMethod.OPTIONS)
  public Collection<String> options() {
    return options;
  }
  
  @PreAuthorize("#oauth2.hasScope('ROLE_RO')")
  @RequestMapping(value = "/all", method = RequestMethod.GET)
  public Collection<String> getAllOptions() {
    
    Set<String> fields = new HashSet<String>();
    
    for (String object : options)  {
      String className = Character.toUpperCase(object.charAt(0)) + object.substring(1);
      
      try {
        Class<?> clazz = Class.forName("obp.object." + className);
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
          if (method.getName().startsWith("get")) {
            String field = Character.toLowerCase(method.getName().charAt(3)) + method.getName().substring(4);
            fields.add(field);
          }
        }

        
      } catch (ClassNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    fields.remove("class");
    
    return fields;
  }
}
