
package com.nsb.xmatrix.fragment.other;

import com.nsb.xmatrix.R;
import com.nsb.xmatrix.core.BaseFragment;
import com.nsb.xmatrix.utils.TokenUtils;
import com.nsb.xmatrix.utils.XToastUtils;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.dialog.DialogLoader;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;
import com.xuexiang.xutil.XUtil;

import butterknife.BindView;

/**
 *
 * @since 2019-10-15 22:38
 */
@Page(name = "设置")
public class SettingsFragment extends BaseFragment implements SuperTextView.OnSuperTextViewClickListener {

    @BindView(R.id.menu_common)
    SuperTextView menuCommon;
    @BindView(R.id.menu_privacy)
    SuperTextView menuPrivacy;
    @BindView(R.id.menu_push)
    SuperTextView menuPush;
    @BindView(R.id.menu_helper)
    SuperTextView menuHelper;
    @BindView(R.id.menu_change_account)
    SuperTextView menuChangeAccount;
    @BindView(R.id.menu_logout)
    SuperTextView menuLogout;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_settings;
    }

    @Override
    protected void initViews() {
        menuCommon.setOnSuperTextViewClickListener(this);
        menuPrivacy.setOnSuperTextViewClickListener(this);
        menuPush.setOnSuperTextViewClickListener(this);
        menuHelper.setOnSuperTextViewClickListener(this);
        menuChangeAccount.setOnSuperTextViewClickListener(this);
        menuLogout.setOnSuperTextViewClickListener(this);
    }

    @SingleClick
    @Override
    public void onClick(SuperTextView superTextView) {
        switch (superTextView.getId()) {
            case R.id.menu_common:
            case R.id.menu_privacy:
            case R.id.menu_push:
            case R.id.menu_helper:
                XToastUtils.toast(superTextView.getLeftString());
                break;
            case R.id.menu_change_account:
                XToastUtils.toast(superTextView.getCenterString());
                break;
            case R.id.menu_logout:
                DialogLoader.getInstance().showConfirmDialog(
                        getContext(),
                        getString(R.string.lab_logout_confirm),
                        getString(R.string.lab_yes),
                        (dialog, which) -> {
                            dialog.dismiss();
                            XUtil.getActivityLifecycleHelper().exit();
                            TokenUtils.handleLogoutSuccess();
                        },
                        getString(R.string.lab_no),
                        (dialog, which) -> dialog.dismiss()
                );
                break;
            default:
                break;
        }
    }
}
