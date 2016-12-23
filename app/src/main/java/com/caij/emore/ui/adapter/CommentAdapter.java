//package com.caij.emore.ui.adapter;
//
//import android.content.Context;
//import android.text.Html;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.caij.emore.R;
//import com.caij.emore.bean.Comment;
//import com.caij.emore.utils.DateUtil;
//import com.caij.emore.utils.ImageLoader;
//import com.caij.emore.widget.FixClickableSpanBugTextView;
//import com.caij.rvadapter.BaseViewHolder;
//import com.caij.rvadapter.adapter.BaseAdapter;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//
///**
// * Created by Caij on 2016/6/16.
// */
//public class CommentAdapter extends BaseAdapter<Comment, CommentAdapter.CommentViewHolder> {
//
//    private final ImageLoader.ImageConfig mAvatarImageConfig;
//
//    public CommentAdapter(Context context) {
//        super(context);
//        mAvatarImageConfig = new ImageLoader.ImageConfigBuild().setCircle(true).build();
//    }
//
//    @Override
//    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        return new CommentViewHolder(mInflater.inflate(R.layout.item_weibo_comment, parent, false));
//    }
//
//    @Override
//    public void onBindViewHolder(CommentViewHolder holder, int position) {
//        Comment comment = getItem(position);
//        holder.txtContent.setText(comment.getTextSpannableString());
//
//        String createAt = DateUtil.convWeiboDate(holder.txtContent.getContext(), comment.getCreated_at());
//        String from = String.format("%s", Html.fromHtml(comment.getSource()));
//        String desc = String.format("%s %s", createAt, from);
//        holder.txtDesc.setText(desc);
//
//        holder.txtName.setText(comment.getUser().getName());
//
//
//        ImageLoader.loadUrl(mContext, holder.imgPhoto, comment.getUser().getAvatar_large(),
//                R.drawable.circle_image_placeholder, mAvatarImageConfig);
//    }
//
//    public static class CommentViewHolder extends BaseViewHolder {
//
//        @BindView(R.id.imgPhoto)
//        ImageView imgPhoto;
//        @BindView(R.id.txtName)
//        TextView txtName;
//        @BindView(R.id.txtContent)
//        FixClickableSpanBugTextView txtContent;
//        @BindView(R.id.txtDesc)
//        TextView txtDesc;
//
//        public CommentViewHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//        }
//    }
//
//}
