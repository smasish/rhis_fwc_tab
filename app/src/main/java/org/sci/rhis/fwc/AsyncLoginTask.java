package org.sci.rhis.fwc;

import android.app.Activity;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by jamil.zaman on 16/08/2015.
 */
public class AsyncLoginTask extends SendPostRequestAsyncTask {
    /*AsyncLoginTask(Activity activity) { super(activity);}*/
    AsyncLoginTask(AsyncCallback cb) { super(cb);}

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        /*try {
            JSONObject json = new JSONObject(result);
            for ( Iterator<String> i = json.keys(); i.hasNext(); ) {
                System.out.println("" + i.next());
            }

            if(json.getBoolean("loginStatus")) { //if successful login
                //first create the provider object
                ProviderInfo provider = ProviderInfo.getProvider();
                provider.setProviderName(json.getString("ProvName"));
                provider.setProviderCode(json.getString("ProvCode"));
                provider.setProviderFacility(json.getString("FacilityName"));
                Intent intent = new Intent(getContext(), SecondActivity.class);
                getContext().startActivity(intent);
                System.out.println("Post Response: " + result);
            } else {
                //todo: displaya red colored text view that log in failed.
            }
        } catch (JSONException jse) {
            System.out.println("JSON Exception Thrown:\n " );
            jse.printStackTrace();
        }*/
    }
}
