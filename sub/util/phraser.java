package sub.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sub.Constant;

public class phraser {    
    public static String idToaddress(String id, int s88){
		return id;
    }

    public static String addressToid(String address){
		return address;
    }
    
    public static String websearchToaddress(String search){
        search=search.replaceAll(" ","+");
        search=search.replaceAll("_","+");
        search=search.trim();
        search="www.google.co.kr/search?q="+search;

        return search;
    }

    public static String tagtoraw(String tag){
		//테그 입력 문자열이 형식을 만족시키는 경우
		if(tag.matches("[.][0-4]( +[0-9][0-3]?)*")){
			//StringBuilder sb에서 새로운 문자열 만든다.
			StringBuilder sb = new StringBuilder(Constant.EMPTY_TAG);

			tag = tag.substring(tag.indexOf(".")+1);
			StringTokenizer st = new StringTokenizer(tag);
			
			//x는 테그 정보를 가진 ArrayList이다.
			ArrayList<Integer> x = new ArrayList<>();
			while(st.hasMoreTokens())x.add(Integer.valueOf(st.nextToken()));

			//x에서 테그 정보 하나씩 분해해 sb에서 문자열 만든다.
			for(int q=0;q<x.size();q++){
				if(q==0){
					sb.setCharAt(0,(char)(x.get(0)+48));
				} else {
					if(x.get(q)<10)sb.setCharAt(x.get(q),'1');
					else sb.setCharAt(x.get(q)/10,(char)(x.get(q)%10+48));
				}
			}

			//sb 테그 저장.
            return sb.toString();
			
		//테그 입력 문자열이 형식을 만족시키지 않는 경우
		} else {
			//테그 입력 문자열이 10인 경우
			if(tag.trim().equals("10")){
                return "tagbuffer";
				
			//테그 입력 문자열이 445인 경우
			} else if(tag.contains("445")){
                return Constant.NULL_TAG;
				
			//테그 입력 문자열이 1115인 경우
			} else if(tag.contains("1115")){
                return "delete";
				
			//테그 입력 전혀 형식에 맞지 않는 경우
			} else {
                return "cancle";
			}
		}
    }

    public static String rawtotag(String raw){
		//원래 테그 so, 새로 만드는 테그 s, 테그 모두 39자
		StringBuilder s	= new StringBuilder();
		s.append(".");

		if(raw.equals(Constant.NULL_TAG))return "---null------null------null------null---";

		ArrayList<Integer> ai = new ArrayList<>();
		for(int c=0;c<raw.length();c++){
			if(c==0){
				ai.add(Integer.valueOf(raw.charAt(c)-48));
			}else if(raw.charAt(c)!='0'&&raw.charAt(c)!='9'){
				if(raw.charAt(c)!='1'){
					ai.add(10*c+Integer.valueOf(raw.charAt(c)-48));
				}else {
                    ai.add(c);
                }
			}
		}
		for(Integer i:ai)s.append(i+" ");		

		return s.toString();
    }

    public static String tagsearchToquery(String tagsearch){
		//특수 조건 검사
		if(!tagsearch.contains(".")){
			if(tagsearch.equals("10")){
                return Constant.EMPTY_TAG;
			} else if(tagsearch.equals("1111")){
                return Constant.NULL_TAG;
			} else if(tagsearch.equals("111")){
                return ".*9.*";
			} else{
                return null;
            }
		//일반 조건 검사
		} else if(tagsearch.matches(".[0-4](..[0-4])?( +-?[0-9][0-3]?)*")){
            //"[0-4][0-3][0-3][0-3][0-3][0-3][0-3][0-3][0-3][0-3]"

            StringBuilder query = new StringBuilder();
			StringTokenizer st = new StringTokenizer(tagsearch," .");
            if(tagsearch.contains("..")){
                query.append("[").append(st.nextToken()).append("-").append(st.nextToken()).append("]");
            }else{
                String point = st.nextToken();
                query.append("[").append(point).append("-").append(point).append("]");
            }

            HashMap<Integer,Integer> x = new HashMap<>();
			while(st.hasMoreTokens()){
                int d = Integer.parseInt(st.nextToken());
                if(d<10&&d>-10){
                    x.put(d>0?d:-d,d>0?1:-1);
                }else{
                    x.put(d>0?d/10:-d/10,d%10);
                }
            }

            for(int i=1;i<10;i++){
                if(x.containsKey(i)){
                    int grade = x.get(i);
                    if(grade<0){
                        query.append("[0-").append(-1-grade).append("]");
                    }else{
                        query.append("[").append(grade).append("-3]");
                    }
                }else{
                    query.append("[0-3]");
                }
            }
            return query.toString();
        } else{
            return null;
        }
    }

    public static String addressTonumber(String address){
        Pattern pattern = Pattern.compile("[1-2]?[0-9]{6}");
        Matcher matcher = pattern.matcher(address);
		matcher.find();

        try{
            return matcher.group();
        }catch(Exception e){
            return null;
        }
    }
}