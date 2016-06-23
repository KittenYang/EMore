package com.caij.weiyo.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.caij.weiyo.Key;
import com.caij.weiyo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Caij on 2016/6/17.
 */
public class PublishBottomFragment extends BaseFragment {

    @BindView(R.id.btn_location)
    LinearLayout btnLocation;
    @BindView(R.id.btnCamera)
    LinearLayout btnCamera;
    @BindView(R.id.btn_emotion)
    LinearLayout btnEmotion;
    @BindView(R.id.btn_mention)
    LinearLayout btnMention;
    @BindView(R.id.btn_trends)
    LinearLayout btnTrends;
    @BindView(R.id.btn_overflow)
    LinearLayout btnOverflow;
    @BindView(R.id.btn_send)
    LinearLayout btnSend;
    @BindView(R.id.layBtns)
    LinearLayout layBtns;

    private Unbinder unbinder;

    public static PublishBottomFragment newInstence(int type) {
        PublishBottomFragment fragment = new PublishBottomFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Key.TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_publish_buttom, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
