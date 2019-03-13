package testvladkuz.buhsoftttm.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;

import testvladkuz.buhsoftttm.R;
import testvladkuz.buhsoftttm.adapter.TTMFinishAdapter;
import testvladkuz.buhsoftttm.classes.TTM;
import testvladkuz.buhsoftttm.sqldatabase.DatabaseHandler;

public class TwoFragment extends Fragment implements TTMFinishAdapter.onCallOneFragmentFunctionsListener{

    RecyclerView list;
    String TAG = "APP";
    public String  actualfilepath="";
    ArrayList<TTM> items = new ArrayList<>();
    ArrayList<Boolean> checkable = new ArrayList<Boolean>();
    TextView textNoTTNs;

    TTMFinishAdapter adapter;
    DatabaseHandler db;
    FloatingActionButton delete, done, select;
    LinearLayout edit;

    public TwoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_two, container, false);

        db = new DatabaseHandler(getActivity());

        delete = v.findViewById(R.id.delete);
        select = v.findViewById(R.id.selectall);
        done = v.findViewById(R.id.done);
        edit = v.findViewById(R.id.edit);
        textNoTTNs = v.findViewById(R.id.text_no_ttns);

        list = v.findViewById(R.id.list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        list.setLayoutManager(linearLayoutManager);

        items = db.getFinishTTM();

        if(items.size() == 0)
            textNoTTNs.setVisibility(View.VISIBLE);
        else
            for (int i = 0; i < items.size(); i++)
                checkable.add(false);



        adapter = new TTMFinishAdapter(getActivity(), items, false, checkable, this);
        list.setAdapter(adapter);

        return v;
    }

    @Override
    public void showAndHideButtons(boolean show) {
        if(show) {
            edit.setVisibility(View.VISIBLE);

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

                            for(int i = ch.size() - 1; i >= 0; i--) {

                                if(ch.get(i)) {

                                    adapter.deleteItems(i);

                                }

                            }

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
                    if(adapter.getSelected()) {
                        adapter.selectAll(false);
                    } else {
                        adapter.selectAll(true);
                    }
                }
            });
        } else {
            edit.setVisibility(View.GONE);
        }
    }
}
