package com.caij.weiyo.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.caij.weiyo.R;
import com.caij.weiyo.bean.ImageFolder;
import com.caij.weiyo.utils.ImageLoader;
import com.caij.weiyo.view.recyclerview.RecyclerViewOnItemClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/6/23.
 */
public class FolderAdapter extends BaseAdapter<ImageFolder, FolderAdapter.FolderViewHolder> {

    private final ImageLoader.ImageConfig mImageConfig;

    public FolderAdapter(Context context) {
        super(context);
        mImageConfig = new ImageLoader.ImageConfigBuild().
                setScaleType(ImageLoader.ScaleType.CENTER_CROP).build();
    }

    @Override
    public FolderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_item_folder, parent, false);
        return new FolderViewHolder(view, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(FolderViewHolder holder, int position) {
        ImageFolder folder = getItem(position);
        String path = "file://" + folder.getImgPath();
        ImageLoader.load(context, holder.imageView, path, R.drawable.weibo_image_placeholder, mImageConfig);
        holder.tvFolderName.setText(folder.getName());
        holder.tvFolderImageCount.setText(folder.getCount() + context.getString(R.string.image_count));
        holder.ivSelect.setVisibility(folder.isSelected() ? View.VISIBLE : View.GONE);
    }

    public static class FolderViewHolder extends BaseViewHolder {

        @BindView(R.id.imageView)
        ImageView imageView;
        @BindView(R.id.tv_folder_name)
        TextView tvFolderName;
        @BindView(R.id.tv_folder_image_count)
        TextView tvFolderImageCount;
        @BindView(R.id.iv_select)
        ImageView ivSelect;

        public FolderViewHolder(View itemView, RecyclerViewOnItemClickListener onItemClickListener) {
            super(itemView, onItemClickListener);
            ButterKnife.bind(this, itemView);
        }
    }

}
