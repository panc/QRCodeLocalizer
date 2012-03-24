package at.qraz.qrcodelocalizer;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class CodeLocation {

    private static final String PREFIX = "POINT (";
    private static final String POSTFIX = ")";
    private static final String KEY_GEOMETRY = "geometry";
    private static final String KEY_TIME = "last";

    private double _longitude;
    private double _latitude;
    private long _time;

    private String _timeString;

    public CodeLocation() {
        _timeString = "";
    }

    public CodeLocation(double longitude, double latitude, long time) {
        _longitude = longitude;
        _latitude = latitude;
        _time = time;

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        _timeString = sdf.format(new Date(time));
    }

    public CodeLocation(JSONObject json) throws JSONException {
        String geo = json.getString(KEY_GEOMETRY);

        geo = geo.replace(PREFIX, "").replace(POSTFIX, "");
        String[] coordinates = geo.split(" ");

        if (coordinates.length == 2) {
            _longitude = Double.parseDouble(coordinates[0]);
            _latitude = Double.parseDouble(coordinates[1]);
        }

        _timeString = json.getString(KEY_TIME);
    }

    public double getLongitude() {
        return _longitude;
    }

    public double getLatitude() {
        return _latitude;
    }

    public String getTime() {
        return _timeString;
    }

    @Override
    public String toString() {
        JSONObject data = new JSONObject();

        try {
            data.put(KEY_GEOMETRY, PREFIX + _longitude + " " + _latitude + POSTFIX);
            data.put(KEY_TIME, _time);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return data.toString();
    }

    public static CodeLocation parseJSON(String json) {
        if (json != null) {
            try {
                JSONObject j = new JSONObject(json);
                return new CodeLocation(j);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
