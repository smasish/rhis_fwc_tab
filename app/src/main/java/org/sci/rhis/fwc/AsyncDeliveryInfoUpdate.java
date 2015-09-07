package org.sci.rhis.fwc;

/**
 * Created by jamil.zaman on 03/09/15.
 * Framework classes later be extended to handle offline issues
 */
public class AsyncDeliveryInfoUpdate extends SendPostRequestAsyncTask{

    //AsyncClientInfoUpdate(Activity activity) { super(activity);}
    AsyncDeliveryInfoUpdate(AsyncCallback cb) {
        super(cb);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}
