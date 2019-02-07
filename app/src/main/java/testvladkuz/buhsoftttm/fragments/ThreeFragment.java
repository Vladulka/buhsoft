package testvladkuz.buhsoftttm.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import testvladkuz.buhsoftttm.ItemsActivity;
import testvladkuz.buhsoftttm.R;
import testvladkuz.buhsoftttm.ScannerActivity;
import testvladkuz.buhsoftttm.UTMItemActivity;
import testvladkuz.buhsoftttm.adapter.ItemsDialogAdapter;
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
                        db.updateUserInfo(new Settings("url", url.getText().toString()));
                        url_text.setText(url.getText().toString());
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        return v;
    }

}
