package com.caij.emore.utils;

import android.graphics.Bitmap;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.LruCache;
import android.util.SparseIntArray;

import com.caij.emore.R;
import com.caij.emore.bean.Emotion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Caij on 2016/6/18.
 */
public class EmotionsUtil {

    public static String WEB_CARD_IMAGE_KEY  = "[网页]";
    public static String IMAGE_CARD_IMAGE_KEY  = "[查看图片]";
    public static String VIDEO_CARD_IMAGE_KEY  = "[查看视频]";

    private static ArrayList<Emotion> imageEmotionList = new ArrayList<>();
    private static Map<String, Integer> imageEmotionMap = new ArrayMap<>();

    private static SparseIntArray softEmotionMap = new SparseIntArray();
    private static ArrayList<Emotion> softEmotionList = new ArrayList<>(80);

    private static ArrayList<Emotion> langEmotionList = new ArrayList<>();

    private static LruCache<String, Bitmap> emotionLruCache = new LruCache<String, Bitmap>(1024 * 1024){
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount();
        }
    };

    static {
        imageEmotionMap.put(WEB_CARD_IMAGE_KEY, R.mipmap.timeline_card_small_web);
        imageEmotionMap.put(IMAGE_CARD_IMAGE_KEY, R.mipmap.timeline_icon_photo);
        imageEmotionMap.put(VIDEO_CARD_IMAGE_KEY, R.mipmap.timeline_card_small_video);

        imageEmotionMap.put("[抱抱]", R.mipmap.d_baobao);
        imageEmotionList.add(new Emotion("[抱抱]", R.mipmap.d_baobao));

        imageEmotionMap.put("[二哈]", R.mipmap.d_erha);
        imageEmotionList.add(new Emotion("[二哈]", R.mipmap.d_erha));

        imageEmotionMap.put("[摊手]", R.mipmap.d_tanshou);
        imageEmotionList.add(new Emotion("[摊手]", R.mipmap.d_tanshou));

        imageEmotionMap.put("[微笑]", R.mipmap.d_hehe);
        imageEmotionList.add(new Emotion("[微笑]", R.mipmap.d_hehe));

        imageEmotionMap.put("[嘻嘻]", R.mipmap.d_xixi);
        imageEmotionList.add(new Emotion("[嘻嘻]", R.mipmap.d_xixi));

        imageEmotionMap.put("[哈哈]", R.mipmap.d_haha);
        imageEmotionList.add(new Emotion("[哈哈]", R.mipmap.d_haha));

        imageEmotionMap.put("[爱你]", R.mipmap.d_aini);
        imageEmotionList.add(new Emotion("[爱你]", R.mipmap.d_aini));

        imageEmotionMap.put("[挖鼻]", R.mipmap.d_wabishi);
        imageEmotionList.add(new Emotion("[挖鼻]", R.mipmap.d_wabishi));

        imageEmotionMap.put("[吃惊]", R.mipmap.d_chijing);
        imageEmotionList.add(new Emotion("[吃惊]", R.mipmap.d_chijing));

        imageEmotionMap.put("[晕]", R.mipmap.d_yun);
        imageEmotionList.add(new Emotion("[晕]", R.mipmap.d_yun));

        imageEmotionMap.put("[泪]", R.mipmap.d_lei);
        imageEmotionList.add(new Emotion("[泪]", R.mipmap.d_lei));

        imageEmotionMap.put("[馋嘴]", R.mipmap.d_chanzui);
        imageEmotionList.add(new Emotion("[馋嘴]", R.mipmap.d_chanzui));

        imageEmotionMap.put("[抓狂]", R.mipmap.d_zhuakuang);
        imageEmotionList.add(new Emotion("[抓狂]", R.mipmap.d_zhuakuang));

        imageEmotionMap.put("[坏笑]", R.mipmap.d_huaixiao);
        imageEmotionList.add(new Emotion("[坏笑]", R.mipmap.d_huaixiao));

        imageEmotionMap.put("[甜]", R.mipmap.d_tian);
        imageEmotionList.add(new Emotion("[甜]", R.mipmap.d_tian));

        imageEmotionMap.put("[污]", R.mipmap.d_wu);
        imageEmotionList.add(new Emotion("[污]", R.mipmap.d_wu));

        imageEmotionMap.put("[哼]", R.mipmap.d_heng);
        imageEmotionList.add(new Emotion("[哼]", R.mipmap.d_heng));

        imageEmotionMap.put("[可爱]", R.mipmap.d_keai);
        imageEmotionList.add(new Emotion("[可爱]", R.mipmap.d_keai));

        imageEmotionMap.put("[怒]", R.mipmap.d_nu);
        imageEmotionList.add(new Emotion("[怒]", R.mipmap.d_nu));

        imageEmotionMap.put("[汗]", R.mipmap.d_han);
        imageEmotionList.add(new Emotion("[汗]", R.mipmap.d_han));

        imageEmotionMap.put("[害羞]", R.mipmap.d_haixiu);
        imageEmotionList.add(new Emotion("[害羞]", R.mipmap.d_haixiu));

        imageEmotionMap.put("[睡]", R.mipmap.d_shuijiao);
        imageEmotionList.add(new Emotion("[睡]", R.mipmap.d_shuijiao));

        imageEmotionMap.put("[钱]", R.mipmap.d_qian);
        imageEmotionList.add(new Emotion("[钱]", R.mipmap.d_qian));

        imageEmotionMap.put("[偷笑]", R.mipmap.d_touxiao);
        imageEmotionList.add(new Emotion("[偷笑]", R.mipmap.d_touxiao));

        imageEmotionMap.put("[笑cry]", R.mipmap.d_xiaoku);
        imageEmotionList.add(new Emotion("[笑cry]", R.mipmap.d_xiaoku));

        imageEmotionMap.put("[doge]", R.mipmap.d_doge);
        imageEmotionList.add(new Emotion("[doge]", R.mipmap.d_doge));

        imageEmotionMap.put("[喵喵]", R.mipmap.d_miao);
        imageEmotionList.add(new Emotion("[喵喵]", R.mipmap.d_miao));

        imageEmotionMap.put("[酷]", R.mipmap.d_ku);
        imageEmotionList.add(new Emotion("[酷]", R.mipmap.d_ku));

        imageEmotionMap.put("[衰]", R.mipmap.d_shuai);
        imageEmotionList.add(new Emotion("[衰]", R.mipmap.d_shuai));

        imageEmotionMap.put("[闭嘴]", R.mipmap.d_bizui);
        imageEmotionList.add(new Emotion("[闭嘴]", R.mipmap.d_bizui));

        imageEmotionMap.put("[鄙视]", R.mipmap.d_bishi);
        imageEmotionList.add(new Emotion("[鄙视]", R.mipmap.d_bishi));

        imageEmotionMap.put("[色]", R.mipmap.d_huaxin);
        imageEmotionList.add(new Emotion("[色]", R.mipmap.d_huaxin));

        imageEmotionMap.put("[鼓掌]", R.mipmap.d_guzhang);
        imageEmotionList.add(new Emotion("[鼓掌]", R.mipmap.d_guzhang));

        imageEmotionMap.put("[悲伤]", R.mipmap.d_beishang);
        imageEmotionList.add(new Emotion("[悲伤]", R.mipmap.d_beishang));

        imageEmotionMap.put("[思考]", R.mipmap.d_sikao);
        imageEmotionList.add(new Emotion("[思考]", R.mipmap.d_sikao));

        imageEmotionMap.put("[生病]", R.mipmap.d_shengbing);
        imageEmotionList.add(new Emotion("[生病]", R.mipmap.d_shengbing));

        imageEmotionMap.put("[亲亲]", R.mipmap.d_qinqin);
        imageEmotionList.add(new Emotion("[亲亲]", R.mipmap.d_qinqin));

        imageEmotionMap.put("[怒骂]", R.mipmap.d_numa);
        imageEmotionList.add(new Emotion("[怒骂]", R.mipmap.d_numa));

        imageEmotionMap.put("[太开心]", R.mipmap.d_taikaixin);
        imageEmotionList.add(new Emotion("[太开心]", R.mipmap.d_taikaixin));

        imageEmotionMap.put("[白眼]", R.mipmap.d_landelini);
        imageEmotionList.add(new Emotion("[白眼]", R.mipmap.d_landelini));

        imageEmotionMap.put("[右哼哼]", R.mipmap.d_youhengheng);
        imageEmotionList.add(new Emotion("[右哼哼]", R.mipmap.d_youhengheng));

        imageEmotionMap.put("[左哼哼]", R.mipmap.d_zuohengheng);
        imageEmotionList.add(new Emotion("[左哼哼]", R.mipmap.d_zuohengheng));

        imageEmotionMap.put("[嘘]", R.mipmap.d_xu);
        imageEmotionList.add(new Emotion("[嘘]", R.mipmap.d_xu));

        imageEmotionMap.put("[委屈]", R.mipmap.d_weiqu);
        imageEmotionList.add(new Emotion("[委屈]", R.mipmap.d_weiqu));

        imageEmotionMap.put("[吐]", R.mipmap.d_tu);
        imageEmotionList.add(new Emotion("[吐]", R.mipmap.d_tu));

        imageEmotionMap.put("[可怜]", R.mipmap.d_kelian);
        imageEmotionList.add(new Emotion("[可怜]", R.mipmap.d_kelian));

        imageEmotionMap.put("[哈欠]", R.mipmap.d_dahaqi);
        imageEmotionList.add(new Emotion("[哈欠]", R.mipmap.d_dahaqi));

        imageEmotionMap.put("[挤眼]", R.mipmap.d_jiyan);
        imageEmotionList.add(new Emotion("[挤眼]", R.mipmap.d_jiyan));

        imageEmotionMap.put("[失望]", R.mipmap.d_shiwang);
        imageEmotionList.add(new Emotion("[失望]", R.mipmap.d_shiwang));

        imageEmotionMap.put("[顶]", R.mipmap.d_ding);
        imageEmotionList.add(new Emotion("[顶]", R.mipmap.d_ding));

        imageEmotionMap.put("[疑问]", R.mipmap.d_yiwen);
        imageEmotionList.add(new Emotion("[疑问]", R.mipmap.d_yiwen));

        imageEmotionMap.put("[困]", R.mipmap.d_kun);
        imageEmotionList.add(new Emotion("[困]", R.mipmap.d_kun));

        imageEmotionMap.put("[感冒]", R.mipmap.d_ganmao);
        imageEmotionList.add(new Emotion("[感冒]", R.mipmap.d_ganmao));

        imageEmotionMap.put("[拜拜]", R.mipmap.d_baibai);
        imageEmotionList.add(new Emotion("[拜拜]", R.mipmap.d_baibai));

        imageEmotionMap.put("[黑线]", R.mipmap.d_heixian);
        imageEmotionList.add(new Emotion("[黑线]", R.mipmap.d_heixian));

        imageEmotionMap.put("[阴险]", R.mipmap.d_yinxian);
        imageEmotionList.add(new Emotion("[阴险]", R.mipmap.d_yinxian));

        imageEmotionMap.put("[打脸]", R.mipmap.d_dalian);
        imageEmotionList.add(new Emotion("[打脸]", R.mipmap.d_dalian));

        imageEmotionMap.put("[傻眼]", R.mipmap.d_shayan);
        imageEmotionList.add(new Emotion("[傻眼]", R.mipmap.d_shayan));

        imageEmotionMap.put("[互粉]", R.mipmap.f_hufen);
        imageEmotionList.add(new Emotion("[互粉]", R.mipmap.f_hufen));

        imageEmotionMap.put("[带着微博去旅行]", R.mipmap.d_travel);
        imageEmotionList.add(new Emotion("[带着微博去旅行]", R.mipmap.d_travel));

        imageEmotionMap.put("[骷髅]", R.mipmap.d_kulou);
        imageEmotionList.add(new Emotion("[骷髅]", R.mipmap.d_kulou));

        imageEmotionMap.put("[炸鸡啤酒]", R.mipmap.d_zhajipijiu);
        imageEmotionList.add(new Emotion("[炸鸡啤酒]", R.mipmap.d_zhajipijiu));

        imageEmotionMap.put("[马到成功]", R.mipmap.d_madaochenggong);
        imageEmotionList.add(new Emotion("[马到成功]", R.mipmap.d_madaochenggong));

        imageEmotionMap.put("[心]", R.mipmap.l_xin);
        imageEmotionList.add(new Emotion("[心]", R.mipmap.l_xin));

        imageEmotionMap.put("[伤心]", R.mipmap.l_shangxin);
        imageEmotionList.add(new Emotion("[伤心]", R.mipmap.l_shangxin));

        imageEmotionMap.put("[猪头]", R.mipmap.d_zhutou);
        imageEmotionList.add(new Emotion("[猪头]", R.mipmap.d_zhutou));

        imageEmotionMap.put("[熊猫]", R.mipmap.d_xiongmao);
        imageEmotionList.add(new Emotion("[熊猫]", R.mipmap.d_xiongmao));

        imageEmotionMap.put("[兔子]", R.mipmap.d_tuzi);
        imageEmotionList.add(new Emotion("[兔子]", R.mipmap.d_tuzi));

        imageEmotionMap.put("[握手]", R.mipmap.h_woshou);
        imageEmotionList.add(new Emotion("[握手]", R.mipmap.h_woshou));

        imageEmotionMap.put("[作揖]", R.mipmap.h_zuoyi);
        imageEmotionList.add(new Emotion("[作揖]", R.mipmap.h_zuoyi));

        imageEmotionMap.put("[赞]", R.mipmap.h_zan);
        imageEmotionList.add(new Emotion("[赞]", R.mipmap.h_zan));

        imageEmotionMap.put("[耶]", R.mipmap.h_ye);
        imageEmotionList.add(new Emotion("[耶]", R.mipmap.h_ye));

        imageEmotionMap.put("[good]", R.mipmap.h_good);
        imageEmotionList.add(new Emotion("[good]", R.mipmap.h_good));

        imageEmotionMap.put("[弱]", R.mipmap.h_ruo);
        imageEmotionList.add(new Emotion("[弱]", R.mipmap.h_ruo));

        imageEmotionMap.put("[NO]", R.mipmap.h_buyao);
        imageEmotionList.add(new Emotion("[NO]", R.mipmap.h_buyao));

        imageEmotionMap.put("[ok]", R.mipmap.h_ok);
        imageEmotionList.add(new Emotion("[ok]", R.mipmap.h_ok));

        imageEmotionMap.put("[haha]", R.mipmap.h_haha);
        imageEmotionList.add(new Emotion("[haha]", R.mipmap.h_haha));

        imageEmotionMap.put("[来]", R.mipmap.h_lai);
        imageEmotionList.add(new Emotion("[来]", R.mipmap.h_lai));

        imageEmotionMap.put("[拳头]", R.mipmap.h_quantou);
        imageEmotionList.add(new Emotion("[拳头]", R.mipmap.h_quantou));

        imageEmotionMap.put("[威武]", R.mipmap.f_v5);
        imageEmotionList.add(new Emotion("[威武]", R.mipmap.f_v5));

        imageEmotionMap.put("[鲜花]", R.mipmap.w_xianhua);
        imageEmotionList.add(new Emotion("[鲜花]", R.mipmap.w_xianhua));

        imageEmotionMap.put("[钟]", R.mipmap.o_zhong);
        imageEmotionList.add(new Emotion("[钟]", R.mipmap.o_zhong));

        imageEmotionMap.put("[浮云]", R.mipmap.w_fuyun);
        imageEmotionList.add(new Emotion("[浮云]", R.mipmap.w_fuyun));

        imageEmotionMap.put("[飞机]", R.mipmap.o_feiji);
        imageEmotionList.add(new Emotion("[飞机]", R.mipmap.o_feiji));

        imageEmotionMap.put("[月亮]", R.mipmap.w_yueliang);
        imageEmotionList.add(new Emotion("[月亮]", R.mipmap.w_yueliang));

        imageEmotionMap.put("[太阳]", R.mipmap.w_taiyang);
        imageEmotionList.add(new Emotion("[太阳]", R.mipmap.w_taiyang));

        imageEmotionMap.put("[微风]", R.mipmap.w_weifeng);
        imageEmotionList.add(new Emotion("[微风]", R.mipmap.w_weifeng));

        imageEmotionMap.put("[下雨]", R.mipmap.w_xiayu);
        imageEmotionList.add(new Emotion("[下雨]", R.mipmap.w_xiayu));

        imageEmotionMap.put("[给力]", R.mipmap.f_geili);
        imageEmotionList.add(new Emotion("[给力]", R.mipmap.f_geili));

        imageEmotionMap.put("[神马]", R.mipmap.f_shenma);
        imageEmotionList.add(new Emotion("[神马]", R.mipmap.f_shenma));

        imageEmotionMap.put("[围观]", R.mipmap.o_weiguan);
        imageEmotionList.add(new Emotion("[围观]", R.mipmap.o_weiguan));

        imageEmotionMap.put("[话筒]", R.mipmap.o_huatong);
        imageEmotionList.add(new Emotion("[话筒]", R.mipmap.o_huatong));

        imageEmotionMap.put("[奥特曼]", R.mipmap.d_aoteman);
        imageEmotionList.add(new Emotion("[奥特曼]", R.mipmap.d_aoteman));

        imageEmotionMap.put("[草泥马]", R.mipmap.d_shenshou);
        imageEmotionList.add(new Emotion("[草泥马]", R.mipmap.d_shenshou));

        imageEmotionMap.put("[萌]", R.mipmap.f_meng);
        imageEmotionList.add(new Emotion("[萌]", R.mipmap.f_meng));

        imageEmotionMap.put("[囧]", R.mipmap.f_jiong);
        imageEmotionList.add(new Emotion("[囧]", R.mipmap.f_jiong));

        imageEmotionMap.put("[织]", R.mipmap.f_zhi);
        imageEmotionList.add(new Emotion("[织]", R.mipmap.f_zhi));

        imageEmotionMap.put("[礼物]", R.mipmap.o_liwu);
        imageEmotionList.add(new Emotion("[礼物]", R.mipmap.o_liwu));

        imageEmotionMap.put("[喜]", R.mipmap.f_xi);
        imageEmotionList.add(new Emotion("[喜]", R.mipmap.f_xi));

        imageEmotionMap.put("[围脖]", R.mipmap.o_weibo);
        imageEmotionList.add(new Emotion("[围脖]", R.mipmap.o_weibo));

        imageEmotionMap.put("[音乐]", R.mipmap.o_yinyue);
        imageEmotionList.add(new Emotion("[音乐]", R.mipmap.o_yinyue));

        imageEmotionMap.put("[绿丝带]", R.mipmap.o_lvsidai);
        imageEmotionList.add(new Emotion("[绿丝带]", R.mipmap.o_lvsidai));

        imageEmotionMap.put("[蛋糕]", R.mipmap.o_dangao);
        imageEmotionList.add(new Emotion("[蛋糕]", R.mipmap.o_dangao));

        imageEmotionMap.put("[蜡烛]", R.mipmap.o_lazhu);
        imageEmotionList.add(new Emotion("[蜡烛]", R.mipmap.o_lazhu));

        imageEmotionMap.put("[干杯]", R.mipmap.o_ganbei);
        imageEmotionList.add(new Emotion("[干杯]", R.mipmap.o_ganbei));

        imageEmotionMap.put("[男孩儿]", R.mipmap.d_nanhaier);
        imageEmotionList.add(new Emotion("[男孩儿]", R.mipmap.d_nanhaier));

        imageEmotionMap.put("[女孩儿]", R.mipmap.d_nvhaier);
        imageEmotionList.add(new Emotion("[女孩儿]", R.mipmap.d_nvhaier));

        imageEmotionMap.put("[肥皂]", R.mipmap.d_feizao);
        imageEmotionList.add(new Emotion("[肥皂]", R.mipmap.d_feizao));

        imageEmotionMap.put("[照相机]", R.mipmap.o_zhaoxiangji);
        imageEmotionList.add(new Emotion("[照相机]", R.mipmap.o_zhaoxiangji));

        imageEmotionMap.put("[浪]", R.mipmap.d_lang);
        imageEmotionList.add(new Emotion("[浪]", R.mipmap.d_lang));

        imageEmotionMap.put("[沙尘暴]", R.mipmap.w_shachenbao);
        imageEmotionList.add(new Emotion("[沙尘暴]", R.mipmap.w_shachenbao));

        imageEmotionMap.put("[最右]", R.mipmap.d_zuiyou);
        imageEmotionList.add(new Emotion("[最右]", R.mipmap.d_zuiyou));

        //-------------------------------------------------------------------

        softEmotionMap.put(0x1f603, R.mipmap.emoji_0x1f603);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f603)), R.mipmap.emoji_0x1f603));

        softEmotionMap.put(0x1f60d, R.mipmap.emoji_0x1f60d);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f60d)), R.mipmap.emoji_0x1f60d));

        softEmotionMap.put(0x1f612, R.mipmap.emoji_0x1f612);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f612)), R.mipmap.emoji_0x1f612));

        softEmotionMap.put(0x1f633, R.mipmap.emoji_0x1f633);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f633)), R.mipmap.emoji_0x1f633));

        softEmotionMap.put(0x1f601, R.mipmap.emoji_0x1f601);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f601)), R.mipmap.emoji_0x1f601));

        softEmotionMap.put(0x1f618, R.mipmap.emoji_0x1f618);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f618)), R.mipmap.emoji_0x1f618));

        softEmotionMap.put(0x1f609, R.mipmap.emoji_0x1f609);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f609)), R.mipmap.emoji_0x1f609));

        softEmotionMap.put(0x1f620, R.mipmap.emoji_0x1f620);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f620)), R.mipmap.emoji_0x1f620));

        softEmotionMap.put(0x1f61e, R.mipmap.emoji_0x1f61e);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f61e)), R.mipmap.emoji_0x1f61e));

        softEmotionMap.put(0x1f625, R.mipmap.emoji_0x1f625);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f625)), R.mipmap.emoji_0x1f625));

        softEmotionMap.put(0x1f62d, R.mipmap.emoji_0x1f62d);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f62d)), R.mipmap.emoji_0x1f62d));

        softEmotionMap.put(0x1f61d, R.mipmap.emoji_0x1f61d);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f61d)), R.mipmap.emoji_0x1f61d));

        softEmotionMap.put(0x1f621, R.mipmap.emoji_0x1f621);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f621)), R.mipmap.emoji_0x1f621));

        softEmotionMap.put(0x1f623, R.mipmap.emoji_0x1f623);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f623)), R.mipmap.emoji_0x1f623));

        softEmotionMap.put(0x1f614, R.mipmap.emoji_0x1f614);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f614)), R.mipmap.emoji_0x1f614));

        softEmotionMap.put(0x1f604, R.mipmap.emoji_0x1f604);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f604)), R.mipmap.emoji_0x1f604));

        softEmotionMap.put(0x1f637, R.mipmap.emoji_0x1f637);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f637)), R.mipmap.emoji_0x1f637));

        softEmotionMap.put(0x1f61a, R.mipmap.emoji_0x1f61a);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f61a)), R.mipmap.emoji_0x1f61a));

        softEmotionMap.put(0x1f613, R.mipmap.emoji_0x1f613);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f613)), R.mipmap.emoji_0x1f613));

        softEmotionMap.put(0x1f602, R.mipmap.emoji_0x1f602);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f602)), R.mipmap.emoji_0x1f602));

