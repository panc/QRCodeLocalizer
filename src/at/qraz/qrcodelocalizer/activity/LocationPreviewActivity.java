package at.qraz.qrcodelocalizer.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import at.qraz.qrcodelocalizer.MapViewHelper;
import at.qraz.qrcodelocalizer.R;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class LocationPreviewActivity extends MapActivity {

    private final int DIALOG_SHOW_MAPTYPE_SELECTION = 1;
    
    private MyLocationOverlay _myLocationOverlay;
    private MapView _mapView; 

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.location_preview);

        _mapView = (MapView) findViewById(R.id.largeMapView);
        _mapView.setBuiltInZoomControls(true);

        MapViewHelper.initialize((LocationManager) getSystemService(LOCATION_SERVICE));
        MapViewHelper.setToCurrentLocation(_mapView, MapViewHelper.ZOOM_LEVEL_LARGE);

        _myLocationOverlay = new MyLocationOverlay(this, _mapView);
        _mapView.getOverlays().add(_myLocationOverlay);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater f = getMenuInflater();
        f.inflate(R.menu.location_preview_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toggleMapType:
                showDialog(DIALOG_SHOW_MAPTYPE_SELECTION);
                break;
        }

        return true;
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        switch (id) {
            case 1:
                return createMapTypeSelection();

            default:
                return null;
        }
    }

    private Dialog createMapTypeSelection() {
        final String[] items = { getString(R.string.map), getString(R.string.satellite)};

        int selectedItem = _mapView.isSatellite() ? 1 : 0;
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.mapTypeSelection));
        builder.setSingleChoiceItems(items, selectedItem, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                _mapView.setSatellite(item == 1);                
                dismissDialog(DIALOG_SHOW_MAPTYPE_SELECTION);
            }
        });

        return builder.create();
    }
}