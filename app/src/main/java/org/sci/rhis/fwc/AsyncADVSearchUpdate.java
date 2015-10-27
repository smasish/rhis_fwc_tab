package org.sci.rhis.fwc;

/**
 * Created by mohammod.alamin on 10/25/2015.
 */
public class AsyncADVSearchUpdate extends SendPostRequestAsyncTask {

    AsyncADVSearchUpdate(AsyncCallback cb){super(cb);}

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}
