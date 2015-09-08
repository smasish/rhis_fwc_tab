package org.sci.rhis.fwc;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by jamil.zaman on 14/08/2015.
 */
public class AsyncClientInfoUpdate extends SendPostRequestAsyncTask{
    String mName;
    ProgressBar bar;

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
        if(bar != null) {
            bar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {

        bar = (ProgressBar) getActivity().findViewById(R.id.progressBarLogin);
        if(bar != null) {
            bar.setVisibility(View.VISIBLE);
        }
    }
}
