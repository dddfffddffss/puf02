package sub;

import java.util.ArrayList;

import sub.data.DataInterface;

public interface Provider extends DataInterface{
    public ArrayList<indexedJSON> random(String poolname, int sample_count, int count);
    public ArrayList<indexedJSON> latest(String poolname, int offset, int count, boolean ascending);
    public ArrayList<indexedJSON> tag(String poolname, String query);
    public void end();
}
