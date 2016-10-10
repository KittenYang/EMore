package com.caij.emore.ui.view;

import com.caij.emore.bean.SinaSearchRecommend;

import java.util.List;

/**
 * Created by Caij on 2016/7/26.
 */
public interface SearchRecommendView extends BaseView {
    void onSearchSuccess(List<SinaSearchRecommend.RecommendData> data);
}
