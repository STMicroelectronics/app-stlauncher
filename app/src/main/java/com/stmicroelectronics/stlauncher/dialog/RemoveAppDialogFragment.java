package com.stmicroelectronics.stlauncher.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class RemoveAppDialogFragment extends DialogFragment {

    public interface RemoveAppDialogListener {
        void onDialogPositiveRemoveApp(String name);
        void onDialogNegativeRemoveApp(String name);
    }

    private final String mName;
    private final RemoveAppDialogListener mListener;
    private Context mContext;

    public RemoveAppDialogFragment(String name, RemoveAppDialogListener listener) {
        super();
        mName = name;
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
        builder.setMessage("Do you want to remove the application " + mName + " ?");
        builder.setPositiveButton("Yes", (dialog, which) -> mListener.onDialogPositiveRemoveApp(mName));
        builder.setNegativeButton("No", (dialog, which) -> mListener.onDialogNegativeRemoveApp(mName));
        return builder.create();
    }
}
