package org.sci.rhis.fwc;

/**
 * Created by mohammod.alamin on 10/4/2015.
 */
public class AsyncNewbornInfoUpdate extends SendPostRequestAsyncTask{

    //AsyncClientInfoUpdate(Activity activity) { super(activity);}
    AsyncNewbornInfoUpdate(AsyncCallback cb) {
        super(cb);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}