//        softEmotionMap.put(0x1f622, R.mipmap.emoji_0x1f622);
//        softEmotionList.add(new Emotion(String.valueOf(0x1f622), R.mipmap.emoji_0x1f622));

        softEmotionMap.put(0x1f60a, R.mipmap.emoji_0x1f60a);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f60a)), R.mipmap.emoji_0x1f60a));

        softEmotionMap.put(0x1f622, R.mipmap.emoji_0x1f622);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f622)), R.mipmap.emoji_0x1f622));

        softEmotionMap.put(0x1f61c, R.mipmap.emoji_0x1f61c);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f61c)), R.mipmap.emoji_0x1f61c));

        softEmotionMap.put(0x1f628, R.mipmap.emoji_0x1f628);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f628)), R.mipmap.emoji_0x1f628));

        softEmotionMap.put(0x1f630, R.mipmap.emoji_0x1f630);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f630)), R.mipmap.emoji_0x1f630));

        softEmotionMap.put(0x1f632, R.mipmap.emoji_0x1f632);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f632)), R.mipmap.emoji_0x1f632));

        softEmotionMap.put(0x1f60f, R.mipmap.emoji_0x1f60f);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f60f)), R.mipmap.emoji_0x1f60f));

        softEmotionMap.put(0x1f631, R.mipmap.emoji_0x1f631);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f631)), R.mipmap.emoji_0x1f631));

        softEmotionMap.put(0x1f62a, R.mipmap.emoji_0x1f62a);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f62a)), R.mipmap.emoji_0x1f62a));

        softEmotionMap.put(0x1f616, R.mipmap.emoji_0x1f616);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f616)), R.mipmap.emoji_0x1f616));

        softEmotionMap.put(0x1f60c, R.mipmap.emoji_0x1f60c);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f60c)), R.mipmap.emoji_0x1f60c));

        softEmotionMap.put(0x1f47f, R.mipmap.emoji_0x1f47f);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f47f)), R.mipmap.emoji_0x1f47f));

        softEmotionMap.put(0x1f47b, R.mipmap.emoji_0x1f47b);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f47b)), R.mipmap.emoji_0x1f47b));

        softEmotionMap.put(0x1f385, R.mipmap.emoji_0x1f385);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f385)), R.mipmap.emoji_0x1f385));

        softEmotionMap.put(0x1f467, R.mipmap.emoji_0x1f467);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f467)), R.mipmap.emoji_0x1f467));

        softEmotionMap.put(0x1f466, R.mipmap.emoji_0x1f466);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f466)), R.mipmap.emoji_0x1f466));

        softEmotionMap.put(0x1f469, R.mipmap.emoji_0x1f469);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f469)), R.mipmap.emoji_0x1f469));

        softEmotionMap.put(0x1f468, R.mipmap.emoji_0x1f468);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f468)), R.mipmap.emoji_0x1f468));

        softEmotionMap.put(0x1f436, R.mipmap.emoji_0x1f436);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f436)), R.mipmap.emoji_0x1f436));

        softEmotionMap.put(0x1f431, R.mipmap.emoji_0x1f431);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f431)), R.mipmap.emoji_0x1f431));

        softEmotionMap.put(0x1f44d, R.mipmap.emoji_0x1f44d);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f44d)), R.mipmap.emoji_0x1f44d));

        softEmotionMap.put(0x1f44e, R.mipmap.emoji_0x1f44e);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f44e)), R.mipmap.emoji_0x1f44e));

        softEmotionMap.put(0x1f44a, R.mipmap.emoji_0x1f44a);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f44a)), R.mipmap.emoji_0x1f44a));

        softEmotionMap.put(0x270a, R.mipmap.emoji_0x270a);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x270a)), R.mipmap.emoji_0x270a));

        softEmotionMap.put(0x270c, R.mipmap.emoji_0x270c);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x270c)), R.mipmap.emoji_0x270c));

        softEmotionMap.put(0x1f4aa, R.mipmap.emoji_0x1f4aa);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f4aa)), R.mipmap.emoji_0x1f4aa));

        softEmotionMap.put(0x1f44f, R.mipmap.emoji_0x1f44f);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f44f)), R.mipmap.emoji_0x1f44f));

        softEmotionMap.put(0x1f448, R.mipmap.emoji_0x1f448);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f448)), R.mipmap.emoji_0x1f448));

        softEmotionMap.put(0x1f446, R.mipmap.emoji_0x1f446);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f446)), R.mipmap.emoji_0x1f446));

        softEmotionMap.put(0x1f449, R.mipmap.emoji_0x1f449);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f449)), R.mipmap.emoji_0x1f449));

        softEmotionMap.put(0x1f447, R.mipmap.emoji_0x1f447);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f447)), R.mipmap.emoji_0x1f447));

        softEmotionMap.put(0x1f44c, R.mipmap.emoji_0x1f44c);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f44c)), R.mipmap.emoji_0x1f44c));

        softEmotionMap.put(0x2764, R.mipmap.emoji_0x2764);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x2764)), R.mipmap.emoji_0x2764));

        softEmotionMap.put(0x1f494, R.mipmap.emoji_0x1f494);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f494)), R.mipmap.emoji_0x1f494));

        softEmotionMap.put(0x1f64f, R.mipmap.emoji_0x1f64f);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f64f)), R.mipmap.emoji_0x1f64f));

        softEmotionMap.put(0x2600, R.mipmap.emoji_0x2600);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x2600)), R.mipmap.emoji_0x2600));

        softEmotionMap.put(0x1f319, R.mipmap.emoji_0x1f319);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f319)), R.mipmap.emoji_0x1f319));

        softEmotionMap.put(0x1f31f, R.mipmap.emoji_0x1f31f);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f31f)), R.mipmap.emoji_0x1f31f));

        softEmotionMap.put(0x26a1, R.mipmap.emoji_0x26a1);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x26a1)), R.mipmap.emoji_0x26a1));

        softEmotionMap.put(0x2601, R.mipmap.emoji_0x2601);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x2601)), R.mipmap.emoji_0x2601));

        softEmotionMap.put(0x2614, R.mipmap.emoji_0x2614);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x2614)), R.mipmap.emoji_0x2614));

        softEmotionMap.put(0x1f341, R.mipmap.emoji_0x1f341);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f341)), R.mipmap.emoji_0x1f341));

        softEmotionMap.put(0x1f33b, R.mipmap.emoji_0x1f33b);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f33b)), R.mipmap.emoji_0x1f33b));

        softEmotionMap.put(0x1f343, R.mipmap.emoji_0x1f343);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f343)), R.mipmap.emoji_0x1f343));

        softEmotionMap.put(0x1f457, R.mipmap.emoji_0x1f457);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f457)), R.mipmap.emoji_0x1f457));

        softEmotionMap.put(0x1f380, R.mipmap.emoji_0x1f380);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f380)), R.mipmap.emoji_0x1f380));

        softEmotionMap.put(0x1f444, R.mipmap.emoji_0x1f444);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f444)), R.mipmap.emoji_0x1f444));

        softEmotionMap.put(0x1f339, R.mipmap.emoji_0x1f339);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f339)), R.mipmap.emoji_0x1f339));

        softEmotionMap.put(0x2615, R.mipmap.emoji_0x2615);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x2615)), R.mipmap.emoji_0x2615));

        softEmotionMap.put(0x1f382, R.mipmap.emoji_0x1f382);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f382)), R.mipmap.emoji_0x1f382));

        softEmotionMap.put(0x1f559, R.mipmap.emoji_0x1f559);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f559)), R.mipmap.emoji_0x1f559));

        softEmotionMap.put(0x1f37a, R.mipmap.emoji_0x1f37a);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f37a)), R.mipmap.emoji_0x1f37a));

        softEmotionMap.put(0x1f50d, R.mipmap.emoji_0x1f50d);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f50d)), R.mipmap.emoji_0x1f50d));

        softEmotionMap.put(0x1f4f1, R.mipmap.emoji_0x1f4f1);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f4f1)), R.mipmap.emoji_0x1f4f1));

        softEmotionMap.put(0x1f3e0, R.mipmap.emoji_0x1f3e0);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f3e0)), R.mipmap.emoji_0x1f3e0));

        softEmotionMap.put(0x1f697, R.mipmap.emoji_0x1f697);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f697)), R.mipmap.emoji_0x1f697));

        softEmotionMap.put(0x1f381, R.mipmap.emoji_0x1f381);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f381)), R.mipmap.emoji_0x1f381));

        softEmotionMap.put(0x26bd, R.mipmap.emoji_0x26bd);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x26bd)), R.mipmap.emoji_0x26bd));

        softEmotionMap.put(0x1f4a3, R.mipmap.emoji_0x1f4a3);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f4a3)), R.mipmap.emoji_0x1f4a3));

        softEmotionMap.put(0x1f48e, R.mipmap.emoji_0x1f48e);
        softEmotionList.add(new Emotion(String.valueOf(Character.toChars(0x1f48e)), R.mipmap.emoji_0x1f48e));

        //-----------------------------------------------------------------------------------

        imageEmotionMap.put("[笑哈哈]", R.mipmap.lxh_xiaohaha);
        langEmotionList.add(new Emotion("[笑哈哈]", R.mipmap.lxh_xiaohaha));

        imageEmotionMap.put("[好爱哦]", R.mipmap.lxh_haoaio);
        langEmotionList.add(new Emotion("[好爱哦]", R.mipmap.lxh_haoaio));

        imageEmotionMap.put("[噢耶]", R.mipmap.lxh_oye);
        langEmotionList.add(new Emotion("[噢耶]", R.mipmap.lxh_oye));

        imageEmotionMap.put("[偷乐]", R.mipmap.lxh_toule);
        langEmotionList.add(new Emotion("[偷乐]", R.mipmap.lxh_toule));

        imageEmotionMap.put("[泪流满面]", R.mipmap.lxh_leiliumanmian);
        langEmotionList.add(new Emotion("[泪流满面]", R.mipmap.lxh_leiliumanmian));

        imageEmotionMap.put("[巨汗]", R.mipmap.lxh_juhan);
        langEmotionList.add(new Emotion("[巨汗]", R.mipmap.lxh_juhan));

        imageEmotionMap.put("[抠鼻屎]", R.mipmap.lxh_koubishi);
        langEmotionList.add(new Emotion("[抠鼻屎]", R.mipmap.lxh_koubishi));

        imageEmotionMap.put("[求关注]", R.mipmap.lxh_qiuguanzhu);
        langEmotionList.add(new Emotion("[求关注]", R.mipmap.lxh_qiuguanzhu));

        imageEmotionMap.put("[好喜欢]", R.mipmap.lxh_haoxihuan);
        langEmotionList.add(new Emotion("[好喜欢]", R.mipmap.lxh_haoxihuan));

        imageEmotionMap.put("[崩溃]", R.mipmap.lxh_bengkui);
        langEmotionList.add(new Emotion("[崩溃]", R.mipmap.lxh_bengkui));

        imageEmotionMap.put("[好囧]", R.mipmap.lxh_haojiong);
        langEmotionList.add(new Emotion("[好囧]", R.mipmap.lxh_haojiong));

        imageEmotionMap.put("[震惊]", R.mipmap.lxh_zhenjing);
        langEmotionList.add(new Emotion("[震惊]", R.mipmap.lxh_zhenjing));

        imageEmotionMap.put("[别烦我]", R.mipmap.lxh_biefanwo);
        langEmotionList.add(new Emotion("[别烦我]", R.mipmap.lxh_biefanwo));

        imageEmotionMap.put("[不好意思]", R.mipmap.lxh_buhaoyisi);
        langEmotionList.add(new Emotion("[不好意思]", R.mipmap.lxh_buhaoyisi));

        imageEmotionMap.put("[羞嗒嗒]", R.mipmap.lxh_xiudada);
        langEmotionList.add(new Emotion("[羞嗒嗒]", R.mipmap.lxh_xiudada));

        imageEmotionMap.put("[得意地笑]", R.mipmap.lxh_deyidexiao);
        langEmotionList.add(new Emotion("[得意地笑]", R.mipmap.lxh_deyidexiao));

        imageEmotionMap.put("[纠结]", R.mipmap.lxh_jiujie);
        langEmotionList.add(new Emotion("[纠结]", R.mipmap.lxh_jiujie));

        imageEmotionMap.put("[给劲]", R.mipmap.lxh_feijin);
        langEmotionList.add(new Emotion("[给劲]", R.mipmap.lxh_feijin));

        imageEmotionMap.put("[悲催]", R.mipmap.lxh_beicui);
        langEmotionList.add(new Emotion("[悲催]", R.mipmap.lxh_beicui));

        imageEmotionMap.put("[甩甩手]", R.mipmap.lxh_shuaishuaishou);
        langEmotionList.add(new Emotion("[甩甩手]", R.mipmap.lxh_shuaishuaishou));

        imageEmotionMap.put("[好棒]", R.mipmap.lxh_haobang);
        langEmotionList.add(new Emotion("[好棒]", R.mipmap.lxh_haobang));

        imageEmotionMap.put("[瞧瞧]", R.mipmap.lxh_qiaoqiao);
        langEmotionList.add(new Emotion("[瞧瞧]", R.mipmap.lxh_qiaoqiao));

        imageEmotionMap.put("[不想上班]", R.mipmap.lxh_buxiangshangban);
        langEmotionList.add(new Emotion("[不想上班]", R.mipmap.lxh_buxiangshangban));

        imageEmotionMap.put("[困死了]", R.mipmap.lxh_kunsile);
        langEmotionList.add(new Emotion("[困死了]", R.mipmap.lxh_kunsile));

        imageEmotionMap.put("[许愿]", R.mipmap.lxh_xuyuan);
        langEmotionList.add(new Emotion("[许愿]", R.mipmap.lxh_xuyuan));

        imageEmotionMap.put("[丘比特]", R.mipmap.lxh_qiubite);
        langEmotionList.add(new Emotion("[丘比特]", R.mipmap.lxh_qiubite));

        imageEmotionMap.put("[有鸭梨]", R.mipmap.lxh_youyali);
        langEmotionList.add(new Emotion("[有鸭梨]", R.mipmap.lxh_youyali));

        imageEmotionMap.put("[想一想]", R.mipmap.lxh_xiangyixiang);
        langEmotionList.add(new Emotion("[想一想]", R.mipmap.lxh_xiangyixiang));

        imageEmotionMap.put("[躁狂症]", R.mipmap.kuangzaozheng);
        langEmotionList.add(new Emotion("[躁狂症]", R.mipmap.kuangzaozheng));

        imageEmotionMap.put("[转发]", R.mipmap.lxh_zhuanfa);
        langEmotionList.add(new Emotion("[转发]", R.mipmap.lxh_zhuanfa));

        imageEmotionMap.put("[互相膜拜]", R.mipmap.lxh_xianghumobai);
        langEmotionList.add(new Emotion("[互相膜拜]", R.mipmap.lxh_xianghumobai));

        imageEmotionMap.put("[雷锋]", R.mipmap.lxh_leifeng);
        langEmotionList.add(new Emotion("[雷锋]", R.mipmap.lxh_leifeng));

        imageEmotionMap.put("[杰克逊]", R.mipmap.lxh_jiekexun);
        langEmotionList.add(new Emotion("[杰克逊]", R.mipmap.lxh_jiekexun));

        imageEmotionMap.put("[玫瑰]", R.mipmap.lxh_meigui);
        langEmotionList.add(new Emotion("[玫瑰]", R.mipmap.lxh_meigui));

        imageEmotionMap.put("[hold住]", R.mipmap.lxh_holdzhu);
        langEmotionList.add(new Emotion("[hold住]", R.mipmap.lxh_holdzhu));

        imageEmotionMap.put("[群体围观]", R.mipmap.lxh_quntiweiguan);
        langEmotionList.add(new Emotion("[群体围观]", R.mipmap.lxh_quntiweiguan));

        imageEmotionMap.put("[推荐]", R.mipmap.lxh_tuijian);
        langEmotionList.add(new Emotion("[推荐]", R.mipmap.lxh_tuijian));

        imageEmotionMap.put("[赞啊]", R.mipmap.lxh_zana);
        langEmotionList.add(new Emotion("[赞啊]", R.mipmap.lxh_zana));

        imageEmotionMap.put("[被电]", R.mipmap.lxh_beidian);
        langEmotionList.add(new Emotion("[被电]", R.mipmap.lxh_beidian));

        imageEmotionMap.put("[霹雳]", R.mipmap.lxh_pili);
        langEmotionList.add(new Emotion("[霹雳]", R.mipmap.lxh_pili));

    }

    public static Integer getDefaultDrawableId(String key) {
        return imageEmotionMap.get(key);
    }

    public static int getSoftDrawableId(int key) {
        return softEmotionMap.get(key);
    }

    public synchronized static Bitmap getCacheEmotion(String key) {
        return emotionLruCache.get(key);
    }

    public synchronized static Bitmap putCacheEmotion(String key, Bitmap bitmap) {
        return emotionLruCache.put(key, bitmap);
    }

    public static ArrayList<Emotion> getDefaultEmotions() {
        return imageEmotionList;
    }

    public static ArrayList<Emotion> getSoftEmotions() {
        return softEmotionList;
    }

    public static ArrayList<Emotion> getLangEmotions() {
        return langEmotionList;
    }

}
