package testvladkuz.buhsoftttm.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;

import testvladkuz.buhsoftttm.R;
import testvladkuz.buhsoftttm.UTMItemActivity;
import testvladkuz.buhsoftttm.adapter.TTMAdapter;
import testvladkuz.buhsoftttm.classes.ALC;
import testvladkuz.buhsoftttm.classes.Items;
import testvladkuz.buhsoftttm.classes.TTM;
import testvladkuz.buhsoftttm.sqldatabase.DatabaseHandler;
import testvladkuz.buhsoftttm.utils.RealPathUtil;

import static android.app.Activity.RESULT_OK;

public class OneFragment extends Fragment implements TTMAdapter.onCallOneFragmentFunctionsListener{

    public OneFragment() {
        // Required empty public constructor
    }

    RecyclerView list;
    public String  actualfilepath="";
    ArrayList<TTM> items = new ArrayList<>();
    ArrayList<Boolean> checkable = new ArrayList<Boolean>();
    TextView textNoTTNs;

    TTMAdapter adapter;
    DatabaseHandler db;
    FloatingActionButton delete, done, select;
    FloatingActionMenu menu;
    LinearLayout edit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_one, container, false);

        db = new DatabaseHandler(getActivity());

        menu = v.findViewById(R.id.menu);
        delete = v.findViewById(R.id.delete);
        select = v.findViewById(R.id.selectall);
        done = v.findViewById(R.id.done);
        edit = v.findViewById(R.id.edit);
        textNoTTNs = v.findViewById(R.id.text_no_ttns);

        list = v.findViewById(R.id.list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        list.setLayoutManager(linearLayoutManager);

        items = db.getAllTTM();

        if(items.size() == 0)
            textNoTTNs.setVisibility(View.VISIBLE);
        else
            for (int i = 0; i < items.size(); i++)
                checkable.add(false);

        adapter = new TTMAdapter(getActivity(), items, false, checkable, this);
        list.setAdapter(adapter);

        FloatingActionButton fab = v.findViewById(R.id.fab1);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("text/xml");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                //intent.putExtra("browseCoa", itemToBrowse);
                //Intent chooser = Intent.createChooser(intent, "Select a File to Upload");
                try {
                    //startActivityForResult(chooser, FILE_SELECT_CODE);
                    startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), 101);
                } catch (Exception ex) {
                    System.out.println("browseClick :"+ex);//android.content.ActivityNotFoundException ex
                }
            }
        });

        FloatingActionButton fab1 = v.findViewById(R.id.fab2);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(db.getUserInfo("url").equals("")) {
                    Toast.makeText(getActivity(), "Введите адрес УТМ в настройках", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getActivity(), UTMItemActivity.class);
                    startActivity(intent);
                }
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = null;
        String fullerror ="";
        String fullxml="";

        if(data != null) {
            uri = data.getData();
        }

        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                try {
                    File file = new File(RealPathUtil.getPath(getActivity(), uri));
                    StringBuilder text = new StringBuilder();
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = br.readLine()) != null) {
                        text.append(line + '\n');
                    }
                    fullxml = text.toString();

                    XmlPullParserFactory xppf = XmlPullParserFactory.newInstance();
                    xppf.setNamespaceAware(true);
                    XmlPullParser parser = xppf.newPullParser();
                    parser.setInput(new StringReader(fullxml));

                    String textValue = "";
                    boolean inEntryIdentity = false, inEntryHeader = false, inEntryShipper = false, inEntryContent = false, inEntryProduct = false;
                    TTM obj = new TTM();
                    Items items = new Items();
                    ALC alc = new ALC();
                    int futureId = db.getTTNSize() + 1;
                    int futuredIdItem = db.getItemsSize() + 1;

                    while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {

                        String tagName = parser.getName();
                        String tagPref = parser.getPrefix();

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
                                } else if ("Identity".equalsIgnoreCase(tagName)) {
                                    if(db.findTTNByGuid(textValue) == -1) {
                                        obj.setGuid(textValue);
                                        inEntryIdentity = true;
                                    } else {
                                        Toast.makeText(getActivity(), "Накладная уже была добавлена", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                break;
                            // конец тэга
                            case XmlPullParser.END_TAG:
                                if (inEntryHeader && inEntryIdentity) {
                                    if ("Header".equalsIgnoreCase(tagName)) {
                                        inEntryHeader = false;
                                        obj.setId(futureId);
                                        obj.setStatus("0");
                                        obj.setFileid("0");
                                        obj.setType("0");
                                        db.addNewTTN(obj);
                                        adapter.addNewTTM(obj);
                                    } else if ("Shipper".equalsIgnoreCase(tagName)) {
                                        inEntryShipper = false;
                                    } else if ("ClientRegId".equalsIgnoreCase(tagName)) {
                                        obj.setFsrar(textValue);
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
                                if (inEntryContent && inEntryIdentity) {
                                    if ("Content".equalsIgnoreCase(tagName)) {
                                        inEntryContent = false;
                                    } else if ("Position".equalsIgnoreCase(tagName)) {
                                        inEntryProduct = false;
                                        items.setCode("");
                                        items.setDocid(String.valueOf(futureId));
                                        items.setType("0");
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
                                        items.setNums(Integer.valueOf(textValue));
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

                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Проблемы с чтением накладной. Повторите попытку.", Toast.LENGTH_LONG).show();
                }
            }
        }
        textNoTTNs.setVisibility(View.GONE);
        menu.close(false);
    }

    @Override
    public void showAndHideButtons(boolean show) {
        if(show) {
            edit.setVisibility(View.VISIBLE);
            menu.setVisibility(View.GONE);

            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.open(false);
                    showAndHideButtons(false);
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    android.app.AlertDialog.Builder ad;

                    ad = new android.app.AlertDialog.Builder(getActivity());

                    ad.setTitle("Удаление элемента"); // заголовок

                    ad.setMessage("Вы действительно хотите удалить выбранные элементы?"); // сообщение

                    ad.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int arg1) {

                            ArrayList<Boolean> ch = adapter.getCheckable();

                            for(int i = ch.size() - 1; i >= 0; i--)
                                if(ch.get(i))
                                    adapter.deleteItems(i);

                            adapter.selectAll(false);
                        }

                    });

                    ad.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int arg1) {

                        }

                    });

                    ad.setCancelable(true);

                    ad.show();
                }
            });

            select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(adapter.getSelected())
                        adapter.selectAll(false);
                    else
                        adapter.selectAll(true);
                }
            });
        } else {
            edit.setVisibility(View.GONE);
            menu.setVisibility(View.VISIBLE);
        }
    }
}
