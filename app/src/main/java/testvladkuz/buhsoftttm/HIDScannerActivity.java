package testvladkuz.buhsoftttm;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import java.math.BigInteger;
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
import testvladkuz.buhsoftttm.adapter.TTMAdapterUTM;
import testvladkuz.buhsoftttm.classes.ALC;
import testvladkuz.buhsoftttm.classes.Items;
import testvladkuz.buhsoftttm.classes.TTM;
import testvladkuz.buhsoftttm.fragments.BottomSheetFragment;
import testvladkuz.buhsoftttm.fragments.BottomSheetFragmentScanner;
import testvladkuz.buhsoftttm.sqldatabase.DatabaseHandler;

public class HIDScannerActivity extends AppCompatActivity implements BottomSheetFragmentScanner.onEventListenerFragment {

    EditText resultEditText, resultBarcodeEditText;
    DatabaseHandler db;
    String myJSON;
    JSONArray ans;
    ProgressBar progress, barcodeProgress;
    String idString, titleString, subtitleString, dateString;
    RelativeLayout barcodeLayout;
    ImageView resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hid_scanner);

        idString = getIntent().getStringExtra("docid");
        titleString = getIntent().getStringExtra("title");
        subtitleString = getIntent().getStringExtra("shortname");
        dateString = getIntent().getStringExtra("date");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("HID - сканер для " + titleString);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HIDScannerActivity.this, ItemsActivity.class);
                intent.putExtra("title", getIntent().getStringExtra("title"));
                intent.putExtra("shortname", getIntent().getStringExtra("shortname"));
                intent.putExtra("date", getIntent().getStringExtra("date"));
                intent.putExtra("docid", getIntent().getStringExtra("docid"));
                intent.putExtra("code", getIntent().getStringExtra("code"));
                intent.putExtra("result", "-11");
                startActivity(intent);
            }
        });


        resultEditText = findViewById(R.id.code);
        resultBarcodeEditText = findViewById(R.id.barcode);
        barcodeLayout = findViewById(R.id.barcodeLayout);
        resetButton = findViewById(R.id.resetButton);
        progress = findViewById(R.id.progress);
        barcodeProgress = findViewById(R.id.barcodeProgress);

        db = new DatabaseHandler(this);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultEditText.setText("");
                resultBarcodeEditText.setText("");
            }
        });

        resultEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_ENTER))
                {
                    String codeString = resultEditText.getText().toString();
                    if(codeString.length() == 68) {
                        progress.setVisibility(View.VISIBLE);
                        String pdf417 = db.findALCByPDF417(codeString, getIntent().getStringExtra("docid"), false);
                        if(!pdf417.equals("-2")) {
                            String alccode = db.findALCByPDF417(codeString, getIntent().getStringExtra("docid"), true);
                            if(!alccode.equals("-1")) {
                                db.addNewALC(new ALC(getIntent().getStringExtra("docid"), alccode, codeString, "1"));
//                                goToItemsActivity();
                            } else {
                                alccode = codeString.substring(3, 19);
                                StringBuilder alc = new StringBuilder(new BigInteger(alccode, 36).toString());
                                if(alc.length() < 20) {
                                    for(int i = 0; i < 20 - alc.length(); i++) {
                                        alc.insert(0, "0");
                                    }
                                }

                                getItemByAlc(codeString, alc.toString());
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Данный алко-код уже был отсканирован.", Toast.LENGTH_LONG).show();
                        }
                    } else if(codeString.length() == 150) {
                        String matrix = db.findALCByMatrix(codeString, idString);
                        if(matrix.equals("-1")) {
                            BottomSheetFragmentScanner bottomSheetDialogFragment = new BottomSheetFragmentScanner();

                            Bundle bundle = new Bundle();
                            bundle.putString("title", titleString);
                            bundle.putString("shortname", subtitleString);
                            bundle.putString("date", dateString);
                            bundle.putString("docid", getIntent().getStringExtra("docid"));
                            bundle.putString("code", resultEditText.getText().toString());

                            bottomSheetDialogFragment.setArguments(bundle);

                            bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                            Toast.makeText(getApplicationContext(), "Данного алко-кода нет в накладной.", Toast.LENGTH_LONG).show();
                        } else if(matrix.equals("-2")) {
                            Toast.makeText(getApplicationContext(), "Данный штрих-код уже был отсканирован.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Возникла ошибка сканирования.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Произошла ошибка. Повторите сканирование.", Toast.LENGTH_LONG).show();
                        resultEditText.setText("");
                    }
                }
                return false;
            }
        });

        resultEditText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if(s.length() != 0 ) {
                    resetButton.setVisibility(View.VISIBLE);
                } else if(s.length() == 0) {
                    resetButton.setVisibility(View.GONE);
                    barcodeLayout.setVisibility(View.GONE);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        resultBarcodeEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String codeString = resultBarcodeEditText.getText().toString();
                    if(codeString.length() == 13) {
                        getItemByCode(codeString);
                    } else {
                        Toast.makeText(getApplicationContext(), "Произошла ошибка. Повторите сканирование.", Toast.LENGTH_LONG).show();
                        resultBarcodeEditText.setText("");
                    }
                }
                return false;
            }
        });
    }

    public void getItemByAlc(final String code, final String alc){
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                HttpPost httppost = new HttpPost("https://online.buhsoft.ru/getbarcod/barcod_off.php");
                //будем передавать два параметра
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);

                nameValuePairs.add(new BasicNameValuePair("kodexch","0015"));
                nameValuePairs.add(new BasicNameValuePair("kod_bd","android"));
                nameValuePairs.add(new BasicNameValuePair("alccode", alc));

                InputStream inputStream = null;
                String result = null;
                try {
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
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
                myJSON = result;
                showAlcList(code, alc);
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }

    void showAlcList(String code, String alc){
        String name = null, barcod = null;
        int i = db.getItemsSize();

        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            ans = jsonObj.getJSONArray("result");

            if(ans.getJSONObject(0).getString("name").equals("")) {
                Toast.makeText(getApplicationContext(), "Товара с данным алко - кодом нет в базе данных.", Toast.LENGTH_LONG).show();
            } else {
                name = ans.getJSONObject(0).getString("name");
                barcod = ans.getJSONObject(0).getString("barkod");
                db.addNewToFooter(new Items(i, getIntent().getStringExtra("docid"), name, alc, "", "", "", "", "", "",1, "1", barcod, "1"));
                db.addNewALC(new ALC(getIntent().getStringExtra("docid"), String.valueOf(i), code, "1"));
                if(i < db.getItemsSize()) {
                    Toast.makeText(getApplicationContext(), "Данного алко-кода нет в накладной. Он был добавлен в контрафакт.", Toast.LENGTH_LONG).show();
                    resultEditText.setText("");
                } else {
                    Toast.makeText(getApplicationContext(), "Произошла ошибка. Повторите сканирование.", Toast.LENGTH_LONG).show();
                }
            }
            progress.setVisibility(View.GONE);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void getItemByCode(final String code){
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                HttpPost httppost = new HttpPost("https://online.buhsoft.ru/getbarcod/barcod_off.php");
                //будем передавать два параметра
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);

                nameValuePairs.add(new BasicNameValuePair("kodexch","0016"));
                nameValuePairs.add(new BasicNameValuePair("kod_bd","android"));
                nameValuePairs.add(new BasicNameValuePair("code", code));

                InputStream inputStream = null;
                String result = null;
                try {
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
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
                myJSON = result;
                showList(code);
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }

    protected void showList(final String code){
        try {
            JSONObject jsonObj = new JSONObject(myJSON);

            if(jsonObj.getString("result").equals("")) {
                Toast.makeText(getApplicationContext(), "Товара с данным штрих - кодом нет в базе данных.", Toast.LENGTH_LONG).show();
            } else {
                ans = jsonObj.getJSONArray("result");
                JSONObject object = ans.getJSONObject(0);

                int i = db.getItemsSize();

                db.addNewToFooter(new Items(i, idString, object.getString("name"), object.getString("alccode"), "", "", "", "", "", "",1, "1", code, "1"));
                db.addNewALC(new ALC(idString, String.valueOf(i), resultEditText.getText().toString(), "1"));

                if(i < db.getItemsSize()) {
                    Toast.makeText(getApplicationContext(), "Товар был добавлен в накладную.", Toast.LENGTH_LONG).show();
                    barcodeLayout.setVisibility(View.GONE);
                    resultEditText.setText("");
                    resultBarcodeEditText.setText("");
                } else {
                    Toast.makeText(getApplicationContext(), "Произошла ошибка. Повторите сканирование.", Toast.LENGTH_LONG).show();
                }
            }

            barcodeProgress.setVisibility(View.GONE);
            progress.setVisibility(View.GONE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void scanBarcode() {
        barcodeLayout.setVisibility(View.VISIBLE);
        resultBarcodeEditText.setFocusable(true);
    }
}
