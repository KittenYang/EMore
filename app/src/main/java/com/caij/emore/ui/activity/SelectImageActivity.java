package com.caij.emore.ui.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.bean.Image;
import com.caij.emore.bean.ImageFolder;
import com.caij.emore.ui.adapter.FolderAdapter;
import com.caij.emore.ui.adapter.GridImageAdapter;
import com.caij.emore.utils.ExecutorServiceUtil;
import com.caij.emore.utils.ImageUtil;
import com.caij.emore.utils.NavigationUtil;
import com.caij.emore.utils.ToastUtil;
import com.caij.emore.view.recyclerview.RecyclerViewOnItemClickListener;

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
public class SelectImageActivity extends BaseToolBarActivity implements GridImageAdapter.ImageSelectListener {

    private static final int REQUEST_CODE_CAPTURE = 100;
    private static final int MY_PERMISSIONS_REQUEST_READ_IMAGE = 100;

    @BindView(R.id.tv_folder)
    TextView tvFolder;
    @BindView(R.id.rl_bottom)
    RelativeLayout rlBottom;
    @BindView(R.id.recycler_view_folder)
    RecyclerView recyclerViewFolder;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private MenuItem mMiImageCount;

    private GridImageAdapter mImageAdapter;
    private FolderAdapter mFolderAdapter;

    private Handler mHandler;
    private String camareOutputPath;
    private ArrayList<String> mSelectImages;
    private int mMaxImageSelectCount;

    public static Intent newIntent(Context context, int maxImageCount) {
        Intent intent = new Intent(context, SelectImageActivity.class);
        intent.putExtra(Key.MAX, maxImageCount);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        mMaxImageSelectCount = getIntent().getIntExtra(Key.MAX, 0);
        initView();
        mHandler = new Handler();
        mSelectImages = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
//                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                ActivityCompat.requestPermissions(getActivity(),
//                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                        MY_PERMISSIONS_REQUEST_READ_IMAGE);
//            } else {
//                ActivityCompat.requestPermissions(getActivity(),
//                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                        MY_PERMISSIONS_REQUEST_READ_IMAGE);
//            }
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_IMAGE);
        }else {
            initDate();
        }
    }

    @Override
    public int getAttachLayoutId() {
        return R.layout.fragment_select_image;
    }

    private void initView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setBackgroundColor(getResources().getColor(R.color.white));
        mImageAdapter = new GridImageAdapter(this);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.bottom = getResources().getDimensionPixelSize(R.dimen.image_item_space);
                outRect.top = getResources().getDimensionPixelSize(R.dimen.image_item_space);
                outRect.left = getResources().getDimensionPixelSize(R.dimen.image_item_space);
                outRect.right = getResources().getDimensionPixelSize(R.dimen.image_item_space);
            }
        });
        mRecyclerView.setAdapter(mImageAdapter);

        mFolderAdapter = new FolderAdapter(this);
        recyclerViewFolder.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFolder.setAdapter(mFolderAdapter);

        mFolderAdapter.setOnItemClickListener(new RecyclerViewOnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ImageFolder folder = mFolderAdapter.getItem(position);
                loadImageByFolderId(folder.getBucketId());
                hideImageFolder();
                tvFolder.setText(folder.getName());
            }
        });

        mImageAdapter.setOnItemClickListener(new RecyclerViewOnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Image image = mImageAdapter.getItem(position);
                if (image.getType() == 1) {
                    Intent intent = new Intent();
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    camareOutputPath = ImageUtil.createCameraImagePath(SelectImageActivity.this);
                    Uri uri = Uri.fromFile(new File(camareOutputPath));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(intent, REQUEST_CODE_CAPTURE);
                } else if (image.getType() == 2) {
                    ArrayList<String> paths = new ArrayList<String>();
                    paths.add(image.getPath());
                    NavigationUtil.startImagePreActivity(SelectImageActivity.this, view, paths, 0);
                }
            }
        });
        mImageAdapter.setOnSelectListener(this);
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
                mSelectImages.clear();
                mMiImageCount.setTitle(getString(R.string.complete) + "(" + mSelectImages.size() + "/" + mMaxImageSelectCount + ")");
            }
        };
        ExecutorServiceUtil.executeAsyncTask(imageAsyncTask);
    }

    private List<ImageFolder> getLocalImageFolder() {
        ArrayList<ImageFolder> folderList = new ArrayList<>();
        String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        String orderBy = MediaStore.Images.Media._ID;
        Cursor cursor = getContentResolver().query(
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
            cursor = getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    columns,
                    selection, new String[]{bucketId}, orderBy);
        } else {
            columns = new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
            cursor = getContentResolver().query(
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


    @Override
    public boolean onSelect(boolean isSelect, Image image) {
        if (isSelect && mSelectImages.size() >= mMaxImageSelectCount) {
            ToastUtil.show(this, String.format(getString(R.string.max_image_select_hint), mMaxImageSelectCount));
            return false;
        }else {
            if (isSelect) {
                mSelectImages.add(image.getPath());
            } else {
                mSelectImages.remove(image.getPath());
            }
            mMiImageCount.setTitle(getString(R.string.complete) + "(" + mSelectImages.size() + "/" + mMaxImageSelectCount + ")");
            return true;
        }
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
        } else {
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
                        ArrayList<String> paths = new ArrayList<>();
                        paths.add(camareOutputPath);
                        intent.putStringArrayListExtra(Key.IMAGE_PATHS, paths);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_image, menu);
        mMiImageCount = menu.findItem(R.id.image_count);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.image_count) {
            if (mSelectImages == null || mSelectImages.size() == 0) {
                ToastUtil.show(this, R.string.unselect_image);
            }else {
                Intent intent = new Intent();
                intent.putStringArrayListExtra(Key.IMAGE_PATHS, mSelectImages);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_IMAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initDate();
                } else {
                    ToastUtil.show(this, getString(R.string.refused_to_read_image));
                }
            }
        }
    }
}
