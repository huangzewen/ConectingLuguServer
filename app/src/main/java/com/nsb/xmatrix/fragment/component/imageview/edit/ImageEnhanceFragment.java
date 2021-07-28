

package com.nsb.xmatrix.fragment.component.imageview.edit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.SeekBar;

import androidx.appcompat.widget.AppCompatImageView;

import com.xuexiang.xaop.annotation.Permission;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.imageview.edit.PhotoEnhance;
import com.nsb.xmatrix.R;
import com.nsb.xmatrix.base.BaseFragment;
import com.nsb.xmatrix.utils.XToastUtils;
import com.xuexiang.xutil.app.IntentUtils;
import com.xuexiang.xutil.app.PathUtils;
import com.xuexiang.xutil.display.ImageUtils;

import butterknife.BindView;

import static android.app.Activity.RESULT_OK;
import static com.xuexiang.xaop.consts.PermissionConsts.STORAGE;
import static com.nsb.xmatrix.fragment.component.expands.XQRCodeFragment.REQUEST_IMAGE;

/**
 *
 * @since 2019-10-21 11:55
 */
@Page(name = "图片增强处理")
public class ImageEnhanceFragment extends BaseFragment implements SeekBar.OnSeekBarChangeListener {
    @BindView(R.id.sb_saturation)
    SeekBar sbSaturation;
    @BindView(R.id.sb_brightness)
    SeekBar sbBrightness;
    @BindView(R.id.sb_contrast)
    SeekBar sbContrast;
    @BindView(R.id.iv_content)
    AppCompatImageView ivContent;

    private PhotoEnhance mPhotoEnhance;
    private int mProgress = 0;

    /**
     * 布局的资源id
     *
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_image_enhance;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.addAction(new TitleBar.TextAction("选择图片") {
            @SingleClick
            @Override
            public void performAction(View view) {
                selectImage();
            }
        });
        return titleBar;
    }

    @Permission(STORAGE)
    private void selectImage() {
        startActivityForResult(IntentUtils.getDocumentPickerIntent(IntentUtils.DocumentType.IMAGE), REQUEST_IMAGE);
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {
        mPhotoEnhance = new PhotoEnhance();

        sbSaturation.setMax(255);
        sbSaturation.setProgress(128);
        sbSaturation.setOnSeekBarChangeListener(this);

        sbBrightness.setMax(255);
        sbBrightness.setProgress(128);
        sbBrightness.setOnSeekBarChangeListener(this);

        sbContrast.setMax(255);
        sbContrast.setProgress(128);
        sbContrast.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //选择系统图片并解析
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    Bitmap bitmap = ImageUtils.getBitmap(PathUtils.getFilePathByUri(uri));
                    mPhotoEnhance.setBitmap(bitmap);
                    ivContent.setImageBitmap(bitmap);
                    resetProgress();
                }
            }
        }
    }

    private void resetProgress() {
        sbSaturation.setProgress(128);
        sbBrightness.setProgress(128);
        sbContrast.setProgress(128);
        mProgress = 128;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mProgress = progress;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (!mPhotoEnhance.hasSetBitmap()) {
            XToastUtils.warning("请先选择图片");
            return;
        }

        int type = 0;
        switch (seekBar.getId()) {
            case R.id.sb_saturation:
                mPhotoEnhance.setSaturation(mProgress);
                type = PhotoEnhance.ENHANCE_SATURATION;
                break;
            case R.id.sb_brightness:
                mPhotoEnhance.setBrightness(mProgress);
                type = PhotoEnhance.ENHANCE_BRIGHTNESS;
                break;
            case R.id.sb_contrast:
                mPhotoEnhance.setContrast(mProgress);
                type = PhotoEnhance.ENHANCE_CONTRAST;
                break;
            default:
                break;
        }
        ivContent.setImageBitmap(mPhotoEnhance.handleImage(type));
    }
}
