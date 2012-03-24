package at.qraz.qrcodelocalizer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.google.android.maps.MapView;

public class MapViewEx extends MapView {

    public static final int ZOOM_LEVEL_SMALL = 16;
    public static final int ZOOM_LEVEL_LARGE = 18;
    
    private int _zoomLevel = -1;
    private MapAreaChangedListener _mapAreaChangedListener;

    public MapViewEx(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }

    public void setMapAreaChangedListener(MapAreaChangedListener listener) {
        _mapAreaChangedListener = listener;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        
        int zoomLevel = getZoomLevel();
        if (zoomLevel != _zoomLevel) {
            _zoomLevel = zoomLevel;
            
            if (_mapAreaChangedListener != null)
                _mapAreaChangedListener.mapAreaChanged(_zoomLevel);
        }
        
        // TODO: notify listener when user moves the map (center)
    }
}
