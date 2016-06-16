package com.caij.weiyo.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.text.util.Linkify;

import com.caij.weiyo.R;
import com.caij.weiyo.bean.Comment;
import com.caij.weiyo.bean.User;
import com.caij.weiyo.bean.Weibo;
import com.caij.weiyo.view.CenteredImageSpan;
import com.caij.weiyo.view.MyURLSpan;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Caij on 2016/6/8.
 */
public class SpannableStringUtil {

    private static Map<String, Integer> emotionMap = new HashMap<>();
    private static LruCache<String, Bitmap>  emotionLruCache;

    static {
//        emotionMap.put("[躁狂症]", R.mipmap.lxh_kuangzaozheng);
        emotionMap.put("[来]", R.mipmap.h_lai);
        emotionMap.put("[困]", R.mipmap.d_kun);
        emotionMap.put("[最右]", R.mipmap.d_zuiyou);
        emotionMap.put("[晕]", R.mipmap.d_yun);
//        emotionMap.put("[蛋糕]", R.mipmap.o_dangao);
        emotionMap.put("[偷乐]", R.mipmap.lxh_toule);
        emotionMap.put("[闭嘴]", R.mipmap.d_bizui);
//        emotionMap.put("[威武]", R.mipmap.f_v5);
        emotionMap.put("[互相膜拜]", R.mipmap.lxh_xianghumobai);
        emotionMap.put("[haha]", R.mipmap.h_haha);
//        emotionMap.put("[伤心]", R.mipmap.l_shangxin);
//        emotionMap.put("[鲜花]", R.mipmap.w_xianhua);
        emotionMap.put("[男孩儿]", R.mipmap.d_nanhaier);
//        emotionMap.put("[手机]", R.mipmap.o_shouji);
        emotionMap.put("[打哈气]", R.mipmap.d_dahaqi);
        emotionMap.put("[震惊]", R.mipmap.lxh_zhenjing);
        emotionMap.put("[爱你]", R.mipmap.d_aini);
//        emotionMap.put("[爱心传递]", R.mipmap.l_aixinchuandi);
        emotionMap.put("[害羞]", R.mipmap.d_haixiu);
//        emotionMap.put("[温暖帽子]", R.mipmap.o_wennuanmaozi);
//        emotionMap.put("[拳头]", R.mipmap.h_quantou);
//        emotionMap.put("[月亮]", R.mipmap.w_yueliang);
        emotionMap.put("[鼓掌]", R.mipmap.d_guzhang);
//        emotionMap.put("[雪人]", R.mipmap.w_xueren);
        emotionMap.put("[崩溃]", R.mipmap.lxh_bengkui);
        emotionMap.put("[兔子]", R.mipmap.d_tuzi);
//        emotionMap.put("[实习]", R.mipmap.o_shixi);
//        emotionMap.put("[照相机]", R.mipmap.o_zhaoxiangji);
//        emotionMap.put("[做鬼脸]", R.mipmap.d_zuoguilian);
//        emotionMap.put("[电影]", R.mipmap.o_dianying);
        emotionMap.put("[可怜]", R.mipmap.d_kelian);
        emotionMap.put("[有鸭梨]", R.mipmap.lxh_youyali);
        emotionMap.put("[黑线]", R.mipmap.d_heixian);
        emotionMap.put("[心]", R.mipmap.l_xin);
        emotionMap.put("[困死了]", R.mipmap.lxh_kunsile);
        emotionMap.put("[委屈]", R.mipmap.d_weiqu);
        emotionMap.put("[赞]", R.mipmap.h_zan);
//        emotionMap.put("[礼物]", R.mipmap.o_liwu);
        emotionMap.put("[不要]", R.mipmap.h_buyao);
//        emotionMap.put("[互粉]", R.mipmap.f_hufen);
        emotionMap.put("[转发]", R.mipmap.lxh_zhuanfa);
        emotionMap.put("[怒骂]", R.mipmap.d_numa);
//        emotionMap.put("[最差]", R.mipmap.h_zuicha);
        emotionMap.put("[太开心]", R.mipmap.d_taikaixin);
//        emotionMap.put("[书呆子]", R.mipmap.d_shudaizi);
//        emotionMap.put("[自行车]", R.mipmap.o_zixingche);
//        emotionMap.put("[帅]", R.mipmap.f_shuai);
        emotionMap.put("[疑问]", R.mipmap.d_yiwen);
        emotionMap.put("[酷]", R.mipmap.d_ku);
        emotionMap.put("[想一想]", R.mipmap.lxh_xiangyixiang);
        emotionMap.put("[ok]", R.mipmap.h_ok);
        emotionMap.put("[奥特曼]", R.mipmap.d_aoteman);
//        emotionMap.put("[围脖]", R.mipmap.o_weibo);
//        emotionMap.put("[干杯]", R.mipmap.o_ganbei);
        emotionMap.put("[杰克逊]", R.mipmap.lxh_jiekexun);
//        emotionMap.put("[萌]", R.mipmap.f_meng);
        emotionMap.put("[hold住]", R.mipmap.lxh_holdzhu);
//        emotionMap.put("[沙尘暴]", R.mipmap.w_shachenbao);
        emotionMap.put("[顶]", R.mipmap.d_ding);
//        emotionMap.put("[红丝带]", R.mipmap.o_hongsidai);
        emotionMap.put("[求关注]", R.mipmap.lxh_qiuguanzhu);
        emotionMap.put("[衰]", R.mipmap.d_shuai);
        emotionMap.put("[丘比特]", R.mipmap.lxh_qiubite);
        emotionMap.put("[纠结]", R.mipmap.lxh_jiujie);
//        emotionMap.put("[雪]", R.mipmap.w_xue);
        emotionMap.put("[呵呵]", R.mipmap.d_hehe);
//        emotionMap.put("[带着微博去旅行]", R.mipmap.d_lvxing);
        emotionMap.put("[泪流满面]", R.mipmap.lxh_leiliumanmian);
        emotionMap.put("[哈哈]", R.mipmap.d_haha);
//        emotionMap.put("[蜡烛]", R.mipmap.o_lazhu);
        emotionMap.put("[悲伤]", R.mipmap.d_beishang);
        emotionMap.put("[得意地笑]", R.mipmap.lxh_deyidexiao);
//        emotionMap.put("[微风]", R.mipmap.w_weifeng);
        emotionMap.put("[doge]", R.mipmap.d_doge);
        emotionMap.put("[霹雳]", R.mipmap.lxh_pili);
        emotionMap.put("[钱]", R.mipmap.d_qian);
        emotionMap.put("[推荐]", R.mipmap.lxh_tuijian);
        emotionMap.put("[别烦我]", R.mipmap.lxh_biefanwo);
        emotionMap.put("[笑哈哈]", R.mipmap.lxh_xiaohaha);
//        emotionMap.put("[咖啡]", R.mipmap.o_kafei);
        emotionMap.put("[哼]", R.mipmap.d_heng);
        emotionMap.put("[阴险]", R.mipmap.d_yinxian);
        emotionMap.put("[嘻嘻]", R.mipmap.d_xixi);
        emotionMap.put("[可爱]", R.mipmap.d_keai);
//        emotionMap.put("[话筒]", R.mipmap.o_huatong);
//        emotionMap.put("[绿丝带]", R.mipmap.o_lvsidai);
        emotionMap.put("[熊猫]", R.mipmap.d_xiongmao);
        emotionMap.put("[嘘]", R.mipmap.d_xu);
        emotionMap.put("[赞啊]", R.mipmap.lxh_zana);
//        emotionMap.put("[织]", R.mipmap.f_zhi);
//        emotionMap.put("[浮云]", R.mipmap.w_fuyun);
//        emotionMap.put("[下雨]", R.mipmap.w_xiayu);
        emotionMap.put("[草泥马]", R.mipmap.d_shenshou);
        emotionMap.put("[雷锋]", R.mipmap.lxh_leifeng);
//        emotionMap.put("[喜]", R.mipmap.f_xi);
        emotionMap.put("[挖鼻屎]", R.mipmap.d_wabishi);
        emotionMap.put("[汗]", R.mipmap.d_han);
        emotionMap.put("[喵喵]", R.mipmap.d_miao);
        emotionMap.put("[好喜欢]", R.mipmap.lxh_haoxihuan);
        emotionMap.put("[感冒]", R.mipmap.d_ganmao);
//        emotionMap.put("[发红包]", R.mipmap.o_fahongbao);
//        emotionMap.put("[愤怒]", R.mipmap.d_fennu);
//        emotionMap.put("[给力]", R.mipmap.f_geili);
        emotionMap.put("[馋嘴]", R.mipmap.d_chanzui);
        emotionMap.put("[good]", R.mipmap.h_good);
        emotionMap.put("[吐]", R.mipmap.d_tu);
        emotionMap.put("[甩甩手]", R.mipmap.lxh_shuaishuaishou);
        emotionMap.put("[不好意思]", R.mipmap.lxh_buhaoyisi);
        emotionMap.put("[许愿]", R.mipmap.lxh_xuyuan);
        emotionMap.put("[握手]", R.mipmap.h_woshou);
//        emotionMap.put("[围观]", R.mipmap.o_weiguan);
        emotionMap.put("[左哼哼]", R.mipmap.d_zuohengheng);
//        emotionMap.put("[风扇]", R.mipmap.o_fengshan);
        emotionMap.put("[思考]", R.mipmap.d_sikao);
//        emotionMap.put("[足球]", R.mipmap.o_zuqiu);
        emotionMap.put("[被电]", R.mipmap.lxh_beidian);
        emotionMap.put("[泪]", R.mipmap.d_lei);
//        emotionMap.put("[太阳]", R.mipmap.w_taiyang);
        emotionMap.put("[右哼哼]", R.mipmap.d_youhengheng);
        emotionMap.put("[好棒]", R.mipmap.lxh_haobang);
        emotionMap.put("[悲催]", R.mipmap.lxh_beicui);
        emotionMap.put("[睡觉]", R.mipmap.d_shuijiao);
        emotionMap.put("[猪头]", R.mipmap.d_zhutou);
        emotionMap.put("[玫瑰]", R.mipmap.lxh_meigui);
        emotionMap.put("[生病]", R.mipmap.d_shengbing);
        emotionMap.put("[偷笑]", R.mipmap.d_touxiao);
        emotionMap.put("[女孩儿]", R.mipmap.d_nvhaier);
        emotionMap.put("[给劲]", R.mipmap.lxh_feijin);
        emotionMap.put("[群体围观]", R.mipmap.lxh_quntiweiguan);
//        emotionMap.put("[囧]", R.mipmap.f_jiong);
        emotionMap.put("[亲亲]", R.mipmap.d_qinqin);
        emotionMap.put("[笑cry]", R.mipmap.d_xiaoku);
        emotionMap.put("[拜拜]", R.mipmap.d_baibai);
        emotionMap.put("[鄙视]", R.mipmap.d_bishi);
        emotionMap.put("[噢耶]", R.mipmap.lxh_oye);
        emotionMap.put("[懒得理你]", R.mipmap.d_landelini);
        emotionMap.put("[怒]", R.mipmap.d_nu);
        emotionMap.put("[瞧瞧]", R.mipmap.lxh_qiaoqiao);
        emotionMap.put("[耶]", R.mipmap.h_ye);
//        emotionMap.put("[音乐]", R.mipmap.o_yinyue);
//        emotionMap.put("[神马]", R.mipmap.f_shenma);
//        emotionMap.put("[冰棍]", R.mipmap.o_bingun);
//        emotionMap.put("[手套]", R.mipmap.o_shoutao);
//        emotionMap.put("[汽车]", R.mipmap.o_qiche);
//        emotionMap.put("[落叶]", R.mipmap.w_luoye);
//        emotionMap.put("[钟]", R.mipmap.o_zhong);
        emotionMap.put("[吃惊]", R.mipmap.d_chijing);
        emotionMap.put("[失望]", R.mipmap.d_shiwang);
//        emotionMap.put("[西瓜]", R.mipmap.o_xigua);
        emotionMap.put("[好爱哦]", R.mipmap.lxh_haoaio);
        emotionMap.put("[巨汗]", R.mipmap.lxh_juhan);
//        emotionMap.put("[飞机]", R.mipmap.o_feiji);
        emotionMap.put("[好囧]", R.mipmap.lxh_haojiong);
        emotionMap.put("[抠鼻屎]", R.mipmap.lxh_koubishi);
        emotionMap.put("[花心]", R.mipmap.d_huaxin);
        emotionMap.put("[羞嗒嗒]", R.mipmap.lxh_xiudada);
        emotionMap.put("[不想上班]", R.mipmap.lxh_buxiangshangban);
        emotionMap.put("[弱]", R.mipmap.h_ruo);

    }

