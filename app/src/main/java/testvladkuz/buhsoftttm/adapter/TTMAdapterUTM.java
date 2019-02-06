package testvladkuz.buhsoftttm.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.util.ArrayList;

import testvladkuz.buhsoftttm.ItemsActivity;
import testvladkuz.buhsoftttm.R;
import testvladkuz.buhsoftttm.adapter.holders.TTMViewHolder;
import testvladkuz.buhsoftttm.classes.TTM;
import testvladkuz.buhsoftttm.sqldatabase.DatabaseHandler;

public class TTMAdapterUTM extends RecyclerView.Adapter<TTMViewHolder>{

    private ArrayList<TTM> data;
    Context context;
    Boolean selected = false;
    DatabaseHandler db;
    int checkedPos = 0;
    View v;

    public TTMAdapterUTM(Context context, ArrayList<TTM> data) {
        this.data = data;
        this.context = context;
        db = new DatabaseHandler(context);
    }

    @Override
    public TTMViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        v = inflater.inflate(R.layout.ttm_item, parent, false);
        return new TTMViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final TTMViewHolder holder, final int position) {

        holder.checkBox.setVisibility(View.VISIBLE);
        holder.checkBox.setChecked(data.get(position).getChecked());

        holder.title.setText(data.get(position).getTitle());
        holder.date.setText(data.get(position).getDate());
        holder.shortName.setText(data.get(position).getShortname());

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position != checkedPos) {
                    if(checkedPos != -1) {
                        data.get(checkedPos).setChecked(false);
                    }
                    data.get(position).setChecked(true);
                    checkedPos = position;
                } else {
                    data.get(position).setChecked(false);
                    checkedPos = -1;
                }
                notifyDataSetChanged();
                Toast.makeText(context, String.valueOf(checkedPos), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public int getCheckedPosition() {
        return checkedPos;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addNewItem(TTM item) {
        data.add(item);
        notifyItemInserted(data.size() - 1);
    }
}
