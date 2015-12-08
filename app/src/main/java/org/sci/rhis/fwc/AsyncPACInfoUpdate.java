package org.sci.rhis.fwc;

/**
 * Created by armaan-ul.islam on 12/08/2015.
 */
public class AsyncPACInfoUpdate extends SendPostRequestAsyncTask{

    AsyncPACInfoUpdate(AsyncCallback cb) {
        super(cb);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}