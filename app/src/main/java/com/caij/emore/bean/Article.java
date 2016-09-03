package com.caij.emore.bean;

import android.text.Spanned;

import com.caij.emore.bean.response.Response;

/**
 * Created by Caij on 2016/9/3.
 */
public class Article extends Response {

    private transient Spanned contextSpanned;

    public Spanned getContextSpanned() {
        return contextSpanned;
    }

    public void setContextSpanned(Spanned contextSpanned) {
        this.contextSpanned = contextSpanned;
    }

    /**
     * code : 100000
     * msg :
     * data : {"article":"<div class=\"H5_add_a S_bor1\" node-type=\"userProfile\" data-url=\"sinaweibo://userinfo?uid=1750070171\">\n\t<div class=\"face\">\n\t\t<img src=\"http://tva3.sinaimg.cn/crop.133.113.754.754.50/684ff39bgw1f6wlmiignrj20rt0rtta8.jpg\" alt=\"36氪\" />\n\t\t\t\t<i class=\"icon icon-bluev\"><\/i>\n\t\t\t<\/div>\n\t<div class=\"con S_bor1\">\n\t\t<div class=\"t H5_autocut\">36氪\t\t\t<i class=\"icon-bulev\"><\/i>\t\t<\/div>\n\t\t <div class=\"c H5_autocut S_txt2\">36氪（36Kr.com）是中国领先的科技新媒体，报道最新的互联网科技新闻以及最有潜力的互联网创业企业。36氪分享交流群：283286571<\/div>\n\t<\/div>\n\t\t\t\t<div class=\"box-plus\">\n\t\t\t\t\t\t\t<button class=\"btn added_v2\" disabled=\"\"><i class=\"icon-font icon-font-added\"><\/i><\/button>\n\t\t\t\t\t<\/div>\n\t\t<\/div>\n\n<div class=\"header\">\n<h1 class=\"title\" node-type=\"articleTitle\">大部分人已经失去挣一个亿的机会了<\/h1>\n\t<div class=\"info clearfix\" >\n\t\t<span class=\"f1\" node-type=\"articleInfo\">\n\t\t\t<span class=\"time\">2016-08-29 19:18<\/span>\n\t\t\t\t\t<\/span>\n\t\t<span class=\"f2\" node-type=\"articleInfo\">\n\n\t\t\t <span class=\"read\" node-type=\"read_count_box\" style=\"display:none\">\n\t\t\t        阅读 <span node-type=\"read_count\">0<\/span>\n\n\t\t    <\/span>\n\t\t\t\t<\/span>\n\t<\/div>\n\t<div class=\"share layout-box\" node-type=\"share\">\n\t    <div class=\"box-col\"><button suda-url=\"http://beacon.sina.com.cn/e.gif?longblog||||weibo||||longblog||\" class=\"btn btn_c1\"><i class=\"icon-font icon-font-wb\"><\/i><\/button><\/div>\n\t\t<div class=\"box-col\"><button suda-url=\"http://beacon.sina.com.cn/e.gif?longblog||||sixin||||longblog||\" class=\"btn btn_c2\"><i class=\"icon-font icon-font-wbmsg\"><\/i><\/button><\/div>\n\t\t<div class=\"box-col\"><button suda-url=\"http://beacon.sina.com.cn/e.gif?longblog||||weixin||||longblog||\" class=\"btn btn_c3\"><i class=\"icon-font icon-font-wx\"><\/i><\/button><\/div>\n\t\t<div class=\"box-col\">\t<button suda-url=\"http://beacon.sina.com.cn/e.gif?longblog||||pengyouquan||||longblog||\" class=\"btn btn_c4\"><i class=\"icon-font icon-font-wxshow\"><\/i><\/button><\/div>\n\t<\/div>\n<\/div>\n<div class=\"WBA_content clearfix\">\n\t<p><span style=\"display:block;\" node-type=\"imgs\" node-data=\"http://tc.sinaimg.cn/maxwidth.2048/tc.service.weibo.com/pic_36krcnd_com/daf1414f25da83e9c107ef1332240dd3.jpg\"><i class=\"H5_img_loading\" style=\"width:200px; height:111px;\"; node-type=\"imgLoad\" replace=\"http://tc.sinaimg.cn/maxwidth.2048/tc.service.weibo.com/pic_36krcnd_com/daf1414f25da83e9c107ef1332240dd3.jpg\"><\/i><\/span><\/p><p>编者按：本文来源微信公众号\u201c青山资本\u201d（ID：cyanhillvc），授权36氪发布。项目投递：<a>BP@hwazing.com<\/a><\/p><p>好好的周一，今天被万达集团董事长王健林所说的\u201c小目标\u201d刷屏了。<br/><\/p><p><span style=\"display:block;\" node-type=\"imgs\" node-data=\"http://tc.sinaimg.cn/maxwidth.2048/tc.service.weibo.com/pic_36krcnd_com/5681abef6dadc656880802f1e6a25500.jpg\"><i class=\"H5_img_loading\" style=\"width:200px; height:111px;\"; node-type=\"imgLoad\" replace=\"http://tc.sinaimg.cn/maxwidth.2048/tc.service.weibo.com/pic_36krcnd_com/5681abef6dadc656880802f1e6a25500.jpg\"><\/i><\/span><\/p><p>\u201c先定一个能达到的小目标\u201d<\/p><p><span style=\"display:block;\" node-type=\"imgs\" node-data=\"http://tc.sinaimg.cn/maxwidth.2048/tc.service.weibo.com/pic_36krcnd_com/7414742991e6bf1d0b4170c1ddaafc51.jpg\"><i class=\"H5_img_loading\" style=\"width:200px; height:111px;\"; node-type=\"imgLoad\" replace=\"http://tc.sinaimg.cn/maxwidth.2048/tc.service.weibo.com/pic_36krcnd_com/7414742991e6bf1d0b4170c1ddaafc51.jpg\"><\/i><\/span><\/p><p>\u201c比方说我先挣它一个亿\u201d<\/p><p>好多吃瓜群众都在酸酸地调侃：一个亿很多吗？！<\/p><p><b>其实，我们中的大多数人已经失去了挣一个亿的机会，或者说其实没有挣一个亿的能力！<\/b><\/p><p>悲伤逆流成河~~~<\/p><p>我们先看两个人，起个中文名，张三和李四。两人都很聪明，具远大抱负，并来自同样适度的中等背景。但是，性格和行为方式有很大不同。<\/p><ul><li><p>张三积极参与社交活动，很受欢迎；他是很多校园团体成员，并且通常是领导。李四沉着，矜持；他不参加团体活动；他通常被注意到，但既不被喜欢，也不遭讨厌；有人无明显原因而怨恨他。<\/p><\/li><li><p>张三兴趣广泛，可得到的工作总不止一个；李四只选择一份工作\u2014\u2014追求某种特殊的任务或在大学课程之外选修\u2014\u2014他将所有业余时间都致力于此。<\/p><\/li><li><p>张三能使自己轻易适应他人，但发现让自己适应变化的环境却更困难；李四能使自己适应环境，但与人打交道时绝不退让。<\/p><\/li><li><p>张三学习成绩全部优秀；李四成绩很不稳定：有些课程得90分以上，有些课程只能刚刚及格。<\/p><\/li><li><p>张三在人们心目中形象充满阳光，活泼快乐。李四形象严肃，认真。但有些罕见而短暂的迹象似乎表明：在他们隐藏的内心世界，角色发生了颠倒：沉着活泼的是李四；而张三则受到某种可怕、难以形容的恐惧所驱使。<\/p><\/li><li><p>你觉得未来谁能挣一个亿？ 相当多的人会选择张三。<\/p><\/li><\/ul><p><b>但实际上，其实，李四才是财富创造者。张三其实不会\"挣\"钱，他只是财富占有者。<\/b><\/p><p>极少人会做如此地区分财富创造者和财富占有者的不同。但美国小说家安·兰德在《商人为什么需要哲学》一书中，提出了财富创造者和财富占有者的不同，并做了明确的区分。 在他看来，也是大多数人未能真正觉察的：<\/p><p>大多数人将所有富人合在一起，形成同一种类，他们拒绝考虑本质性问题：财富的来源，以及获得财富的方式。<\/p><p>他给出的理由是：<\/p><p>货币是交换工具；只要它能被用来与物质产品和服务交换，它就代表财富。财富不会自然增长；它必须由人们创造。大自然只是给我们提供原材料，但正是人类的智力必须去发现如何利用原材料的知识。是人类思维和劳动将原材料转换成食物、衣服、住所或电视机\u2014\u2014转换成人们为生存、舒适和快乐所需要的所有产品。<\/p><p><b>财富创造者是发现者，他将其发现转换成物质产品。<\/b>在一个劳动分工复杂的工业社会，可能就是一或两个人的合伙：科学家，他们发现新知识；实业家（也就是商人），发现如何使用那种知识，如何将物质资源和人类劳动整合进企业，以生产适于销售的产品。<\/p><p><b>财富占有者则完全是不同类型。<\/b>从实质上看，他不具\u201c创造性\u201d\u2014\u2014他寻求变得富有，但不是通过征服自然，而是通过操纵人；不是通过智识努力，而是通过社交策略。他不生产，他在重新分配：他只是将已存在的财富从主人口袋转移到自己口袋。<\/p><p>财富占有者可能成为\u201c政治家\u201d\u2014\u2014或是\u201c寻求捷径\u201d的商人\u2014\u2014或是那种\u201c混合经济\u201d的破坏性产物：商人通过政府恩惠而变得富有，例如特权、补助或特许；也就是说他们通过合法的力量而变得富有。<\/p><p>\n如何区分财富占有者和创造者？<\/p><p>没有任何单一外部特征能被用来作为创造财富之品质的可靠标志。归于张三和李四的品质可能发生变化。<\/p><p>但是其品质的最后净重总是概括了同样要点：<b>财富创造者的本质特征是其独立判断；而财富占有者的本质特征是其社会依赖。<\/b><\/p><ul><li><p>具有独立判断之人是个极度自负之人：他相信自己处理存在问题的心智能力。他看着这个世界，想要知道：\u201c能做什么呢？\u201d或者：\u201c事态如何能得到改善？\u201d<\/p><\/li><li><b>最重要的是：财富创造者是发明者和革新者。他性格中最明显缺失的品质是顺从，也就是，被动接受被寄予的、已知道的、已确立的东西或是现状。<\/b><\/li><li><p>他决不说：\u201c对我爷爷足够有益的东西对我也足够有益。\u201d他只会说：\u201c昨天对我足够有益的东西，明天不会还对我足够有益。\u201d<\/p><\/li><\/ul><p>他不会坐等\u201c好运\u201d来临，或是人们给他机会。<b>他自己创造并利用机会。他决不抱怨：\u201c我无法避免！\u201d\u2014\u2014他能避免，并且做到了。<\/b><\/p><ul><li><p>从不创造财富的人是\u201c两面下注\u201d之人，他们\u201c谨慎行事\u201d，等待机会跟随潮流；他们是赌徒或不顾一切的投机家，根据当下的刺激、道听途说或自己无法解释的情感以及盲目地直觉参与赌博。<\/p><\/li><li><p>财富创造者不采取上述两种方式。<\/p><\/li><li><b>他不等待潮流\u2014\u2014他设定潮流的方向。他不赌博\u2014\u2014他采取\u201c适当的冒险\u201d，自己真正承担上述两种情形试图忽视的责任：也就是判断的责任。<\/b><\/li><\/ul><p>从不创造财富之人有一种\u201c雇员心态\u201d，即使他从事的是行政工作；他想方设法逃脱最低程度的努力，就好像任何努力都是一种不合理的要求；当他不能采取正确行动时，他大声叫喊：\u201c可是没有人告诉我如何行动！\u201d<\/p><p>财富创造者有一种\u201c雇主心态\u201d，即使当他只是一个办公室的勤杂工\u2014\u2014那就是为什么他不会长期做勤杂工的原因。在每一份工作中，他都致力于最大程度的努力；学会他所能学会的有关商业的一切，这远远超过他的工作需要。他从不需要被告知\u2014\u2014即使是面对其通常义务之外的情形。这些都是他为什么从一个办公室勤杂工升迁到公司总裁的原因。<\/p><p>在他通常严酷且面无表情的外表背后，财富创造者总以一个爱人的激情、一个改革者的热情、一个圣人的奉献及一个受难者的忍耐致力于他的事业。作为一项标准，他充满皱纹的前额和资产负债表是其能够允许世人了解的惟一证据。<\/p><p>注意，其新冒险事业是长期发展，在它们能赢利前需要几十年时间。那些梦想通过抽彩票式赌博而赢得财富的人绝不会理解他的心理。<b>只有\u201c财富占有者\u201d的生活和行为是短期规划的，他的眼光决不超越当下时刻；\u201c财富创造者\u201d的生活、思想和行为都是长期规划的。<\/b>因为只有对自己判断拥有完全信心，他对未来也就拥有了完全信心，只有长期计划才能满足他利益。<\/p><p>对财富创造者而言（对艺术家也是如此），工作不是一项痛苦义务或是必要的恶，而是一种生活方式；对他来说，生产活动是本质，是存在的意义和乐趣；这是充满活力的状态。<\/p><p>更可贵的是，\u201c财富创造者\u201d并非如此热衷于财富。对他来说，财富只是达到目的的一种手段\u2014\u2014拓展其活动范围的手段。大多数财富创造者对奢华的生活漠然处之，他们的生活方式与其财富相比，其简朴程度令人吃惊。<\/p><p>\n有多少人会是真正的财富创造者？<\/p><p>格林斯潘经济咨询公司总裁艾伦·格林斯潘大胆估算，在他看来，在我们的商业世界，真正的财富创造者\u2014\u2014也就是完全自治并具独立判断能力之人\u2014\u2014占多大比例。他思考片刻，有点伤心地回答：<b>\u201c在华尔街\u2014\u2014大约是 5%；在工业界\u2014\u2014大约是 15%。\u201d<\/b><\/p><p>当然，在中国，也不超过这个比例。似乎，大多数人都被排除在财富创造者的行列了。所以，真正能\u201c挣\u201d一个亿的人寥寥无几。<\/p><p>好在，这个创业的时代，给了我们中的很多人天时和地利。在我们身边，有很多人具备这样的品质：<\/p><p>独立判断、不安于现状，自己创造和利用机会，自己设定潮流的方向，用改变世界的决心，做一件极为需要忍耐的事情。<\/p><p>就是说的我们当中的这些创业者了！<\/p><p>本文内容引自安·兰德《商人为什么需要哲学》，由吕建高翻译。<\/p><\/div>\n\n<div class=\"link\">\n\t\t<\/div>\n    <script type=\"text/javascript\">\n        $CONFIG['rewardcomponent']='seller=1750070171&bid=1000234771&oid=1022%3A2309351000014013890869534970&access_type=mobileLayer&share=1&sign=b627a5fe48091805376ef859946ff742';\n        $CONFIG['extendparam']='type=layer&version=14969db6a189e619'; \n    <\/script>\n    <div id=\"pl_reward\">\n    <\/div>\n    <script type=\"text/javascript\" src=\"http://js.t.sinajs.cn/t5/apps/fans_service_platform/js/pl/rewardPlug/index.js?version=14969db6a189e619\"><\/script>\n\n\n","config":{"uid":"1750070171","id":"","cid":"2309351000014013890869534970","vid":"2300562462","object_id":"1022:2309351000014013890869534970","v_p":"5","from":"","wm":"","ip":"","containerid":"2309351000014013890869534970","v":"1","page":"1","count":"20","index_count":"3","max_id":"0","read_count":"13万+","is_owner":"","is_login":"1","desc":"","image":"http://tc.sinaimg.cn/maxwidth.2048/tc.service.weibo.com/pic_36krcnd_com/daf1414f25da83e9c107ef1332240dd3.jpg","author":"36氪","card_ad":false,"comment":true,"ispay":0,"third_module":true,"copyright":0},"scripts":"","title":"大部分人已经失去挣一个亿的机会了","version":"2"}
     */

