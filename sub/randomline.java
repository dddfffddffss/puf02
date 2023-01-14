package sub;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import org.json.simple.JSONArray;

import sub.util.input;
import sub.util.openweb;
import sub.util.phraser;

public class randomline
{
	Provider p;
	HashMap<String, LinkedList<indexedJSON>> Lines;

	public randomline() throws Exception{
		openweb.start_goodbyedpi();

		p = providedata.build();
		Lines = new HashMap<>();
	}

	private void openLine(String poolname, indexedJSON line) throws Exception{
		JSONArray contents = line.getcontent();
		String id = (String)contents.get(0);
		String raw = (String)contents.get(1);
		p.set_ai(poolname, line.getindex(), p.get_time());

		openweb.open(phraser.idToaddress(id, p.get_setting("s88")));
		System.out.println(line_info(id, raw));
		
		makeTag(poolname, line);

	}

	private void makeTag(String poolname, indexedJSON line, boolean sudo){
		System.out.print("\n");
		System.out.println(line_info((String)line.getcontent().get(0), (String)line.getcontent().get(1)));
		if(sudo || p.get_setting("s89")==2 ||
		 ( p.get_setting("s89")==1 && ((String)line.getcontent().get(1)).contains("9"))){
			System.out.print(Constant.TAG_CATEGORIES);
			System.out.print(Constant.TAG_MAKING_RULE);
			String cmd = phraser.tagtoraw(input.get_string());
			if(cmd.equals("delete")){
				System.out.println(Constant.DELETE);
				p.delete_line(poolname, line.getindex());
			}else if(cmd.equals("cancle")){
				System.out.println(Constant.CANCLE);
				return;
			}else if(cmd.equals("tagbuffer")){
				line.setpoolname(poolname);
				p.add_tagbuffer(line);
				return;
			}else if(cmd.equals("cancle")){
				System.out.println(Constant.CANCLE);
				return;
			}else{
				System.out.println(Constant.EDITED);
				p.set_tag(poolname, line.getindex(), cmd);
			}
		}
	}

	private void makeTag(String poolname, indexedJSON line){
		makeTag(poolname, line, false);
	}

	private String line_info(String id, String raw){
		if(id.length()>Constant.STR_LIMIT){
			return (String.format(Constant.LONG_LINE_INFO, id, phraser.rawtotag(raw)));
		}else{
			return (String.format(Constant.SHORT_LINE_INFO, id, phraser.rawtotag(raw)));
		}
	}

	private String line_info(int fake_index, String id, String raw){
		if(id.length()>Constant.STR_LIMIT){
			return (String.format(Constant.LONG_LINE_INFO_WITH_INDEX, fake_index, id, phraser.rawtotag(raw)));
		}else{
			return (String.format(Constant.SHORT_LINE_INFO_WITH_INDEX, fake_index, id, phraser.rawtotag(raw)));
		}
	}

	private void add(String poolname, String id){
		if(id.isEmpty()){
			System.out.print(Constant.CANCLE);
		}else if(p.contains_id(poolname, id)){
			System.out.print("이미 풀에 있습니다.\n");
		}else{
			p.add_line(poolname, id, Constant.NULL_TAG, p.get_time());
			System.out.println(String.format("%-3.3s: %-70.70s | 추가\n",poolname, id));
		}
	}

	private void delete(String poolname, int index){
		System.out.println(String.format(" %-3.3s: %-70.70s | 삭제\n",poolname, p.get_line(poolname, index).get(0)));
		p.delete_line(poolname, index);
	}

	private void delete(String poolname, String id){
		int index = p.search(poolname, id);
		if(index>-1){
			System.out.println(String.format(" %-3.3s: %-70.70s | 삭제\n",poolname, id));
			p.delete_line(poolname, index);
		}else{
			System.out.println(String.format(" %-70.70s | 풀에 없음\n", id));
		}
	}

///////////////////////////////////////  public  ///////////////////////////////////////

	public int get_setting(String cate){
		return p.get_setting(cate);
	}

	public void set_setting(String cate, int sw){
		System.out.println(String.format(" %s: %d\n", cate, sw));
		p.set_setting(cate, sw);
	}

	public boolean contains_pool(String poolname){
		return p.contains_pool(poolname);
	}

	public void add_buffer(String command){
		p.add_buffer(command);
	}

	public void print_menu(){
		System.out.println(String.format(Constant.INTRO, p.get_setting("s88"), p.get_setting("s89")));
	}

