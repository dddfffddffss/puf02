package sub.data;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import sub.Constant;
import sub.indexedJSON;

/*

	<json파일 속성>
		- pooldata은 pool의 메타데이터를, pools은 pool의 내용을 가진다.
		- pooldata은 poolname이 JSONArray로 있고, pools은 line을 JSONArray로 가진다.
		- id가 콘텐츠다.
		- ai는 true가 본거 false가 안본거
		- line : frs내에서 처리해야 한다.
		- 추가 : lc, line,idcounter 3가지 확인 필요 
		- 삭제 : lc, line 2가지 확인 필요
		- json입출력 : 다른 메소드로 자료를 넘길 때 필요한 최소 자료만 넘기기
		- get메소드 : pool의 내용은 int형식으로, pooldata, pools의 내용은 json형식으로 f02res에서 출력한다.
			- JSONObject형식으로 출력되었으면, 그 메소드에서 다시 frs를 호출할 이유는 없다.
			- int, String형식으로 출력되었으면, 그 메소드에서 다시 frs를 호출할 수 있다.
		- set메소드 : 모든 setter은 frs를 경유한다.
		

	<json파일 형식>
	int "s88", "s89"
	String buf : [String "buffer" *]

	JSONObject "pools" : {
		JSONObject "pool" : {
			JSONArray "contents(id)" : ["tag","ai","etc"]
			*
		}
		*
	}
*/

public class DataManager implements DataInterface{
    JSONObject data;

	int s88, s89, s105, isdatafree;
    String time;
    JSONArray buf;
    HashMap<String, Integer> index_counter;
    HashMap<String,HashMap<Integer, JSONArray>> pools;

    ArrayList<indexedJSON> tagbuffer;

    public DataManager() throws ParseException{
        try{
            JSONParser jp = new JSONParser();
            data = (JSONObject)jp.parse(new FileReader(Constant.DATA_DIR));
    
            s88 = Long.valueOf((Long)data.get("s88")).intValue();
            s89 = Long.valueOf((Long)data.get("s89")).intValue();
            s105 = Long.valueOf((Long)data.get("s105")).intValue();
            time = (String)data.get("time");
            buf = (JSONArray)data.get("buf");
            tagbuffer = new ArrayList<>();

            index_counter = new HashMap<>();
            for(Object poolObject:((JSONObject)data.get("index_counter")).entrySet()){
                Entry<String, Long> content = (Entry<String, Long>)poolObject;
                index_counter.put(content.getKey(), content.getValue().intValue());
            }
            pools = loadpools(data);
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("새로 생성합니다.");
            s88 = 0;
            s89 = 0;
            s105 = 0;
            time = "0";
            buf = new JSONArray();
            tagbuffer = new ArrayList<>();
            pools = new HashMap<>();
            index_counter = new HashMap<>();
        }
    }

    private HashMap<String,HashMap<Integer, JSONArray>> loadpools(JSONObject data){
        JSONObject temp = (JSONObject)data.get("pools");
        pools = new HashMap<>();
        for(Object poolname:temp.keySet()){
            JSONObject indexedcontents = (JSONObject)temp.get(poolname);
            HashMap<Integer, JSONArray> pool = new HashMap<>();
            for(Object index_Object:indexedcontents.entrySet()){
                Entry<String, JSONArray> content = (Entry<String, JSONArray>)index_Object;
                pool.put(Integer.parseInt(content.getKey()), content.getValue());
            }
            pools.put((String)poolname, pool);
        }
        return pools;
    }

    @Override
    public int search(String poolname, String id) {
        HashMap<Integer, JSONArray> temp = pools.get(poolname);
        for(Entry<Integer,JSONArray> item:temp.entrySet()){
            if(item.getValue().get(0).equals(id))return item.getKey();
        }
        return -1;
    }

    @Override
    public boolean contains_pool(String poolname) {
        return pools.containsKey(poolname);
    }

    @Override
    public boolean contains_index(String poolname, int index) {
        try{
            return pools.get(poolname).containsKey(index);
        }catch(Exception e){
            return false;
        }
    }

