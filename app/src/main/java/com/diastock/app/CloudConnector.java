package com.diastock.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import org.apache.http.NameValuePair;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import static android.content.Context.MODE_PRIVATE;

public class CloudConnector extends AsyncTask<String, Void, String> {

    private TaskDelegate delegate;
    private String result = "";
    //private final static String SERVICE_URI = "http://192.168.1.6";
    //private final static String SERVICE_URI = "http://192.168.1.45:60383/JSONMobileServer.svc";
    private static String SERVICE_URI = "http://79.8.2.2:8089/JSONMobileServer.svc";
    private ArrayList<MenuDataModel> dataModels;
    private static CustomMenuAdapter adapter;
    Context context;
    ListView menuListView;
    ProgressDialog progressDialog;
    Activity caller;

    String postExceuteMethod;

    int postStep;

    public int getPostStep() {
        return postStep;
    }

    public void setPostStep(int postStep) {
        this.postStep = postStep;
    }

    public String getPostExceuteMethod() {
        return postExceuteMethod;
    }

    public void setPostExceuteMethod(String postExceuteMethod) {
        this.postExceuteMethod = postExceuteMethod;
    }

    public CloudConnector(Context context, TaskDelegate delegate, Activity caller, String postExceuteMethod) {

        this.context = context;
        this.delegate = delegate;
        this.caller = caller;
        this.postExceuteMethod = postExceuteMethod;

        SharedPreferences prefs = caller.getSharedPreferences("SETTINGS", MODE_PRIVATE);

        if (prefs != null) {
            String restoredUri = prefs.getString("CLOUD_URI", SERVICE_URI);

            if (restoredUri != null) {
                SERVICE_URI = restoredUri + "/JSONMobileServer.svc";
            }
        }
    }

/*
    private HttpClient getNewHttpClient() {
        try {

            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);


            HttpParams params = new BasicHttpParams();


            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

*/
    private String SendData() throws Exception {

        URL url;

        if (DataExchange.getInstance().getFunctionName() == DataExchange.Operations.FMENU)
            url = new URL(SERVICE_URI + "/MenuProcessor");
        else
            url = new URL(SERVICE_URI + "/DataProcessor");

        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setReadTimeout(15000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);

        Map<String, String> headers = new HashMap<>();

        //headers.put("X-CSRF-Token", "fetch");
        headers.put("content-type", "application/json");

        for (String headerKey : headers.keySet()) {
            conn.setRequestProperty(headerKey, headers.get(headerKey));
        }
        //HttpClient client = getNewHttpClient();// new DefaultHttpClient();


        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {

                if (BuildConfig.DEBUG) {
                    return true;
                }

                if (hostname.toLowerCase().equals(SERVICE_URI.toLowerCase().replace("https://","").replace("/jsonmobileserver.svc","")))
                    return true;
                else
                    return false;
            }
        };

        conn.setHostnameVerifier(hostnameVerifier);
        /*HttpParams params = client.getParams();
        HttpConnectionParams.setConnectionTimeout(params, 30000);
        HttpConnectionParams.setSoTimeout(params, 30000);

        ((DefaultHttpClient) client).setParams(params);

        HttpPost post;
*/
        try {

            if (DataExchange.getInstance().getFunctionName() == DataExchange.Operations.FMENU) {
                //post = new HttpPost(SERVICE_URI + "/MenuProcessor");
                List<NameValuePair> value = new ArrayList<NameValuePair>(1);

                String serializedMessage = DataExchange.getInstance().serialize();

                //value.add(new BasicNameValuePair("dataexchange", serializedMessage));
                //conn.addRequestProperty("dataexchange", serializedMessage);

                OutputStream out = new BufferedOutputStream(conn.getOutputStream());

                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(out, "UTF-8"));

                writer.write("dataexchange=" + serializedMessage);
                writer.flush();
                InputStreamReader in = new InputStreamReader((conn.getInputStream()));
                //post.setEntity(new UrlEncodedFormEntity(value, HTTP.ISO_8859_1));
                String header = conn.getHeaderField("Set-Cookie");
                //Map<String, List<String>> responseHeaders = conn.getHeaderFields();

                //HttpResponse response = client.execute(post);
                //BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                BufferedReader bufferedReader = new BufferedReader(in);
                String line;
                String jsonString = "";
                while ((line = bufferedReader.readLine()) != null) jsonString += line;
                bufferedReader.close();

                String message = jsonString.substring(5);

                UserMenu.getInstance().DeSerialize(message);
            } else {
                //post = new HttpPost(SERVICE_URI + "/DataProcessor");

                List<NameValuePair> value = new ArrayList<NameValuePair>(1);

                String serializedMessage = DataExchange.getInstance().serialize();



                //if (serializedMessage.contains("|")) {
                //    value.add(new BasicNameValuePair("dataexchange", serializedMessage));

                //    HttpEntity entity = new StringEntity(value.toString(), HTTP.ISO_8859_1);

                //    post.setEntity(entity);
                //} else {
//                value.add(new BasicNameValuePair("dataexchange", serializedMessage));

                OutputStream out = new BufferedOutputStream(conn.getOutputStream());

                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(out, "UTF-8"));

