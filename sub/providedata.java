package sub;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.Map.Entry;
import java.util.stream.IntStream;

import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

public class providedata extends sub.data.DataManager implements Provider{
    Thread autosave;
    Random r;

    private providedata() throws ParseException{
        super();
        r = new Random();
        autosave = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!Thread.currentThread().isInterrupted()){
                    try{
                        Thread.sleep(150000);
                        save();
                    }catch(Exception e){
                        System.out.println("자동 저장 종료");
                    }
                }
            }
        });
        autosave.start();
    }

    @Override
    public ArrayList<indexedJSON> random(String poolname, int sample_count, int count){
        // 1. 중복 없는 인덱스 뽑기
        int linecount = get_linecount(poolname);
        if(sample_count>linecount)sample_count = linecount;

		IntStream ds = r.ints(0, linecount).distinct();
        int[] sample_index = ds.limit(sample_count).toArray();

        // 2. 인덱스에 나온 line으로 새로운 리스트 만들기
        ArrayList<Integer> indexList = get_indexset(poolname);
        ArrayList<indexedJSON> sample = new ArrayList<>(sample_count);
        for(int i=0;i<sample_count;i++){
            int index = indexList.get(sample_index[i]);
            sample.add(new indexedJSON(index, get_line(poolname, index)));
        }

        // 3. sort
        Collections.sort(sample, new Comparator<indexedJSON>() {
            @Override
            public int compare(indexedJSON o1, indexedJSON o2) {
                return Integer.parseInt((String)o1.getcontent().get(2)) - Integer.parseInt((String)o2.getcontent().get(2));
            }
        });

        // 4. count개 뽑기
        return new ArrayList<>(sample.subList(0, Math.min(count,linecount)));
    }

    @Override
    public ArrayList<indexedJSON> latest(String poolname, int offset, int count, boolean ascending) {
        ArrayList<indexedJSON> list = new ArrayList<>();
        int indexcount = get_indexcount(poolname);

        // 최신으로부터의 offset!
        if(offset<0 || offset >= indexcount){
            offset = 0;
        }else{
            offset = get_linecount(poolname) - 1 - offset;
        }

        int start_point;
        for(start_point=indexcount; offset>-1; start_point--){
            if(contains_index(poolname, start_point))offset--;
        }
        
        for(int i=start_point+1; count>0; i = (ascending?--i:++i)%indexcount){
            if(contains_index(poolname, i)){
                count--;
                list.add(new indexedJSON(i, get_line(poolname, i)));
            }
        }

        return list;
    }

    @Override
    public ArrayList<indexedJSON> tag(String poolname, String query) {
        if(query==null){
            return null;
        }else{
            ArrayList<indexedJSON> temp = new ArrayList<>();
            for(Entry<Integer,JSONArray> e:get_lines(poolname).entrySet()){
                if(((String)e.getValue().get(1)).matches(query)){
                    temp.add(new indexedJSON(e.getKey(), e.getValue()));
                }
            }
            Collections.sort(temp, new Comparator<indexedJSON>() {
                @Override
                public int compare(indexedJSON o1, indexedJSON o2) {
                    int i1 = Integer.parseInt((String)o1.getcontent().get(2));
                    int i2 = Integer.parseInt((String)o2.getcontent().get(2));
                    return i1-i2;
                }
            });
            return temp;
        }
    }

    public void end(){
        autosave.interrupt();
        save();
    }
    
    public static Provider build() throws ParseException {
        return new providedata();
    }
}