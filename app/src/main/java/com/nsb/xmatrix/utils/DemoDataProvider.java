/*
 * Copyright (C) 2020 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.nsb.xmatrix.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.gson.reflect.TypeToken;
import com.kunminx.linkage.bean.DefaultGroupedItem;
import com.nsb.xmatrix.R;
import com.nsb.xmatrix.adapter.entity.NewInfo;
import com.nsb.xmatrix.fragment.component.imageview.preview.ImageViewInfo;
import com.nsb.xmatrix.fragment.component.imageview.preview.NineGridInfo;
import com.nsb.xmatrix.fragment.component.core.CustomGroupedItem;
import com.nsb.xmatrix.fragment.component.core.ElemeGroupedItem;
import com.xuexiang.xaop.annotation.MemoryCache;
import com.xuexiang.xui.adapter.simple.AdapterItem;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.widget.banner.widget.banner.BannerItem;
import com.xuexiang.xutil.data.DateUtils;
import com.xuexiang.xutil.net.JsonUtil;
import com.xuexiang.xutil.resource.ResourceUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 演示数据
 *
 *
 * @since 2018/11/23 下午5:52
 */
public class DemoDataProvider {

    public static String[] titles = new String[]{
            "",
            "",
            ""
    };

    public static String[] urls = new String[]{//640*360 360/640=0.5625
            "https://tse1-mm.cn.bing.net/th/id/R-C.acc58d72bcf45fa391a0ebcb33b16f12?rik=O1yihSqxvTEhwA&riu=http%3a%2f%2fimages.saferbrand.com%2fis%2fimage%2fwoodstream%2fsb-lc-article-soil-biology-header%3fqlt%3d75&ehk=zn9bo3eJ7q569dv9oBWn8Mixy0oJOktWeMvJeU8E%2foU%3d&risl=&pid=ImgRaw",//数字乡村
            "https://tse4-mm.cn.bing.net/th/id/OIP-C.tXcVeZPcbrD6aeN0SNowygHaEr?pid=ImgDet&rs=1",//智慧农业
            "https://tse3-mm.cn.bing.net/th/id/OIP-C.7waG048DQbyRC7dUtK3-8wHaEP?pid=ImgDet&rs=1",//智慧康养
    };

    public static List<List<NineGridInfo>> sNineGridPics;
    public static List<List<NineGridInfo>> sNineGridVideos;

    @MemoryCache
    public static List<BannerItem> getBannerList() {
        List<BannerItem> list = new ArrayList<>();
        for (int i = 0; i < urls.length; i++) {
            BannerItem item = new BannerItem();
            item.imgUrl = urls[i];
            item.title = titles[i];

            list.add(item);
        }
        return list;
    }

    /**
     * 用于占位的空信息
     *
     * @return
     */
    @MemoryCache
    public static List<NewInfo> getDemoNewInfos() {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");

        List<NewInfo> list = new ArrayList<>();

        list.add(new NewInfo("订阅号", "数字乡村-打造乡村振兴的数字引擎。")
                .setUserName(sd.format(DateUtils.nDaysBeforeToday(1)))
                .setSummary("数字乡村围绕乡村土地管理，乡村治理和乡村产业服务展开，以村民，三类土地和服务为主，实现乡村数字化的村民，村务，土地的最核心数字化管理。\n")
                //.setImageUrl("https://img1.baidu.com/it/u=3727139887,4262022445&fm=26&fmt=auto&gp=0.jpg")
                .setImageUrl("https://yunche-pro.oss-cn-shanghai.aliyuncs.com/news_0.jpg")
        );


        list.add(new NewInfo("公众号", "智慧农业-打造科技服务三农新样本")
                .setUserName(sd.format(DateUtils.nDaysBeforeToday(0)))
                .setSummary("致力于智慧农业解决方案,提供温室物联网设备,助力中国农业发展。\n")
                //.setImageUrl("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fccgoldenet.com%2FappUpdata%2Fimage%2F20180507%2F20180507143739_0289.png&refer=http%3A%2F%2Fccgoldenet.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1627897680&t=20f4f69a12eba5e36013ef4d9d1e80c7")
                .setImageUrl("https://yunche-pro.oss-cn-shanghai.aliyuncs.com/news_1.jpg")
                );

        list.add(new NewInfo("服务号", "智慧康养-筑品质百城,兴康养万家")
                .setUserName(sd.format(DateUtils.nDaysBeforeToday(2)))
                .setSummary("“智慧康养产品将突破传统养老在居家照顾、出行、安全保护、健康管理、精神关爱等方面的难点。\n")
                //.setImageUrl("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fn.sinaimg.cn%2Fsinacn%2F20170309%2F46e5-fycapec3749228.jpg&refer=http%3A%2F%2Fn.sinaimg.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1627899094&t=3485a7c10765c7928f157a0ecfe2d878")
                .setImageUrl("https://yunche-pro.oss-cn-shanghai.aliyuncs.com/news_2.jpg")
        );

        return list;
    }

    public static List<AdapterItem> getGridItems(Context context) {
        return getGridItems(context, R.array.grid_titles_entry, R.array.grid_icons_entry);
    }

    public static String[] farmers = new String[]{
            "Tomas K|wheat 30h㎡|Odinsgatan 6 Göteborg",
            "Tomas K|wheat 30h㎡|Odinsgatan 6 Göteborg",
            "Tomas K|wheat 30h㎡|Odinsgatan 6 Göteborg",
            "Tomas K|wheat 30h㎡|Odinsgatan 6 Göteborg",
            "Tomas K|wheat 30h㎡|Odinsgatan 6 Göteborg"
    };

    private static List<AdapterItem> getGridItems(Context context, int titleArrayId, int iconArrayId) {
        List<AdapterItem> list = new ArrayList<>();
        String[] titles = ResUtils.getStringArray(titleArrayId);
        Drawable[] icons = ResUtils.getDrawableArray(context, iconArrayId);
        for (int i = 0; i < titles.length; i++) {
            list.add(new AdapterItem(titles[i], icons[i]));
        }
        return list;
    }

    /**
     * 用于占位的空信息
     *
     * @return
     */
    @MemoryCache
    public static List<NewInfo> getEmptyNewInfo() {
        List<NewInfo> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(new NewInfo());
        }
        return list;
    }

    @MemoryCache
    public static List<DefaultGroupedItem> getGroupItems() {
        List<DefaultGroupedItem> items = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            if (i % 10 == 0) {
                items.add(new DefaultGroupedItem(true, "菜单" + i / 10));
            } else {
                items.add(new DefaultGroupedItem(new DefaultGroupedItem.ItemInfo("这是标题" + i, "菜单" + i / 10, "这是内容" + i)));
            }
        }
        return items;
    }

    @MemoryCache
    public static List<CustomGroupedItem> getCustomGroupItems() {
        List<CustomGroupedItem> items = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            if (i % 10 == 0) {
                items.add(new CustomGroupedItem(true, "菜单" + i / 10));
            } else {
                items.add(new CustomGroupedItem(new CustomGroupedItem.ItemInfo("这是标题" + i, "菜单" + i / 10, "这是内容" + i)));
            }
        }
        return items;
    }

    @MemoryCache
    public static List<ElemeGroupedItem> getElemeGroupItems() {
        return JsonUtil.fromJson(ResourceUtils.readStringFromAssert("eleme.json"), new TypeToken<List<ElemeGroupedItem>>() {
        }.getType());
    }


    public static List<List<ImageViewInfo>> sPics;
    public static List<List<ImageViewInfo>> sVideos;

}
