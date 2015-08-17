package org.sci.rhis.fwc;

import android.content.Context;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by jamil.zaman on 16/08/2015.
 */
public class AsyncLoginTask extends SendPostRequestAsyncTask {
    AsyncLoginTask(Context context) { super(context);}

    @Override
    protected void onPostExecute(String result) {
        try {
            JSONObject json = new JSONObject(result);
            for ( Iterator<String> i = json.keys(); i.hasNext(); ) {
                System.out.println("" + i.next());
            }

            if(json.getBoolean("loginStatus")) {
                Intent intent = new Intent(getContext(), SecondActivity.class);
                getContext().startActivity(intent);
                System.out.println("Post Response: " + result);
            };
        } catch (JSONException jse) {
            System.out.println("JSON Exception Thrown:\n " );
            jse.printStackTrace();
        }
    }
}
