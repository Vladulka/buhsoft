package testvladkuz.buhsoftttm;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import testvladkuz.buhsoftttm.adapter.ItemsAdapter;
import testvladkuz.buhsoftttm.adapter.ItemsDialogAdapter;
import testvladkuz.buhsoftttm.fragments.BottomSheetFragment;
import testvladkuz.buhsoftttm.sqldatabase.DatabaseHandler;

public class ItemsActivity extends AppCompatActivity implements  BottomSheetFragment.onEventListenerFragment {

    DatabaseHandler db;
    RecyclerView list;
    ItemsAdapter adapter;
    ImageView camera, scanner;
    Dialog dialog;
    ItemsDialogAdapter dialogAdapter;
    TextView title, subtitle, date;
    String titleString, subtitleString, dateString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_footer);

        db = new DatabaseHandler(this);

        title = findViewById(R.id.title);
        subtitle = findViewById(R.id.shortName);
        date = findViewById(R.id.date);

        titleString = getIntent().getStringExtra("title");
        subtitleString = getIntent().getStringExtra("shortname");
        dateString = getIntent().getStringExtra("date");

        title.setText(titleString);
        subtitle.setText(subtitleString);
        date.setText(dateString);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(titleString);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        if(getIntent().getStringExtra("result").equals("-1")) {


            BottomSheetFragment bottomSheetDialogFragment = new BottomSheetFragment();

            Bundle bundle = new Bundle();
            bundle.putString("type", "-1");
            bundle.putString("title", titleString);
            bundle.putString("shortname", subtitleString);
            bundle.putString("date", dateString);
            bundle.putString("docid", getIntent().getStringExtra("docid"));
            bundle.putString("code", getIntent().getStringExtra("code"));

            bottomSheetDialogFragment.setArguments(bundle);

            bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
        } else if(getIntent().getStringExtra("result").equals("-3")) {
//            dialog = new Dialog(this);
//            dialog.setContentView(R.layout.dialog_docs);
//            RecyclerView docs = dialog.findViewById(R.id.docs);
//            docs.setLayoutManager(new LinearLayoutManager(this));
//
//            dialogAdapter = new TTMDialogAdapter(this, db.getAllTTM(), getIntent().getStringExtra("code"), this);
//            docs.setAdapter(dialogAdapter);
//            dialog.show();
        }

        camera = findViewById(R.id.camera);
        scanner = findViewById(R.id.scanner);

        list = findViewById(R.id.list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(linearLayoutManager);

        adapter = new ItemsAdapter(this, db.getAllItems(getIntent().getStringExtra("docid")));
        list.setAdapter(adapter);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ItemsActivity.this, ScannerActivity.class);
                intent.putExtra("title", titleString);
                intent.putExtra("shortname", subtitleString);
                intent.putExtra("date", dateString);
                intent.putExtra("docid", getIntent().getStringExtra("docid"));
                intent.putExtra("type", "-2");
                startActivity(intent);
            }
        });

        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ItemsActivity.this, HIDScannerActivity.class);
                intent.putExtra("title", titleString);
                intent.putExtra("shortname", subtitleString);
                intent.putExtra("date", dateString);
                intent.putExtra("docid", getIntent().getStringExtra("docid"));
                intent.putExtra("type", "-2");
                startActivity(intent);
            }
        });
    }

    @Override
    public void onCheckedElement(int position) {
        adapter.updateItem(position);
    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event)
//    {
//        if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
//
//        }
//
//        if(keyCode == KeyEvent.KEYCODE_BACK) {
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//        }
//
//        return true;
//    }
}
