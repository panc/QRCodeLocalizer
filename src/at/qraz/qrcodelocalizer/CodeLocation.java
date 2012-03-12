package at.qraz.qrcodelocalizer;

import org.json.JSONException;
import org.json.JSONObject;

public class CodeLocation {

	private final String PREFIX = "POINT (";
	private final String POSTFIX = ")";
	private final String KEY_GEO = "geometry";
	
	private double _longitude;
	private double _latitude;
	private long _time;
	
	public CodeLocation(double longitude, double latitude, long time){
		_longitude = longitude;
		_latitude = latitude;
		_time = time;
	}
	
	public CodeLocation(String json){
		if(json != null){
			try {
				JSONObject j = new JSONObject(json);
				String geo = j.getString(KEY_GEO);
			
				geo = geo.replace(PREFIX, "").replace(POSTFIX, "");
				String[] coordinates = geo.split(" ");
				
				if(coordinates.length == 2){
					_longitude = Integer.parseInt(coordinates[0]);
					_latitude = Integer.parseInt(coordinates[1]);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public double getLongitude(){
		return _longitude;
	}
	
	public double getLatitude(){
		return _latitude;
	}
	
	public long getTime(){
		return _time;
	}
	
	@Override
	public String toString(){
		// create the json object
		JSONObject data = new JSONObject();
		
		try {
			data.put(KEY_GEO, PREFIX + _longitude + " " + _latitude + POSTFIX);
			data.put("recordTime", _time);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return data.toString();
	}
}
