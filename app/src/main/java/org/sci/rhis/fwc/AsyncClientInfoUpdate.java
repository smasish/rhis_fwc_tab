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

    //AsyncClientInfoUpdate(Activity activity) { super(activity);}
    AsyncClientInfoUpdate(AsyncCallback cb) {
        super(cb);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}
