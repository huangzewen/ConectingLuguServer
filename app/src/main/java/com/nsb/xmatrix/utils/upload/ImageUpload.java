/*
 * Copyright (C) 2021 xuexiangjys(xuexiangjys@163.com)
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

package com.nsb.xmatrix.utils.upload;

import com.nsb.xmatrix.utils.UrlConst;

import java.io.File;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ImageUpload{

    private static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");
    private static final OkHttpClient client = new OkHttpClient();
    public static void run(File f) throws Exception {
        final File file=f;
        new Thread() {
            @Override
            public void run() {
                //子线程需要做的工作
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        //取 XXXX_XX_XX_XX_XX_XX.jpeg 24个字符文件名
                        .addFormDataPart("file",f.getName().substring(f.getName().length()-24),
                                RequestBody.create(MEDIA_TYPE_JPG, file))
                        .build();
                //设置为自己的ip地址
                Request request = new Request.Builder()
                        .url(UrlConst.upload)
                        .post(requestBody)
                        .build();
                try(Response response = client.newCall(request).execute()){
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    System.out.println(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
