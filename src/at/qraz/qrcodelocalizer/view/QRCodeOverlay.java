package at.qraz.qrcodelocalizer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import at.qraz.qrcodelocalizer.CodeLocation;
import at.qraz.qrcodelocalizer.R;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class QRCodeOverlay extends Overlay {

    private Context _context;
    private CodeLocation _location;
    private Drawable _icon;
    
    public QRCodeOverlay(Context context, CodeLocation location) {
        _context = context;
        _location = location;
        _icon = _context.getResources().getDrawable(R.drawable.location_marker);
    }
    
    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        Projection p = mapView.getProjection();
        Point position = p.toPixels(new GeoPoint((int)(_location.getLatitude() * 1E6), (int)(_location.getLongitude() * 1E6)), null);
        
        int width = _icon.getMinimumWidth();
        int height = _icon.getMinimumHeight();
        
        int left = position.x - width / 2;
        int top = position.y - height;
        
        _icon.setBounds(left, top, left + width, top + height);
        _icon.draw(canvas);        
    }
}
