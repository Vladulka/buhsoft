package testvladkuz.buhsoftttm.fragments;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.params.BasicHttpParams;
import testvladkuz.buhsoftttm.R;
import testvladkuz.buhsoftttm.classes.Settings;
import testvladkuz.buhsoftttm.sqldatabase.DatabaseHandler;

public class ThreeFragment extends Fragment {

    public ThreeFragment() {
        // Required empty public constructor
    }

    Dialog dialog;
    TextView url_text;
    LinearLayout url_layout;
    DatabaseHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_three, container, false);

        db = new DatabaseHandler(getActivity());
        url_text = v.findViewById(R.id.url);
        url_layout = v.findViewById(R.id.url_layout);

        if(!db.getUserInfo("url").equals("")) {
            url_text.setText(db.getUserInfo("url"));
        } else{
            url_text.setText("Введите адрес УТМ");
        }

        url_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.dialog_url);

                final EditText url = dialog.findViewById(R.id.url);
                url.setText(db.getUserInfo("url"));

                Button current = dialog.findViewById(R.id.upload);
                current.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String urlString = url.getText().toString();

                        checkStatus(urlString);

                        db.updateUserInfo(new Settings("url", urlString));
                        url_text.setText(urlString);
                        dialog.dismiss();

                    }
                });
                dialog.show();
            }
        });

        return v;
    }

    public void checkStatus(final String url){
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String thePath = null;
                try {
                    thePath = URLEncoder.encode(url, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                HttpGet httppost = new HttpGet(thePath);
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
                    return "-1";
                }
                finally {
                    try{if(inputStream != null)inputStream.close();}catch(Exception ignored){}
                }
                return result;

            }
            @Override
            protected void onPostExecute(String result){
                if(result.equals("-1") || result.equals("")) {
                    Toast.makeText(getActivity(), "Ошибка подключения. УТМ недоступна", Toast.LENGTH_SHORT).show();
                }
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }

}
