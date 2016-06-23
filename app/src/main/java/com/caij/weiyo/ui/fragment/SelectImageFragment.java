package com.caij.weiyo.ui.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caij.weiyo.Key;
import com.caij.weiyo.R;
import com.caij.weiyo.bean.Image;
import com.caij.weiyo.bean.ImageFolder;
import com.caij.weiyo.ui.adapter.FolderAdapter;
import com.caij.weiyo.ui.adapter.GridImageAdapter;
import com.caij.weiyo.utils.ExecutorServiceUtil;
import com.caij.weiyo.utils.ImageUtil;
import com.caij.weiyo.utils.LogUtil;
import com.caij.weiyo.view.recyclerview.RecyclerViewOnItemClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Caij on 2016/6/22.
 */
public class SelectImageFragment extends RecyclerViewFragment {

    private static final int REQUEST_CODE_CAPTURE = 100;

    @BindView(R.id.tv_folder)
    TextView tvFolder;
    @BindView(R.id.rl_bottom)
    RelativeLayout rlBottom;
    @BindView(R.id.recycler_view_folder)
    RecyclerView recyclerViewFolder;

    private GridImageAdapter mImageAdapter;
    private FolderAdapter mFolderAdapter;

    private Handler mHandler;

    private String camareOutputPath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initDate();
        mHandler = new Handler();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_select_image;
    }

    private void initView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mLoadMoreLoadMoreRecyclerView.setLayoutManager(gridLayoutManager);
        mLoadMoreLoadMoreRecyclerView.setBackgroundColor(getResources().getColor(R.color.white));
        mImageAdapter = new GridImageAdapter(getActivity());
        mLoadMoreLoadMoreRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.bottom = getResources().getDimensionPixelSize(R.dimen.image_item_space);
                outRect.top = getResources().getDimensionPixelSize(R.dimen.image_item_space);
                outRect.left = getResources().getDimensionPixelSize(R.dimen.image_item_space);
                outRect.right = getResources().getDimensionPixelSize(R.dimen.image_item_space);
