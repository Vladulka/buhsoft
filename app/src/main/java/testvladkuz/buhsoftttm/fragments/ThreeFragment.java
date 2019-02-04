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

import testvladkuz.buhsoftttm.ItemsActivity;
import testvladkuz.buhsoftttm.R;
import testvladkuz.buhsoftttm.ScannerActivity;
import testvladkuz.buhsoftttm.UTMItemActivity;
import testvladkuz.buhsoftttm.adapter.ItemsDialogAdapter;

public class ThreeFragment extends Fragment {

    public ThreeFragment() {
        // Required empty public constructor
    }

    Dialog dialog;
    EditText url;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_three, container, false);

        url = v.findViewById(R.id.url);

        url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.dialog_url);

                final EditText url = dialog.findViewById(R.id.url);

                Button current = dialog.findViewById(R.id.upload);
                current.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), UTMItemActivity.class);
                        intent.putExtra("url", url.getText().toString());
                        startActivity(intent);
                    }
                });
                dialog.show();
            }
        });

        return v;
    }

}
