package at.qraz.qrcodelocalizer.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
        
        ActionBar bar = getActionBar();
        bar.setHomeButtonEnabled(true);
        bar.setTitle(R.string.save);
        bar.setIcon(R.drawable.ic_cab_done_holo_dark);
    }
    
    @Override
    public void onBackPressed() {
        // todo as user
        super.onBackPressed();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Settings.update(mQrazUrl.getText().toString(), mUpdateUrl.getText().toString(), mUserName.getText().toString(), mPassword.getText().toString());
                return close();
            
            case R.id.cancel:
                return close();
                
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    private boolean close(){
        Intent intent = new Intent(this, Main.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        return true;
    }
}