package com.nsb.xmatrix.fragment.farmland;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.nsb.xmatrix.R;
import com.nsb.xmatrix.base.BaseFragment;
import com.nsb.xmatrix.fragment.component.camera.CameraViewActivity;
import com.nsb.xmatrix.fragment.component.imageview.preview.ImageViewInfo;
import com.nsb.xmatrix.utils.OkHttpUtil;
import com.nsb.xmatrix.utils.SettingSPUtils;
import com.nsb.xmatrix.utils.XToastUtils;
import com.nsb.xmatrix.utils.upload.ProgressListener;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.imageview.preview.PreviewBuilder;
import com.xuexiang.xutil.common.StringUtils;
import com.xuexiang.xutil.net.JsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.nsb.xmatrix.fragment.component.camera.CameraViewActivity.REQUEST_CODE_OPEN_CAMERA;

@Page(name = "Farmland Management")
public class FarmlandFragment extends BaseFragment {

    @BindView(R.id.land_profile)
    TextView txtLandProfile;

    @BindView(R.id.land_result)
    TextView txtLandResult;

    @BindView(R.id.upload_progress)
    ProgressBar mProgressBar;

    @BindView(R.id.btn_farmland_scan)
    Button btnScan;

    @BindView(R.id.btn_farmland_analysis)
    Button btnAnalysis;

    @BindView(R.id.land_img)
    ImageView landImage;

    final String blankImage = "@drawable/blank" ;
    String imgPath ="";
    String tips="";

    /**
     * 布局的资源id
     *
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_farmland;
    }

    //在消息队列中实现对Image控件的更改
/*    private Handler handle = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    System.out.println("111");
                    Bitmap bmp=(Bitmap)msg.obj;
                    landImage.setImageBitmap(bmp);
                    break;
            }
        };
    };*/

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {
        //find all views
        txtLandProfile=(TextView)findViewById(R.id.land_profile);
        txtLandResult=(TextView)findViewById(R.id.land_result);
        mProgressBar=(ProgressBar)findViewById(R.id.upload_progress);
        btnScan=(Button)findViewById(R.id.btn_farmland_scan);
        btnAnalysis=(Button)findViewById(R.id.btn_farmland_analysis);
        landImage=(ImageView)findViewById(R.id.land_img);

        tips="  Our AI engine will assist identification and recognition of rice diseases and pests using deep CNN."
                +"\n  The result include classification of the disease and health index of the crop.";

        txtLandProfile.setText(tips);
        txtLandProfile.setMovementMethod(ScrollingMovementMethod.getInstance());

       //监听btnAnalysis事件
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FarmlandFragment.this.startActivityForResult(new Intent(FarmlandFragment.this.getContext(),
                        CameraViewActivity.class), REQUEST_CODE_OPEN_CAMERA);
            }
        });
        //btnAnalysis click listener
        btnAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //upload file to flask server and call CNN model to predict result
                uploadImageToServer(imgPath);
            }
        });
        landImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreviewBuilder from = PreviewBuilder.from(getActivity());
                ImageViewInfo imf=new ImageViewInfo(blankImage);
                from.setImg(imf);
                from.setSingleFling(true);
                from.setProgressColor(SettingSPUtils.getInstance().isUseCustomTheme() ? R.color.custom_color_main_theme : R.color.xui_config_color_main_theme);
                from.setType(PreviewBuilder.IndicatorType.Number);
                from.start();
            }
        });

        //新建线程加载图片信息，发送到消息队列中
/*        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bmp = getURLImage(url);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = bmp;
                handle.sendMessage(msg);
            }
        }).start();*/
    }

    //fragment重新刷新的方法
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.CART_BROADCAST");
        BroadcastReceiver mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent){
                imgPath = intent.getStringExtra("imgForUploadToServer");
                showFileImage(imgPath);
            }
        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
    }

    private void showFileImage(String imgPath){
        if(StringUtils.isEmptyTrim(imgPath)) return;
        landImage=(ImageView)findViewById(R.id.land_img);
        landImage.setImageBitmap(getLoacalBitmap(imgPath));
    }

    //上传图片
    private void uploadImageToServer(String imgPath) {
        File file=new File(imgPath);
        try {
            OkHttpUtil.uploadFile(new ProgressListener() {
                @Override
                public void onProgress(long currentBytes, long contentLength, boolean done)  {
                    int progress = (int) (currentBytes * 100 / contentLength);
                    mProgressBar.setProgress(progress);
                    if(progress>=100) {
                    }
                }
            }, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    //when receive result from machine learning model --RiceHealthModel
                    if (response != null) {
                        try {
                            String result = response.body().string();
                            JSONObject jsonObject = JsonUtil.toJSONObject(result);
                            double score = jsonObject.getDouble("score");
                            String disease = jsonObject.getString("disease");
                            disease=score<90?"Abnormal,"+disease:"Normal"+disease;
                            if(score<90) {
                                txtLandResult.setTextColor(Color.RED);
                            } else {
                                //@color/xui_config_color_light_blue
                                txtLandResult.setTextColor(0x299EE3);
                            }
                            txtLandResult.setText(String.format("Result:%s|Health Idx:%,.2f",disease,score));
                            //todo: handle return result
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            },  file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载本地图片
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    //加载URL图片
    public Bitmap getURLImage(String url) {
        Bitmap bmp = null;
        try {
            URL myurl = new URL(url);
            // 获得连接
            HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
            conn.setConnectTimeout(6000);//设置超时
            conn.setDoInput(true);
            conn.setUseCaches(false);//不缓存
            conn.connect();
            InputStream is = conn.getInputStream();//获得图片的数据流
            bmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }
}
