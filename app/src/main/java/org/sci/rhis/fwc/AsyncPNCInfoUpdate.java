package org.sci.rhis.fwc;

/**
 * Created by mohammod.alamin on 10/29/2015.
 */
public class AsyncPNCInfoUpdate extends SendPostRequestAsyncTask{

    AsyncPNCInfoUpdate(AsyncCallback cb) {
        super(cb);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}