    private int code;
    private String msg;
    /**
     * article : <div class="H5_add_a S_bor1" node-type="userProfile" data-url="sinaweibo://userinfo?uid=1750070171">
     <div class="face">
     <img src="http://tva3.sinaimg.cn/crop.133.113.754.754.50/684ff39bgw1f6wlmiignrj20rt0rtta8.jpg" alt="36氪" />
     <i class="icon icon-bluev"></i>
     </div>
     <div class="con S_bor1">
     <div class="t H5_autocut">36氪			<i class="icon-bulev"></i>		</div>
     <div class="c H5_autocut S_txt2">36氪（36Kr.com）是中国领先的科技新媒体，报道最新的互联网科技新闻以及最有潜力的互联网创业企业。36氪分享交流群：283286571</div>
     </div>
     <div class="box-plus">
     <button class="btn added_v2" disabled=""><i class="icon-font icon-font-added"></i></button>
     </div>
     </div>

     <div class="header">
     <h1 class="title" node-type="articleTitle">大部分人已经失去挣一个亿的机会了</h1>
     <div class="info clearfix" >
     <span class="f1" node-type="articleInfo">
     <span class="time">2016-08-29 19:18</span>
     </span>
     <span class="f2" node-type="articleInfo">

     <span class="read" node-type="read_count_box" style="display:none">
     阅读 <span node-type="read_count">0</span>

     </span>
     </span>
     </div>
     <div class="share layout-box" node-type="share">
     <div class="box-col"><button suda-url="http://beacon.sina.com.cn/e.gif?longblog||||weibo||||longblog||" class="btn btn_c1"><i class="icon-font icon-font-wb"></i></button></div>
     <div class="box-col"><button suda-url="http://beacon.sina.com.cn/e.gif?longblog||||sixin||||longblog||" class="btn btn_c2"><i class="icon-font icon-font-wbmsg"></i></button></div>
     <div class="box-col"><button suda-url="http://beacon.sina.com.cn/e.gif?longblog||||weixin||||longblog||" class="btn btn_c3"><i class="icon-font icon-font-wx"></i></button></div>
     <div class="box-col">	<button suda-url="http://beacon.sina.com.cn/e.gif?longblog||||pengyouquan||||longblog||" class="btn btn_c4"><i class="icon-font icon-font-wxshow"></i></button></div>
     </div>
     </div>
     <div class="WBA_content clearfix">
     <p><span style="display:block;" node-type="imgs" node-data="http://tc.sinaimg.cn/maxwidth.2048/tc.service.weibo.com/pic_36krcnd_com/daf1414f25da83e9c107ef1332240dd3.jpg"><i class="H5_img_loading" style="width:200px; height:111px;"; node-type="imgLoad" replace="http://tc.sinaimg.cn/maxwidth.2048/tc.service.weibo.com/pic_36krcnd_com/daf1414f25da83e9c107ef1332240dd3.jpg"></i></span></p><p>编者按：本文来源微信公众号“青山资本”（ID：cyanhillvc），授权36氪发布。项目投递：<a>BP@hwazing.com</a></p><p>好好的周一，今天被万达集团董事长王健林所说的“小目标”刷屏了。<br/></p><p><span style="display:block;" node-type="imgs" node-data="http://tc.sinaimg.cn/maxwidth.2048/tc.service.weibo.com/pic_36krcnd_com/5681abef6dadc656880802f1e6a25500.jpg"><i class="H5_img_loading" style="width:200px; height:111px;"; node-type="imgLoad" replace="http://tc.sinaimg.cn/maxwidth.2048/tc.service.weibo.com/pic_36krcnd_com/5681abef6dadc656880802f1e6a25500.jpg"></i></span></p><p>“先定一个能达到的小目标”</p><p><span style="display:block;" node-type="imgs" node-data="http://tc.sinaimg.cn/maxwidth.2048/tc.service.weibo.com/pic_36krcnd_com/7414742991e6bf1d0b4170c1ddaafc51.jpg"><i class="H5_img_loading" style="width:200px; height:111px;"; node-type="imgLoad" replace="http://tc.sinaimg.cn/maxwidth.2048/tc.service.weibo.com/pic_36krcnd_com/7414742991e6bf1d0b4170c1ddaafc51.jpg"></i></span></p><p>“比方说我先挣它一个亿”</p><p>好多吃瓜群众都在酸酸地调侃：一个亿很多吗？！</p><p><b>其实，我们中的大多数人已经失去了挣一个亿的机会，或者说其实没有挣一个亿的能力！</b></p><p>悲伤逆流成河~~~</p><p>我们先看两个人，起个中文名，张三和李四。两人都很聪明，具远大抱负，并来自同样适度的中等背景。但是，性格和行为方式有很大不同。</p><ul><li><p>张三积极参与社交活动，很受欢迎；他是很多校园团体成员，并且通常是领导。李四沉着，矜持；他不参加团体活动；他通常被注意到，但既不被喜欢，也不遭讨厌；有人无明显原因而怨恨他。</p></li><li><p>张三兴趣广泛，可得到的工作总不止一个；李四只选择一份工作——追求某种特殊的任务或在大学课程之外选修——他将所有业余时间都致力于此。</p></li><li><p>张三能使自己轻易适应他人，但发现让自己适应变化的环境却更困难；李四能使自己适应环境，但与人打交道时绝不退让。</p></li><li><p>张三学习成绩全部优秀；李四成绩很不稳定：有些课程得90分以上，有些课程只能刚刚及格。</p></li><li><p>张三在人们心目中形象充满阳光，活泼快乐。李四形象严肃，认真。但有些罕见而短暂的迹象似乎表明：在他们隐藏的内心世界，角色发生了颠倒：沉着活泼的是李四；而张三则受到某种可怕、难以形容的恐惧所驱使。</p></li><li><p>你觉得未来谁能挣一个亿？ 相当多的人会选择张三。</p></li></ul><p><b>但实际上，其实，李四才是财富创造者。张三其实不会"挣"钱，他只是财富占有者。</b></p><p>极少人会做如此地区分财富创造者和财富占有者的不同。但美国小说家安·兰德在《商人为什么需要哲学》一书中，提出了财富创造者和财富占有者的不同，并做了明确的区分。 在他看来，也是大多数人未能真正觉察的：</p><p>大多数人将所有富人合在一起，形成同一种类，他们拒绝考虑本质性问题：财富的来源，以及获得财富的方式。</p><p>他给出的理由是：</p><p>货币是交换工具；只要它能被用来与物质产品和服务交换，它就代表财富。财富不会自然增长；它必须由人们创造。大自然只是给我们提供原材料，但正是人类的智力必须去发现如何利用原材料的知识。是人类思维和劳动将原材料转换成食物、衣服、住所或电视机——转换成人们为生存、舒适和快乐所需要的所有产品。</p><p><b>财富创造者是发现者，他将其发现转换成物质产品。</b>在一个劳动分工复杂的工业社会，可能就是一或两个人的合伙：科学家，他们发现新知识；实业家（也就是商人），发现如何使用那种知识，如何将物质资源和人类劳动整合进企业，以生产适于销售的产品。</p><p><b>财富占有者则完全是不同类型。</b>从实质上看，他不具“创造性”——他寻求变得富有，但不是通过征服自然，而是通过操纵人；不是通过智识努力，而是通过社交策略。他不生产，他在重新分配：他只是将已存在的财富从主人口袋转移到自己口袋。</p><p>财富占有者可能成为“政治家”——或是“寻求捷径”的商人——或是那种“混合经济”的破坏性产物：商人通过政府恩惠而变得富有，例如特权、补助或特许；也就是说他们通过合法的力量而变得富有。</p><p>
     如何区分财富占有者和创造者？</p><p>没有任何单一外部特征能被用来作为创造财富之品质的可靠标志。归于张三和李四的品质可能发生变化。</p><p>但是其品质的最后净重总是概括了同样要点：<b>财富创造者的本质特征是其独立判断；而财富占有者的本质特征是其社会依赖。</b></p><ul><li><p>具有独立判断之人是个极度自负之人：他相信自己处理存在问题的心智能力。他看着这个世界，想要知道：“能做什么呢？”或者：“事态如何能得到改善？”</p></li><li><b>最重要的是：财富创造者是发明者和革新者。他性格中最明显缺失的品质是顺从，也就是，被动接受被寄予的、已知道的、已确立的东西或是现状。</b></li><li><p>他决不说：“对我爷爷足够有益的东西对我也足够有益。”他只会说：“昨天对我足够有益的东西，明天不会还对我足够有益。”</p></li></ul><p>他不会坐等“好运”来临，或是人们给他机会。<b>他自己创造并利用机会。他决不抱怨：“我无法避免！”——他能避免，并且做到了。</b></p><ul><li><p>从不创造财富的人是“两面下注”之人，他们“谨慎行事”，等待机会跟随潮流；他们是赌徒或不顾一切的投机家，根据当下的刺激、道听途说或自己无法解释的情感以及盲目地直觉参与赌博。</p></li><li><p>财富创造者不采取上述两种方式。</p></li><li><b>他不等待潮流——他设定潮流的方向。他不赌博——他采取“适当的冒险”，自己真正承担上述两种情形试图忽视的责任：也就是判断的责任。</b></li></ul><p>从不创造财富之人有一种“雇员心态”，即使他从事的是行政工作；他想方设法逃脱最低程度的努力，就好像任何努力都是一种不合理的要求；当他不能采取正确行动时，他大声叫喊：“可是没有人告诉我如何行动！”</p><p>财富创造者有一种“雇主心态”，即使当他只是一个办公室的勤杂工——那就是为什么他不会长期做勤杂工的原因。在每一份工作中，他都致力于最大程度的努力；学会他所能学会的有关商业的一切，这远远超过他的工作需要。他从不需要被告知——即使是面对其通常义务之外的情形。这些都是他为什么从一个办公室勤杂工升迁到公司总裁的原因。</p><p>在他通常严酷且面无表情的外表背后，财富创造者总以一个爱人的激情、一个改革者的热情、一个圣人的奉献及一个受难者的忍耐致力于他的事业。作为一项标准，他充满皱纹的前额和资产负债表是其能够允许世人了解的惟一证据。</p><p>注意，其新冒险事业是长期发展，在它们能赢利前需要几十年时间。那些梦想通过抽彩票式赌博而赢得财富的人绝不会理解他的心理。<b>只有“财富占有者”的生活和行为是短期规划的，他的眼光决不超越当下时刻；“财富创造者”的生活、思想和行为都是长期规划的。</b>因为只有对自己判断拥有完全信心，他对未来也就拥有了完全信心，只有长期计划才能满足他利益。</p><p>对财富创造者而言（对艺术家也是如此），工作不是一项痛苦义务或是必要的恶，而是一种生活方式；对他来说，生产活动是本质，是存在的意义和乐趣；这是充满活力的状态。</p><p>更可贵的是，“财富创造者”并非如此热衷于财富。对他来说，财富只是达到目的的一种手段——拓展其活动范围的手段。大多数财富创造者对奢华的生活漠然处之，他们的生活方式与其财富相比，其简朴程度令人吃惊。</p><p>
     有多少人会是真正的财富创造者？</p><p>格林斯潘经济咨询公司总裁艾伦·格林斯潘大胆估算，在他看来，在我们的商业世界，真正的财富创造者——也就是完全自治并具独立判断能力之人——占多大比例。他思考片刻，有点伤心地回答：<b>“在华尔街——大约是 5%；在工业界——大约是 15%。”</b></p><p>当然，在中国，也不超过这个比例。似乎，大多数人都被排除在财富创造者的行列了。所以，真正能“挣”一个亿的人寥寥无几。</p><p>好在，这个创业的时代，给了我们中的很多人天时和地利。在我们身边，有很多人具备这样的品质：</p><p>独立判断、不安于现状，自己创造和利用机会，自己设定潮流的方向，用改变世界的决心，做一件极为需要忍耐的事情。</p><p>就是说的我们当中的这些创业者了！</p><p>本文内容引自安·兰德《商人为什么需要哲学》，由吕建高翻译。</p></div>

     <div class="link">
     </div>
     <script type="text/javascript">
     $CONFIG['rewardcomponent']='seller=1750070171&bid=1000234771&oid=1022%3A2309351000014013890869534970&access_type=mobileLayer&share=1&sign=b627a5fe48091805376ef859946ff742';
     $CONFIG['extendparam']='type=layer&version=14969db6a189e619';
     </script>
     <div id="pl_reward">
     </div>
     <script type="text/javascript" src="http://js.t.sinajs.cn/t5/apps/fans_service_platform/js/pl/rewardPlug/index.js?version=14969db6a189e619"></script>



     * config : {"uid":"1750070171","id":"","cid":"2309351000014013890869534970","vid":"2300562462","object_id":"1022:2309351000014013890869534970","v_p":"5","from":"","wm":"","ip":"","containerid":"2309351000014013890869534970","v":"1","page":"1","count":"20","index_count":"3","max_id":"0","read_count":"13万+","is_owner":"","is_login":"1","desc":"","image":"http://tc.sinaimg.cn/maxwidth.2048/tc.service.weibo.com/pic_36krcnd_com/daf1414f25da83e9c107ef1332240dd3.jpg","author":"36氪","card_ad":false,"comment":true,"ispay":0,"third_module":true,"copyright":0}
     * scripts :
     * title : 大部分人已经失去挣一个亿的机会了
     * version : 2
     */

