package com.example.pethome.storeapp.ui.animals.image;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import com.example.pethome.storeapp.R;
public class AnimalImagePickerDialogFragment extends DialogFragment
        implements View.OnClickListener {
    private static final String LOG_TAG = AnimalImagePickerDialogFragment.class.getSimpleName();
    private static final String DIALOG_FRAGMENT_TAG = LOG_TAG;
    private ImagePickerOptionListener mImagePickerOptionListener;
    public static AnimalImagePickerDialogFragment newInstance() {
        return new AnimalImagePickerDialogFragment();
    }
    public static void showDialog(FragmentManager fragmentManager) {
        AnimalImagePickerDialogFragment imagePickerDialogFragment
                = (AnimalImagePickerDialogFragment) fragmentManager
                .findFragmentByTag(DIALOG_FRAGMENT_TAG);
        if (imagePickerDialogFragment == null) {
            imagePickerDialogFragment = AnimalImagePickerDialogFragment.newInstance();
            imagePickerDialogFragment.show(fragmentManager, DIALOG_FRAGMENT_TAG);
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mImagePickerOptionListener = (ImagePickerOptionListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getParentFragment() + " must implement ImagePickerOptionListener");
        }
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireActivity());
        View rootView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_animal_image_picker, null);
        rootView.findViewById(R.id.view_picker_camera_overlay).setOnClickListener(this);
        rootView.findViewById(R.id.view_picker_gallery_overlay).setOnClickListener(this);
        rootView.findViewById(R.id.btn_picker_close).setOnClickListener(this);
        dialogBuilder.setView(rootView);
        return dialogBuilder.create();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.view_picker_camera_overlay:
                mImagePickerOptionListener.onTakeNewPhoto();
                dismiss();
                break;
            case R.id.view_picker_gallery_overlay:
                mImagePickerOptionListener.onPickPhotoFromGallery();
                dismiss();
                break;
            case R.id.btn_picker_close:
                dismiss();
                break;
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mImagePickerOptionListener = null;
    }
    interface ImagePickerOptionListener {
        void onTakeNewPhoto();
        void onPickPhotoFromGallery();
    }
}