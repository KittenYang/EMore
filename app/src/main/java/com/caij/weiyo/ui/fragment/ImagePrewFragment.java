package com.caij.weiyo.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.caij.weiyo.Key;
import com.caij.weiyo.R;
import com.caij.weiyo.utils.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Caij on 2016/6/24.
 */
public class ImagePrewFragment extends BaseFragment {

    @BindView(R.id.iv_image)
    ImageView mIvImage;

    private PhotoViewAttacher mAttacher;

    public static ImagePrewFragment newInstance(String path) {
        ImagePrewFragment fragment = new ImagePrewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Key.IMAGE_PATH, path);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_prew, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String path = getArguments().getString(Key.IMAGE_PATH);
        if (path != null && path.startsWith("/")) {
            path = "file://" + path;
        }
        ImageLoader.ImageConfig config = new ImageLoader.ImageConfigBuild()
                .setScaleType(ImageLoader.ScaleType.FIT_CENTER).build();
        ImageLoader.load(getActivity(), mIvImage, path, android.R.color.black, config);
    }

}
