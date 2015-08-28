package org.sci.rhis.fwc;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by jamil.zaman on 8/28/2015.
 */
public abstract class ClinicalServiceActivity extends FWCServiceActivity {
    @Override
    public abstract void callbackAsyncTask(String result);
}
