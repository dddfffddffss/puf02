package sub.data;

import org.json.simple.parser.ParseException;

public class ManagerFactory{
    public static DataInterface create() throws ParseException{
        return new DataManager();
    }
}