package obp.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class SearchHelperService {
    
    private static String IN_DATE_FORMAT = "\\d{4}-\\d{2}-\\d{2}";
    private Pattern datePattern = Pattern.compile(IN_DATE_FORMAT);

    private static String[] dateKeys = {
            "startDate",
            "endDate",
            "lastUpdated",
            "dateOfBirth" 
    };
    
    public String removeDates(String searchTerms) {
        if (searchTerms == null) {
            return null;
        }
        return searchTerms.replaceAll(IN_DATE_FORMAT, "").trim();
    }
    
    public List<String> allDates(String searchTerms) {
        String[] splits = searchTerms.split(" ");
        List<String> dates = new ArrayList<>();
        
        for(String split : splits) {
            Matcher matcher = datePattern.matcher(split);
            if (matcher.matches()) {
                dates.add(split);
            }
        }
        
        return dates;
    }
    
    public Criteria getProperCriteria(String key, Object value) {
        if (value instanceof String) {
            for(String dateKey : dateKeys) {
                if (dateKey.equalsIgnoreCase(key)) {
                    return getDateCriteria(key, (String)value);
                }
            }
        } 
        return Criteria.where(key).is(value);
    }

    
    public Query createOrDateQuery(String searchTerms) {
        Matcher dateMatcher = datePattern.matcher(searchTerms);
        List<String> allDates = getAllDates(dateMatcher);
        if (!allDates.isEmpty()) {
            Query query = new Query();
            Criteria orDates = new Criteria();
            List<Criteria> dateCriterias = new ArrayList<>();
            for(String date : allDates) {
                for(String key: dateKeys) {
                    dateCriterias.add(getDateCriteria(key, date));  
                }
            }
            if (!dateCriterias.isEmpty()) {
                orDates.orOperator(dateCriterias.toArray(new Criteria[dateCriterias.size()]));
                query.addCriteria(orDates);
            }
            
            return query;
        }

        return null;
    }

    private Criteria getDateCriteria(String key, String date) {
        String[] splitDate = date.split("-");
        Calendar start = Calendar.getInstance();
        start.clear();
        start.set(Calendar.YEAR, Integer.valueOf(splitDate[0]));
        start.set(Calendar.MONTH, Integer.valueOf(splitDate[1]) - 1);
        start.set(Calendar.DAY_OF_MONTH, Integer.valueOf(splitDate[2]));
        
        Calendar end = Calendar.getInstance();
        end.setTime(start.getTime());
        end.add(Calendar.HOUR_OF_DAY, 24);
        return Criteria.where(key).gte(start.getTime()).andOperator(
                Criteria.where(key).lte(end.getTime()));
    }


    private List<String> getAllDates(Matcher dateMatcher) {
        List<String> allMatches = new ArrayList<>();
        
        while(dateMatcher.find()) {
            allMatches.add(dateMatcher.group());
        }
        
        return allMatches;
    }
}
