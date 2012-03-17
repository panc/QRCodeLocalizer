package at.qraz.qrcodelocalizer;

import android.os.Bundle;
import at.qraz.qrscanner.R;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class LocationPreviewActivity extends MapActivity {

    private MyLocationOverlay _myLocationOverlay;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.location_preview);

        MapView mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);

        MapViewHelper.setToCurrentLocation(mapView, MapViewHelper.ZOOM_LEVEL_LARGE);
        
        _myLocationOverlay = new MyLocationOverlay(this, mapView);        
        mapView.getOverlays().add(_myLocationOverlay);
    }

    @Override
    protected void onPause() {
        _myLocationOverlay.disableCompass();
        _myLocationOverlay.disableMyLocation();
        
        super.onPause();
    }
    
    @Override
    protected void onResume() {
        _myLocationOverlay.enableCompass();
        _myLocationOverlay.enableMyLocation();

        super.onResume();
    }
    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
}
