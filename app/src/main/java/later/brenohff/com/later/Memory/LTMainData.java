package later.brenohff.com.later.Memory;

public class LTMainData {

    private static LTMainData instance = null;

    private LTMainData(){

    }

    public static LTMainData getInstance(){
        if(instance == null){
            instance = new LTMainData();
        }

        return instance;
    }
}
