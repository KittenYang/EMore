package com.caij.emore.ui.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.caij.emore.Event;
import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.bean.Emotion;
import com.caij.emore.widget.recyclerview.BaseAdapter;
import com.caij.emore.widget.recyclerview.BaseViewHolder;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.widget.recyclerview.RecyclerViewOnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/6/18.
 */
public class EmotionItemFragment extends BaseFragment {

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    public static EmotionItemFragment newInstance(ArrayList<Emotion> emotions) {
        EmotionItemFragment fragment = new EmotionItemFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Key.OBJ, emotions);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emotion_item, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayList<Emotion> emotions = (ArrayList<Emotion>) getArguments().getSerializable(Key.OBJ);
        int page = emotions.size() / 20;
        if (emotions.size() % 20 > 0) {
            page ++;
        }
        viewPager.setAdapter(new MyPageAdapter(page, emotions));
    }

    private class MyPageAdapter extends PagerAdapter {

        private int page;
        ArrayList<Emotion> emotions;

        MyPageAdapter(int page, ArrayList<Emotion> emotions) {
            this.page = page;
            this.emotions = emotions;
        }

        @Override
        public int getCount() {
            return page;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = getActivity().getLayoutInflater().
                    inflate(R.layout.view_emotion_item, container, false);
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 7);
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    if (parent.getChildLayoutPosition(view) >= 14) { //最下面一行不要间距
                        outRect.bottom = 0;
                    }else {
                        outRect.bottom =  getResources().getDimensionPixelSize(R.dimen.spacing_emotion);
                    }
                }
            });
            recyclerView.setAdapter(new MyGridAdapter(container.getContext(), emotions, position));
            container.addView(view);
            return view;
        }
    }

    private static class MyGridAdapter extends BaseAdapter<Emotion, EmotionViewHolder> {

        private int position;

        public MyGridAdapter(Context context, List<Emotion> entities, int position) {
            super(context, entities);
            this.position = position;
        }

        @Override
        public int getItemCount() {
            return 21;
        }

        @Override
        public EmotionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ImageView imageView = new ImageView(parent.getContext());
            return new EmotionViewHolder(imageView, mOnItemClickListener);
        }

        @Override
        public void onBindViewHolder(EmotionViewHolder holder, int position) {
            if (position == 20) {
                holder.imageView.setImageResource(R.mipmap.compose_emotion_delete);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RxBus.getDefault().post(Event.ON_EMOTION_DELETE_CLICK, null);
                    }
                });
            }else {
                int index = this.position * 20 + position;
                if (index < getEntities().size()) {
                    final Emotion emotion = getItem(index);
                    holder.imageView.setImageResource(emotion.drawableId);
                    holder.imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RxBus.getDefault().post(Event.ON_EMOTION_CLICK, emotion);
                            LogUtil.d(emotion, "emotion click key " + emotion.key);
                        }
                    });
                }else {
                    holder.imageView.setImageResource(R.mipmap.d_aini);
                    holder.itemView.setVisibility(View.GONE);
                }
            }
        }
    }

    public static class EmotionViewHolder extends BaseViewHolder {

        private ImageView imageView;

        public EmotionViewHolder(View itemView, RecyclerViewOnItemClickListener onItemClickListener) {
            super(itemView, onItemClickListener);
            imageView = (ImageView) itemView;
        }
    }
}