    @Override
    public boolean contains_id(String poolname, String id) {
        try{
            for(Entry<Integer,JSONArray> entry:pools.get(poolname).entrySet()){
                if(((String)entry.getValue().get(0)).equals(id)){
                    return true;
                }
            }
            return false;
        }catch(Exception e){
            return false;
        }
    }

    @Override
    public Set<String> get_poollist() {
        return pools.keySet();
    }

    @Override
    public int get_setting(String cate) {
        if(cate=="s88"){
            return s88;
        }else if(cate=="s89"){
            return s89;
        }else{
            return s105;
        }   
    }

    @Override
    public void set_setting(String cate, int sw) {
        if(cate=="s88"){
            s88 = sw;
        }else if(cate=="s89"){
            s89 = sw;
        }else{
            s105 = sw;
        }
    }

    @Override
    public JSONArray get_buffer() {
        return buf;
    }

    @Override
    public void add_buffer(String address) {
        buf.add(address);
    }

    @Override
    public boolean delete_buffer(int index) {
        try{
            buf.remove(index);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    @Override
    public void reset_buffer() {
        buf.clear();
    }

    @Override
    public ArrayList<indexedJSON> get_tagbuffer() {
        return tagbuffer;
    }

    @Override
    public void add_tagbuffer(indexedJSON line) {
        tagbuffer.add(line);
        
    }

    @Override
    public boolean delete_tagbuffer(int index) {
        try{
            tagbuffer.remove(index);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    @Override
    public void reset_tagbuffer() {
        tagbuffer.clear();
    }

    @Override
    public String get_time() {
        return time;
    }

    @Override
    public int get_linecount(String poolname){
        return pools.get(poolname).size();
    }

    @Override
    public int get_indexcount(String poolname){
        return (Integer)index_counter.get(poolname);
    }

    @Override
    public HashMap<Integer, JSONArray> get_lines(String poolname) {
        return pools.get(poolname);
    }

    @Override
    public JSONArray get_line(String poolname, int index) {
        if(pools.get(poolname).containsKey(index)){
            return pools.get(poolname).get(index);
        }else{
            return null;
        }
    }

    @Override
    public ArrayList<Integer> get_indexset(String poolname) {
        return new ArrayList<>(pools.get(poolname).keySet());
    }

    @Override
    public void set_id(String poolname, int index, String id) {
        pools.get(poolname).get(index).set(0,id);
    }

    @Override
    public void set_id(String poolname, HashMap<Integer, String> id_list) {
        for(int index:id_list.keySet()){
            pools.get(poolname).get(index).set(0,id_list.get(index));
        }
    }

    @Override
    public void set_tag(String poolname, int index, String tag) {
        pools.get(poolname).get(index).set(1,tag);
    }

    @Override
    public void set_ai(String poolname, int index, String ai) {
        pools.get(poolname).get(index).set(2, ai);
    }

    @Override
    public void add_line(String poolname, String id, String tag, String ai){
        if(!contains_pool(poolname)){
            pools.put(poolname, new HashMap<>());
            index_counter.put(poolname, 0);
        }
        int new_index = (int)index_counter.get(poolname)+1;
		JSONArray data = new JSONArray();
        data.add(id);
        data.add(tag);
        data.add(ai);

        pools.get(poolname).put(new_index, data);
        index_counter.put(poolname, new_index);
    }

    @Override
    public boolean delete_line(String poolname, int index) {
        if(pools.get(poolname).containsKey(index)){
            pools.get(poolname).remove(index);
            if(pools.get(poolname).isEmpty())pools.remove(poolname);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void save(){
        synchronized(this){
            time = Integer.toString(Integer.parseInt(time)+1);

            FileWriter fs;
            JSONObject savefile = new JSONObject();
            savefile.put("s88",s88);
            savefile.put("s89",s89);
            savefile.put("s105",s105);
            savefile.put("time",time);
            savefile.put("buf",buf);
            savefile.put("index_counter", index_counter);
            savefile.put("pools",pools);

            try {
                fs = new FileWriter(Constant.DATA_DIR);
                savefile.writeJSONString(fs);
                fs.close();
            } catch (IOException e) {
                try {
                    fs = new FileWriter("data\\backupdata.json");
                    savefile.writeJSONString(fs);
                    fs.close();
                } catch (IOException e1) {
                    System.out.println("자동 저장 안됨!");
                }
            }
        }
    }
}