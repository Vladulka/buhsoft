package testvladkuz.buhsoftttm;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import testvladkuz.buhsoftttm.adapter.ItemsAdapter;
import testvladkuz.buhsoftttm.adapter.ItemsDialogAdapter;
import testvladkuz.buhsoftttm.classes.ALC;
import testvladkuz.buhsoftttm.classes.Items;
import testvladkuz.buhsoftttm.fragments.BottomSheetFragment;
import testvladkuz.buhsoftttm.sqldatabase.DatabaseHandler;

public class ItemsActivity extends AppCompatActivity implements  BottomSheetFragment.onEventListenerFragment {

    DatabaseHandler db;
    RecyclerView list;
    ItemsAdapter adapter;
    ImageView camera, scanner, share;
    Dialog dialog;
    ItemsDialogAdapter dialogAdapter;
    TextView title, subtitle, date;
    ArrayList<Items> itemsList = new ArrayList<>();
    String idString, titleString, subtitleString, dateString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_footer);

        db = new DatabaseHandler(this);

        title = findViewById(R.id.title);
        subtitle = findViewById(R.id.shortName);
        date = findViewById(R.id.date);

        idString = getIntent().getStringExtra("docid");
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
            bundle.putString("docid", idString);
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
        share = findViewById(R.id.share);

        if(getIntent().getStringExtra("activity_type").equals("1")) {
            camera.setVisibility(View.GONE);
            scanner.setVisibility(View.GONE);
        }

        list = findViewById(R.id.list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(linearLayoutManager);

        itemsList = db.getAllItems(idString);
        adapter = new ItemsAdapter(this, itemsList);
        list.setAdapter(adapter);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ItemsActivity.this, ScannerActivity.class);
                intent.putExtra("title", titleString);
                intent.putExtra("shortname", subtitleString);
                intent.putExtra("date", dateString);
                intent.putExtra("docid", idString);
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
                intent.putExtra("docid", idString);
                intent.putExtra("type", "-2");
                startActivity(intent);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(db.getALCSize(idString) != 0 ) {
                    generateNoteOnSD(getApplicationContext());
                } else {
                    Toast.makeText(getApplicationContext(), "Нет отсканированных товаров", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public void generateNoteOnSD(Context context) {
        String textString = "";

        for(int i = 0; i < itemsList.size(); i++) {
            ArrayList<ALC> alc = new ArrayList<>();
            alc = db.getAllALCByShare(String.valueOf(itemsList.get(i).getId()));
            for(int j = 0; j < alc.size(); j++)
                textString = textString + alc.get(j).getAlc() + ";" + itemsList.get(i).getAlccode() + "\n";
        }

        File exportfile = createFile(textString);
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);

        if(exportfile.exists()) {
            intentShareFile.setType("application/txt");

            intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                    "Файл выгрузки документа " + titleString);
            intentShareFile.putExtra(Intent.EXTRA_TEXT, "Документ был создан в приложении BuhsoftTTM на Android");

// wrap File object into a content provider. NOTE: authority here should match authority in manifest declaration
            Uri uri = FileProvider.getUriForFile(ItemsActivity.this, BuildConfig.APPLICATION_ID + ".provider", exportfile);

            intentShareFile.putExtra(Intent.EXTRA_STREAM, uri);

            startActivity(Intent.createChooser(intentShareFile, "Поделиться файлом"));


            Toast.makeText(context, "Файл успешно сохранен на телефон в папку BuhsoftTTM", Toast.LENGTH_SHORT).show();
            db.updateTTNById(idString);
        }
    }

    private File createFile(String textString) {

        File root = new File(Environment.getExternalStorageDirectory(), "BuhsoftTTM");
        if (!root.exists())
            root.mkdirs();

        File gpxfile = new File(root, titleString + ".txt");
        FileWriter writer = null;

        try {
            writer = new FileWriter(gpxfile);
            writer.append(textString);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return gpxfile;
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
