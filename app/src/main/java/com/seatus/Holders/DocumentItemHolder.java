package com.seatus.Holders;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.seatus.BaseClasses.FragmentHandlingActivity;
import com.seatus.Fragments.EditProfileFragment;
import com.seatus.Fragments.UpgradeAccountFragment;
import com.seatus.Models.DocumentItem;
import com.seatus.R;
import com.seatus.ViewModels.ActivityViewModel;
import com.skocken.efficientadapter.lib.viewholder.EfficientViewHolder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by saqib on 10/25/2017.
 */

public class DocumentItemHolder extends EfficientViewHolder<DocumentItem> {

    public DocumentItemHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void updateView(Context context, DocumentItem object) {


    }
}
