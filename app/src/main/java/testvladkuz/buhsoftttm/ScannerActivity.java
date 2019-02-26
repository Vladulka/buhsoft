package testvladkuz.buhsoftttm;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.params.BasicHttpParams;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import testvladkuz.buhsoftttm.classes.ALC;
import testvladkuz.buhsoftttm.classes.Items;
import testvladkuz.buhsoftttm.sqldatabase.DatabaseHandler;
import testvladkuz.buhsoftttm.utils.CameraSelectorDialogFragment;
import testvladkuz.buhsoftttm.utils.FormatSelectorDialogFragment;
import testvladkuz.buhsoftttm.utils.MessageDialogFragment;

public class ScannerActivity extends AppCompatActivity implements MessageDialogFragment.MessageDialogListener,
        ZXingScannerView.ResultHandler, FormatSelectorDialogFragment.FormatSelectorDialogListener,
        CameraSelectorDialogFragment.CameraSelectorDialogListener {
    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private static final String SELECTED_FORMATS = "SELECTED_FORMATS";
    private static final String CAMERA_ID = "CAMERA_ID";
    private ZXingScannerView mScannerView;
    private boolean mFlash;
    private boolean mAutoFocus;
    private ArrayList<Integer> mSelectedIndices;
    private int mCameraId = -1;
    DatabaseHandler db;
    String myJSON;
    JSONArray ans;
    String docid, code;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        if (state != null) {
            mFlash = state.getBoolean(FLASH_STATE, false);
            mAutoFocus = state.getBoolean(AUTO_FOCUS_STATE, true);
            mSelectedIndices = state.getIntegerArrayList(SELECTED_FORMATS);
            mCameraId = state.getInt(CAMERA_ID, -1);
        } else {
            mFlash = false;
            mAutoFocus = true;
            mSelectedIndices = null;
            mCameraId = -1;
        }

        db = new DatabaseHandler(this);
        docid = getIntent().getStringExtra("docid");

        setContentView(R.layout.activity_scanner);

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        setupFormats();
        contentFrame.addView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera(mCameraId);
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, mFlash);
        outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus);
        outState.putIntegerArrayList(SELECTED_FORMATS, mSelectedIndices);
        outState.putInt(CAMERA_ID, mCameraId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem;

        if (mFlash) {
            menuItem = menu.add(Menu.NONE, R.id.menu_flash, 0, R.string.flash_on);
        } else {
            menuItem = menu.add(Menu.NONE, R.id.menu_flash, 0, R.string.flash_off);
        }
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);


        if (mAutoFocus) {
            menuItem = menu.add(Menu.NONE, R.id.menu_auto_focus, 0, R.string.auto_focus_on);
        } else {
            menuItem = menu.add(Menu.NONE, R.id.menu_auto_focus, 0, R.string.auto_focus_off);
        }
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);

        menuItem = menu.add(Menu.NONE, R.id.menu_formats, 0, R.string.formats);
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);

        menuItem = menu.add(Menu.NONE, R.id.menu_camera_selector, 0, R.string.select_camera);
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.menu_flash:
                mFlash = !mFlash;
                if (mFlash) {
                    item.setTitle(R.string.flash_on);
                } else {
                    item.setTitle(R.string.flash_off);
                }
                mScannerView.setFlash(mFlash);
                return true;
            case R.id.menu_auto_focus:
                mAutoFocus = !mAutoFocus;
                if (mAutoFocus) {
                    item.setTitle(R.string.auto_focus_on);
                } else {
                    item.setTitle(R.string.auto_focus_off);
                }
                mScannerView.setAutoFocus(mAutoFocus);
                return true;
            case R.id.menu_formats:
                DialogFragment fragment = FormatSelectorDialogFragment.newInstance(this, mSelectedIndices);
                fragment.show(getSupportFragmentManager(), "format_selector");
                return true;
            case R.id.menu_camera_selector:
                mScannerView.stopCamera();
                DialogFragment cFragment = CameraSelectorDialogFragment.newInstance(this, mCameraId);
                cFragment.show(getSupportFragmentManager(), "camera_selector");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void handleResult(Result rawResult) {
//        try {
//            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//            r.play();
//        } catch (Exception e) {
//        }

        if (getIntent().getStringExtra("type").equals("-1")) {
            if(!rawResult.getBarcodeFormat().toString().equals("EAN_13")) {
                Toast.makeText(getApplicationContext(), "Произошла ошибка. Повторите сканирование.", Toast.LENGTH_LONG).show();
                mScannerView.resumeCameraPreview(this);
            } else {
                getItemByCode(rawResult.getText());
            }

        } else {
            String errorLevel = "0";

            String code = rawResult.getText();

            if(rawResult.getBarcodeFormat().toString().equals("PDF_417")) {
                String pdf417 = db.findALCByPDF417(rawResult.getText(), getIntent().getStringExtra("docid"), false);
                if(!pdf417.equals("-2")) {
                    String alccode = db.findALCByPDF417(rawResult.getText(), getIntent().getStringExtra("docid"), true);
                    if(!alccode.equals("-1")) {
                        db.addNewALC(new ALC(getIntent().getStringExtra("docid"), alccode, rawResult.getText(), "1"));
                        goToItemsActivity();
                    } else {
                        alccode = rawResult.getText().substring(3, 19);
                        StringBuilder alc = new StringBuilder(new BigInteger(alccode, 36).toString());
                        if(alc.length() < 20) {
                            for(int i = 0; i < 20 - alc.length(); i++) {
                                alc.insert(0, "0");
                            }
                        }

                        getItemByAlc(rawResult.getText(), alc.toString());
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Данный алко-код уже был отсканирован.", Toast.LENGTH_SHORT).show();
                    mScannerView.resumeCameraPreview(this);
                }
            } else if(code.length() == 150) {
                Intent intent = new Intent(ScannerActivity.this, ItemsActivity.class);
                intent.putExtra("title", getIntent().getStringExtra("title"));
                intent.putExtra("shortname", getIntent().getStringExtra("shortname"));
                intent.putExtra("date", getIntent().getStringExtra("date"));
                intent.putExtra("docid", getIntent().getStringExtra("docid"));
                intent.putExtra("code", getIntent().getStringExtra("code"));

                String matrix = db.findALCByMatrix(rawResult.getText(), getIntent().getStringExtra("docid"));
                if(matrix.equals("-1")) {
                    intent.putExtra("result", "-1");
                    intent.putExtra("code", rawResult.getText());
                    Toast.makeText(getApplicationContext(), "Данного алко-кода нет в накладной.", Toast.LENGTH_LONG).show();
                } else if(matrix.equals("-2")) {
                    intent.putExtra("result", "-2");
                    Toast.makeText(getApplicationContext(), "Данный штрих-код уже был отсканирован.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Возникла ошибка сканирования.", Toast.LENGTH_LONG).show();
                    intent.putExtra("result", "0");
                }
                startActivity(intent);

            }
        }
    }

    public void closeMessageDialog() {
        closeDialog("scan_results");
    }

    public void closeFormatsDialog() {
        closeDialog("format_selector");
    }

    public void closeDialog(String dialogName) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        DialogFragment fragment = (DialogFragment) fragmentManager.findFragmentByTag(dialogName);
        if (fragment != null) {
            fragment.dismiss();
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // Resume the camera
        mScannerView.resumeCameraPreview(this);
    }

    @Override
    public void onFormatsSaved(ArrayList<Integer> selectedIndices) {
        mSelectedIndices = selectedIndices;
        setupFormats();
    }

    @Override
    public void onCameraSelected(int cameraId) {
        mCameraId = cameraId;
        mScannerView.startCamera(mCameraId);
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
    }

    public void setupFormats() {
        List<BarcodeFormat> formats = new ArrayList<BarcodeFormat>();
        if (mSelectedIndices == null || mSelectedIndices.isEmpty()) {
            mSelectedIndices = new ArrayList<Integer>();
            for (int i = 0; i < ZXingScannerView.ALL_FORMATS.size(); i++) {
                mSelectedIndices.add(i);
            }
        }

        for (int index : mSelectedIndices) {
            formats.add(ZXingScannerView.ALL_FORMATS.get(index));
        }
        if (mScannerView != null) {
            mScannerView.setFormats(formats);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
        closeMessageDialog();
        closeFormatsDialog();
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
                mScannerView.resumeCameraPreview(this);
            } else {
                name = ans.getJSONObject(0).getString("name");
                barcod = ans.getJSONObject(0).getString("barkod");
                db.addNewToFooter(new Items(i, docid, name, alc, "", "", "", "", "", "",1, "1", barcod));
                db.addNewALC(new ALC(docid, String.valueOf(i), code, "1"));
                if(i < db.getItemsSize()) {
                    Toast.makeText(getApplicationContext(), "Данного алко-кода нет в накладной. Он был добавлен в контрафакт.", Toast.LENGTH_LONG).show();
                    goToItemsActivity();
                } else {
                    Toast.makeText(getApplicationContext(), "Произошла ошибка. Повторите сканирование.", Toast.LENGTH_LONG).show();
                    mScannerView.resumeCameraPreview(this);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    void goToItemsActivity() {
        Intent intent = new Intent(ScannerActivity.this, ItemsActivity.class);
        intent.putExtra("title", getIntent().getStringExtra("title")).putExtra("shortname", getIntent().getStringExtra("shortname")).putExtra("date", getIntent().getStringExtra("date")).putExtra("docid", getIntent().getStringExtra("docid")).putExtra("code", getIntent().getStringExtra("code")).putExtra("result", "-11");
        startActivity(intent);
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

            Intent intent = new Intent(ScannerActivity.this, ItemsActivity.class);
            intent.putExtra("title", getIntent().getStringExtra("title"));
            intent.putExtra("shortname", getIntent().getStringExtra("shortname"));
            intent.putExtra("date", getIntent().getStringExtra("date"));
            intent.putExtra("docid", getIntent().getStringExtra("docid"));
            intent.putExtra("code", getIntent().getStringExtra("code"));
            intent.putExtra("result", "-11");

            if(jsonObj.getString("result").equals("")) {
                Toast.makeText(getApplicationContext(), "Товара с данным штрих - кодом нет в базе данных.", Toast.LENGTH_LONG).show();
                mScannerView.resumeCameraPreview(this);
            } else {
                ans = jsonObj.getJSONArray("result");
                JSONObject object = ans.getJSONObject(0);

                int i = db.getItemsSize();

                db.addNewToFooter(new Items(i, docid, object.getString("name"), object.getString("alccode"), "", "", "", "", "", "",1, "1", code));
                db.addNewALC(new ALC(docid, String.valueOf(i), getIntent().getStringExtra("code"), "1"));

                if(i < db.getItemsSize()) {
                    Toast.makeText(getApplicationContext(), "Товар был добавлен в накладную.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Произошла ошибка. Повторите сканирование.", Toast.LENGTH_LONG).show();
                }
            }

            startActivity(intent);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
