package later.brenohff.com.later.Memory;

import later.brenohff.com.later.Models.LTUser;

public class LTMainData {

    private static LTMainData instance = null;
    private LTUser user;

    private LTMainData() {
    }

    public static LTMainData getInstance() {
        if (instance == null) {
            instance = new LTMainData();
        }

        return instance;
    }

    public LTUser getUser() {
        return user;
    }

    public void setUser(LTUser user) {
        this.user = user;
    }
}
