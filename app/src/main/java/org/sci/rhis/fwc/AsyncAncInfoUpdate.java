package org.sci.rhis.fwc;

/**
 * Created by jamil.zaman on 18/10/15.
 */
public class AsyncAncInfoUpdate extends SendPostRequestAsyncTask{


    AsyncAncInfoUpdate(AsyncCallback cb) {
        super(cb);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}
