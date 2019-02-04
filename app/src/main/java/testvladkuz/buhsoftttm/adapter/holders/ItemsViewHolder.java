package testvladkuz.buhsoftttm.adapter.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import testvladkuz.buhsoftttm.R;

public class ItemsViewHolder extends RecyclerView.ViewHolder {

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
        button = itemView.findViewById(R.id.button);
    }

}