    public static final String USER_INFO_SCHEME = "com.caij.weiyo.userinfo://";
    public static final String HTTP_SCHEME = "weibohttp://";
    public static final String TOPIC_SCHEME = "com.caij.weiyo.topics://";

    public static void praseName(Spannable spannableString) {
        // 用户名称
//			Pattern pattern = Pattern.compile("@([a-zA-Z0-9_\\-\\u4e00-\\u9fa5]+)");
        Pattern pattern = Pattern.compile("@[\\w\\p{InCJKUnifiedIdeographs}-]{1,26}");
        String scheme = USER_INFO_SCHEME;
        Linkify.addLinks(spannableString, pattern, scheme);
    }

    public static void praseHttpUrl(Spannable spannableString) {
        String scheme = HTTP_SCHEME;
        Linkify.addLinks(spannableString, Pattern.compile("http://[a-zA-Z0-9+&@#/%?=~_\\-|!:,\\.;]*[a-zA-Z0-9+&@#/%=~_|]"),
                scheme);
    }

    public static void praseTopic(Spannable spannableString) {
        // 话题
        Pattern dd = Pattern.compile("#[\\p{Print}\\p{InCJKUnifiedIdeographs}&&[^#]]+#");
        //Pattern dd = Pattern.compile("#([a-zA-Z0-9_\\-\\u4e00-\\u9fa5]+)#");
        String scheme = TOPIC_SCHEME;
        Linkify.addLinks(spannableString, dd, scheme);
    }

