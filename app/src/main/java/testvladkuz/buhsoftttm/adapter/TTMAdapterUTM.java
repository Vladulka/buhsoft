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
    private ArrayList<Boolean> checkable;
    Context context;
    Boolean chk, selected = false;
    DatabaseHandler db;
    int checkedPos = -1;
    View v;

    onCallOneFragmentFunctionsListener eventListener;

    public interface onCallOneFragmentFunctionsListener{
        void showAndHideButtons(boolean show);
    }

    public TTMAdapterUTM(Context context, ArrayList<TTM> data, Boolean check, ArrayList<Boolean> checked, onCallOneFragmentFunctionsListener eventListener) {
        this.data = data;
        this.chk = check;
        this.context = context;
        this.checkable = checked;
        this.eventListener = eventListener;
        db = new DatabaseHandler(context);
    }

    public TTMAdapterUTM(Context context, ArrayList<TTM> data, Boolean check, ArrayList<Boolean> checked) {
        this.data = data;
        this.chk = check;
        this.context = context;
        this.checkable = checked;
        db = new DatabaseHandler(context);
    }

    public TTMAdapterUTM(Context context, ArrayList<TTM> data, Boolean check) {
        this.data = data;
        this.chk = check;
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

        if(chk) {
            holder.checkBox.setVisibility(View.VISIBLE);
            if(checkable.get(position)) {
                holder.checkBox.setChecked(true);
            } else {
                holder.checkBox.setChecked(false);
            }
        } else {
            holder.checkBox.setVisibility(View.GONE);
        }

        holder.title.setText(data.get(position).getTitle());
        holder.date.setText(data.get(position).getDate());
        holder.shortName.setText(data.get(position).getShortname());

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position != checkedPos) {
                    if(checkedPos != -1) {
                        checkable.set(checkedPos, false);
                    }
                    checkedPos = position;
                    checkable.set(checkedPos, true);
                    holder.checkBox.setChecked(true);
                } else {
                    holder.checkBox.setChecked(false);
                    checkable.set(position, false);
                    checkedPos = -1;

                }
            }
        });

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(position != checkedPos) {
                    if(checkedPos != -1) {
                        checkable.set(checkedPos, false);
                    }
                    checkable.set(position, true);
                    holder.checkBox.setChecked(true);
                    checkedPos = position;
                } else {
                    holder.checkBox.setChecked(isChecked);
                    checkable.set(position, isChecked);
                    checkedPos = -1;
                }

            }
        });
    }

    public int getCheckedPosition() {
        return checkedPos;
    }

    public ArrayList<Boolean> getCheckable() {
        return checkable;
    }

    public void open(Boolean m) {

        if(m) {

            chk = true;

        } else {

            chk = false;

        }

        notifyItemRangeChanged(0, data.size());

        notifyDataSetChanged();

    }

    public void selectAll(Boolean m) {

        for(int i = 0; i < checkable.size(); i++) {

            checkable.set(i, m);

        }

        selected = m;

        chk = true;

        notifyItemRangeChanged(0, data.size());

        notifyDataSetChanged();

    }


    @Override
    public int getItemCount() {
        return data.size();
    }
}
