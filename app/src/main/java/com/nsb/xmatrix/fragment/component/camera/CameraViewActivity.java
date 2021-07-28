

package com.nsb.xmatrix.fragment.component.camera;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.cameraview.AspectRatio;
import com.google.android.cameraview.CameraView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.nsb.xmatrix.utils.RotateSensorHelper;
import com.xuexiang.xaop.annotation.IOThread;
import com.xuexiang.xaop.annotation.Safe;
import com.xuexiang.xaop.annotation.SingleClick;
import com.nsb.xmatrix.R;
import com.nsb.xmatrix.utils.Utils;
import com.nsb.xmatrix.utils.XToastUtils;
import com.xuexiang.xui.widget.alpha.XUIAlphaImageView;
import com.xuexiang.xutil.common.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.nsb.xmatrix.fragment.component.camera.PictureCropActivity.REQUEST_CODE_PICTURE_CROP;

/**
 * 功能齐全的相机拍摄页面
 *
 *
 * @since 2019-10-17 9:17
 */
public class CameraViewActivity extends AppCompatActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback,
        AspectRatioFragment.Listener {
    public static final int REQUEST_CODE_OPEN_CAMERA = 1245;
    private static final String TAG = "CameraViewActivity";

    private static final int REQUEST_CAMERA_PERMISSION = 1;

    public static final String KEY_PICTURE_PATH = "key_picture_path";
    public static final String KEY_IS_CAMERA = "key_is_camera";

    private static final String FRAGMENT_DIALOG = "dialog";

    private static final int[] FLASH_OPTIONS = {
            CameraView.FLASH_AUTO,
            CameraView.FLASH_OFF,
            CameraView.FLASH_ON,
    };

    private static final int[] FLASH_ICONS = {
            R.drawable.ic_flash_auto,
            R.drawable.ic_flash_off,
            R.drawable.ic_flash_on,
    };

    private static final int[] FLASH_TITLES = {
            R.string.flash_auto,
            R.string.flash_off,
            R.string.flash_on,
    };
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.iv_picture_select)
    XUIAlphaImageView ivPictureSelect;
    @BindView(R.id.camera)
    CameraView mCameraView;

    private int mCurrentFlash;

    private Unbinder mUnbinder;

    private RotateSensorHelper mSensorHelper;

    public static void open(@NonNull Activity activity) {
        activity.startActivityForResult(new Intent(activity, CameraViewActivity.class), REQUEST_CODE_OPEN_CAMERA);
    }

    public static void open(@NonNull Activity activity, int requestCode) {
        activity.startActivityForResult(new Intent(activity, CameraViewActivity.class), requestCode);
    }

    public static void open(@NonNull Fragment fragment) {
        fragment.startActivityForResult(new Intent(fragment.getContext(), CameraViewActivity.class), REQUEST_CODE_OPEN_CAMERA);
    }

    public static void open(@NonNull Fragment fragment, int requestCode) {
        fragment.startActivityForResult(new Intent(fragment.getContext(), CameraViewActivity.class), requestCode);
    }

    @SingleClick
    @OnClick({R.id.iv_camera_button, R.id.iv_picture_select})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_camera_button:
                if (mCameraView != null) {
                    mCameraView.takePicture();
                }
                break;
            case R.id.iv_picture_select:
                Utils.getPictureSelector(this)
                        .maxSelectNum(1)
                        .isCamera(false)
                        .compress(false)
                        .selectionMode(PictureConfig.SINGLE)
                        .forResult(PictureConfig.CHOOSE_REQUEST);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_view);
        mUnbinder = ButterKnife.bind(this);

        if (mCameraView != null) {
            mCameraView.addCallback(mCallback);
        }

        List<View> views = new ArrayList<>();
        views.add(toolbar);
        views.add(ivPictureSelect);
        views.add(toolbar);
        mSensorHelper = new RotateSensorHelper(this, views);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (permissions.length != 1 || grantResults.length != 1) {
                    throw new RuntimeException("Error on requesting camera permission.");
                }
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    XToastUtils.toast(R.string.camera_permission_not_granted);
                }
                // No need to start camera here; it is handled by onResume
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_camera, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.aspect_ratio:
                FragmentManager fragmentManager = getSupportFragmentManager();
                if (mCameraView != null && fragmentManager.findFragmentByTag(FRAGMENT_DIALOG) == null) {
                    final Set<AspectRatio> ratios = mCameraView.getSupportedAspectRatios();
                    final AspectRatio currentRatio = mCameraView.getAspectRatio();
                    AspectRatioFragment.newInstance(ratios, currentRatio).show(fragmentManager, FRAGMENT_DIALOG);
                }
                return true;
            case R.id.switch_flash:
                if (mCameraView != null) {
                    mCurrentFlash = (mCurrentFlash + 1) % FLASH_OPTIONS.length;
                    item.setTitle(FLASH_TITLES[mCurrentFlash]);
                    item.setIcon(FLASH_ICONS[mCurrentFlash]);
                    mCameraView.setFlash(FLASH_OPTIONS[mCurrentFlash]);
                }
                return true;
            case R.id.switch_camera:
                if (Camera.getNumberOfCameras() > 1) {
                    if (mCameraView != null) {
                        int facing = mCameraView.getFacing();
                        mCameraView.setFacing(facing == CameraView.FACING_FRONT ? CameraView.FACING_BACK : CameraView.FACING_FRONT);
                    }
                } else {
                    XToastUtils.error("当前设备不支持切换摄像头！");
                }
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAspectRatioSelected(@NonNull AspectRatio ratio) {
        if (mCameraView != null) {
            XToastUtils.toast(ratio.toString());
            mCameraView.setAspectRatio(ratio);
        }
    }

    /**
     * 拍照的回调
     */
    private CameraView.Callback mCallback = new CameraView.Callback() {
        @Override
        public void onCameraOpened(CameraView cameraView) {
            Log.d(TAG, "onCameraOpened");
        }

        @Override
        public void onCameraClosed(CameraView cameraView) {
            Log.d(TAG, "onCameraClosed");
        }

        @Override
        public void onPictureTaken(final CameraView cameraView, final byte[] data) {
            handlePictureTaken(data);
        }
    };

    @IOThread
    private void handlePictureTaken(byte[] data) {
        //图像文件名生成
        String picPath = Utils.handleOnPictureTaken(data);
        if (!StringUtils.isEmpty(picPath)) {
            // --disable PictureCrop huyq2002 2021-7-24
            //PictureCropActivity.open(this, true, picPath);
            Intent intent = new Intent("android.intent.action.CART_BROADCAST");
            intent.putExtra("imgForUploadToServer",picPath);
            LocalBroadcastManager.getInstance(CameraViewActivity.this).sendBroadcast(intent);
            sendBroadcast(intent);
            finish();
        } else {
            XToastUtils.error("图片保存失败！");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择
                    // --disable PictureCrop huyq2002 2021-7-24
                    //PictureCropActivity.open(this, false, result.get(0).getPath());
                    List<LocalMedia> result = PictureSelector.obtainMultipleResult(data);
                    Intent intent0 = new Intent("android.intent.action.CART_BROADCAST");
                    intent0.putExtra("imgForUploadToServer",result.get(0).getPath());
                    LocalBroadcastManager.getInstance(CameraViewActivity.this).sendBroadcast(intent0);
                    sendBroadcast(intent0);
                    finish();
                    break;
                case REQUEST_CODE_PICTURE_CROP:
                    setResult(RESULT_OK, data);
                    String imgPath=data.getStringExtra(KEY_PICTURE_PATH);
                    Intent intent1 = new Intent("android.intent.action.CART_BROADCAST");
                    intent1.putExtra("imgForUploadToServer",imgPath);
                    LocalBroadcastManager.getInstance(CameraViewActivity.this).sendBroadcast(intent1);
                    sendBroadcast(intent1);
                    finish();
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ConfirmationDialogFragment
                    .newInstance(R.string.camera_permission_confirmation,
                            new String[]{Manifest.permission.CAMERA},
                            REQUEST_CAMERA_PERMISSION,
                            R.string.camera_permission_not_granted)
                    .show(getSupportFragmentManager(), FRAGMENT_DIALOG);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    protected void onPause() {
        stopCamera();
        super.onPause();
    }

    @Safe
    private void startCamera() {
        if (mCameraView != null) {
            mCameraView.start();
        }
    }

    @Safe
    private void stopCamera() {
        if (mCameraView != null) {
            mCameraView.stop();
        }
    }

    @Override
    protected void onDestroy() {
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        if (isFinishing()) {
            onRelease();
        }
        super.onStop();
    }

    /**
     * 资源释放
     */
    protected void onRelease() {
        mSensorHelper.recycle();
    }

    public static class ConfirmationDialogFragment extends DialogFragment {
        private static final String ARG_MESSAGE = "message";
        private static final String ARG_PERMISSIONS = "permissions";
        private static final String ARG_REQUEST_CODE = "request_code";
        private static final String ARG_NOT_GRANTED_MESSAGE = "not_granted_message";

        public static ConfirmationDialogFragment newInstance(@StringRes int message,
                                                             String[] permissions, int requestCode, @StringRes int notGrantedMessage) {
            ConfirmationDialogFragment fragment = new ConfirmationDialogFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_MESSAGE, message);
            args.putStringArray(ARG_PERMISSIONS, permissions);
            args.putInt(ARG_REQUEST_CODE, requestCode);
            args.putInt(ARG_NOT_GRANTED_MESSAGE, notGrantedMessage);
            fragment.setArguments(args);
            return fragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Bundle args = getArguments();
            return new AlertDialog.Builder(getActivity())
                    .setMessage(args.getInt(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok,
                            (dialog, which) -> {
                                String[] permissions = args.getStringArray(ARG_PERMISSIONS);
                                if (permissions == null) {
                                    throw new IllegalArgumentException();
                                }
                                ActivityCompat.requestPermissions(getActivity(),
                                        permissions, args.getInt(ARG_REQUEST_CODE));
                            })
                    .setNegativeButton(android.R.string.cancel,
                            (dialog, which) -> XToastUtils.toast(args.getInt(ARG_NOT_GRANTED_MESSAGE)))
                    .create();
        }

    }

}
