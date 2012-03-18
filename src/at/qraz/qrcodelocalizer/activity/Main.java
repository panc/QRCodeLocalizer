package at.qraz.qrcodelocalizer.activity;

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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import at.qraz.qrcodelocalizer.CodeLocation;
import at.qraz.qrcodelocalizer.MapViewHelper;
import at.qraz.qrcodelocalizer.R;
import at.qraz.qrcodelocalizer.Settings;
import at.qraz.qrcodelocalizer.WebServiceClient;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class Main extends MapActivity {

    private MapView _mapView;
    private TextView _gpsLocationTextView;
    private TextView _qrCodeContentsTextView;
    private TextView _qrCodeLocationTextView;
    
    private TextView _submitNewLocationHeadline;
    private View _submitNewLocationLine;
    private Button _submitNewLocationButton;
    
    private LocationManager _locationManager;

    private CodeLocation _qrCodeLocation;
    private CodeLocation _gpsLocation;

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

        _gpsLocationTextView = (TextView) findViewById(R.id.gpsLocationTextView);
        _qrCodeLocationTextView = (TextView) findViewById(R.id.qrCodeLocationTextView);
        _qrCodeContentsTextView = (TextView) findViewById(R.id.qrCodeContentsTextView);
        _submitNewLocationHeadline = (TextView) findViewById(R.id.submitNewLocationHeadline);
        _submitNewLocationLine = (View) findViewById(R.id.submitNewLocationLine);
        _submitNewLocationButton = (Button) findViewById(R.id.submitNewLocationButton);

        _locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        MapViewHelper.initialize(_locationManager);
        Location l = _locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

        System.out.println("is null" + l == null);
        
        _gpsLocation = l == null
            ? new CodeLocation()
            : new CodeLocation(l.getLongitude(), l.getLatitude(), l.getTime());
                
        setLocationText(_gpsLocationTextView, _gpsLocation);

        _mapView = (MapView) findViewById(R.id.smallMapView);
        MapViewHelper.setToCurrentLocation(_mapView, MapViewHelper.ZOOM_LEVEL_SMALL);

        Object data = getLastNonConfigurationInstance();
        if (data != null && data instanceof CodeLocation) {

            _qrCodeLocation = (CodeLocation) data;
            _qrCodeContentsTextView.setText(_qrCodeLocation.getQRCodeContents());
            
            setSubmitRegionVisibility(true);
            setLocationText(_qrCodeLocationTextView, _qrCodeLocation);
        }
        else
            setSubmitRegionVisibility(false);
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

    @Override
    public Object onRetainNonConfigurationInstance() {
        return _qrCodeLocation;
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
                Intent about = new Intent(this, AboutActivity.class);
                startActivity(about);
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
                    showToast("Scanning Cancled!");
                }
                else if (result.getFormatName().equals(IntentIntegrator.QR_CODE_TYPES)) {
                    String qrCodeContents = result.getContents();
                    _qrCodeContentsTextView.setText(qrCodeContents);

                    requestAndSetQRCodeLocation(qrCodeContents);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            showToast(e.toString());
        }
    }

    public void submitNewLocationButtonClick(View v) {
        try {
            if (_gpsLocation == null || _qrCodeLocation == null) {
                showToast(getString(R.string.noDataForUpdate));
                return;
            }

            WebServiceClient ws = new WebServiceClient();
            int statusCode = ws.updateQRCodeLocation(_gpsLocation, _qrCodeLocation.getQRCodeContents());

            if (statusCode != HttpURLConnection.HTTP_OK) {
                showToast(getString(R.string.updateFailed) + " (Status: " + statusCode + ")");
            }
            else {
                showToast(getString(R.string.updateSuccessful));
        
                _qrCodeContentsTextView.setText("");
                _qrCodeLocationTextView.setText("");
                setSubmitRegionVisibility(false);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            showToast(e.toString());
        }
    }

    public void scanButtonClick(View v) {
        _qrCodeContentsTextView.setText("");
        _qrCodeLocationTextView.setText("");
        
        IntentIntegrator.initiateScan(this);
    }

    /*
     * reqest the current location of the qr-code from the server
     */
    private void requestAndSetQRCodeLocation(String qrCodeContents) throws ClientProtocolException, IOException {
        WebServiceClient ws = new WebServiceClient();
        _qrCodeLocation = ws.getQRCodeLocation(qrCodeContents);

        if (_qrCodeLocation == null){
            _qrCodeLocationTextView.setText(getString(R.string.requestFailed));
        }
        else{
            setLocationText(_qrCodeLocationTextView, _qrCodeLocation);
            setSubmitRegionVisibility(true);
        }
    }

    private void setLocationText(TextView view, CodeLocation loc) {
        String text = "Breitengrad: \t\t" + loc.getLatitude();
        text += "\nLšngengrad: \t" + loc.getLongitude();
        text += "\nZeitpunkt: \t\t" + loc.getTime();

        view.setText(text);
    }

    private void setSubmitRegionVisibility(boolean visible) {
        int color = getResources().getColor(visible ? R.color.headlineColor : R.color.textColor);
        
        _submitNewLocationLine.setBackgroundColor(color);
        _submitNewLocationHeadline.setTextColor(color);
        _submitNewLocationButton.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }
    
    private void showToast(String message) {
        Toast.makeText(this, message, 5000).show();
    }

    LocationListener onLocationChange = new LocationListener() {
        public void onLocationChanged(Location loc) {
            _gpsLocation = new CodeLocation(loc.getLongitude(), loc.getLatitude(), loc.getTime());
            setLocationText(_gpsLocationTextView, _gpsLocation);

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
}