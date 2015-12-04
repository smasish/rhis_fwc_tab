package org.sci.rhis.fwc;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by jamil.zaman on 18/11/15.
 */
public class LocationHolder {

    class JsonBuilder extends AsyncTask<String, Integer, Integer> {


        protected Integer doInBackground(String... params) {

            return 0;
        }

        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            //getSpinner(R.id.advSearchDistrict).setAdapter(zillaAdapter);
            //loadVillages.setVisibility(View.GONE);
        }

        protected void onProgressUpdate (Integer... progress) {
            //Toast.makeText(context, "The Village list is Loading", Toast.LENGTH_LONG).show();
        }


    };
    LocationHolder(String code, String nameEng, String nameBeng, String sublocationStr) {
        this.code = code;
        englishName = nameEng;
        banglaName = nameBeng;
        this.sublocationStr = sublocationStr;
    }

    LocationHolder() {
        this.code = "none";
        englishName = "";
        banglaName = "";
        this.sublocationStr = "";
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

    private static String villageString;
    private static String zillaUpazillaUnionString;
    private static JSONObject villageJson = null;
    private static JsonBuilder jsonBuilder = null;

    public static JSONObject getVillageJson() {
        return villageJson;
    }

    public static JsonBuilder getJsonBuilder() {
        return jsonBuilder;
    }

    public String getSublocationStr() {
        return sublocationStr;
    }

    public static String getVillageString() {
        return villageString;
    }

    public static void setVillageString(String villageString) throws JSONException{
        LocationHolder.villageString = villageString;
        //jsonBuilder = new
        villageJson = new JSONObject(villageString);

    }

    /*public static JsonBuilder getJsonBuilder() {
        return new JsonBuilder();
    }*/

    public static void setJsonBuilder(JsonBuilder builder) {
        jsonBuilder = builder;
    }

    public static String getZillaUpazillaUnionString() {
        return zillaUpazillaUnionString;
    }

    public static void setZillaUpazillaUnionString(String zillaUpazillaUnionString) {
        LocationHolder.zillaUpazillaUnionString = zillaUpazillaUnionString;
    }

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
/*
            if(jsonStr.equals("")) { //parse if the string is valid
               return;
            }*/
            JSONObject json = new JSONObject(jsonStr.equals("") ? "{}": jsonStr);

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

    public static void loadJsonFile(String fileName, StringBuilder jsonBuilder, AssetManager am) throws IOException {
        InputStream is = am.open(fileName);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        jsonBuilder.append(new String(buffer, "UTF-8"));
    }
}
