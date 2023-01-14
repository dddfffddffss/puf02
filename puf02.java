//다중 작업
//버퍼 여러개
//etc칸에 작가이름 만들기
//메모에는 artist로 만든다.
//gui로 변경
//테그 검색 한번에 열기
//랜덤줄 한번에 열기

import sub.randomline;
import sub.util.input;

public class puf02
{	public static void main(String args[])
    {
		int k=0;
		String poolname = null, command;
		randomline rl = null;
			
		try{
			rl = new randomline();
			rl.print_menu();
			
			while(true){
				command = input.get_string();
				
				if(!command.matches("[0-9]{1,4}")){
					if(command.length()>1){
						System.out.println(String.format(" %-90.90s | 버퍼 추가",command));
						rl.add_buffer(command);
					}
					continue;
				}
				
				k = Integer.parseInt(command);
				if(k==65||k==87||k == 97||k==107||k==106){
					System.out.print("poolname: ");
					poolname = input.get_string();
					if(!rl.contains_pool(poolname) || poolname.isEmpty()){
						System.out.println("풀 없음!\n");
						continue;
					}
				} else if(k == 100) {
					System.out.print("poolname: ");
					poolname = input.get_string();
					if(poolname.isEmpty()){
						System.out.println("풀 없음!\n");
						continue;
					}
				}

				if(k == 65)rl.tagline(poolname);

				else if(k == 66)rl.buffer();

				else if(k == 69)rl.tagbuffer();

				else if(k == 77)rl.s77();

				else if(k == 87)rl.latest(poolname);

				else if(k == 88)rl.set_setting("s88", rl.get_setting("s88")==1?0:1);

				else if(k == 89)rl.set_setting("s89", (rl.get_setting("s89")+1)%3);

				else if(k == 97)rl.print_list(poolname, true);

				else if(k == 99)rl.print_menu();

				else if(k == 100)rl.addlist(poolname);

				else if(k == 101)rl.deletelist();

				else if(k == 102)rl.s102();
				
				else if(k == 1021)rl.so1();

				else if(k == 104)rl.openweb();

				else if(k == 106)rl.checkdeleted(poolname);

				else if(k == 107)rl.deleteverticaltag(poolname);

				else if(k == 444)rl.end();

				else if(rl.contains_pool(command))rl.random(command);

				else System.out.println("옳지 않은 명령어!\n");
			}
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println(k+" 번 메뉴에서 에러!\n종료하려면 아무 키나 입력하세요.");
			input.get_string();
			rl.end();
		}
    }
}