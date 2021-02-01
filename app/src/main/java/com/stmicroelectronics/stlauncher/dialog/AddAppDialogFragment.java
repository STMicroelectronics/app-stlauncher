package com.stmicroelectronics.stlauncher.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.List;

public class AddAppDialogFragment extends DialogFragment {

    public interface AddAppDialogListener {
        void onDialogPositiveAddApp(String name);
        void onDialogNegativeAddApp();
    }

    private final List<String> mNames;
    private String mName;
    private final AddAppDialogListener mListener;
    private Context mContext;

    public AddAppDialogFragment(List<String> names, AddAppDialogListener listener) {
        super();
        mNames = names;
        mListener = listener;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Select application from the following list");

        CharSequence[] sequence = mNames.toArray(new CharSequence[0]);
        builder.setSingleChoiceItems(sequence, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mName = mNames.get(which);
            }
        });

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onDialogPositiveAddApp(mName);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onDialogNegativeAddApp();
            }
        });
        return builder.create();
    }
}
