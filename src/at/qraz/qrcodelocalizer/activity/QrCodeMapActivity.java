package at.qraz.qrcodelocalizer.activity;

import java.util.List;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import at.qraz.qrcodelocalizer.CodeLocation;
import at.qraz.qrcodelocalizer.R;
import at.qraz.qrcodelocalizer.WebServiceClient;
import at.qraz.qrcodelocalizer.view.MapViewEx;
import at.qraz.qrcodelocalizer.view.QRCodeOverlay;
import at.qraz.qrcodelocalizer.view.ZoomLevelChangedListener;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

public class QrCodeMapActivity extends MapActivity {

    private final int DIALOG_SHOW_MAPTYPE_SELECTION = 1;

    private MyLocationOverlay _myLocationOverlay;
    private MapViewEx _mapView;
    private int _minZoomLevel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.qrcodemap);

        _mapView = (MapViewEx) findViewById(R.id.largeMapView);
        _mapView.setBuiltInZoomControls(true);

        _mapView.setZoomLevelChangedListener(new ZoomLevelChangedListener() {

            @Override
            public void zoomLevelChanged(int newZoomLevel) {
                loadQrCodesForChangedZoomLevel(newZoomLevel);
            }
        });

        LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location l = manager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        
        MapController controller = _mapView.getController();
        controller.setZoom(MapViewEx.ZOOM_LEVEL_LARGE);
        controller.setCenter(new GeoPoint((int) (l.getLatitude() * 1E6), (int) (l.getLongitude() * 1E6)));
        
        _myLocationOverlay = new MyLocationOverlay(this, _mapView);
        List<Overlay> overlays = _mapView.getOverlays();
        overlays.add(_myLocationOverlay);

        Intent i = getIntent();
        String content = i.getStringExtra("Content");
        if (content != null) {
            overlays.add(new QRCodeOverlay(this, new CodeLocation(i.getDoubleExtra("Longitude", 0), i.getDoubleExtra("Latitude", 0), 0, i.getStringExtra("Content"))));
        }

        ActionBar bar = getActionBar();
        bar.setHomeButtonEnabled(true);
        bar.setDisplayHomeAsUpEnabled(true);
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

            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, Main.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
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
        final String[] items = { getString(R.string.map), getString(R.string.satellite) };

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

    protected void loadQrCodesForChangedZoomLevel(int newZoomLevel) {
        if (newZoomLevel < _minZoomLevel) {

            try {
                WebServiceClient wsClient = new WebServiceClient();
                wsClient.getCodeLocationsForViewPoint(/* ??? */);

                _minZoomLevel = newZoomLevel;
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
