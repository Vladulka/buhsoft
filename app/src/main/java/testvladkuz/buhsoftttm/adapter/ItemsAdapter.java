package testvladkuz.buhsoftttm.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import testvladkuz.buhsoftttm.R;
import testvladkuz.buhsoftttm.adapter.holders.ItemsViewHolder;
import testvladkuz.buhsoftttm.classes.Items;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsViewHolder> {

    private ArrayList<Items> data;
    Context context;

    public ItemsAdapter(Context context, ArrayList<Items> data) {
        this.data = data;
        this.context = context;
    }

    @Override
    public ItemsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.footer_item, parent, false);
        return new ItemsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ItemsViewHolder holder, int position) {
        holder.progress.setMax(data.get(position).getNums());
        holder.progress.setProgress(Integer.valueOf(data.get(position).getFactnums()), true);

        holder.title.setText(data.get(position).getTitle());
        holder.alcCode.setText(data.get(position).getAlccode());

        holder.fact.setText(data.get(position).getFactnums());
        holder.max.setText(String.valueOf(data.get(position).getNums()));
    }

    public void updateItem(int position) {
        data.get(position).setFactnums(String.valueOf(Integer.valueOf(data.get(position).getFactnums()) + 1));
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addNewItem(Items items) {
        data.add(items);
        notifyDataSetChanged();
    }

}
