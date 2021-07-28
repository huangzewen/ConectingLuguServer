
package com.nsb.xmatrix.fragment.component.core;

import com.nsb.xmatrix.R;
import com.nsb.xmatrix.core.BaseFragment;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;

/**
 *
 * @since 2021/6/30 1:21 AM
 */
@Page
public class GridItemFragment extends BaseFragment {

    public static final String KEY_TITLE_NAME = "title_name";

    /**
     * 自动注入参数，不能是private
     */
    @AutoWired(name = KEY_TITLE_NAME)
    String title;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_grid_item;
    }

    @Override
    protected void initArgs() {
        // 自动注入参数必须在initArgs里进行注入
        XRouter.getInstance().inject(this);
    }

    @Override
    protected String getPageTitle() {
        return title;
    }

    @Override
    protected void initViews() {

    }

}
