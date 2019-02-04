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

import java.util.ArrayList;

import testvladkuz.buhsoftttm.ItemsActivity;
import testvladkuz.buhsoftttm.R;
import testvladkuz.buhsoftttm.adapter.holders.TTMViewHolder;
import testvladkuz.buhsoftttm.classes.TTM;
import testvladkuz.buhsoftttm.fragments.OneFragment;
import testvladkuz.buhsoftttm.sqldatabase.DatabaseHandler;

public class TTMAdapter extends RecyclerView.Adapter<TTMViewHolder>{

    private ArrayList<TTM> data;
    private ArrayList<Boolean> checkable;
    Context context;
    Boolean chk, selected = false;
    DatabaseHandler db;
    View v;

    onCallOneFragmentFunctionsListener eventListener;

    public interface onCallOneFragmentFunctionsListener{
        void showAndHideButtons(boolean show);

    }

    public TTMAdapter(Context context, ArrayList<TTM> data, Boolean check, ArrayList<Boolean> checked, onCallOneFragmentFunctionsListener eventListener) {
        this.data = data;
        this.chk = check;
        this.context = context;
        this.checkable = checked;
        this.eventListener = eventListener;
        db = new DatabaseHandler(context);
    }

    public TTMAdapter(Context context, ArrayList<TTM> data, Boolean check, ArrayList<Boolean> checked) {
        this.data = data;
        this.chk = check;
        this.context = context;
        this.checkable = checked;
        db = new DatabaseHandler(context);
    }

    public TTMAdapter(Context context, ArrayList<TTM> data, Boolean check) {
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

                if(!chk) {

                    Intent intent = new Intent(context, ItemsActivity.class);
                    intent.putExtra("title", String.valueOf(data.get(position).getTitle()));
                    intent.putExtra("date", String.valueOf(data.get(position).getDate()));
                    intent.putExtra("shortname", String.valueOf(data.get(position).getShortname()));
                    intent.putExtra("docid", String.valueOf(data.get(position).getId()));
                    intent.putExtra("result", "0");
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, holder.item, "title");
                    context.startActivity(intent, options.toBundle());

                } else {

                    if(checkable.get(position)) {

                        checkable.set(position, false);

                        holder.checkBox.setChecked(false);

                    } else {

                        checkable.set(position, true);

                        holder.checkBox.setChecked(true);

                    }

                }
            }
        });

        holder.button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                open(true);
                eventListener.showAndHideButtons(true);
                return false;
            }
        });

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                checkable.set(position, isChecked);
            }
        });
    }

    public void deleteItems(final int position) {

        db.deleteTTN(String.valueOf(data.get(position).getId()));

        db.deleteFooter(String.valueOf(data.get(position).getId()));

        db.deleteALC(String.valueOf(data.get(position).getId()));

        data.remove(position);

        notifyItemRemoved(position);

        notifyItemRangeChanged(position, data.size());

        notifyItemRangeChanged(0, data.size());
    }

    public ArrayList<Boolean> getCheckable() {

        return checkable;

    }

    public boolean getSelected() {
        return selected;
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


    public void addNewTTM(TTM ttm) {
        data.add(ttm);
        checkable.add(false);
        notifyDataSetChanged();
    }

}
