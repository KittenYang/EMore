package com.caij.emore.widget.weibo.detail;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.bumptech.glide.request.target.Target;
import com.caij.emore.api.ex.SchedulerTransformer;
import com.caij.emore.bean.ImageInfo;
import com.caij.emore.bean.StatusImageInfo;
import com.caij.emore.utils.ExecutorServicePool;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.utils.ImageUtil;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.NavigationUtil;
import com.caij.emore.utils.rxjava.RxUtil;
import com.caij.emore.utils.rxjava.SubscriberAdapter;
import com.caij.emore.widget.weibo.ImageInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import rx.Subscription;

/**
 * Created by Caij on 2016/6/13.
 * 这个是微博详情页的大图，需要全部加载，之前的控件ImageView展示不了，需要用WebView
 */
public class StatusDetailItemImageViewGroupOf1BigImage extends ViewGroup implements ImageInterface {

    private StatusImageInfo mImageInfo;
    private Handler mMainHandler;
    private WebView mWebView;
    private Subscription mGetImageFileSubscription;

    public StatusDetailItemImageViewGroupOf1BigImage(Context context) {
        super(context);
        init(context);
    }

    public StatusDetailItemImageViewGroupOf1BigImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StatusDetailItemImageViewGroupOf1BigImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public StatusDetailItemImageViewGroupOf1BigImage(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(final Context context) {
        mMainHandler = new Handler();
        mWebView = new WebView(context);
        addView(mWebView);
        // // TODO: 2016/6/13 add progressbar

        mWebView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mImageInfo != null) {
                    ArrayList<ImageInfo> images = new ArrayList<ImageInfo>();
                    images.add(new ImageInfo(mImageInfo.getBmiddle().getUrl(), mImageInfo.getBmiddle().getWidth(),
                            mImageInfo.getBmiddle().getHeight(), getImageType(mImageInfo.getBmiddle().getType())));

                    ArrayList<ImageInfo> hdImages = new ArrayList<ImageInfo>();
                    StatusImageInfo.Image orignImage = mImageInfo.getOriginal();

                    if (orignImage != null) {
                        hdImages.add(new ImageInfo(orignImage.getUrl(), orignImage.getWidth(),
                                orignImage.getHeight(), getImageType(orignImage.getType())));
                    }else {
                        hdImages.add(null);
                    }

                    NavigationUtil.startImagePreActivity(context, v, images, hdImages, 0);
                }
            }
        });
    }

    protected String getImageType(String type) {
        if (ImageUtil.ImageType.GIF.toLowerCase().contains(type)
                || ImageUtil.ImageType.GIF.toUpperCase().contains(type)) {
            return ImageUtil.ImageType.GIF;
        }else {
            return ImageUtil.ImageType.PNG;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int availableWidth = width - getPaddingLeft() - getPaddingRight();
        int height = 0;
        int imageHeight = 0;
        for (int i = 0; i < getChildCount(); i ++) {
            View childView = getChildAt(i);
            if (mImageInfo != null) {
                imageHeight = (int) (availableWidth * mImageInfo.getBmiddle().getHeight() * 1.0f / mImageInfo.getBmiddle().getWidth());
                childView.measure(MeasureSpec.makeMeasureSpec(availableWidth, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(imageHeight, MeasureSpec.EXACTLY));
                LogUtil.d(this, "mPicUrl width =  %s   height = %s", mImageInfo.getBmiddle().getWidth(),
                        mImageInfo.getBmiddle().getHeight());
            }
        }
        height = imageHeight;
        setMeasuredDimension(width, height);
        LogUtil.d(this, "onMeasure width =  %s   height = %s", width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i ++) {
            View childView = getChildAt(i);
            childView.layout(getPaddingLeft(), getPaddingTop(),
                    getPaddingLeft() + childView.getMeasuredWidth(),
                    getPaddingTop() + childView.getMeasuredHeight());
        }
    }

    private void disPlayPics(final StatusImageInfo imageInfo) {
        mGetImageFileSubscription = RxUtil.createDataObservable(new RxUtil.Provider<File>() {
                @Override
                public File getData() throws Exception {
                    return ImageLoader.getFile(getContext(), imageInfo.getBmiddle().getUrl(),
                            Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                }
            }).compose(SchedulerTransformer.<File>create())
            .subscribe(new SubscriberAdapter<File>() {
                @Override
                public void onNext(File file) {
                    readLargePicture(mWebView, file);
                }
            });
    }

    private void readLargePicture(final WebView large, File file) {
        // TODO: 2016/6/14 尺寸有点不对 后面需要解决
        large.getSettings().setJavaScriptEnabled(true);
        large.getSettings().setUseWideViewPort(true);
        large.getSettings().setLoadWithOverviewMode(true);
        large.getSettings().setBuiltInZoomControls(true);
        large.getSettings().setDisplayZoomControls(false);

        large.setVerticalScrollBarEnabled(false);
        large.setHorizontalScrollBarEnabled(false);

        String str1 = "file://" + file.getAbsolutePath();
        String str2 = "<html>\n<head>\n     <style>\n          html,body{background:#e1e1e1;margin:0;padding:0;}          *{-webkit-tap-highlight-color:rgba(0, 0, 0, 0);}\n     </style>\n     <script type=\"text/javascript\">\n     var imgUrl = \""
                + str1
                + "\";"
                + "     var objImage = new Image();\n"
                + "     var realWidth = 0;\n"
                + "     var realHeight = 0;\n"
                + "\n"
                + "     function onLoad() {\n"
                + "          objImage.onload = function() {\n"
                + "               realWidth = objImage.width;\n"
                + "               realHeight = objImage.height;\n"
                + "\n"
                + "               document.gagImg.src = imgUrl;\n"
                + "               onResize();\n"
                + "          }\n"
                + "          objImage.src = imgUrl;\n"
                + "     }\n"
                + "\n"
                + "     function imgOnClick() {\n"
                + "			window.picturejs.onClick();"
                + "     }\n"
                + "     function onResize() {\n"
                + "          var scale = 1;\n"
                + "          var newWidth = document.gagImg.width;\n"
                + "          if (realWidth > newWidth) {\n"
                + "               scale = realWidth / newWidth;\n"
                + "          } else {\n"
                + "               scale = newWidth / realWidth;\n"
                + "          }\n"
                + "\n"
                + "          hiddenHeight = Math.ceil(30 * scale);\n"
                + "          document.getElementById('hiddenBar').style.height = hiddenHeight + \"px\";\n"
                + "          document.getElementById('hiddenBar').style.marginTop = -hiddenHeight + \"px\";\n"
                + "     }\n"
                + "     </script>\n"
                + "</head>\n"
                + "<body onload=\"onLoad()\" onresize=\"onResize()\" onclick=\"Android.toggleOverlayDisplay();\">\n"
                + "     <table style=\"width: 100%;height:100%;\">\n"
                + "          <tr style=\"width: 100%;\">\n"
                + "               <td valign=\"middle\" align=\"center\" style=\"width: 100%;\">\n"
                + "                    <div style=\"display:block\">\n"
                + "                         <img name=\"gagImg\" src=\"\" width=\"100%\" style=\"\" onclick=\"imgOnClick()\" />\n"
                + "                    </div>\n"
//                + "                    <div id=\"hiddenBar\" style=\"position:absolute; width: 100%; background: #000000;\"></div>\n"
                + "               </td>\n" + "          </tr>\n" + "     </table>\n" + "</body>\n" + "</html>";
        large.loadDataWithBaseURL("file:///android_asset/", str2, "text/html", "utf-8", null);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        mWebView.removeAllViews();
        mWebView.destroy();

        if (mGetImageFileSubscription != null) {
            mGetImageFileSubscription.unsubscribe();
        }
    }

    @Override
    public void setPics(List<String> pic_ids, LinkedHashMap<String, StatusImageInfo> imageInfoLinkedHashMap) {
        this.mImageInfo = imageInfoLinkedHashMap.get(pic_ids.get(0));
        requestLayout();
        //因为请求重新绘制requestLayout是通过主线程handler发送消息， 这个再通过handler发送消息展示图片就会在绘制以后
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                disPlayPics(mImageInfo);
            }
        });
    }
}
