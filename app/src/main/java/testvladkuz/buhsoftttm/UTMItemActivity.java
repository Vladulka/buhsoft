package testvladkuz.buhsoftttm;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import testvladkuz.buhsoftttm.adapter.TTMAdapterUTM;
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
    Button add;
    TTMAdapterUTM adapter;
    ArrayList<String> urls = new ArrayList<>();
    ArrayList<String> guids = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    ProgressBar progressBar;
    TextView empty_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utm);

        utm = findViewById(R.id.list);
        add = findViewById(R.id.add);
        empty_text = findViewById(R.id.empty_text);
        progressBar = findViewById(R.id.progress);

        linearLayoutManager = new LinearLayoutManager(this);
        utm.setLayoutManager(linearLayoutManager);

        getUTN(db.getUserInfo("url"));

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(urls.size() != 0 && adapter.getCheckedPosition() != -1) {
                    getTTNItems(urls.get(adapter.getCheckedPosition()));
                } else {
                    Toast.makeText(getApplicationContext(), "Вы не выбрали накладную.", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                    return "";
                }
                finally {
                    try{if(inputStream != null)inputStream.close();}catch(Exception ignored){}
                }
                return result;

            }
            @Override
            protected void onPostExecute(String result){
                if(!result.equals("")) {
                    showList(result);
                } else {
                    Toast.makeText(getApplicationContext(), "Возникли проблемы с подключением к УТМ.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    empty_text.setText("Возникли проблемы с подключением к УТМ.");
                    empty_text.setVisibility(View.VISIBLE);
                }
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }

    protected void showList(String fullxml){
        String textValue = "";
        boolean inEntryHeader = false, inEntryURL = false;

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
                            if(db.findTTNByFileId(parser.getAttributeValue(0)) == -1) {
                                inEntryURL = true;
                                guids.add(parser.getAttributeValue(0));
                            }
                        }
                        if ("A".equalsIgnoreCase(tagName)) {
                            inEntryHeader = true;
                        }
                        break;
                    // конец тэга
                    case XmlPullParser.END_TAG:
                        if (inEntryHeader) {
                            if (inEntryURL) {
                                if ("url".equalsIgnoreCase(tagName)) {
                                    urls.add(textValue);
                                    inEntryURL = false;
                                }

                            }
                            if ("A".equalsIgnoreCase(tagName)) {
                                inEntryHeader = false;
                                getTTNs();
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
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getTTNs() {
        if(urls.size() != 0) {
            for(int i =0; i < urls.size(); i++) {
                getTTMInfo(urls.get(i));
            }
        } else {
            progressBar.setVisibility(View.GONE);
            empty_text.setVisibility(View.VISIBLE);
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
                                obj.setChecked(false);
                                ttms.add(obj);
                                inEntryHeader = false;
                            } else if ("Shipper".equalsIgnoreCase(tagName)) {
                                inEntryShipper = false;
                            } else if ("ClientRegId".equalsIgnoreCase(tagName)) {
                                obj.setFsrar(textValue);
                            } else if ("Identity".equalsIgnoreCase(tagName)) {
                                obj.setGuid(textValue);
                            } else if ("NUMBER".equalsIgnoreCase(tagName)) {
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
            if(urls.size() == ttms.size()) {
                ttms.get(0).setChecked(true);
                adapter = new TTMAdapterUTM(this, ttms);
                utm.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getTTNItems(final String url){
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
                addTTNItem(result);

            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }

    protected void addTTNItem(String fullxml){
        try {
            XmlPullParserFactory xppf = XmlPullParserFactory.newInstance();
            xppf.setNamespaceAware(true);
            XmlPullParser parser = xppf.newPullParser();
            parser.setInput(new StringReader(fullxml));

            String textValue = "";
            boolean inEntryHeader = false, inEntryShipper = false, inEntryContent = false, inEntryProduct = false;
            TTM obj = new TTM();
            Items items = new Items();
            ALC alc = new ALC();
            int futureId = db.getTTNSize() + 1;
            int futuredIdItem = db.getItemsSize() + 1;

            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {

                String tagName = parser.getName();
                String tagPref = parser.getPrefix();
//                        Toast.makeText(getApplicationContext(), tagName, Toast.LENGTH_SHORT).show();

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
                        } else if ("Content".equalsIgnoreCase(tagName)) {
                            inEntryContent = true;
                        } else if ("Position".equalsIgnoreCase(tagName)) {
                            inEntryProduct = true;
                        }
                        break;
                    // конец тэга
                    case XmlPullParser.END_TAG:
                        if (inEntryHeader) {
                            if ("Header".equalsIgnoreCase(tagName)) {
                                inEntryHeader = false;
                                obj.setStatus("0");
                                obj.setId(futureId);
                                obj.setType("1");
                                obj.setFileid(guids.get(adapter.getCheckedPosition()));
                                db.addNewTTN(obj);
                            } else if ("Shipper".equalsIgnoreCase(tagName)) {
                                inEntryShipper = false;
                            } else if ("ClientRegId".equalsIgnoreCase(tagName)) {
                                obj.setFsrar(textValue);
                            } else if ("Identity".equalsIgnoreCase(tagName)) {
                                obj.setGuid(textValue);
                            } else if ("NUMBER".equalsIgnoreCase(tagName)) {
                                obj.setTitle(textValue);
                            } else if ("DATE".equalsIgnoreCase(tagName)) {
                                obj.setDate(textValue);
                            } else if ("INN".equalsIgnoreCase(tagName)) {
                                obj.setInn(textValue);
                            } else if ("ShortName".equalsIgnoreCase(tagName) && inEntryShipper) {
                                obj.setShortname(textValue);
                            }
                        }
                        if (inEntryContent) {
                            if ("Content".equalsIgnoreCase(tagName)) {
                                inEntryContent = false;
                            } else if ("Position".equalsIgnoreCase(tagName)) {
                                inEntryProduct = false;
                                items.setCode("");
                                items.setDocid(String.valueOf(futureId));
                                db.addNewToFooter(items);
                                futuredIdItem++;
                            } else if ("FullName".equalsIgnoreCase(tagName) && tagPref.equals("pref")) {
//                                        Toast.makeText(getActivity(), textValue, Toast.LENGTH_SHORT).show();
                                items.setTitle(textValue);
                            } else if ("AlcCode".equalsIgnoreCase(tagName)) {
//                                        Toast.makeText(getActivity(), tagPref + ":   " + textValue, Toast.LENGTH_SHORT).show();
                                items.setAlccode(textValue);
                            } else if ("Capacity".equalsIgnoreCase(tagName)) {
//                                        Toast.makeText(getActivity(), tagPref + ":   " + textValue, Toast.LENGTH_SHORT).show();
                                items.setCapacity(textValue);
                            } else if ("AlcVolume".equalsIgnoreCase(tagName)) {
//                                        Toast.makeText(getActivity(), tagPref + ":   " + textValue, Toast.LENGTH_SHORT).show();
                                items.setVolume(textValue);
                            } else if ("FARRegId".equalsIgnoreCase(tagName) && tagPref.equals("wb")) {
//                                        Toast.makeText(getActivity(), tagPref + ":   " + textValue, Toast.LENGTH_SHORT).show();
                                items.setFar1(textValue);
                            } else if ("FARRegId".equalsIgnoreCase(tagName) && tagPref.equals("ce")) {
//                                        Toast.makeText(getActivity(), tagPref + ":   " + textValue, Toast.LENGTH_SHORT).show();
                                items.setFar2(textValue);
                            } else if ("amc".equalsIgnoreCase(tagName)) {
//                                        Toast.makeText(getActivity(), tagPref + ":   " + textValue, Toast.LENGTH_SHORT).show();
                                alc.setAlc(textValue);
                                alc.setDocid(String.valueOf(futureId));
                                alc.setItemid(String.valueOf(futuredIdItem));
                                alc.setStatus("0");
                                db.addNewALC(alc);
                            } else if ("Quantity".equalsIgnoreCase(tagName)) {
                                int price = (int) Double.parseDouble(textValue);
                                items.setNums(price);
                                items.setFactnums("0");
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

            Intent intent = new Intent(UTMItemActivity.this, MainActivity.class);
            startActivity(intent);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
