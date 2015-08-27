package org.sci.rhis.fwc;

import android.app.Activity;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by jamil.zaman on 14/08/2015.
 */
public class AsyncClientInfoUpdate extends SendPostRequestAsyncTask{
    String mName;

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getName() {
        return mName;
    }

    AsyncClientInfoUpdate(Activity activity) { super(activity);}

    private void populateClientDetails(JSONObject json, HashMap<String, Integer> fieldMapping) {
        Iterator<String> i = fieldMapping.keySet().iterator();
        String key = "";

        while(i.hasNext()) {
            key = i.next();
            if (fieldMapping.get(key) != null) { //If the field exist in the mapping table
                try {
                    ((EditText) getActivity().findViewById(fieldMapping.get(key))).setText(json.get(key).toString());
                } catch (JSONException jse) {
                    System.out.println("JSON Exception Thrown:\n " );
                    jse.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            JSONObject json = new JSONObject(result);
            String key = "";

            //DEBUG
            for ( Iterator<String> ii = json.keys(); ii.hasNext(); ) {
                key = ii.next();
                System.out.println("1.Key:" + key + " Value:\'" + json.get(key)+"\'");
            }

            if(json.get("False").toString().equals("")) {
                populateClientDetails(json, DatabaseFieldMapping.CLIENT_INTRO);
                //populateClientDetails(json, DatabaseFieldMapping.CLIENT_INFO);
            }

        } catch (JSONException jse) {
            System.out.println("JSON Exception Thrown:\n " );
            jse.printStackTrace();
        }
    }
}
