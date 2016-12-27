package com.caij.emore.ui.adapter.delegate;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.caij.emore.EMApplication;
import com.caij.emore.R;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.bean.ImageInfo;
import com.caij.emore.database.bean.DirectMessage;
import com.caij.emore.utils.DateUtil;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.utils.glide.MaskTransformation;
import com.caij.emore.widget.recyclerview.OnItemPartViewClickListener;
import com.caij.rvadapter.BaseViewHolder;

/**
 * Created by Ca1j on 2016/12/23.
 */

public interface MessageDelegateProvider {

    public static final int TYPE_SELT_TEXT = 1;
    public static final int TYPE_OTHER_TEXT = 2;
    public static final int TYPE_SELT_IMAGE = 3;
    public static final int TYPE_OTHER_IMAGE = 4;

    public static abstract class BaseMessageDelegate extends BaseItemViewDelegate<DirectMessage> {

        private final ImageLoader.ImageConfig mAvatarImageConfig;

        public BaseMessageDelegate(OnItemPartViewClickListener onClickListener) {
            super(onClickListener);
            mAvatarImageConfig = new ImageLoader.ImageConfigBuild().
                    setScaleType(ImageLoader.ScaleType.CENTER_CROP)
                    .setCircle(true)
                    .build();
        }

        @Override
        public void convert(BaseViewHolder baseViewHolder, DirectMessage directMessage, int position) {
            Context context = baseViewHolder.getConvertView().getContext();
            ImageView avatarImageView = baseViewHolder.getView(R.id.iv_avatar);
            ImageLoader.loadUrl(context, avatarImageView, directMessage.getSender().getAvatar_large(),
                    R.drawable.circle_image_placeholder, mAvatarImageConfig);
        }

