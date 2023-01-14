package sub.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.json.simple.JSONArray;

import sub.indexedJSON;

public interface DataInterface {
    public int search(String poolname, String id);
    public boolean contains_pool(String poolname);
    public boolean contains_index(String poolname, int index);
    public boolean contains_id(String poolname, String id);

    public Set<String> get_poollist();
    public int get_linecount(String poolname);
    public int get_indexcount(String poolname);
    public String get_time();
    public int get_setting(String cate);
    public void set_setting(String cate, int sw);

    public JSONArray get_buffer();
    public void add_buffer(String address);
    public boolean delete_buffer(int index);
    public void reset_buffer();

    public ArrayList<indexedJSON> get_tagbuffer();
    public void add_tagbuffer(indexedJSON line);
    public boolean delete_tagbuffer(int index);
    public void reset_tagbuffer();
    
    public void add_line(String poolname, String id, String tag,String ai);
    public boolean delete_line(String poolname, int index);
    public void save() throws IOException;

    // get content
    public HashMap<Integer, JSONArray> get_lines(String poolname);
    public JSONArray get_line(String poolname, int index);
    public ArrayList<Integer> get_indexset(String poolname);

    // set content
    public void set_id(String poolname, int index,String id);
    public void set_id(String poolname, HashMap<Integer,String> id_list);
    public void set_tag(String poolname, int index,String tag);
    public void set_ai(String poolname, int index,String ai);
}