	//97번
	public void print_list(String poolname, boolean print_index){
		int index_limit = p.get_indexcount(poolname);

		StringBuilder sb = new StringBuilder();
		for(int fake_index=0, index=0;index<index_limit+1;index++){
			if(p.contains_index(poolname, index)){
				String id = (String)p.get_line(poolname, index).get(0);
				String raw = (String)p.get_line(poolname, index).get(1);
				sb.append(print_index?line_info(fake_index++, id, raw):line_info(id, raw));
			}
		}

		System.out.println(sb.toString());
		System.out.println(String.format(" %d개 line \n", p.get_linecount(poolname)));
	}

	//randomline!
	public void random(String poolname) throws Exception{
		if(!Lines.containsKey(poolname)){
			Lines.put(poolname, new LinkedList<>());
		}
		if(Lines.get(poolname).isEmpty()){
			Lines.get(poolname).addAll(p.random(poolname, 75, 25));
		}
		
		openLine(poolname, Lines.get(poolname).pop());
	}

	//87번 메뉴
	public void latest(String poolname) throws Exception{
		print_list(poolname, true);
		
		System.out.println("[ 입력 형식: [-]?[0-9]+, 최신올라가기 / ]");
		System.out.print(Constant.INPUT);

		String cmd = input.get_string();
		int offset, linecount = p.get_linecount(poolname);
		boolean ascending = true;
		if(cmd.matches("-[0-9]+")){
			offset = -Integer.parseInt(cmd);
			ascending = false;
		}else if(cmd.matches("[0-9]+")){
			offset = Integer.parseInt(cmd);
			ascending = true;
		}else if(cmd.equals("/")){
			offset = -1;
		}else{
			System.out.println(Constant.END);
			return;
		}
		System.out.print("\n");

		LinkedList<indexedJSON> temp = new LinkedList<>();
		while(true){
			if(temp.isEmpty()){
				temp.addAll(p.latest(poolname, offset, 20, ascending));
				offset += ascending?-20:20;
				offset %= linecount;
			}
			indexedJSON line = temp.pop();
			System.out.print(line_info((String)line.getcontent().get(0), (String)line.getcontent().get(1)));
			System.out.print("[열기 1, 스킵 2, 이전 라인 테그변경 3, 삭제 1115, 종료 etc] 입력: ");

			cmd = input.get_string();
			if(cmd.equals("1")){
				openLine(poolname, line);
			}else if(cmd.equals("3")){
				System.out.print("\n");
				makeTag(poolname, line, true);
			}else if(cmd.equals("1115")){
				delete(poolname, line.getindex());
			}else if(!cmd.equals("2")){
				System.out.println(Constant.END);
				return;
			}
			System.out.print("\n");
		}
	}

	//65번
	public void tagline(String poolname) throws Exception{
		//테그 검색 문자열(s) 입력
		System.out.println(Constant.TAG_CATEGORIES);
		System.out.println("[ 검색 < .[0-4](..[0-4])?( +-?[0-9][0-3]?)* >, 공백 10, 부분null 111, 완전null 1111 ]\n");
		System.out.print(Constant.SEARCH);

		ArrayList<indexedJSON> temp = p.tag(poolname, phraser.tagsearchToquery(input.get_string()));
		if(temp==null){
			System.out.println("잘못된 형식!\n");
			return;
		}

		System.out.println("\n총 "+temp.size()+"개 검색됨.\n");
		for(int i=0;i<temp.size();i++){
		    indexedJSON line = temp.get(i);
			System.out.print(line_info((String)line.getcontent().get(0), (String)line.getcontent().get(1)));
			System.out.print("[ 열기 1, 스킵 2, 이전 라인 테그변경 3, 삭제 1115 ] 입력: ");

			String cmd = input.get_string();
			if(cmd.equals("1")){
				openLine(poolname, line);
			}else if(cmd.equals("3")){
				System.out.print("\n");
				makeTag(poolname, line, true);
			}else if(cmd.equals("1115")){
				delete(poolname, line.getindex());
			}else if(!cmd.equals("2")){
				System.out.println(Constant.END);
				System.out.print("\n");
				return;
			}
			System.out.print("\n");
		}
		return;
	}

