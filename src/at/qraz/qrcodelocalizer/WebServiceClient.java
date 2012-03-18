package at.qraz.qrcodelocalizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import android.util.Base64;

public class WebServiceClient {

    public int updateQRCodeLocation(CodeLocation location, String qrCode) throws JSONException, ClientProtocolException, IOException {
        HttpPut httpPut = null;
        try {
            // send the json string to the webservice
            String url = createUrl(qrCode);
            String json = location.toString();
            
            httpPut = new HttpPut(url);
            httpPut.setHeader("Content-Type", "application/json");
            httpPut.setEntity(new StringEntity(json));

            addAuthorizationHeader(httpPut);
            
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(httpPut);

            return response.getStatusLine().getStatusCode();
        }
        finally {
            if (httpPut != null)
                httpPut.abort();
        }
    }

    public CodeLocation getQRCodeLocation(String qrCode) throws ClientProtocolException, IOException {
        HttpGet httpGet = null;
        try {
            httpGet = new HttpGet(createUrl(qrCode));
            
            addAuthorizationHeader(httpGet);
            
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(httpGet);

            if (response.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK)
                return null;
            
            HttpEntity entity = response.getEntity();
            StringBuilder sb = new StringBuilder();

            if (entity != null) {
                InputStream instream = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(instream));

                String line = null;
                while ((line = reader.readLine()) != null)
                    sb.append(line);

                instream.close();
            }

            return new CodeLocation(qrCode, sb.toString());
        }
        finally {
            if (httpGet != null)
                httpGet.abort();
        }
    }

    private void addAuthorizationHeader(HttpMessage message) throws UnsupportedEncodingException {
        
        String auth = Settings.getUserName() + ":" + Settings.getPassword();
        
        byte[] input = auth.getBytes("UTF-8");
        auth = Base64.encodeToString(input, Base64.NO_WRAP);
        auth = "Basic " + auth;

        message.addHeader("Authorization", auth);
    }

    private String createUrl(String qrCode) {
        // create the url to call for the update
        String id = qrCode.replace(Settings.getQrazUrl(), "");
        return Settings.getAPIUrl() + id + "/";
    }
}
