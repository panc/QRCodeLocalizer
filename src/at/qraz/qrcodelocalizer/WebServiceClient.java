package at.qraz.qrcodelocalizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

public class WebServiceClient {

    // public static String QRCODE_CONTENT_PREFIX = "http://qraz.at/";
    // private static String UPDATE_URL = "http://qraz.at/api/code/";
    // public static String QRCODE_CONTENT_PREFIX = "http://dev.qraz.at/";
    // private static String UPDATE_URL = "http://dev.qraz.at/api/code/";

    public int updateQRCodeLocation(CodeLocation location, String qrCode) throws JSONException, ClientProtocolException, IOException {
        HttpPut httpPut = null;
        try {
            // send the json string to the webservice
            httpPut = new HttpPut(createUrl(qrCode));
            httpPut.setHeader("Content-Type", "application/json");
            httpPut.setEntity(new StringEntity(location.toString()));

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
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(httpGet);

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

            return new CodeLocation(sb.toString());
        }
        finally {
            if (httpGet != null)
                httpGet.abort();
        }
    }

    private String createUrl(String qrCode) {
        // create the url to call for the update
        String id = qrCode.replace(Settings.getQrazUrl(), "");
        // String id = "uWEhEw";

        return Settings.getUpdateUrl() + id + "/";
    }
}