//                if (parent.getChildLayoutPosition(view) % 3 == 0){
//                    outRect.left = 0;
//                } if (parent.getChildLayoutPosition(view) % 3 == 2) {
//                    outRect.right = 0;
//                }
            }
        });
        mLoadMoreLoadMoreRecyclerView.setAdapter(mImageAdapter);

        mFolderAdapter = new FolderAdapter(getActivity());
        recyclerViewFolder.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewFolder.setAdapter(mFolderAdapter);

        mFolderAdapter.setOnItemClickListener(new RecyclerViewOnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ImageFolder folder = mFolderAdapter.getItem(position);
                loadImageByFolderId(folder.getBucketId());
                hideImageFolder();
            }
        });

        mImageAdapter.setOnItemClickListener(new RecyclerViewOnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Image image = mImageAdapter.getItem(position);
                if (image.getType() == 1) {
                    Intent intent = new Intent();
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    camareOutputPath = ImageUtil.createCamareImage(getActivity());
                    Uri uri = Uri.fromFile(new File(camareOutputPath));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(intent, REQUEST_CODE_CAPTURE);
                }else if (image.getType() == 2) {

                }
            }
        });
    }

    private void initDate() {
        AsyncTask<Object, Object, List<ImageFolder>> folderAsyncTask = new AsyncTask<Object, Object, List<ImageFolder>>() {

            @Override
            protected List<ImageFolder> doInBackground(Object... params) {
                return getLocalImageFolder();
            }

            @Override
            protected void onPostExecute(List<ImageFolder> imageFolders) {
                super.onPostExecute(imageFolders);
                mFolderAdapter.setEntities(imageFolders);
                mFolderAdapter.notifyDataSetChanged();
                loadImageByFolderId(null);
            }
        };
        ExecutorServiceUtil.executeAsyncTask(folderAsyncTask);
    }

    @Override
    protected void onUserFirstVisible() {

    }

    private void loadImageByFolderId(final String id) {
        AsyncTask<Object, Object, List<Image>> imageAsyncTask = new AsyncTask<Object, Object, List<Image>>() {

            @Override
            protected List<Image> doInBackground(Object... params) {
                return getImagesByFolderId(id);
            }

            @Override
            protected void onPostExecute(List<Image> images) {
                super.onPostExecute(images);
                mImageAdapter.setEntities(images);
                mImageAdapter.notifyDataSetChanged();
            }
        };
        ExecutorServiceUtil.executeAsyncTask(imageAsyncTask);
    }

    private List<ImageFolder> getLocalImageFolder() {
        ArrayList<ImageFolder> folderList = new ArrayList<>();
        String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        String orderBy = MediaStore.Images.Media._ID;
        Cursor cursor = getActivity().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null, null, orderBy);
        if (cursor != null && cursor.getCount() > 0) {
            int dataColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

            String bucketId;
            String imagePath;
            String folderName;
            int folderIdColumn = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
            int folderNameColumn = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            while (cursor.moveToNext()) {
                bucketId = cursor.getString(folderIdColumn);
                imagePath = cursor.getString(dataColumn);
                folderName = cursor.getString(folderNameColumn);
                ImageFolder folderIn = contains(folderList, bucketId);
                if (folderIn == null) {
                    ImageFolder folder = new ImageFolder();
                    folder.setBucketId(bucketId);
                    folder.setImgPath(imagePath);
                    folder.setName(folderName);
                    folder.setCount(1);
                    folderList.add(folder);
                } else {
                    folderIn.setCount(folderIn.getCount() + 1);
                }

                if (cursor.isLast()) {
                    ImageFolder item = new ImageFolder();
                    item.setName("所有图片");
                    item.setCount(cursor.getCount());
                    item.setSelected(true);
                    item.setImgPath(cursor.getString(dataColumn));
                    folderList.add(item);
                }

            }

            cursor.close();
        }

        Collections.reverse(folderList);

        return folderList;
    }

    private ImageFolder contains(ArrayList<ImageFolder> folders, String bucketId) {
        for (ImageFolder folder : folders) {
            if (bucketId.equals(folder.getBucketId())) {
                return folder;
            }
        }
        return null;
    }

    private ArrayList<Image> getImagesByFolderId(String bucketId) {
        ArrayList<Image> imageList = new ArrayList<Image>();

        Image item = new Image();
        item.setType(1);
        imageList.add(item);

        String[] columns;
        String orderBy = MediaStore.Images.Media._ID + " DESC";
        String selection;
        Cursor cursor;
        if (bucketId != null) {
            columns = new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_ID};
            selection = MediaStore.Images.Media.BUCKET_ID + "=?";
            cursor = getActivity().getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    columns,
                    selection, new String[]{bucketId}, orderBy);
        } else {
            columns = new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
            cursor = getActivity().getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    columns,
                    null, null, orderBy);
        }

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                item = new Image();
                int idColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                int dataColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                item.setId(cursor.getLong(idColumn));
                item.setPath(cursor.getString(dataColumn));
                item.setType(2);
                imageList.add(item);
            }
            cursor.close();
        }

        return imageList;
    }

    private void showImageFolder() {
        recyclerViewFolder.setVisibility(View.INVISIBLE);
        //这里最开始没有recyclerViewFolder的高度， 需要通过mHandler 等测量完才开始动画
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Animator animator = ObjectAnimator.ofFloat(recyclerViewFolder, View.TRANSLATION_Y, recyclerViewFolder.getHeight(), 0);
                animator.setDuration(300);
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        recyclerViewFolder.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                animator.start();
            }
        });
    }

    private void hideImageFolder() {
        Animator animator = ObjectAnimator.ofFloat(recyclerViewFolder, View.TRANSLATION_Y, 0, recyclerViewFolder.getHeight());
        animator.setDuration(300);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                recyclerViewFolder.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    @OnClick(R.id.rl_bottom)
    public void onClick() {
        if (recyclerViewFolder.getVisibility() == View.VISIBLE) {
            hideImageFolder();
        }else {
            showImageFolder();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_CAPTURE:
                if (resultCode == Activity.RESULT_OK) {
                    if (!TextUtils.isEmpty(camareOutputPath)) {
                        Intent intent = new Intent();
                        intent.putExtra(Key.URL, camareOutputPath);
                        getActivity().setResult(Activity.RESULT_OK, intent);
                        getActivity().finish();
                    }
                }
                break;
        }
    }
}
