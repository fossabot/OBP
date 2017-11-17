package obp.services;

import obp.repo.ReadOnlyRepository;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HistoryServiceImpl<T>{
    ReadOnlyRepository repo;
    Class<T> thisType;

    public HistoryServiceImpl(ReadOnlyRepository<T, String> repo, Class<T> thisType){
        this.repo=repo;
        this.thisType=thisType;
    }
    public List<T> getByParentId(String id){
      return repo.getByParentId(id,thisType);
    }

    public List<T> getByParentId(String id, String timerange){
        List<T> results= null;

        boolean onlyTopOne=false;

        Calendar cal =getCalendar(timerange);
        if( timerange.equalsIgnoreCase("none") ){
            onlyTopOne=true;
        }else if( !timerange.equalsIgnoreCase("all") && cal==null ){
            return results;
        }

        if(onlyTopOne){
            results = repo.getByParentIdTopOne(id,thisType);
        }else if(cal!=null ) {
            Date daysPast = cal.getTime();
            results = repo.getByParentIdAndAsofDate(id,daysPast,thisType);
        }else{
            results = repo.getByParentId(id,thisType);
        }
        return results;
    }


    private Calendar getCalendar(String timerange){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        switch (timerange){
            case  "none":
                cal = null;
                break;
            case "1hr":
                cal.add(Calendar.HOUR_OF_DAY,-1);
                break;
            case "1day":
                cal.add(Calendar.DAY_OF_YEAR,-1);
                break;
            case "7days":
                cal.add(Calendar.DAY_OF_YEAR,-7);
                break;
            case "30days":
                cal.add(Calendar.DAY_OF_YEAR,-30);
                break;
            case "all":
                cal = null;
                break;
            default:
                cal = null;
        }
        return cal;
    }

    public List<T> getLocationByParentId(String id){
        return repo.getLocationByParentId(id,thisType);
    }

    public <T> List<T> getLocationByParentId(String id, String timerange) {
        List<T> results= null;

        boolean onlyTopOne=false;

        Calendar cal =getCalendar(timerange);
        if( timerange.equalsIgnoreCase("none") ){
            onlyTopOne=true;
        }else if( !timerange.equalsIgnoreCase("all") && cal==null ){
            return results;
        }

        if(onlyTopOne){
            results = repo.getLocationByParentIdTopOne(id,thisType);
        }else if(cal!=null ) {
            Date daysPast = cal.getTime();
            results = repo.getLocationByParentIdAndAsofDate(id,daysPast,thisType);
        }else{
            results = repo.getLocationByParentId(id,thisType);
        }
        return results;
    }
}
