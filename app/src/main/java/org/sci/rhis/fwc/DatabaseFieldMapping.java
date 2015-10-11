package org.sci.rhis.fwc;

import java.util.HashMap;

/**
 * Created by jamil.zaman on 24/08/2015.
 */
public class DatabaseFieldMapping {
    public static HashMap<String, Integer> CLIENT_INTRO;
    public static HashMap<String, Integer> CLIENT_INFO;
    public static HashMap<String, Integer> PROVIDER_INFO;

    public static void InitializeClientIntroduction() {
        CLIENT_INTRO = new HashMap<String, Integer>();
        CLIENT_INTRO.put("cName", R.id.Client_name);
        CLIENT_INTRO.put("cAge", R.id.Clients_Age);
        CLIENT_INTRO.put("cMobileNo", R.id.Clients_Mobile_no);
        CLIENT_INTRO.put("cDist", R.id.Clients_District);
        CLIENT_INTRO.put("cUpz", R.id.Clients_Upazila);
        CLIENT_INTRO.put("cUnion", R.id.Clients_Union);
        //CLIENT_INTRO.put("cMouza", R.id.Client_);
        CLIENT_INTRO.put("cVill", R.id.Clients_Village);
        //CLIENT_INTRO.put("cHealthID", R.id.Clients_HealthId);
        CLIENT_INTRO.put("cElcoNo", R.id.Clients_ElcoNo);
        CLIENT_INTRO.put("cHusbandName", R.id.Clients_Husband);
    }

    public static void InitializeClientInformation() {
        CLIENT_INFO = new HashMap<String, Integer>();
        CLIENT_INFO.put("cLMP", R.id.lmpDate);
        CLIENT_INFO.put("cEDD", R.id.edd);
        CLIENT_INFO.put("cPara", R.id.para);
        CLIENT_INFO.put("cGravida", R.id.gravida);
        CLIENT_INFO.put("cBoy", R.id.SonNum);
        CLIENT_INFO.put("cGirl", R.id.DaughterNum);
        //CLIENT_INTRO.put("cMouza", R.id.Client_);
        //CLIENT_INFO.put("cLastChildAge", R.id.);
        //CLIENT_INTRO.put("cHealthID", R.id.Clients_HealthId);
        //CLIENT_INFO.put("cHeight", R.id.Clients_ElcoNo);
        //CLIENT_INFO.put("cBloodGroup", R.id.Clients_Husband);
        //CLIENT_INFO.put("cHistoryComplicated", R.id.Clients_Upazila);
        //CLIENT_INFO.put("cHistoryComplicatedContent", R.id.Clients_Union);
    }
    public static void Initializefwc()
    {
        PROVIDER_INFO.put("FacilityName",R.id.fwc_heading);
    }
}
