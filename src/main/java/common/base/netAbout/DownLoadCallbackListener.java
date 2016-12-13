package common.base.netAbout;

import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-12-13
 * Time: 18:06
 * DESC: 使用Retrofit进行下载的网络回调，由于使用Retrofit进行下载时，网络请求响应的类型需要ResponseBody类型
 * 所以单独提供一个这样的响应监听对象
 * 注：下载大文件时请绕过，目前没有下载进度回调
 */
public class DownLoadCallbackListener extends NetDataAndErrorListener<ResponseBody> {
    private File toSave2LocalFile;
    public DownLoadCallbackListener(File toSaveContentFile, INetEvent<ResponseBody> netEvent) {
        super(netEvent);
        this.toSave2LocalFile = toSaveContentFile;
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        boolean dealWithOk = false;
        if (response.isSuccessful()) {
            boolean writtenToDisk = writeResponseBodyToDisk(response.body());
            dealWithOk = writtenToDisk;
            if (!writtenToDisk) {
                if (toSave2LocalFile != null) {
                    toSave2LocalFile.delete();
                }
            }
        }
        if (netEvent != null) {
            netEvent.onResponse(requestType, dealWithOk ? response.body() : null);
        }
    }

    private boolean writeResponseBodyToDisk(ResponseBody body) {
        if (toSave2LocalFile != null) {
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(toSave2LocalFile);
                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    Log.d("info", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }
                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            }
            finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }
}
