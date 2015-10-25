package org.sci.rhis.fwc;

/**
 * Created by mohammod.alamin on 10/20/2015.
 */
public class AsyncNonRegisterClientInfoUpdate  extends SendPostRequestAsyncTask{

    //AsyncNonRegisterClientInfoUpdate(Activity activity) { super(activity);}
    AsyncNonRegisterClientInfoUpdate(AsyncCallback cb) {
        super(cb);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}