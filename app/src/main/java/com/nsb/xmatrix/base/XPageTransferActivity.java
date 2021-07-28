

package com.nsb.xmatrix.base;

import android.os.Bundle;

import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.annotation.Router;
import com.xuexiang.xrouter.launcher.XRouter;
import com.nsb.xmatrix.utils.Utils;
import com.nsb.xmatrix.utils.XToastUtils;
import com.xuexiang.xutil.common.StringUtils;

/**
 * https://xuexiangjys.club/xpage/transfer?pageName=xxxxx&....
 * applink的中转
 *
 *
 * @since 2019-07-06 9:37
 */
@Router(path = "/xpage/transfer")
public class XPageTransferActivity extends BaseActivity {

    @AutoWired(name = "pageName")
    String pageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        XRouter.getInstance().inject(this);

        if (!StringUtils.isEmpty(pageName)) {
            if (openPage(pageName, getIntent().getExtras()) == null) {
                XToastUtils.toast("页面未找到！");
                finish();
            }
        } else {
            XToastUtils.toast("页面未找到！");
            finish();
        }
    }

    @Override
    protected void onRelease() {
        super.onRelease();
        Utils.syncMainPageStatus();

    }
}
