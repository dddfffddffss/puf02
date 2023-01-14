package sub.util;

import java.util.Scanner;

public class input {
	static Scanner sc = new Scanner(System.in);

	public static String get_string(){
		return sc.nextLine().trim();
	}

	public static int get_int() throws Exception{
        return Integer.valueOf(sc.nextLine().trim());
	}

	public static int get_int(boolean msg){
        try{
            return Integer.valueOf(sc.nextLine().trim());
        }catch(Exception e){
            if(msg)System.out.println("잘못된 입력입니다.");
            return -1;
        }
	}
}