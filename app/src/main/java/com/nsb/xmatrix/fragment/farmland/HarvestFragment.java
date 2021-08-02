package com.nsb.xmatrix.fragment.farmland;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.kunminx.linkage.LinkageRecyclerView;
import com.kunminx.linkage.adapter.viewholder.LinkagePrimaryViewHolder;
import com.kunminx.linkage.adapter.viewholder.LinkageSecondaryViewHolder;
import com.kunminx.linkage.bean.BaseGroupedItem;
import com.nsb.xmatrix.entity.HarvestPredict;
import com.nsb.xmatrix.utils.UrlConst;
import com.nsb.xmatrix.utils.XToastUtils;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xhttp2.XHttpSDK;
import com.xuexiang.xhttp2.annotation.ThreadType;
import com.xuexiang.xhttp2.callback.SimpleCallBack;
import com.xuexiang.xhttp2.exception.ApiException;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.utils.SnackbarUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.nsb.xmatrix.utils.DemoDataProvider;
import com.nsb.xmatrix.R;
import com.nsb.xmatrix.base.BaseFragment;
import com.nsb.xmatrix.fragment.component.core.CustomLinkagePrimaryAdapterConfig;
import com.nsb.xmatrix.fragment.component.core.ElemeGroupedItem;
import com.nsb.xmatrix.fragment.component.core.ElemeSecondaryAdapterConfig;
import com.xuexiang.xui.widget.popupwindow.popup.XUISimplePopup;
import com.xuexiang.xutil.app.ActivityUtils;
import com.xuexiang.xutil.display.DensityUtils;
import com.xuexiang.xutil.net.JsonUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

@Page(name = "Harvest Management")
public class HarvestFragment extends BaseFragment implements CustomLinkagePrimaryAdapterConfig.OnPrimaryItemClickListener, ElemeSecondaryAdapterConfig.OnSecondaryItemClickListener {

    @BindView(R.id.linkage)
    LinkageRecyclerView linkage;

    @BindView(R.id.harvest_analysis)
    Button btnAnalysis;

    @BindView(R.id.harvest_view)
    Button btnView;

    /**
     * 布局的资源id
     *
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_linkage_recyclerview;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.addAction(new TitleBar.TextAction("切换") {
            @SingleClick
            @Override
            public void performAction(View view) {
                if (linkage != null) {
                    linkage.setGridMode(!linkage.isGridMode());
                }
            }
        });
        return titleBar;
    }

    private void showPopup(HarvestPredict ret){
        String info=String.format("Quality: Level %s\nEstimated price: %,.2f\nRank (in this area): %s",
                ret.getLevel(),ret.getPrice(),ret.getRank()
        );

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity())
                .setTitle("Production forecast")
                .setMessage(info)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog =  builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(18);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {
        //监听btnAnalysis事件
        btnAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> params = new HashMap<>();
                //todo: replace mock params with true values of the farmLand Id
                params.put("userId","userId");
                params.put("farmLandId","farmLandId");
                XHttpSDK.post(UrlConst.harvest_predict, JsonUtil.toJson(params), ThreadType.TO_MAIN)
                        .keepJson(true)
                        .syncRequest(false)
                        .onMainThread(true)
                        .execute(new SimpleCallBack<String>() {
                            @Override
                            public void onSuccess(String response) throws Throwable {
                                try{
                                    JSONObject jsonObject = JsonUtil.toJSONObject(response);
                                    String level = jsonObject.getString("level");
                                    //fake rank
                                    String rank = "15/78";
                                    //String rank = jsonObject.getString("rank");
                                    //fake price
                                    double price = 3.88;
                                    //double price = jsonObject.getDouble("price");
                                    HarvestPredict ret=new HarvestPredict(level,rank,price);
                                    showPopup(ret);
                                }catch (Exception e){
                                    String s=e.getMessage();
                                }
                            }
                            @Override
                            public void onError(ApiException e) {

                            }
                        });
            }
        });
        //监听btnView事件
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo: replace with real business logic huyq2002 2021-7-24
                XToastUtils.toast("点击了btnView");
            }
        });

        //todo: replace DemoDataProvider.getElemeGroupItems() by calling API to get data
        // huyq2002 2021-7-24
        linkage.init(DemoDataProvider.getElemeGroupItems(), new CustomLinkagePrimaryAdapterConfig(this), new ElemeSecondaryAdapterConfig(this));

    }

    @Override
    public void onPrimaryItemClick(LinkagePrimaryViewHolder holder, View view, String title) {
        SnackbarUtils.Short(view, title).show();
    }

    @Override
    public void onSecondaryItemClick(LinkageSecondaryViewHolder holder, ViewGroup view, BaseGroupedItem<ElemeGroupedItem.ItemInfo> item) {
        SnackbarUtils.Short(view, item.info.getTitle()).show();
    }

    @Override
    public void onGoodAdd(View view, BaseGroupedItem<ElemeGroupedItem.ItemInfo> item) {
        SnackbarUtils.Short(view, "Add：" + item.info.getTitle()).show();
    }
}