    private Data data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private String article;
        /**
         * uid : 1750070171
         * id :
         * cid : 2309351000014013890869534970
         * vid : 2300562462
         * object_id : 1022:2309351000014013890869534970
         * v_p : 5
         * from :
         * wm :
         * ip :
         * containerid : 2309351000014013890869534970
         * v : 1
         * page : 1
         * count : 20
         * index_count : 3
         * max_id : 0
         * read_count : 13万+
         * is_owner :
         * is_login : 1
         * desc :
         * image : http://tc.sinaimg.cn/maxwidth.2048/tc.service.weibo.com/pic_36krcnd_com/daf1414f25da83e9c107ef1332240dd3.jpg
         * author : 36氪
         * card_ad : false
         * comment : true
         * ispay : 0
         * third_module : true
         * copyright : 0
         */

        private Config config;
        private String scripts;
        private String title;
        private String version;

        public String getArticle() {
            return article;
        }

        public void setArticle(String article) {
            this.article = article;
        }

        public Config getConfig() {
            return config;
        }

        public void setConfig(Config config) {
            this.config = config;
        }

        public String getScripts() {
            return scripts;
        }

        public void setScripts(String scripts) {
            this.scripts = scripts;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public static class Config {
            private String uid;
            private String id;
            private String cid;
            private String vid;
            private String object_id;
            private String v_p;
            private String from;
            private String wm;
            private String ip;
            private String containerid;
            private String v;
            private String page;
            private String count;
            private String index_count;
            private String max_id;
            private String read_count;
            private String is_owner;
            private String is_login;
            private String desc;
            private String image;
            private String author;
            private boolean card_ad;
            private boolean comment;
            private int ispay;
            private boolean third_module;
            private int copyright;

            public String getUid() {
                return uid;
            }

            public void setUid(String uid) {
                this.uid = uid;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getCid() {
                return cid;
            }

            public void setCid(String cid) {
                this.cid = cid;
            }

            public String getVid() {
                return vid;
            }

            public void setVid(String vid) {
                this.vid = vid;
            }

            public String getObject_id() {
                return object_id;
            }

            public void setObject_id(String object_id) {
                this.object_id = object_id;
            }

            public String getV_p() {
                return v_p;
            }

            public void setV_p(String v_p) {
                this.v_p = v_p;
            }

            public String getFrom() {
                return from;
            }

            public void setFrom(String from) {
                this.from = from;
            }

            public String getWm() {
                return wm;
            }

            public void setWm(String wm) {
                this.wm = wm;
            }

            public String getIp() {
                return ip;
            }

            public void setIp(String ip) {
                this.ip = ip;
            }

            public String getContainerid() {
                return containerid;
            }

            public void setContainerid(String containerid) {
                this.containerid = containerid;
            }

            public String getV() {
                return v;
            }

            public void setV(String v) {
                this.v = v;
            }

            public String getPage() {
                return page;
            }

            public void setPage(String page) {
                this.page = page;
            }

            public String getCount() {
                return count;
            }

            public void setCount(String count) {
                this.count = count;
            }

            public String getIndex_count() {
                return index_count;
            }

            public void setIndex_count(String index_count) {
                this.index_count = index_count;
            }

            public String getMax_id() {
                return max_id;
            }

            public void setMax_id(String max_id) {
                this.max_id = max_id;
            }

            public String getRead_count() {
                return read_count;
            }

            public void setRead_count(String read_count) {
                this.read_count = read_count;
            }

            public String getIs_owner() {
                return is_owner;
            }

            public void setIs_owner(String is_owner) {
                this.is_owner = is_owner;
            }

            public String getIs_login() {
                return is_login;
            }

            public void setIs_login(String is_login) {
                this.is_login = is_login;
            }

            public String getDesc() {
                return desc;
            }

            public void setDesc(String desc) {
                this.desc = desc;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public String getAuthor() {
                return author;
            }

            public void setAuthor(String author) {
                this.author = author;
            }

            public boolean isCard_ad() {
                return card_ad;
            }

            public void setCard_ad(boolean card_ad) {
                this.card_ad = card_ad;
            }

            public boolean isComment() {
                return comment;
            }

            public void setComment(boolean comment) {
                this.comment = comment;
            }

            public int getIspay() {
                return ispay;
            }

            public void setIspay(int ispay) {
                this.ispay = ispay;
            }

            public boolean isThird_module() {
                return third_module;
            }

            public void setThird_module(boolean third_module) {
                this.third_module = third_module;
            }

            public int getCopyright() {
                return copyright;
            }

            public void setCopyright(int copyright) {
                this.copyright = copyright;
            }
        }
    }
}