                writer.write("dataexchange=" + serializedMessage);
                writer.flush();
                InputStreamReader in = new InputStreamReader((conn.getInputStream()));

                BufferedReader bufferedReader = new BufferedReader(in);
                //post.setEntity(new UrlEncodedFormEntity(value, HTTP.ISO_8859_1));
                //}
                //HttpResponse response = client.execute(post);
                //BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String line;
                String jsonString = "";
                while ((line = bufferedReader.readLine()) != null) jsonString += line;
                bufferedReader.close();

                String message = jsonString.substring(5).replace("\\u000a", "\n");
                DataExchange.getInstance().deSerialize(message);


            }
            //jsonAsObj.wrap(userMenu);

            //System.out.println(userMenu.toString());


        } catch (VersionException ve) {
            result = "VEX";
            Log.e("Error", ve.getMessage());
        } catch (Exception e) {
            result = "KO";
            Log.e("Error", e.getMessage());
        } finally {
            return result;
        }

    }

    @Override
    protected String doInBackground(String... arg0) {
        //android.os.Debug.waitForDebugger();


        String receivedText = "";
        dataModels = new ArrayList<>();

        CustomMenuAdapter adapter;

        try {
            receivedText = SendData();
            /*if (receivedText != "KO" && receivedText != "VEX")
                return "OK";
            else if (receivedText.equals("VEX"))
                return "VEX";
            else
                return "KO";*/

            switch (receivedText) {
                case "KO":
                case "VEX":
                    return receivedText;
                default:
                    return "OK";
            }
        } catch (Exception e) {

            e.printStackTrace();
            return "KO";
        } finally {
        }

        //adapter= new CustomMenuAdapter(dataModels, context);


    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        try {
            if (result.equals("VEX")) {
                throw new Exception(context.getResources().getString(R.string.version_incorrect));
            }


            if (!result.equals("KO")) {
                if (DataExchange.getInstance().getFunctionName() == DataExchange.Operations.FMENU) {
                    if (UserMenu.getInstance().getMenuItems() == 0)
                        result = "KO";
                    else
                        result = "OK";
                } else if (DataExchange.getInstance().getMessagetype().equals(DataExchange.MessageType.ERROR))
                    result = "KO";
                else
                    result = "OK";
            }

            int step = 0;
            if (this.postStep < 2)
                step = result == "KO" ? 0 : 1;
            else
                step = this.postStep;

            delegate.taskCompletionResult(result, step);

        } catch (Exception e) {

            final Exception ex = e;
            caller.runOnUiThread(new Runnable() // while debugging, it comes here, on Step Over it stick for 2 times and then move at the end of method without error
            {
                public void run() {
                    AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                    try {
                        alertMessageBuilder.BuildDialog("Errore", ex.getMessage(), AlertMessageBuilder.Severity.ERROR, context);
                    } catch (Exception e) {
                        Log.e("", e.getMessage());
                    }
                    alertMessageBuilder.Show();
                }
            });
        } finally {
            progressDialog.dismiss();
        }


//
//        runOnUiThread(new Runnable() {
        // public void run() {
//                try {
//                    adapter= new CustomMenuAdapter(dataModels, context);
//                    menuListView.setAdapter(adapter);
//                } catch (Exception ex) {
//                    System.out.println(ex.getMessage());
//                }
        //}
        //});

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(caller);
        progressDialog.setMessage("Invio dati in corso..");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    @Override
    protected void onCancelled() {
        // TODO Auto-generated method stub
        super.onCancelled();
    }

    public CloudConnector(TaskDelegate delegate) {
        this.delegate = delegate;
    }


}

