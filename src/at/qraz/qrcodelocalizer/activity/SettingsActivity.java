package at.qraz.qrcodelocalizer.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import at.qraz.qrcodelocalizer.R;
import at.qraz.qrcodelocalizer.Settings;

public class SettingsActivity extends Activity {
    
    private EditText mQrazUrl;
    private EditText mUpdateUrl;
    private EditText mUserName;
    private EditText mPassword;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.settings);
        
        mQrazUrl = (EditText) findViewById(R.id.qrazUrl);
        mUpdateUrl = (EditText) findViewById(R.id.updateUrl);
        mUserName = (EditText) findViewById(R.id.userName);
        mPassword = (EditText) findViewById(R.id.password);
        
        mQrazUrl.setText(Settings.getQrazUrl());
        mUpdateUrl.setText(Settings.getAPIUrl());
        mUserName.setText(Settings.getUserName());
        mPassword.setText(Settings.getPassword());
    }
    
    @Override
    public void onBackPressed() {
        Settings.update(mQrazUrl.getText().toString(), mUpdateUrl.getText().toString(), mUserName.getText().toString(), mPassword.getText().toString());
        
        super.onBackPressed();
    }
}