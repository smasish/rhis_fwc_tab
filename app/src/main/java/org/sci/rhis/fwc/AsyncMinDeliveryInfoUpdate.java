package org.sci.rhis.fwc;

/**
 * Created by mohammod.alamin on 10/29/2015.
 */
public class AsyncMinDeliveryInfoUpdate extends SendPostRequestAsyncTask{

    AsyncMinDeliveryInfoUpdate(AsyncCallback cb) {
        super(cb);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}