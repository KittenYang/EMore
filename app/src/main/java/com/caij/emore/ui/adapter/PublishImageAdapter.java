//package com.caij.emore.ui.adapter;
//
//import android.content.Context;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//
//import com.caij.emore.R;
//import com.caij.emore.utils.ImageLoader;
//import com.caij.emore.widget.RatioImageView;
//
//import java.util.List;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//
///**
// * Created by Caij on 2016/6/24.
// */
//public class PublishImageAdapter extends BaseAdapter<String, PublishImageAdapter.ImageViewHolder> {
//
//
//    public PublishImageAdapter(Context context) {
//        super(context);
//    }
//
//    public PublishImageAdapter(Context context, List<String> entities) {
//        super(context, entities);
//    }
//
//    @Override
//    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = mInflater.inflate(R.layout.item_publish_image, parent, false);
//        return new ImageViewHolder(view, mOnItemClickListener, this);
//    }
//
//    @Override
//    public void onBindViewHolder(ImageViewHolder holder, int position) {
//        ImageLoader.load(mContext, holder.sivImage, getItem(position), R.drawable.weibo_image_placeholder);
//        holder.ivDelete.setTag(getItem(position));
//    }
//
//    public static class ImageViewHolder extends BaseViewHolder {
//
//        @BindView(R.id.siv_image)
//        RatioImageView sivImage;
//        @BindView(R.id.iv_delete)
//        ImageView ivDelete;
//
//        public ImageViewHolder(View itemView, RecyclerViewOnItemClickListener onItemClickListener, final PublishImageAdapter adapter) {
//            super(itemView, onItemClickListener);
//            ButterKnife.bind(this, itemView);
//            ivDelete.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String path = (String) v.getTag();
//                    adapter.removeEntity(path);
//                    adapter.notifyItemRemoved(getLayoutPosition());
//                }
//            });
//        }
//    }
//
//}
