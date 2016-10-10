package com.caij.emore.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.bean.SinaSearchRecommend;
import com.caij.emore.present.SearchRecommendPresent;
import com.caij.emore.present.imp.SearchRecommendPresentImp;
import com.caij.emore.ui.view.SearchRecommendView;
import com.caij.emore.remote.imp.ServerSearchRecommendImp;
import com.caij.emore.ui.adapter.SearchAdapter;
import com.caij.emore.ui.fragment.WeiboAndUserSearchFragment;
import com.caij.emore.utils.weibo.ThemeUtils;
import com.caij.emore.widget.recyclerview.RecyclerViewOnItemClickListener;
import com.lapism.searchview.SearchView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Caij on 2016/7/25.
 */
public class SearchRecommendActivity extends BaseActivity<SearchRecommendPresent> implements
        SearchRecommendView, RecyclerViewOnItemClickListener {

    public static final int TEXT_CHANGE_QUERY_WHAT = 100;

    @BindView(R.id.searchView)
    SearchView mSearchView;
    @BindView(R.id.rl_root)
    View rootView;

    SearchAdapter mSearchAdapter;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        mSearchView.setVersion(SearchView.VERSION_MENU_ITEM);
        mSearchView.setTheme(SearchView.THEME_LIGHT, true);
        mSearchView.setDivider(false);
        mSearchView.setHint(R.string.search_hint);
        mSearchView.setVoice(false);
        mSearchView.setAnimationDuration(300);
        mSearchView.setTextSize(16);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)) {
                    searchTextSubmit(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchTextChange(newText);
                return true;
            }

        });

        mSearchView.setOnOpenCloseListener(new SearchView.OnOpenCloseListener() {
            @Override
            public void onClose() {
                finish();
            }

            @Override
            public void onOpen() {

            }
        });

        mSearchView.setHint(R.string.search_hint_text);
        mSearchAdapter = new SearchAdapter(this);
        mSearchAdapter.setOnItemClickListener(this);
        mSearchView.setAdapter(mSearchAdapter);

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == TEXT_CHANGE_QUERY_WHAT) {
                    search((String) msg.obj);
                }
            }
        };
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSearchView.open(true);
            }
        }, 200);
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
        }else {
            Message message  = Message.obtain();
            message.obj = newText;
            message.what = TEXT_CHANGE_QUERY_WHAT;
            mHandler.sendMessageDelayed(message, 500);
        }
    }

    private void searchTextSubmit(String query) {
        Bundle bundle = new Bundle();
        bundle.putString(Key.ID, query);
        Intent intent = DefaultFragmentActivity.starFragmentV4(this, "搜索结果", WeiboAndUserSearchFragment.class, bundle);
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
        Intent intent = DefaultFragmentActivity.starFragmentV4(this, "搜索结果", WeiboAndUserSearchFragment.class, bundle);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        mSearchView.close(true);
//        super.onBackPressed();
    }
}
