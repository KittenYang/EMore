package com.caij.emore.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.bean.SinaSearchRecommend;
import com.caij.emore.present.SearchRecommendPresent;
import com.caij.emore.present.imp.SearchRecommendPresentImp;
import com.caij.emore.remote.imp.ServerSearchRecommendImp;
import com.caij.emore.ui.adapter.SearchAdapter;
import com.caij.emore.ui.fragment.StatusAndUserSearchFragment;
import com.caij.emore.ui.view.SearchRecommendView;
import com.caij.emore.utils.InitiateSearch;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.weibo.ThemeUtils;
import com.caij.emore.widget.recyclerview.RecyclerViewOnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Caij on 2016/7/25.
 */
public class SearchRecommendActivity extends BaseActivity<SearchRecommendPresent> implements
        SearchRecommendView, RecyclerViewOnItemClickListener {

    public static final int TEXT_CHANGE_QUERY_WHAT = 100;

    @BindView(R.id.card_search)
    CardView rootView;

    SearchAdapter mSearchAdapter;
    @BindView(R.id.image_search_back)
    ImageView imageSearchBack;
    @BindView(R.id.edit_text_search)
    EditText editTextSearch;
    @BindView(R.id.clearSearch)
    ImageView clearSearch;
    @BindView(R.id.linearLayout_search)
    LinearLayout linearLayoutSearch;
    @BindView(R.id.line_divider)
    View lineDivider;
    @BindView(R.id.listView)
    ListView listView;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        final ViewTreeObserver viewTreeObserver = rootView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (rootView.getViewTreeObserver().isAlive()) {
                    rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                LogUtil.d("SearchRecommendActivity", "SearchRecommendActivity");
                InitiateSearch.handleToolBar(SearchRecommendActivity.this, rootView, listView, editTextSearch, lineDivider);
            }
        });
    }

    @Override
    protected void setTheme() {
        int themePosition = ThemeUtils.getThemePosition(this);
        setTheme(ThemeUtils.THEME_ARR[themePosition][ThemeUtils.APP_SEARCH_THEME_POSITION]);
    }

    @Override
    protected SearchRecommendPresent createPresent() {
        return new SearchRecommendPresentImp(new ServerSearchRecommendImp(), this);
    }

    private void searchTextChange(String newText) {
        mHandler.removeMessages(TEXT_CHANGE_QUERY_WHAT);
        if (TextUtils.isEmpty(newText)) {
            mSearchAdapter.clearEntities();
            mSearchAdapter.notifyDataSetChanged();
        } else {
            Message message = Message.obtain();
            message.obj = newText;
            message.what = TEXT_CHANGE_QUERY_WHAT;
            mHandler.sendMessageDelayed(message, 500);
        }
    }

    private void searchTextSubmit(String query) {
        Bundle bundle = new Bundle();
        bundle.putString(Key.ID, query);
        Intent intent = DefaultFragmentActivity.starFragmentV4(this, "搜索结果", StatusAndUserSearchFragment.class, bundle);
        startActivity(intent);
    }

    private void search(String newText) {
        mSearchAdapter.clearEntities();
        mSearchAdapter.notifyDataSetChanged();

        mPresent.search(newText);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(-1, -1);
    }

    @Override
    public void onSearchSuccess(List<SinaSearchRecommend.RecommendData> data) {
        mSearchAdapter.setEntities(data);
        mSearchAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(View view, int position) {
        SinaSearchRecommend.RecommendData data = mSearchAdapter.getItem(position);
        Bundle bundle = new Bundle();
        bundle.putString(Key.ID, data.getKey());
        Intent intent = DefaultFragmentActivity.starFragmentV4(this, "搜索结果", StatusAndUserSearchFragment.class, bundle);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        InitiateSearch.handleToolBar(SearchRecommendActivity.this, rootView, listView, editTextSearch, lineDivider);
    }
}
