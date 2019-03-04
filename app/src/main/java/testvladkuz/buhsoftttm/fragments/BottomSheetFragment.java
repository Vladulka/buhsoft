package testvladkuz.buhsoftttm.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import testvladkuz.buhsoftttm.R;
import testvladkuz.buhsoftttm.ScannerActivity;
import testvladkuz.buhsoftttm.adapter.ItemsDialogAdapter;
import testvladkuz.buhsoftttm.classes.ALC;
import testvladkuz.buhsoftttm.classes.Items;
import testvladkuz.buhsoftttm.sqldatabase.DatabaseHandler;

public class BottomSheetFragment extends BottomSheetDialogFragment implements ItemsDialogAdapter.ClickListener {

    onEventListenerFragment eventListener;
    String idString, codeString, titleString, subtitleString, dateString;
    DatabaseHandler db;
    ArrayList<Items> arrayList = new ArrayList();
    public interface onEventListenerFragment {
        void onCheckedElement(int position);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            eventListener = (onEventListenerFragment) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }}

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        View v = View.inflate(getContext(), R.layout.dialog_docs, null);
        dialog.setContentView(v);
        dialog.setCanceledOnTouchOutside(false);

        db = new DatabaseHandler(getActivity());

        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) ((View) v.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = layoutParams.getBehavior();
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
            ((View) v.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        }

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            idString = bundle.getString("docid");
            codeString = bundle.getString("code");
            titleString = bundle.getString("title");
            subtitleString = bundle.getString("shortname");
            dateString = bundle.getString("date");
        }

        RecyclerView docs = v.findViewById(R.id.docs);
        docs.setLayoutManager(new LinearLayoutManager(getActivity()));

        Button current = dialog.findViewById(R.id.current);
        current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ScannerActivity.class);
                intent.putExtra("type", "-1");
                intent.putExtra("title", titleString);
                intent.putExtra("shortname", subtitleString);
                intent.putExtra("date", dateString);
                intent.putExtra("docid", idString);
                intent.putExtra("code", codeString);
                startActivity(intent);
            }
        });

        arrayList = db.getAllItems(idString);

        ItemsDialogAdapter dialogAdapter = new ItemsDialogAdapter(getActivity(), arrayList , idString, codeString);
        dialogAdapter.setClickListener(this);
        docs.setAdapter(dialogAdapter);

    }

    @Override
    public void onClick(View view, int position) {
        eventListener.onCheckedElement(position);
        dismiss();
        db.addNewALC(new ALC(idString, String.valueOf(arrayList.get(position).getId()),  codeString,"1"));
        db.updateItemStatus(String.valueOf(arrayList.get(position).getId()));
    }
}
