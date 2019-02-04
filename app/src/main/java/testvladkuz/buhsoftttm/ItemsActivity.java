package testvladkuz.buhsoftttm;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import testvladkuz.buhsoftttm.adapter.ItemsAdapter;
import testvladkuz.buhsoftttm.adapter.ItemsDialogAdapter;
import testvladkuz.buhsoftttm.sqldatabase.DatabaseHandler;

public class ItemsActivity extends AppCompatActivity implements ItemsDialogAdapter.onCallItemsActivityFunctionsListener {

    DatabaseHandler db;
    RecyclerView list;
    ItemsAdapter adapter;
    ImageView camera, scanner;
    Dialog dialog;
    ItemsDialogAdapter dialogAdapter;
    TextView title, subtitle, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_footer);

        db = new DatabaseHandler(this);

        title = findViewById(R.id.title);
        subtitle = findViewById(R.id.shortName);
        date = findViewById(R.id.date);

        title.setText(getIntent().getStringExtra("title"));
        subtitle.setText(getIntent().getStringExtra("shortname"));
        date.setText(getIntent().getStringExtra("date"));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getIntent().getStringExtra("title"));
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
            dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_docs);

            RecyclerView docs = dialog.findViewById(R.id.docs);
            docs.setLayoutManager(new LinearLayoutManager(this));

            Button current = dialog.findViewById(R.id.current);
            current.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ItemsActivity.this, ScannerActivity.class);
                    intent.putExtra("type", "-1");
                    intent.putExtra("docid", getIntent().getStringExtra("docid"));
                    intent.putExtra("code", getIntent().getStringExtra("code"));
                    startActivity(intent);
                }
            });

            dialogAdapter = new ItemsDialogAdapter(this, db.getAllItems(getIntent().getStringExtra("docid")),this, getIntent().getStringExtra("docid"), getIntent().getStringExtra("code"));
            docs.setAdapter(dialogAdapter);
            dialog.show();
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
                intent.putExtra("title", getIntent().getStringExtra("title"));
                intent.putExtra("docid", getIntent().getStringExtra("docid"));
                intent.putExtra("type", "-2");
                startActivity(intent);
            }
        });
    }

    @Override
    public void showDialogView(boolean show) {
        if(!show) {
            dialog.hide();
        }
    }

    @Override
    public void updateItem(int position) {
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
