package org.ei.drishti.service;

import org.apache.commons.io.IOUtils;
import org.ei.drishti.domain.Response;
import org.ei.drishti.domain.ResponseStatus;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.ei.drishti.util.Log.logWarn;

public class HTTPAgent {
    public Response<String> fetch(String requestURLPath) {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(requestURLPath);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(5000);
            BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
            return new Response<String>(ResponseStatus.success, IOUtils.toString(inputStream));
        } catch (Exception e) {
            logWarn(e.getMessage());
            return new Response<String>(ResponseStatus.failure, null);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
}
