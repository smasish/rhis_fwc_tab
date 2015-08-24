package org.sci.rhis.fwc;

import android.app.Activity;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

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

    @Override
    protected void onPostExecute(String result) {
        try {
            JSONObject json = new JSONObject(result);
            String key = "";
            for ( Iterator<String> i = json.keys(); i.hasNext(); ) {
                key = i.next();
                System.out.println("Key:" + key + " Value:" + json.get(key));
            }

            if(json.getBoolean("False") == false) {
                //client does not exist
            } else {
                EditText cName = (EditText)getActivity().findViewById(R.id.Client_name);
                EditText cAge = (EditText)getActivity().findViewById(R.id.Client_name);
                EditText cMobileNo = (EditText)getActivity().findViewById(R.id.Client_name);
                EditText cDist = (EditText)getActivity().findViewById(R.id.Client_name);
                EditText cUpz = (EditText)getActivity().findViewById(R.id.Client_name);
                EditText cUnion = (EditText)getActivity().findViewById(R.id.Client_name);
                EditText cMouza = (EditText)getActivity().findViewById(R.id.Client_name);

            }

//            if(json.getBoolean("loginStatus")) {
//                Intent intent = new Intent(getContext(), SecondActivity.class);
//                getContext().startActivity(intent);
//                System.out.println("Post Response: " + result);
//            };
        } catch (JSONException jse) {
            System.out.println("JSON Exception Thrown:\n " );
            jse.printStackTrace();
        }
    }
}