        @Override
        public void onCreateViewHolder(final BaseViewHolder baseViewHolder) {
            baseViewHolder.setOnClickListener(R.id.iv_avatar, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemPartViewClickListener.onClick(v, baseViewHolder.getAdapterPosition());
                }
            });
        }
    }

    public static abstract class OutMessageDelegate extends BaseMessageDelegate {

        public OutMessageDelegate(OnItemPartViewClickListener onClickListener) {
            super(onClickListener);
        }

        @Override
        public void convert(BaseViewHolder baseViewHolder, DirectMessage message, int position) {
            super.convert(baseViewHolder, message, position);

            if (message.getLocal_status() == DirectMessage.STATUS_SEND) {
                baseViewHolder.setVisible(R.id.pb_loading, true);
                baseViewHolder.setVisible(R.id.iv_fail, false);
            }else if (message.getLocal_status() == DirectMessage.STATUS_SUCCESS
                    || message.getLocal_status() == DirectMessage.STATUS_SERVER) {
                baseViewHolder.setVisible(R.id.pb_loading, false);
                baseViewHolder.setVisible(R.id.iv_fail, false);
            }else if (message.getLocal_status() == DirectMessage.STATUS_FAIL) {
                baseViewHolder.setVisible(R.id.pb_loading, false);
                baseViewHolder.setVisible(R.id.iv_fail, true);
            }
        }

        @Override
        public void onCreateViewHolder(final BaseViewHolder baseViewHolder) {
            super.onCreateViewHolder(baseViewHolder);
            baseViewHolder.setOnClickListener(R.id.iv_fail, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemPartViewClickListener.onClick(v, baseViewHolder.getAdapterPosition());
                }
            });
        }
    }

    public static abstract class ReceiveMessageDelegate extends BaseMessageDelegate {

        public ReceiveMessageDelegate(OnItemPartViewClickListener onClickListener) {
            super(onClickListener);
        }

        @Override
        public void convert(BaseViewHolder baseViewHolder, DirectMessage directMessage, int position) {
            super.convert(baseViewHolder, directMessage, position);
        }
    }

    public static class ReceiveTextMessageDelegate extends ReceiveMessageDelegate {

        public ReceiveTextMessageDelegate(OnItemPartViewClickListener onClickListener) {
            super(onClickListener);
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_chat_other_send_message_text;
        }

        @Override
        public void convert(BaseViewHolder baseViewHolder, DirectMessage directMessage, int position) {
            super.convert(baseViewHolder, directMessage, position);
            TextView tvMessage = baseViewHolder.getView(R.id.tv_message);
            tvMessage.setText(directMessage.getTextContentSpannable());
            baseViewHolder.setText(R.id.tv_name, directMessage.getSender_screen_name());
        }

        @Override
        public boolean isForViewType(DirectMessage directMessage, int i) {
            return Type.getType(directMessage) == TYPE_OTHER_TEXT;
        }
    }

    public static class OutTextMessageDelegate extends OutMessageDelegate {

        public OutTextMessageDelegate(OnItemPartViewClickListener onClickListener) {
            super(onClickListener);
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_chat_self_send_message;
        }

        @Override
        public void convert(BaseViewHolder baseViewHolder, DirectMessage directMessage, int position) {
            super.convert(baseViewHolder, directMessage, position);
            TextView tvMessage = baseViewHolder.getView(R.id.tv_message);
            tvMessage.setText(directMessage.getTextContentSpannable());
        }

        @Override
        public boolean isForViewType(DirectMessage directMessage, int i) {
            return Type.getType(directMessage) == TYPE_SELT_TEXT;
        }
    }

    public static class ReceiveImageMessageDelegate extends ReceiveMessageDelegate {

        private final MaskTransformation mOtherTransformation;

        public ReceiveImageMessageDelegate(OnItemPartViewClickListener onClickListener) {
            super(onClickListener);
            mOtherTransformation = new MaskTransformation(EMApplication.getInstance(), R.drawable.messages_left_bubble);
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_chat_other_send_message_image;
        }

        @Override
        public void convert(BaseViewHolder baseViewHolder, DirectMessage directMessage, int position) {
            super.convert(baseViewHolder, directMessage, position);
            Context context = baseViewHolder.getConvertView().getContext();

            ImageInfo imageInfo = directMessage.getImageInfo();
            ImageView imageView = baseViewHolder.getView(R.id.iv_image);
            ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
            layoutParams.width = imageInfo.getWidth();
            layoutParams.height = imageInfo.getHeight();
            imageView.setLayoutParams(layoutParams);
            ImageLoader.ImageConfig imageConfig = new ImageLoader.ImageConfigBuild().
                    setScaleType(ImageLoader.ScaleType.CENTER_CROP)
                    .setWidthAndHeight(layoutParams.width, layoutParams.height)
                    .setTransformation(mOtherTransformation)
                    .build();
            ImageLoader.loadUrl(context, imageView, imageInfo.getUrl(),
                    R.drawable.messages_left_bubble, imageConfig);

            baseViewHolder.setText(R.id.tv_name, directMessage.getSender_screen_name());
        }

        @Override
        public void onCreateViewHolder(final BaseViewHolder baseViewHolder) {
            super.onCreateViewHolder(baseViewHolder);
            baseViewHolder.setOnClickListener(R.id.iv_image, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemPartViewClickListener.onClick(v, baseViewHolder.getAdapterPosition());
                }
            });
        }

        @Override
        public boolean isForViewType(DirectMessage directMessage, int i) {
            return Type.getType(directMessage) == TYPE_OTHER_IMAGE;
        }
    }

    public static class OutImageMessageDelegate extends OutMessageDelegate {

        private final MaskTransformation mSelfTransformation;

        public OutImageMessageDelegate(OnItemPartViewClickListener onClickListener) {
            super(onClickListener);
            mSelfTransformation = new MaskTransformation(EMApplication.getInstance(), R.drawable.messages_right_bubble);
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_chat_self_send_message_image;
        }

        @Override
        public void convert(BaseViewHolder baseViewHolder, DirectMessage directMessage, int position) {
            super.convert(baseViewHolder, directMessage, position);
            Context context = baseViewHolder.getConvertView().getContext();

            ImageInfo imageInfo = directMessage.getImageInfo();
            ImageView imageView = baseViewHolder.getView(R.id.iv_image);

            ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
            layoutParams.width = imageInfo.getWidth();
            layoutParams.height = imageInfo.getHeight();
            imageView.setLayoutParams(layoutParams);
            ImageLoader.ImageConfig imageConfig = new ImageLoader.ImageConfigBuild().
                    setScaleType(ImageLoader.ScaleType.CENTER_CROP)
                    .setWidthAndHeight(layoutParams.width, layoutParams.height)
                    .setTransformation(mSelfTransformation)
                    .build();
            ImageLoader.loadUrl(context, imageView, imageInfo.getUrl(),
                    R.drawable.messages_right_bubble, imageConfig);
        }

        @Override
        public void onCreateViewHolder(final BaseViewHolder baseViewHolder) {
            super.onCreateViewHolder(baseViewHolder);
            baseViewHolder.setOnClickListener(R.id.iv_image, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemPartViewClickListener.onClick(v, baseViewHolder.getAdapterPosition());
                }
            });
        }

        @Override
        public boolean isForViewType(DirectMessage directMessage, int i) {
            return Type.getType(directMessage) == TYPE_SELT_IMAGE;
        }
    }


    public static class Type {

        public static int getType(DirectMessage directMessage) {
            if (directMessage.getSender_id() == Long.parseLong(UserPrefs.get(EMApplication.getInstance()).getToken().getUid())) {
                if (directMessage.getAtt_ids() != null && directMessage.getAtt_ids().size() > 0 && directMessage.getImageInfo() != null) {
                    return TYPE_SELT_IMAGE;
                }else {
                    return TYPE_SELT_TEXT;
                }
            } else {
                if (directMessage.getAtt_ids() != null && directMessage.getAtt_ids().size() > 0 && directMessage.getImageInfo() != null) {
                    return TYPE_OTHER_IMAGE;
                }else {
                    return TYPE_OTHER_TEXT;
                }
            }
        }
    }
}
