

package com.nsb.xmatrix.fragment.component.camera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.imageview.crop.CropImageType;
import com.xuexiang.xui.widget.imageview.crop.CropImageView;
import com.nsb.xmatrix.R;
import com.xuexiang.xutil.common.StringUtils;
import com.xuexiang.xutil.file.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 图片裁剪页面
 */
public class PictureCropActivity extends AppCompatActivity {

    public static final String KEY_PICTURE_PATH = "key_picture_path";
    public static final String KEY_IS_CAMERA = "key_is_camera";
    public static final int REQUEST_CODE_PICTURE_CROP = 1122;

    public static void open(@NonNull Activity activity, boolean isCamera, String imgPath) {
        Intent intent = new Intent(activity, PictureCropActivity.class);
        intent.putExtra(KEY_IS_CAMERA, isCamera);
        intent.putExtra(KEY_PICTURE_PATH, imgPath);
        activity.startActivityForResult(intent, REQUEST_CODE_PICTURE_CROP);
    }

    @BindView(R.id.crop_image_view)
    CropImageView mCropImageView;
    private Unbinder mUnbinder;

    @AutoWired(name = KEY_PICTURE_PATH)
    String mImgPath;
    /**
     * 是拍摄的图片
     */
    @AutoWired(name = KEY_IS_CAMERA)
    boolean mIsCamera;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_crop);
        mUnbinder = ButterKnife.bind(this);
        XRouter.getInstance().inject(this);

        if (StringUtils.isEmpty(mImgPath)) {
            finish();
            return;
        }

        Bitmap bit = BitmapFactory.decodeFile(mImgPath);
        mCropImageView.setImageBitmap(bit);
        // 触摸时显示网格
        mCropImageView.setGuidelines(CropImageType.CROPIMAGE_GRID_ON);
        // 自由剪切
        //mCropImageView.setFixedAspectRatio(false);
        // 固定剪切
        mCropImageView.setFixedAspectRatio(true);
        mCropImageView.setAspectRatio(90,90);
    }


    @OnClick({R.id.iv_close, R.id.iv_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                handleBackPressed();
                break;
            case R.id.iv_submit:
                Bitmap bitmap = mCropImageView.getCroppedImage();
                try {
                    File file = new File(mImgPath);
                    // tests if file exists
                    if(!file.exists()) {
                        FileOutputStream out = new FileOutputStream(file);
                        //50 是压缩率，表示压缩50%; 如果不压缩是100，表示压缩率为0
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();
                        // 把文件插入到系统图库
                        try {
                            MediaStore.Images.Media.insertImage(this.getContentResolver(),
                                    file.getAbsolutePath(), mImgPath, null);
                            // 通知图库更新
                            this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + mImgPath)));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    handlePictureResult(mImgPath);
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    private void handlePictureResult(String imgPath) throws Exception {
        setResult(RESULT_OK, new Intent().putExtra(KEY_PICTURE_PATH, imgPath));
        finish();
    }

    private void handleBackPressed() {
        if (mIsCamera) {
            FileUtils.deleteFile(mImgPath);
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        handleBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        super.onDestroy();
        Log.d("PictureCropActivity", "onDestroy");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("PictureCropActivity", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("PictureCropActivity", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("PictureCropActivity", "onStop");
    }
}
