package org.sci.rhis.fwc;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {

    Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //addListenerOnButton();
        Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(),"Nikosh.ttf");
        TextView textview = (TextView)findViewById(R.id.providerLabelId);
        textview.setTypeface(tf);

    }

    public void startLogin(View view) {
        final EditText passwdText = (EditText)findViewById(R.id.providerPassword);
        final EditText providerText = (EditText)findViewById(R.id.providerId);
        //Button loginButton = (Button)findViewById(R.id.buttonLogin);
        //final TextView loginBanner = (TextView)findViewById(R.id.textViewBanner);
        ClientInfo client = new ClientInfo();
        //SendPostRequestAsyncTask
        SendPostRequestAsyncTask sendPostReqAsyncTask = new SendPostRequestAsyncTask(this);
        String queryString =   "{" +
                "uid:" + providerText.getText().toString() + "," +
                "upass:" + passwdText.getText().toString() + "," +
                "client:" + "android" +
                "}";
        String servlet = "login";
        String jsonRootkey = "loginInfo";
        sendPostReqAsyncTask.execute(queryString, servlet, jsonRootkey);
    }

    /*
    public void addListenerOnButton() {
        final Context context = this;
        button = (Button) findViewById(R.id.loginbtn1);
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(context, SecondActivity.class);
                startActivity(intent);
            }
        });
    }*/
}

