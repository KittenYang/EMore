package com.caij.emore.widget.weibo.html;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caij.emore.R;
import com.caij.emore.bean.Article;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.widget.RatioImageView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/9/3.
 */
public class ArticleHeadView extends RelativeLayout {

    @BindView(R.id.iv_avatar)
    ImageView ivAvatar;
    @BindView(R.id.tv_user_name)
    TextView tvUserName;
    @BindView(R.id.tv_article_time)
    TextView tvArticleTime;
    @BindView(R.id.tv_article_view_count)
    TextView tvArticleViewCount;
    @BindView(R.id.iv_article)
    RatioImageView ivArticle;
    @BindView(R.id.tv_article_title)
    TextView tvArticleTitle;

    public ArticleHeadView(Context context) {
        super(context);
        init();
    }

    public ArticleHeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ArticleHeadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ArticleHeadView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_article_head, this);
        ButterKnife.bind(this);
    }

    public void setHtml(Article article) {
        Document docu = Jsoup.parse(article.getData().getArticle());
        Elements elements = docu.getElementsByClass("face");
        Element faceElement = elements.get(0);
        Element userImgElement = faceElement.getElementsByTag("img").get(0);
        String userImgUrl = userImgElement.attr("src");
        ImageLoader.ImageConfig ivAvatarImageConfig = new ImageLoader.ImageConfigBuild()
                .setScaleType(ImageLoader.ScaleType.CENTER_CROP)
                .setCircle(true)
                .build();
        ImageLoader.loadUrl(getContext(), ivAvatar, userImgUrl, R.drawable.circle_image_placeholder, ivAvatarImageConfig);
        tvUserName.setText(article.getData().getConfig().getAuthor());

        tvArticleTitle.setText(article.getData().getTitle());

        Element timeElement = docu.getElementsByClass("time").get(0);
        String time = timeElement.text();
        tvArticleTime.setText(time);

        tvArticleViewCount.setText("阅读 " + article.getData().getConfig().getRead_count());

        ImageLoader.ImageConfig ivArticleImageConfig = new ImageLoader.ImageConfigBuild()
                .setScaleType(ImageLoader.ScaleType.CENTER_CROP)
                .build();
        ImageLoader.loadUrl(getContext(), ivArticle, article.getData().getConfig().getImage(), R.drawable.weibo_image_placeholder, ivArticleImageConfig);
    }
}
