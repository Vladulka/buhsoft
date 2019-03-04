package testvladkuz.buhsoftttm.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import testvladkuz.buhsoftttm.R;
import testvladkuz.buhsoftttm.classes.Items;
import testvladkuz.buhsoftttm.sqldatabase.DatabaseHandler;

public class ItemsDialogAdapter extends RecyclerView.Adapter<ItemsDialogAdapter.ItemsViewHolder> {

    private ArrayList<Items> data;
    Context context;
    DatabaseHandler db;
    String alc, doc;

    private ClickListener mClickListener;

    public ItemsDialogAdapter(Context context, ArrayList<Items> data, String doc, String alc) {
        this.alc = alc;
        this.doc = doc;
        this.data = data;
        this.context = context;
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

        holder.progress.setMax(Integer.valueOf(data.get(position).getNums()));
        holder.progress.setProgress(Integer.valueOf(data.get(position).getFactnums()), true);

        holder.title.setText(data.get(position).getTitle());
        holder.alcCode.setText(data.get(position).getAlccode());

        holder.fact.setText(data.get(position).getFactnums());
        holder.max.setText(String.valueOf(data.get(position).getNums()));
    }

    class ItemsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ProgressBar progress;
        public TextView title, alcCode, fact, max;
        public LinearLayout button;

        public ItemsViewHolder(View itemView) {
            super(itemView);

            progress = itemView.findViewById(R.id.progressBar);
            title = itemView.findViewById(R.id.title);
            alcCode = itemView.findViewById(R.id.alccode);
            fact = itemView.findViewById(R.id.fact);
            max = itemView.findViewById(R.id.max);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onClick(view, getAdapterPosition());
        }
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addNewItem(Items items) {
        data.add(items);
    }

    public void setClickListener(ClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ClickListener {
        void onClick(View view, int position);
    }
}