	//77번 메뉴
	public void s77() throws Exception{
		System.out.print(Constant.SEARCH);
		String search = phraser.addressToid(input.get_string());
		if(search.length()==0){
			System.out.print(Constant.END);
			return;
		}
		
		String cmd;
		for(String poolname:p.get_poollist()){
			for(Entry<Integer,JSONArray> entry:p.get_lines(poolname).entrySet()){
				if(((String)entry.getValue().get(0)).contains(search)){
					indexedJSON line = new indexedJSON(entry.getKey(), entry.getValue());
					System.out.print(
						String.format("\n %3.3s: %-80.80s \n [ 열기 1, 스킵 2, 테그변경 3, 삭제 1115 ] 입력:", 
						poolname,
						(String)entry.getValue().get(0))
					);

					cmd = input.get_string();
					if(cmd.equals("1")){
						openLine(poolname, line);
						System.out.print("\n");
						return;
					}else if(cmd.equals("3")){
						makeTag(poolname, line, true);
						return;
					}else if(cmd.equals("1115")){
						delete(poolname, line.getindex());
						System.out.print("\n");
						return;
					}else if(!cmd.equals("2")){
						System.out.println(Constant.END);
						System.out.print("\n");
						return;
					}
					System.out.print("\n");
				}
			}
		}

		System.out.print(" 검색 결과 없음 | [ 웹 검색 /, 사이트 검색 ., 풀 추가 번호] 입력: ");
		cmd = input.get_string();

		if(cmd.equals("/")){
			openweb.search(search);
		}else if(cmd.equals(".")){
			openweb.search(search, p.get_setting("s88"));
		}else if(p.contains_pool(cmd)){
			add(cmd, search);
		} else {
			System.out.print(Constant.END);
		}
	}

	// 메뉴
	public void addlist(String poolname) throws Exception{
		System.out.print(Constant.INPUT);
		String id = phraser.addressToid(input.get_string());

		add(poolname, id);
	}

	//101번 메뉴
	public void deletelist() throws Exception{
		s77();
	}
	
	//66번 메뉴
	public void buffer() throws Exception{
		JSONArray buffer = p.get_buffer();

		if(buffer.isEmpty()){
			System.out.println("버퍼가 비어있습니다.\n");
			return;
		}

		int fake_index = 0;
		for(Object s:buffer){
			System.out.println(String.format("%3d: %-80.80s", fake_index++, (String)s));
		}
		System.out.print("\n [ n 삭제 n, n-m 삭제 n-m, 전제삭제 //, 전체 추가 +poolname,  전체 삭제 -poolname ] ");
		System.out.print(Constant.INPUT);

		String cmd = input.get_string();
		if(cmd.isEmpty()){
			System.out.println(Constant.END);
			System.out.print("\n");
			return;
		}
		String poolname = cmd.substring(1);

		if(cmd.contains("+")){
			for(Object s:buffer)add(poolname, (String)s);
			p.reset_buffer();
		}else if(cmd.matches("[0-9]+")){
			System.out.println(String.format(" %-80.80s | 삭제 ",buffer.get(Integer.parseInt(cmd))));
			p.delete_buffer(Integer.parseInt(cmd));
		}else if(cmd.matches("[0-9]+-[0-9]+")){
			int start = Math.min(0, Integer.parseInt(cmd.substring(0,cmd.indexOf("-"))));
			int end = Math.max(buffer.size()-1,Integer.parseInt(cmd.substring(cmd.indexOf("-")+1)));
			for(int i=start;i<end+1;i++){
				p.delete_buffer(i);
			}
		}else if(cmd.contains("-") && p.contains_pool(poolname)){
			for(Object s:buffer)delete(poolname, (String)s);
			p.reset_buffer();
		}else if(cmd.contains("-") && !p.contains_pool(poolname)){
			System.out.println(" 삭제 불가: 풀 비었음. \n");
		}else if(cmd.matches("//")){
			p.reset_buffer();
		}else{
			System.out.println(Constant.CANCLE);
		}
	}

	//106번
	public void checkdeleted(String poolname) throws Exception{
		ArrayList<check_deleted_html> list = new ArrayList<>();
		ArrayList<indexedJSON> result = new ArrayList<>();
		check_deleted_html t;

		int process = 0, process_count = p.get_linecount(poolname), stopedcount = 0;
		for(Entry<Integer,JSONArray> entry:p.get_lines(poolname).entrySet()){
			process++;
			t = new check_deleted_html(result, new indexedJSON(entry.getKey(), entry.getValue()));
			list.add(t);
			t.start();
			if(process%130==0 || process == process_count){
				while(result.size() != process_count){
					if(result.size()==process)break;
					Thread.sleep(1000);
					if(stopedcount++>10){
						for(check_deleted_html t1:list){
							t1.interrupt();
						}
						System.out.println("일부 확인 실패!");
						break;
					}
				}
			}
		}

		while(result.contains(null)){
			result.remove(null);
		}

		System.out.println(String.format("\n결과: 총 %d개 삭제됨",result.size()));

		for(indexedJSON line:result){
			System.out.println(String.format(" %5d: %-70.70s ", line.getindex(), line.getcontent().get(0)));
		}
		System.out.print(" [ 삭제 1115 ] 입력: ");
		String cmd = input.get_string();
		if(cmd.equals("1115")){
			for(indexedJSON line:result){
				delete(poolname, line.getindex());
			}
		}else{
			System.out.println(Constant.CANCLE);
		}
	}
	
