package testvladkuz.buhsoftttm.adapter.holders;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.TextView;

import testvladkuz.buhsoftttm.R;

public class TTMViewHolder extends RecyclerView.ViewHolder {

    public LinearLayout button;
    public TextView title, shortName, date;
    public CheckBox checkBox;
    public CardView item;

    public TTMViewHolder(View itemView) {
        super(itemView);

        item = itemView.findViewById(R.id.item);

        button = itemView.findViewById(R.id.button);

        title = itemView.findViewById(R.id.title);
        shortName = itemView.findViewById(R.id.shortName);
        date = itemView.findViewById(R.id.date);
        checkBox = itemView.findViewById(R.id.checkbox);
    }

}
