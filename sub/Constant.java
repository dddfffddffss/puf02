package sub;

public final class Constant{
    public static final int STR_LIMIT = 60;
    public static final String DATA_DIR = 						"data\\data.json";

	public static final String DELETE = 						"삭제합니다.\n";
	public static final String EDITED = 						"수정합니다.\n";
	public static final String END = 							"종료합니다.\n";
	public static final String SAVE = 							"저장합니다.\n";
	public static final String CANCLE = 						"취소합니다.\n";
	public static final String INPUT = 							"입력: ";
	public static final String SEARCH = 						"검색: ";

	public static final String SHORT_LINE_INFO = 				" %-70s      < %-27.27s >\n";
	public static final String LONG_LINE_INFO =					" %-70.70s...   < %-27.27s >\n";
	public static final String SHORT_LINE_INFO_WITH_INDEX = 	" %-5d %-70s      < %-27.27s >\n";
	public static final String LONG_LINE_INFO_WITH_INDEX =		" %-5d %-70.70s...   < %-27.27s >\n";

    public static final String EMPTY_TAG = 	"0000000000";
	public static final String NULL_TAG = 	"9999999999";
    public static final String TAG_CATEGORIES = 
    " 1 : / 2 : / 3 : / 4 : / 5 : / 6: / 7: / 8: / 9: \n";
    public static final String TAG_SEARCHING_RULE = (
	" [ 입력 형식 < .[0-4](..[0-4])?( +-?[0-9][0-3]?)* >, 공백 10, 부분null 111, 완전null 1111 ] \n" + 
    "검색 : ");
	public static final String TAG_MAKING_RULE = (
	" [ 입력 형식 < .[0-4]( +[0-9][0-3]?)* * >, 테그버퍼 10, null테그 445, 삭제 1115 ] \n" + 
	"입력 : ");
	
	public static final String INTRO = (
	"\n s88 : %d               s89 : %d \n"+
	"\n 테그로 찾기:65			버퍼 수정:66				테그 버퍼:69\n"+
	"\n 이름으로 찾기:77\n"+
	"\n 최신순:87			사이트 스위치:88			테그 스위치:89\n"+
	"\n 인덱스 목록: 97		메뉴보기:99				추가:100   삭제:101\n"+
	"\n 사이트검색:102  		웹검색:103				한번에검색:1021	\n"+
	"\n 사이트 교환:104		에러페이지 삭제:106			테그세로줄 전체삭제:107  \n"+
	"\n 끝내기:444\n");
}