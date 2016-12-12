package com.caij.emore.ui.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.bean.SinaSearchRecommend;
import com.caij.emore.present.SearchRecommendPresent;
import com.caij.emore.present.imp.SearchRecommendPresentImp;
import com.caij.emore.remote.imp.ServerSearchRecommendImp;
import com.caij.emore.ui.adapter.SearchAdapter;
import com.caij.emore.ui.fragment.StatusAndUserSearchFragment;
import com.caij.emore.ui.view.SearchRecommendView;
import com.caij.emore.utils.AnimUtil;
import com.caij.emore.utils.DensityUtil;
import com.caij.emore.utils.SystemUtil;
import com.caij.emore.utils.weibo.ThemeUtils;
import com.caij.emore.widget.recyclerview.RecyclerViewOnItemClickListener;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Caij on 2016/7/25.
 */
public class SearchRecommendActivity extends BaseActivity<SearchRecommendPresent> implements
        SearchRecommendView, RecyclerViewOnItemClickListener, TextWatcher {

    public static final int TEXT_CHANGE_QUERY_WHAT = 100;

    @BindView(R.id.card_search)
    CardView rootView;
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
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private Handler mHandler;
    SearchAdapter mSearchAdapter;


    Animator mSearchViewShowAnimator;
    Animator mSearchViewHideAnimator;

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

                mSearchViewShowAnimator = createShowAnimator();
                mSearchViewHideAnimator = createHideAnimator();

                mSearchViewShowAnimator.start();
            }
        });

        initView();

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == TEXT_CHANGE_QUERY_WHAT) {
                    search((String) msg.obj);
                }
            }
        };
    }

    private void initView() {
        editTextSearch.addTextChangedListener(this);

        mSearchAdapter = new SearchAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mSearchAdapter);

        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ) {
                    String skyd = editTextSearch.getText().toString();
                    if (!TextUtils.isEmpty(skyd)){
                        searchTextSubmit(skyd);
                    }
                    return true;
                }
                return false;
            }
        });

        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(getResources().getColor(R.color.divider_timeline_item))
                .marginResId(R.dimen.spacing_micro_plu, R.dimen.spacing_micro_plu)
                .size(DensityUtil.dip2px(this, 0.4f))
                .build());

        mSearchAdapter.setOnItemClickListener(this);
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
//        super.onBackPressed();
        mSearchViewHideAnimator.start();
    }

    private Animator createShowAnimator() {
        final Animator animator;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            animator = ViewAnimationUtils.createCircularReveal(rootView,
                    rootView.getWidth() - DensityUtil.dip2px(this, 16), DensityUtil.dip2px(this, 23),
                    0, (float) Math.hypot(rootView.getWidth(), rootView.getHeight()));
        } else {
            animator = ObjectAnimator.ofFloat(rootView, "alpha", 0, 1);
        }

        animator.addListener(new AnimUtil.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                SystemUtil.showKeyBoard(SearchRecommendActivity.this);
            }
        });

        rootView.setVisibility(View.VISIBLE);
        animator.setDuration(300);
        rootView.setEnabled(true);

        return animator;
    }

    private Animator createHideAnimator() {
        final Animator animator;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            animator = ViewAnimationUtils.createCircularReveal(rootView,
                    rootView.getWidth() - DensityUtil.dip2px(this, 16), DensityUtil.dip2px(this, 23),
                    (float) Math.hypot(rootView.getWidth(), rootView.getHeight()), 0);
        } else {
            animator = ObjectAnimator.ofFloat(rootView, "alpha", 1, 0);
        }

        animator.addListener(new AnimUtil.AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                rootView.setVisibility(View.GONE);
                SystemUtil.hideKeyBoard(SearchRecommendActivity.this);

                finish();
            }
        });
        animator.setDuration(300);

        return animator;
    }

    @OnClick(R.id.image_search_back)
    public void onClick() {

    }

    @OnClick({R.id.image_search_back, R.id.clearSearch})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_search_back:
                mSearchViewHideAnimator.start();
                break;
            case R.id.clearSearch:
                editTextSearch.setText("");
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        clearSearch.setVisibility(TextUtils.isEmpty(s) ? View.GONE : View.VISIBLE);

        searchTextChange(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
