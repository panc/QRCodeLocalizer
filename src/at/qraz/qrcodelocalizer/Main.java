package at.qraz.qrcodelocalizer;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.apache.http.client.ClientProtocolException;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import at.qraz.qrscanner.R;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class Main extends MapActivity {

    private TextView _resultTextView;
    private TextView _locationTextView;
    private TextView _codeLocationTextView;
    private TextView _infoTextView;
    private MapView _mapView;
    
    private LocationManager _locationManager;

    private String _lastQRCodeContents;
    private CodeLocation _lastLocation;

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
    @Override
    protected void onStart() {
        Settings.initialize(this);
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        _infoTextView = (TextView) findViewById(R.id.submitResultInfoView);
        _resultTextView = (TextView) findViewById(R.id.resultTextView);
        _locationTextView = (TextView) findViewById(R.id.locationView);
        _codeLocationTextView = (TextView) findViewById(R.id.codeLocationView);
        
        _locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        
        MapViewHelper.initialize(_locationManager);
        Location l = _locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

        _lastLocation = new CodeLocation(l.getLongitude(), l.getLatitude(), l.getTime());
        setLocationText(_locationTextView, _lastLocation);

        _mapView = (MapView)findViewById(R.id.smallMapView);
        
        MapViewHelper.setToCurrentLocation(_mapView, MapViewHelper.ZOOM_LEVEL_SMALL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater f = getMenuInflater();
        f.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settingsMenu:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                break;
                
            case R.id.locationPreviewMenu:
                Intent map = new Intent(this, LocationPreviewActivity.class);
                startActivity(map);
                break;

            case R.id.aboutMenu:
                Toast.makeText(this, R.string.aboutText, 2000).show();
                break;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() == null && result.getFormatName() == null) {
                    _resultTextView.setText("Cancled");
                }
                else if (result.getFormatName().equals(IntentIntegrator.QR_CODE_TYPES)) {
                    _lastQRCodeContents = result.getContents();
                    _resultTextView.setText("Content\n" + _lastQRCodeContents);

                    requestCodeLocation();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), 5000).show();
            _infoTextView.setText(e.toString());
        }
    }

    // pauses listener while app is inactive
    @Override
    public void onPause() {
        super.onPause();
        _locationManager.removeUpdates(onLocationChange);
    }

    // reactivates listener when app is resumed
    @Override
    public void onResume() {
        super.onResume();
        
        _locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, onLocationChange);
    }

    public void submitNewLocationButtonClick(View v) {
        try {
            if (_lastLocation == null || _lastQRCodeContents == null || !_lastQRCodeContents.startsWith(Settings.getQrazUrl())) {
                _infoTextView.setText(R.string.noDataForUpdate);
                return;
            }

            WebServiceClient ws = new WebServiceClient();
            int statusCode = ws.updateQRCodeLocation(_lastLocation, _lastQRCodeContents);

            if (statusCode != HttpURLConnection.HTTP_OK) {
                _infoTextView.setText(getString(R.string.updateFailed) + " (Status: " + statusCode + ")");
            }
            else {
                _infoTextView.setText(getString(R.string.updateSuccessful));
                requestCodeLocation();
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), 5000).show();
            _infoTextView.setText(e.toString());
        }
    }

    public void scanButtonClick(View v) {
        _lastQRCodeContents = "";
        _resultTextView.setText("");
        _infoTextView.setText("");
        
        IntentIntegrator.initiateScan(this);
    }
    
    public void showMapButtonClick(View v) {
        // todo
    }

    LocationListener onLocationChange = new LocationListener() {
        public void onLocationChanged(Location loc) {
            _lastLocation = new CodeLocation(loc.getLongitude(), loc.getLatitude(), loc.getTime());
            setLocationText(_locationTextView, _lastLocation);
            
            MapViewHelper.setToCurrentLocation(_mapView, MapViewHelper.ZOOM_LEVEL_SMALL, loc);
        }

        public void onProviderDisabled(String provider) {
            // required for interface, not used
        }

        public void onProviderEnabled(String provider) {
            // required for interface, not used
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // required for interface, not used
        }
    };

    /*
     * reqest the current location of the qr-code from the server
     */
    private void requestCodeLocation() throws ClientProtocolException, IOException {
        WebServiceClient ws = new WebServiceClient();
        CodeLocation location = ws.getQRCodeLocation(_lastQRCodeContents);

        setLocationText(_codeLocationTextView, location);
    }

    private void setLocationText(TextView view, CodeLocation loc) {
        String text = "Breitengrad: \t\t" + loc.getLatitude();
        text += "\nLängengrad: \t" + loc.getLongitude();
        text += "\nZeitpunkt: \t\t" + loc.getTime();

        view.setText(text);
    }
}