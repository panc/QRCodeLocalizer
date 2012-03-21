package at.qraz.qrcodelocalizer.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import at.qraz.qrcodelocalizer.R;

public class AboutActivity extends Activity {
    
    private TextView _staffTextView;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.about);
        
        _staffTextView = (TextView)findViewById(R.id.staffTextView);
        ImageView image = (ImageView)findViewById(R.id.imageView);
        
        image.setOnLongClickListener(new View.OnLongClickListener() {
            
            @Override
            public boolean onLongClick(View v) {
                _staffTextView.setVisibility(View.VISIBLE);
                return true;
            }
        });
    }
    
    public void onBackButtonClick(View v){
        this.finish();
    }
}