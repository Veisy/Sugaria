package com.vyy.sekerimremake.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vyy.sekerimremake.databinding.CatalogListItemBinding;
import com.vyy.sekerimremake.models.CatalogModel;

public class CatalogListAdapter extends RecyclerView.Adapter<CatalogListAdapter.CatalogListHolder> {

    private final CatalogModel[] catalogItems;
    private final Context mContext;
    private final CatalogListHolder.OnCatalogClickListener mOnCatalogClickListener;

    public CatalogListAdapter(Context context, CatalogModel[] catalogItems,
                              CatalogListHolder.OnCatalogClickListener onCatalogClickListener) {
        this.mContext = context;
        this.catalogItems = catalogItems;
        this.mOnCatalogClickListener = onCatalogClickListener;
    }

    public static class CatalogListHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        CatalogListItemBinding binding;

        final OnCatalogClickListener onCatalogClickListener;

        public CatalogListHolder(@NonNull CatalogListItemBinding b,
                                 OnCatalogClickListener onCatalogClickListener) {
            super(b.getRoot());
            binding = b;
            this.onCatalogClickListener = onCatalogClickListener;
            binding.cartViewCatalogListItem.setOnClickListener(this);
        }

        public void setTransitionName(String imageId) {
            ViewCompat.setTransitionName(binding.cartViewCatalogListItem, imageId);
        }

        @Override
        public void onClick(View v) {
            onCatalogClickListener.onCatalogClick(getAdapterPosition(), binding.cartViewCatalogListItem);
        }

        public interface OnCatalogClickListener {
            void onCatalogClick(int position, View view);
        }
    }

    @NonNull
    @Override
    public CatalogListAdapter.CatalogListHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                   int viewType) {
        return new CatalogListHolder(CatalogListItemBinding.inflate(LayoutInflater.from(mContext),
                parent, false),
                mOnCatalogClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogListHolder holder, int position) {
        int imageId = catalogItems[position].getImage();
        holder.binding.textViewCatalogItem.setText(catalogItems[position].getTitle());
        holder.setTransitionName(String.valueOf(imageId));
        Glide.with(mContext).load(imageId).centerCrop().into(holder.binding.imageViewCatalogItem);
    }

    @Override
    public int getItemCount() {
        return catalogItems.length;
    }

}
