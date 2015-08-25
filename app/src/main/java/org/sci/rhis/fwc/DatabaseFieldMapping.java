package org.sci.rhis.fwc;

import java.util.HashMap;

/**
 * Created by jamil.zaman on 24/08/2015.
 */
public class DatabaseFieldMapping {
    public static HashMap<String, Integer> CLIENT_INTRO;
    public static HashMap<String, Integer> CLIENT_INFO;

    public static void InitializeClientInformation() {
        CLIENT_INTRO = new HashMap<String, Integer>();
        CLIENT_INTRO.put("cName", R.id.Client_name);
        CLIENT_INTRO.put("cAge", R.id.Clients_Village);
        CLIENT_INTRO.put("cMobileNo", R.id.Clients_Mobile_no);
        CLIENT_INTRO.put("cDist", R.id.Clients_District);
        CLIENT_INTRO.put("cUpz", R.id.Clients_Upozila);
        CLIENT_INTRO.put("cUnion", R.id.Clients_Union);
        //CLIENT_INTRO.put("cMouza", R.id.Client_);
        CLIENT_INTRO.put("cVill", R.id.Clients_Village);
    }


}