	// 106-1번
	private class check_deleted_html extends Thread{
		ArrayList<indexedJSON> result;
		indexedJSON line;

		public check_deleted_html(ArrayList<indexedJSON> result, indexedJSON line){
			this.result = result;
			this.line = line;
		}

		@Override
		public void run(){
			try{
				URL login = new URL((String)line.getcontent().get(0));
				HttpURLConnection https = (HttpURLConnection)login.openConnection();
				https.setRequestProperty("user-agent","");

				int rc = https.getResponseCode();
				System.out.println(String.format(" %5d %-80.80s -> %d ",line.getindex(), line.getcontent().get(0), rc));
				if(rc==404){
					result.add(line);
				}else{
					result.add(null);
				}
				if(!https.getURL().equals((String)line.getcontent().get(0))){
					p.set_id(line.getpoolname(), line.getindex(), https.getURL().toString());
				}
			}catch(Exception e) {
				result.add(null);
			}
		}
	}
	
	public void end(){
		p.end();
		openweb.end_goodbyedpi();
		System.exit(0);
	}

	// 69번
	public void tagbuffer() throws Exception{
		ArrayList<indexedJSON> tagbuffer = p.get_tagbuffer();
		if(tagbuffer.isEmpty()){
			System.out.println("테그버퍼 비었음.\n");
			return;
		}

		JSONArray content;
		for(int i=0;i<tagbuffer.size();i++){
			content = tagbuffer.get(i).getcontent();
			line_info(i,(String)content.get(0), (String)content.get(1));
		}
		System.out.println("\n");
		
		Iterator<indexedJSON> ii = tagbuffer.iterator();
		for(int i=0;ii.hasNext();i++){
			indexedJSON line = ii.next();
			content = line.getcontent();
			System.out.print(
				String.format(" %3.3s: %-70.70s \n [ 다음 열기 한자리수, 스킵 12 ] 입력: ", 
				line.getpoolname(), content.get(0)));

			String cmd = input.get_string();
			if(cmd.matches("[0-9]")){
				makeTag(line.getpoolname(), line);
				p.delete_tagbuffer(i);
			}else if(!cmd.equals("12")){
				break;
			}
		}
		System.out.println(Constant.END);
		return;
	}

	//102번 메뉴
	public void s102() throws Exception{
		System.out.print(Constant.SEARCH);
		openweb.search(input.get_string());
		System.out.print("\n");
	}

	//1021번 메뉴
	public void so1() throws Exception{
		System.out.println("[ 입력 문자열, 종료 // ]");
		ArrayList<String> search_list = new ArrayList<>();

		String s = input.get_string();
		for(; !s.equals("//"); s = input.get_string()){
			if(!s.matches(".?"))search_list.add(s.trim());
		}

		for(String search:search_list){
			if(search.length()<2){
				System.out.println(String.format("\n %-70.70s | r검색 길이가 짧음 ", search));
				continue;
			}

			System.out.print(String.format("\n %-70.70s | [ 웹 1, 사이트 2, 스킵 3 ] 입력: ", search));
			String cmd = input.get_string();
			if(cmd.equals("1")){
				openweb.search(search);
			}else if(cmd.equals("2")){
				openweb.search(search, p.get_setting("s88"));
			}else if(!cmd.equals(("3"))){
				break;
			}
		}
		System.out.println(Constant.END);
		return;
	}

	//107번 메뉴
	public void deleteverticaltag(String poolname) throws Exception{
		System.out.println(Constant.TAG_CATEGORIES);
		System.out.print("초기화 테그 번호 : ");
		String cmd = input.get_string();

		if(cmd.matches("[0-9]")){
			int tagcate = Integer.parseInt(cmd);
			System.out.print(String.format(" [ 테그 %s 를 초기화 1115 ] 입력: ", cmd));
			if(input.get_string().equals("1115")){
				for(int index:p.get_indexset(poolname)){
					StringBuilder raw = new StringBuilder((String)p.get_line(poolname, index).get(1));
					raw.setCharAt(tagcate, '9');
					p.set_tag(poolname, index, raw.toString());
				}
			}else{
				System.out.println(Constant.CANCLE);
			}
		}else{
			System.out.println(Constant.CANCLE);
		}
	}

	public void pause(){
		try {
			System.in.read();
		} catch (Exception e) { 
		}
	}

	//104번 메뉴
	public void openweb() throws Exception{
		System.out.print(" 입력 : ");
		String address = input.get_string();
		if(address.contains("hitomi") || address.contains("hiyobi")){
			openweb.open(phraser.idToaddress(address, address.contains("hitomi")?1:0));
		}else{
			System.out.println("형식이 다름");
		}
		System.out.print("\n");
	}
}