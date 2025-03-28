package testvladkuz.buhsoftttm.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.EventListener;

import testvladkuz.buhsoftttm.ItemsActivity;
import testvladkuz.buhsoftttm.R;
import testvladkuz.buhsoftttm.adapter.holders.ItemsViewHolder;
import testvladkuz.buhsoftttm.classes.ALC;
import testvladkuz.buhsoftttm.classes.Items;
import testvladkuz.buhsoftttm.sqldatabase.DatabaseHandler;

public class ItemsDialogAdapter extends RecyclerView.Adapter<ItemsViewHolder> {

    private ArrayList<Items> data;
    Context context;
    DatabaseHandler db;
    String alc, doc;

    onCallItemsActivityFunctionsListener eventListener;

    public interface onCallItemsActivityFunctionsListener{
        void showDialogView(boolean show);
        void updateItem(int position);
    }

    public ItemsDialogAdapter(Context context, ArrayList<Items> data, onCallItemsActivityFunctionsListener eventListener, String doc, String alc) {
        this.alc = alc;
        this.doc = doc;
        this.data = data;
        this.context = context;
        this.eventListener = eventListener;
        db = new DatabaseHandler(context);
    }

    @Override
    public ItemsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.footer_item, parent, false);
        return new ItemsViewHolder(v);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(final ItemsViewHolder holder, final int position) {
//        Toast.makeText(context, data.get(position).getAlccode(), Toast.LENGTH_SHORT).show();
        holder.progress.setMax(Integer.valueOf(data.get(position).getNums()));
        holder.progress.setProgress(Integer.valueOf(data.get(position).getFactnums()), true);

        holder.title.setText(data.get(position).getTitle());
        holder.alcCode.setText(data.get(position).getAlccode());

        holder.fact.setText(data.get(position).getFactnums());
        holder.max.setText(data.get(position).getNums());

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                db.addNewALC(new ALC(doc, String.valueOf(data.get(position).getId()),  alc,"1"));
                db.updateItemStatus(String.valueOf(data.get(position).getId()));
                eventListener.showDialogView(false);
                eventListener.updateItem(position);
//                String id = db.findALC(alc, String.valueOf(data.get(position).getId()));
//                if(id.equals("-1")) {
//                    Intent intent = new Intent(context, ItemsActivity.class);
//                    intent.putExtra("title", data.get(position).getTitle());
//                    intent.putExtra("docid", String.valueOf(data.get(position).getId()));
//                    intent.putExtra("result", "-3");
//                    intent.putExtra("code", alc);
//
//                    context.startActivity(intent);
//                    eventListener.showDialogView(false);
//                } else if(id.equals("-2")) {
//                    Toast.makeText(context, "Данный штрих-код уже был отсканирован в этой накладной.", Toast.LENGTH_SHORT).show();
//                } else {
//                    eventListener.showDialogView(false);
//                }
            }
        });
//        Toast.makeText(context, data.get(position).getAlccode(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addNewItem(Items items) {
        data.add(items);
    }

}
