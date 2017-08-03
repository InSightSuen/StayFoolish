package com.insightsuen.stayfoolish.ui.image;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.insightsuen.stayfoolish.R;
import com.insightsuen.stayfoolish.model.ImageInfo;

import java.util.List;

/**
 * Gallery adapter
 */
public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<ImageInfo> mImageUris;

    public GalleryAdapter(List<ImageInfo> imageUris) {
        mImageUris = imageUris;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gallery_image, parent, false);
        return new ImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ImageViewHolder) {
            ((ImageViewHolder) holder).bind(mImageUris.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mImageUris != null ? mImageUris.size() : 0;
    }

    private static class ImageViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivImage;
        private TextView tvName;
        private TextView tvDate;

        private ImageViewHolder(View itemView) {
            super(itemView);
            ivImage = (ImageView) itemView.findViewById(R.id.iv_image);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
        }

        private void bind(ImageInfo imageInfo) {
            tvName.setText(imageInfo.getName());
            tvDate.setText(imageInfo.getDateTokenString());
        }
    }
}
