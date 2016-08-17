package com.caij.emore.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.caij.emore.R;
import com.lapism.searchview.SearchView;

/**
 * Created by Caij on 2016/7/26.
 */
public class MySearchView extends SearchView {

    public MySearchView(Context context) {
        super(context);
        init();
    }

    public MySearchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MySearchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MySearchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        CardView.LayoutParams params = new CardView.LayoutParams(
                CardView.LayoutParams.MATCH_PARENT,
                CardView.LayoutParams.WRAP_CONTENT
        );
        int marin = getResources().getDimensionPixelOffset(R.dimen.spacing_micro_plu);
        params.setMargins(marin, 2, marin, 2);
        mCardView.setLayoutParams(params);
    }

    public void setVersion(int version) {
        mVersion = version;
        if (mVersion == VERSION_MENU_ITEM) {
            setVisibility(View.GONE);
            mBackImageView.setImageResource(com.lapism.searchview.R.drawable.search_ic_arrow_back_black_24dp);
        }

        mVoiceImageView.setImageResource(com.lapism.searchview.R.drawable.search_ic_mic_black_24dp);
        mEmptyImageView.setImageResource(com.lapism.searchview.R.drawable.search_ic_clear_black_24dp);
    }

    @Override
    public void setTextSize(float size) {
        mEditText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
    }

    @Override
    public boolean isSearchOpen() {
        return false;
//        return super.isSearchOpen();
    }
}