    public static void praseEmotions(Context context, Spannable spannableString) {
        if (emotionLruCache == null) {
            ActivityManager activityStack = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
             int size = (int) (activityStack.getMemoryClass() * 1024 * 1024 * 0.01);
            emotionLruCache = new LruCache<>(size);
            LogUtil.d("emotionLruCache", "emotionLruCache size is %s byte", size);
        }
        Matcher localMatcher = Pattern.compile("\\[(\\S+?)\\]").matcher(spannableString);
        while (localMatcher.find()) {

            String key = localMatcher.group(0);

            int k = localMatcher.start();
            int m = localMatcher.end();

            Integer drawable = emotionMap.get(key);
            if (drawable == null) {
                LogUtil.d("praseEmotions", "%s 表情未找到资源", key);
                continue;
            }
            Bitmap bitmap = emotionLruCache.get(key);
            if (bitmap == null) {
                bitmap = BitmapFactory.decodeResource(context.getResources(), drawable);
                int size = context.getResources().getDimensionPixelSize(R.dimen.text_size_medium);
                bitmap = ImageUtil.zoomBitmap(bitmap, size);
                emotionLruCache.put(key, bitmap);
            }

            CenteredImageSpan l = new CenteredImageSpan(context, bitmap);
            spannableString.setSpan(l, k, m, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }


    public static void urlSpan2ClickSpan(Spannable spannableString, int color, int pressColor) {
        URLSpan[] urlSpans = spannableString.getSpans(0, spannableString.length(), URLSpan.class);
        MyURLSpan weiboSpan = null;
        for (URLSpan urlSpan : urlSpans) {
            weiboSpan = new MyURLSpan(urlSpan.getURL());
            weiboSpan.setTextColor(color);
            weiboSpan.setPressColor(pressColor);
            int start = spannableString.getSpanStart(urlSpan);
            int end = spannableString.getSpanEnd(urlSpan);
            try {
                spannableString.removeSpan(urlSpan);
            } catch (Exception e) {
            }
            spannableString.setSpan(weiboSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    public static void paraeSpannable(Weibo weibo, Context applicationContent) {
        int color = applicationContent.getResources().getColor(R.color.link_text_color);
        int pressColor = applicationContent.getResources().getColor(R.color.link_text_press_color);
        SpannableString contentSpannableString = SpannableString.valueOf(weibo.getText());
        SpannableStringUtil.praseName(contentSpannableString);
        SpannableStringUtil.praseHttpUrl(contentSpannableString);
        SpannableStringUtil.praseTopic(contentSpannableString);
        SpannableStringUtil.praseEmotions(applicationContent, contentSpannableString);
        SpannableStringUtil.urlSpan2ClickSpan(contentSpannableString, color, pressColor);
        weibo.setContentSpannableString(contentSpannableString);

        if (weibo.getRetweeted_status() != null) {
            Weibo reWeibo = weibo.getRetweeted_status();
            String reUserName = "";
            User reUser = reWeibo.getUser();
            if (reUser != null && !TextUtils.isEmpty(reUser.getScreen_name()))
                reUserName = String.format("@%s :", reUser.getScreen_name());
            SpannableString reContentSpannableString = SpannableString.valueOf(reUserName + reWeibo.getText());
            SpannableStringUtil.praseName(reContentSpannableString);
            SpannableStringUtil.praseHttpUrl(reContentSpannableString);
            SpannableStringUtil.praseTopic(reContentSpannableString);
            SpannableStringUtil.praseEmotions(applicationContent, reContentSpannableString);
            SpannableStringUtil.urlSpan2ClickSpan(reContentSpannableString, color, pressColor);
            reWeibo.setContentSpannableString(reContentSpannableString);
        }
    }

    public static void paraeSpannable(Comment comment, Context applicationContent) {
        int color = applicationContent.getResources().getColor(R.color.link_text_color);
        int pressColor = applicationContent.getResources().getColor(R.color.link_text_press_color);
        SpannableString contentSpannableString = SpannableString.valueOf(comment.getText());
        SpannableStringUtil.praseName(contentSpannableString);
        SpannableStringUtil.praseHttpUrl(contentSpannableString);
        SpannableStringUtil.praseTopic(contentSpannableString);
        SpannableStringUtil.praseEmotions(applicationContent, contentSpannableString);
        SpannableStringUtil.urlSpan2ClickSpan(contentSpannableString, color, pressColor);

        comment.setTextSpannableString(contentSpannableString);
    }

}
