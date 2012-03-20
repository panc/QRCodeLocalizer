package at.qraz.qrcodelocalizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

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
        _icon = _context.getResources().getDrawable(R.drawable.icon);
    }
    
    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        Projection p = mapView.getProjection();
        Point position = p.toPixels(new GeoPoint((int)(_location.getLatitude() * 1E6), (int)(_location.getLongitude() * 1E6)), null);
        
        int width = _icon.getMinimumWidth();
        int height = _icon.getMinimumHeight();
        
        int left = position.x - width / 2;
        int top = position.y - height / 2;
        
        _icon.setBounds(left, top, left + width, top + height);
        _icon.draw(canvas);
        
        canvas.drawText(_location.getQRCodeContents(), position.x, position.y, new Paint());
        
    }
}
