

package com.nsb.xmatrix.fragment.component.imageview;

import com.xuexiang.xpage.annotation.Page;
import com.nsb.xmatrix.base.ComponentContainerFragment;
import com.nsb.xmatrix.fragment.component.imageview.edit.ImageCropFragment;
import com.nsb.xmatrix.fragment.component.imageview.edit.ImageEnhanceFragment;
import com.nsb.xmatrix.fragment.component.imageview.edit.PhotoEditFragment;

/**
 *
 * @since 2019-10-21 15:53
 */
@Page(name = "图片编辑")
public class ImageEditFragment extends ComponentContainerFragment {

    /**
     * 获取页面的类集合[使用@Page注解进行注册的页面]
     *
     * @return
     */
    @Override
    protected Class[] getPagesClasses() {
        return new Class[]{
                ImageCropFragment.class,
                ImageEnhanceFragment.class,
                PhotoEditFragment.class
        };
    }
}
