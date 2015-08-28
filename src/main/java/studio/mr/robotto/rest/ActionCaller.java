package studio.mr.robotto.rest;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;

/**
 * Created by aaron on 24/08/2015.
 */
class ActionCaller extends AsyncTask<Action, Void, Object> {
    HttpURLConnection mURLConnection;
    private Action mAction;

    public ActionCaller(Action action) {
        mAction = action;
    }

    private void prepareConnection(Action action, HttpURLConnection connection) throws IOException {
        connection.setRequestMethod(action.getMethod().toString());
        for (Map.Entry<String, String> e : action.getHeaders().entrySet()) {
            connection.setRequestProperty(e.getKey(), e.getValue());
        }
        OutputStream os = connection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(action.getRequestBodyAsJson());
        writer.flush();
        writer.close();
        os.close();
    }

    private String readResponse(HttpURLConnection connection) {
        StringBuffer sb = new StringBuffer();
        InputStream is;
        String result;
        InputStream myis = null;
        String inputLine;
        try {
            myis = connection.getInputStream();
            is = new BufferedInputStream(myis);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            result = sb.toString();
            is.close();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            myis = connection.getErrorStream();
            is = new BufferedInputStream(myis);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            result = sb.toString();
            is.close();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mAction.getConfig().onSetRequest(mAction);
    }

    @Override
    protected Object doInBackground(Action... params) {
        ActionConfig config = mAction.getConfig();
        try {
            URL url = new URL(mAction.getUrl());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            prepareConnection(mAction, urlConnection);
            urlConnection.getResponseMessage();
            int status = urlConnection.getResponseCode();
            String response = readResponse(urlConnection);
            return new Response(status, response);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if (o != null) {
            Response r = (Response) o;
            mAction.getConfig().onSuccess(mAction, r.getStatus(), r.getBody());
            return;
        }
        Log.v("Meec", "Meec");
    }

    private class Response {
        private String mBody;
        private int mStatus;

        public Response(int status, String body) {
            mBody = body;
            mStatus = status;
        }

        public String getBody() {
            return mBody;
        }

        public int getStatus() {
            return mStatus;
        }
    }
}
