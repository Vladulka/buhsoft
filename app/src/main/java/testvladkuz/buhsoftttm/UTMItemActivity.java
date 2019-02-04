package testvladkuz.buhsoftttm;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.params.BasicHttpParams;
import testvladkuz.buhsoftttm.adapter.TTMAdapter;
import testvladkuz.buhsoftttm.classes.ALC;
import testvladkuz.buhsoftttm.classes.Items;
import testvladkuz.buhsoftttm.classes.TTM;
import testvladkuz.buhsoftttm.sqldatabase.DatabaseHandler;

public class UTMItemActivity extends AppCompatActivity {

    String myJSON;
    RecyclerView utm;
    ArrayList<TTM> ttms = new ArrayList<>();
    ArrayList<Boolean> checkable = new ArrayList<Boolean>();
    DatabaseHandler db = new DatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utm);

        utm = findViewById(R.id.list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        utm.setLayoutManager(linearLayoutManager);

//        getUTN(getIntent().getStringExtra("url"));

        String full = "<A>\n" +
                "<url fileId=\"030xB14E400D-553B-42A2-9772-22B2B7C63222\" timestamp=\"2019-01-31T17:03:13.702+0300\">http://185.154.201.243:8181/opt/out/FORM2REGINFO/5</url>\n" +
                "<ver>1</ver>\n" +
                "</A>";
        showList(full);
    }

    public void getUTN(final String url){
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                HttpGet httppost = new HttpGet(url);


                InputStream inputStream = null;
                String result = null;
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();

                    inputStream = entity.getContent();
                    // json is UTF-8 by default
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line).append("\n");
                    }
                    result = sb.toString();
                } catch (Exception e) {
                    // Oops
                }
                finally {
                    try{if(inputStream != null)inputStream.close();}catch(Exception ignored){}
                }
                return result;

            }
            @Override
            protected void onPostExecute(String result){
                showList(result);
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }

    protected void showList(String fullxml){
        String textValue = "";
        boolean inEntryHeader = false;

        try {
            checkable.add(false);
            XmlPullParserFactory  xppf = XmlPullParserFactory.newInstance();
            xppf.setNamespaceAware(true);
            XmlPullParser parser = xppf.newPullParser();
            parser.setInput(new StringReader(fullxml));
            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {

                String tagName = parser.getName();

                switch (parser.getEventType()) {
                    // начало документа
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    // начало тэга
                    case XmlPullParser.START_TAG:
                        if ("url".equalsIgnoreCase(tagName)) {
                            inEntryHeader = true;
                        }
                        break;
                    // конец тэга
                    case XmlPullParser.END_TAG:
                        if (inEntryHeader) {
                            if ("url".equalsIgnoreCase(tagName)) {
                                inEntryHeader = false;
                                getTTMInfo(textValue);
                            }
                        }
                        break;
                    // содержимое тэга
                    case XmlPullParser.TEXT:
                        textValue = parser.getText();
                        break;

                    default:
                        break;
                }
                // следующий элемент
                parser.next();
            }

            TTMAdapter adapter = new TTMAdapter(this, ttms, true, checkable);
            utm.setAdapter(adapter);

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getTTMInfo(final String url){
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                HttpGet httppost = new HttpGet(url);
                //будем передавать два параметра

                InputStream inputStream = null;
                String result = null;
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();

                    inputStream = entity.getContent();
                    // json is UTF-8 by default
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line).append("\n");
                    }
                    result = sb.toString();
                } catch (Exception e) {
                    // Oops
                }
                finally {
                    try{if(inputStream != null)inputStream.close();}catch(Exception ignored){}
                }
                return result;

            }
            @Override
            protected void onPostExecute(String result){
                showTTMInfo(result);

            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }

    protected void showTTMInfo(String fullxml){
        try {
            XmlPullParserFactory xppf = XmlPullParserFactory.newInstance();
            xppf.setNamespaceAware(true);
            XmlPullParser parser = xppf.newPullParser();
            parser.setInput(new StringReader(fullxml));

            String textValue = "";
            boolean inEntryHeader = false, inEntryShipper = false;
            TTM obj = new TTM();

            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {

                String tagName = parser.getName();

                switch (parser.getEventType()) {
                    // начало документа
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    // начало тэга
                    case XmlPullParser.START_TAG:
                        if ("Header".equalsIgnoreCase(tagName)) {
                            inEntryHeader = true;
                        } else if ("Shipper".equalsIgnoreCase(tagName)) {
                            inEntryShipper = true;
                        }
                        break;
                    // конец тэга
                    case XmlPullParser.END_TAG:
                        if (inEntryHeader) {
                            if ("Header".equalsIgnoreCase(tagName)) {
                                ttms.add(obj);
                                inEntryHeader = false;
                            } else if ("Shipper".equalsIgnoreCase(tagName)) {
                                inEntryShipper = false;
                            } else if ("ClientRegId".equalsIgnoreCase(tagName)) {
                                obj.setFsrar(textValue);
                            } else if ("Identity".equalsIgnoreCase(tagName)) {
                                obj.setGuid(textValue);
                            } else if ("NUMBER".equalsIgnoreCase(tagName) || "WBNUMBER".equalsIgnoreCase(tagName)) {
                                obj.setTitle(textValue);
                            } else if ("DATE".equalsIgnoreCase(tagName)) {
                                obj.setDate(textValue);
                            } else if ("INN".equalsIgnoreCase(tagName)) {
                                obj.setInn(textValue);
                            } else if ("ShortName".equalsIgnoreCase(tagName) && inEntryShipper) {
                                obj.setShortname(textValue);
                            }
                        }
                        break;
                    // содержимое тэга
                    case XmlPullParser.TEXT:
                        textValue = parser.getText();
                        break;

                    default:
                        break;
                }
                // следующий элемент
                parser.next();
            }

            TTMAdapter adapter = new TTMAdapter(this, ttms, true, checkable);

            utm.setAdapter(adapter);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
