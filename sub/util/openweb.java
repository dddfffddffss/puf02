package sub.util;

import java.io.IOException;

public class openweb {
    private static Runtime r = Runtime.getRuntime();
    private static Process goodbyedpi;

    public static void start_goodbyedpi() throws IOException{
        goodbyedpi = r.exec("exe\\goodbyedpi.exe");
    }

    public static void end_goodbyedpi(){
        goodbyedpi.destroy();
    }

    public static void open(String address){
        try{
            r.exec("cmd /c start /max /b "+address);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // 일반검색
    public static void search(String search){
        if(search.length()>1){
            openweb.open(phraser.websearchToaddress(search));
        } else {
            System.out.println("검색 길이가 너무 짧습니다.");
			return;
        }
    }

    // 사이트검색
    public static void search(String search, int s88){
        if(search.length()>1){
            openweb.open(phraser.sitesearchToaddress(search, s88));
        } else {
            System.out.println("검색 길이가 너무 짧습니다.");
			return;
        }
    }
}