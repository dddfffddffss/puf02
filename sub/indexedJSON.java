package sub;

import org.json.simple.JSONArray;

public class indexedJSON {
    String poolname;
    int index;
    JSONArray content;

    public indexedJSON(int index, JSONArray content){
        this.index = index;
        this.content = content;
    }

    public void setpoolname(String poolname){
        this.poolname = poolname;
    }

    public String getpoolname(){
        return poolname;
    }

    public int getindex(){
        return index;
    }

    public JSONArray getcontent(){
        return content;
    }
}