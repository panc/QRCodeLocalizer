package at.qraz.qrcodelocalizer;

import android.location.Location;
import android.location.LocationManager;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class MapViewHelper {

    public static final int ZOOM_LEVEL_SMALL = 16;
    public static final int ZOOM_LEVEL_LARGE = 18;
    
    private static LocationManager _locationManager;
    
    public static void initialize(LocationManager locationManager) {

        _locationManager = locationManager;
    }
    
    public static void setToCurrentLocation(MapView map, int zoomLevel) {

        Location l = _locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

        double latitude = l.getLatitude();
        double longitude = l.getLongitude();
        
        MapController controller = map.getController();
        controller.setCenter(new GeoPoint((int) (latitude * 1E6), (int) (longitude * 1E6)));
        controller.setZoom(zoomLevel);        
    }
}
