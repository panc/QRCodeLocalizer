package at.qraz.qrcodelocalizer;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class CodeLocation {

    private final String PREFIX = "POINT (";
    private final String POSTFIX = ")";
    private final String KEY_GEOMETRY = "geometry";
    private final String KEY_TIME = "last";

    private double mLongitude;
    private double mLatitude;
    private long mTime;
    private String mTimeString;
    
    public CodeLocation(double longitude, double latitude, long time) {
        mLongitude = longitude;
        mLatitude = latitude;
        mTime = time;
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        mTimeString = sdf.format(new Date(time));
    }

    public CodeLocation(String json) {
        if (json != null) {
            try {
                JSONObject j = new JSONObject(json);
                String geo = j.getString(KEY_GEOMETRY);

                geo = geo.replace(PREFIX, "").replace(POSTFIX, "");
                String[] coordinates = geo.split(" ");

                if (coordinates.length == 2) {
                    mLongitude = Double.parseDouble(coordinates[0]);
                    mLatitude = Double.parseDouble(coordinates[1]);
                }
                
                mTimeString = j.getString(KEY_TIME);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public double getLongitude() {
        return mLongitude;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public String getTime() {
        return mTimeString;
    }

    @Override
    public String toString() {
        // create the json object
        JSONObject data = new JSONObject();

        try {
            data.put(KEY_GEOMETRY, PREFIX + mLongitude + " " + mLatitude + POSTFIX);
            data.put(KEY_TIME, mTime);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return data.toString();
    }
}
