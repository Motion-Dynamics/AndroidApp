package org.kerlinmichel.motiondynamics.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

public class FileControlDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String fileName = (String)getArguments().get("file_name");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(fileName);
        return builder.create();
    }
} 