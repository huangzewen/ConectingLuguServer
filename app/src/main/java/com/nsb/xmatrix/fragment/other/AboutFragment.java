package com.nsb.xmatrix.fragment.other;

import android.annotation.SuppressLint;
import android.widget.TextView;

import com.nsb.xmatrix.R;
import com.nsb.xmatrix.core.BaseFragment;
import com.nsb.xmatrix.core.webview.AgentWebActivity;
import com.nsb.xmatrix.utils.Utils;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.grouplist.XUIGroupListView;
import com.xuexiang.xutil.app.AppUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;

/**
 *
 * @since 2019-10-30 00:02
 */
@Page(name = "关于")
public class AboutFragment extends BaseFragment {

    @BindView(R.id.tv_version)
    TextView mVersionTextView;
    @BindView(R.id.about_list)
    XUIGroupListView mAboutGroupListView;
    @BindView(R.id.tv_copyright)
    TextView mCopyrightTextView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_about;
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    protected void initViews() {
        mVersionTextView.setText(String.format("版本号：%s", AppUtils.getAppVersionName()));

        XUIGroupListView.newSection(getContext())
                .addItemView(mAboutGroupListView.createItemView(getResources().getString(R.string.title_user_protocol)), v -> Utils.gotoProtocol(this, false, false))
                .addItemView(mAboutGroupListView.createItemView(getResources().getString(R.string.title_privacy_protocol)), v -> Utils.gotoProtocol(this, true, false))
                .addTo(mAboutGroupListView);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.CHINA);
        String currentYear = dateFormat.format(new Date());
        mCopyrightTextView.setText(String.format(getResources().getString(R.string.about_copyright), currentYear));
    }
}
