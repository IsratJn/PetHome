package com.example.pethome.storeapp.ui.common;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.example.pethome.storeapp.R;
import com.example.pethome.storeapp.utils.OrientationUtility;
public class ProgressDialogFragment extends DialogFragment {
    private static final String DIALOG_FRAGMENT_TAG = ProgressDialogFragment.class.getSimpleName();
    private static final String ARGUMENT_PROGRESS_STATUS = "argument.ProgressStatus";
    private static final String PROGRESS_STATUS_EMPTY_DEFAULT = "";
    public static ProgressDialogFragment newInstance(String statusText) {
        ProgressDialogFragment progressDialogFragment = new ProgressDialogFragment();
        Bundle args = new Bundle(1);
        args.putString(ARGUMENT_PROGRESS_STATUS, statusText);
        progressDialogFragment.setArguments(args);
        return progressDialogFragment;
    }
    public static void showDialog(FragmentManager fragmentManager, String statusText) {
        ProgressDialogFragment progressDialogFragment
                = (ProgressDialogFragment) fragmentManager.findFragmentByTag(DIALOG_FRAGMENT_TAG);
        if (progressDialogFragment == null) {
            progressDialogFragment = ProgressDialogFragment.newInstance(statusText);
            progressDialogFragment.showNow(fragmentManager, DIALOG_FRAGMENT_TAG);
        }
        OrientationUtility.lockCurrentScreenOrientation(progressDialogFragment.requireActivity());
    }
    public static void dismissDialog(FragmentManager fragmentManager) {
        ProgressDialogFragment progressDialogFragment
                = (ProgressDialogFragment) fragmentManager.findFragmentByTag(DIALOG_FRAGMENT_TAG);
        if (progressDialogFragment != null && progressDialogFragment.getActivity() != null) {
            OrientationUtility.unlockScreenOrientation(progressDialogFragment.requireActivity());
            progressDialogFragment.dismiss();
        }
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireActivity());
        View rootView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_progress_circle, null);
        Bundle arguments = getArguments();
        String progressStatus = PROGRESS_STATUS_EMPTY_DEFAULT;
        if (arguments != null) {
            progressStatus = arguments.getString(ARGUMENT_PROGRESS_STATUS, PROGRESS_STATUS_EMPTY_DEFAULT);
        }
        TextView textViewProgressStatus = rootView.findViewById(R.id.text_progress_status);
        if (progressStatus.equals(PROGRESS_STATUS_EMPTY_DEFAULT)) {
            textViewProgressStatus.setVisibility(View.GONE);
        } else {
            textViewProgressStatus.setText(progressStatus);
        }
        dialogBuilder.setView(rootView);
        return dialogBuilder.create();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Dialog currentDialog = getDialog();
        Window window = currentDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
        setCancelable(false);
    }
}
