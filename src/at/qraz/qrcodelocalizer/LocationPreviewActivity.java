package at.qraz.qrcodelocalizer;

import android.os.Bundle;
import at.qraz.qrscanner.R;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class LocationPreviewActivity extends MapActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.location_preview);

        MapView mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
    }

    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
}
