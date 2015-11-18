package org.sci.rhis.fwc;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by jamil.zaman on 18/11/15.
 */
public class LocationHolder {
    LocationHolder(String code, String nameEng, String nameBeng, String sublocationStr) {
        this.code = code;
        englishName = nameEng;
        banglaName = nameBeng;
        this.sublocationStr = sublocationStr;
    }

    LocationHolder(String code, String nameEng, String nameBeng, JSONObject subLocation, String sublocationStr) {
        this.code = code;
        englishName = nameEng;
        banglaName = nameBeng;
        if(!sublocationStr.equals("")) {
            try {
                sublocation = new JSONObject("subLocationStr");
            } catch (JSONException jse) {
                sublocation = null;
            }
        } else {
            sublocation = null;
        }
        this.sublocationStr = sublocationStr;
        //Log.d("LOCATION-ADV",this.sublocationStr);
    }

    private String code;
    private String englishName;
    private String banglaName;
    private JSONObject sublocation;
    private String sublocationStr;

    public String getCode() {
        return code;
    }

/*        public void setCode(String code) {
            this.code = code;
        }*/

    public String getEnglishName() {
        return englishName;
    }

        /*public void setEnglishName(String englishName) {
            this.englishName = englishName;
        }*/

    public String getBanglaName() {
        return banglaName;
    }

        /*public void setBanglaName(String banglaName) {
            this.banglaName = banglaName;
        }*/

    public String getSublocation() {
        return sublocationStr;
    }

        /*public void setSublocation(JSONObject sublocation) {
            this.sublocation = sublocation;
        }*/

    @Override
    public String toString() {
        return banglaName;
    }

    public static void loadListFromJson(
            String jsonStr,
            String keyEnglish,
            String keyBangla,
            String keySublocation,
            ArrayList<LocationHolder> holderList) {
        try{
            JSONObject json = new JSONObject(jsonStr);

            String key = "";
            String code = "";

            for(Iterator<String> keys = json.keys(); keys.hasNext();) {
                key = keys.next();
                JSONObject subobject = json.getJSONObject(key);
                //Log.d(LOGTAG, "Code:[" + key + "]->[" + subobject.get(keyBangla) + "]");
                if(keySublocation.equals("Upazila")) {
                    code = key + "_" + subobject.getString("divId");
                } else {
                    code = key;
                }
                holderList.add(
                        new LocationHolder(
                                code,
                                subobject.getString(keyEnglish),
                                subobject.getString(keyBangla),
                                //subobject.getJSONObject(keySublocation),
                                keySublocation.equals("")? "" : subobject.getString(keySublocation)));

            }
        } catch (JSONException jse) {
            jse.printStackTrace();
        }
    }


}
