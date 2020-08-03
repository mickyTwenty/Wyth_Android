package com.seatus.BaseClasses;

import android.app.Activity;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImages;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.seatus.R;
import com.seatus.Utils.permissionutils.PermissionResult;
import com.seatus.Utils.permissionutils.PermissionUtils;
import com.seatus.Views.TitleBar;


/**
 * Created by rohail on 26-Aug-16.
 */
public abstract class ImageChooserFragment extends BaseFragment implements ImageChooserListener {


    private ImageChooserManager imageChooserManager;
    private int chooserType;
    private String mediaPath;


    @Override
    protected void getTitleBar(TitleBar titleBar) {

    }

    @Override
    public void inits() {

    }

    @Override
    public void setEvents() {

    }

    public void pickImage() {

        new AlertDialog.Builder(getContext(), R.style.CustomDialog).setTitle("Select Image").setMessage("Please select image source")
                .setPositiveButton("Camera", (dialogInterface, i) ->
                        {
                            askCompactPermissions(new String[]{PermissionUtils.Manifest_CAMERA, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE}, new PermissionResult() {
                                @Override
                                public void permissionGranted() {
                                    takePicture();
                                }

                                @Override
                                public void permissionDenied() {
                                }

                                @Override
                                public void permissionForeverDenied() {
                                    openSettingsApp(getContext());
                                }
                            });
                        }
                ).setNegativeButton("Gallery", (dialogInterface, i) ->
                {
                    askCompactPermissions(new String[]{PermissionUtils.Manifest_CAMERA, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE}, new PermissionResult() {
                        @Override
                        public void permissionGranted() {
                            chooseImage();
                        }

                        @Override
                        public void permissionDenied() {
                        }

                        @Override
                        public void permissionForeverDenied() {
                            openSettingsApp(getContext());
                        }
                    });
                }
        ).show();
    }

    public void pickAndRemoveImage() {

        new AlertDialog.Builder(getContext(), R.style.CustomDialog).setTitle("Select Image").setMessage("Please select image source")
                .setPositiveButton("Camera", (dialogInterface, i) ->
                        {
                            askCompactPermissions(new String[]{PermissionUtils.Manifest_CAMERA, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE}, new PermissionResult() {
                                @Override
                                public void permissionGranted() {
                                    takePicture();
                                }

                                @Override
                                public void permissionDenied() {
                                }

                                @Override
                                public void permissionForeverDenied() {
                                    openSettingsApp(getContext());
                                }
                            });
                        }
                ).setNegativeButton("Gallery", (dialogInterface, i) ->
                {
                    askCompactPermissions(new String[]{PermissionUtils.Manifest_CAMERA, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE}, new PermissionResult() {
                        @Override
                        public void permissionGranted() {
                            chooseImage();
                        }

                        @Override
                        public void permissionDenied() {
                        }

                        @Override
                        public void permissionForeverDenied() {
                            openSettingsApp(getContext());
                        }
                    });
                }
        )
                .setNeutralButton("Remove", (dialog, which) -> {
                    removeImage();
                }
                ).show();
    }


    private void takePicture() {

        try {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }

        chooserType = ChooserType.REQUEST_CAPTURE_PICTURE;
        imageChooserManager = new ImageChooserManager(this,
                ChooserType.REQUEST_CAPTURE_PICTURE, true);
        imageChooserManager.setImageChooserListener(this);
        try {
            mediaPath = imageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void chooseImage() {

        chooserType = ChooserType.REQUEST_PICK_PICTURE;
        imageChooserManager = new ImageChooserManager(this,
                ChooserType.REQUEST_PICK_PICTURE, true);
        imageChooserManager.setImageChooserListener(this);
        try {
            mediaPath = imageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(getClass().getName(), requestCode + "");
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (imageChooserManager == null) {
                imageChooserManager = new ImageChooserManager(this, requestCode, true);
                imageChooserManager.setImageChooserListener(this);
                imageChooserManager.reinitialize(mediaPath);
            }
            imageChooserManager.submit(requestCode, data);
        }
    }


    @Override
    public void onError(String s) {

    }

    @Override
    public void onImagesChosen(ChosenImages chosenImages) {

    }

    public abstract void removeImage();
}
