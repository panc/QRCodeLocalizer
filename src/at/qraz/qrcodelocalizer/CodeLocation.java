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

    private double _longitude;
    private double _latitude;
    private long _time;

    private String _timeString;
    private String _qrCodeContents;

    public CodeLocation() {
        _timeString = "";
    }

    public CodeLocation(double longitude, double latitude, long time) {
        this(longitude, latitude, time, "");
    }
    
    public CodeLocation(double longitude, double latitude, long time, String contents) {
        _longitude = longitude;
        _latitude = latitude;
        _time = time;
        _qrCodeContents = contents;

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        _timeString = sdf.format(new Date(time));
    }

    public CodeLocation(String qrCodeContents, String json) {

        _qrCodeContents = qrCodeContents;

        if (json != null) {
            try {
                JSONObject j = new JSONObject(json);
                String geo = j.getString(KEY_GEOMETRY);

                geo = geo.replace(PREFIX, "").replace(POSTFIX, "");
                String[] coordinates = geo.split(" ");

                if (coordinates.length == 2) {
                    _longitude = Double.parseDouble(coordinates[0]);
                    _latitude = Double.parseDouble(coordinates[1]);
                }

                _timeString = j.getString(KEY_TIME);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
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

    public String getQRCodeContents() {
        return _qrCodeContents;
    }

    @Override
    public String toString() {
        // create the json object
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
}
