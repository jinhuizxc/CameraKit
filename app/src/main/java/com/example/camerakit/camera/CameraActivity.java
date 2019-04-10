package com.example.camerakit.camera;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.camerakit.R;
import com.flurgle.camerakit.CameraKit;
import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;

import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;

public class CameraActivity extends AppCompatActivity {
    
    public  final String TAG=this.getClass().getSimpleName();
    private CameraView mCameraView;
    //是不是
    private boolean isPhoto =true;
    private boolean isVideoStarted =false;
    private boolean isLight =false;
    private boolean isFlash =false;
    private boolean isFont=false;
    private boolean isStandard =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mCameraView = (CameraView) findViewById(R.id.cv_camera);
        mCameraView.setCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(final byte[] jpeg) {
                super.onPictureTaken(jpeg);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //将cameraview获得的byte数组存储到File文件中：
                        File file = CameraUtils.getFile(jpeg, getExternalCacheDir().getAbsolutePath(), System.currentTimeMillis() + "_original.jpg");
                        try {
                            // 调用压缩方法进行压缩
                            final File compressedImageFile = new Compressor(CameraActivity.this).setDestinationDirectoryPath(file.getParent()).compressToFile(file);
                            Log.i(TAG,"图片保存的地址为："+compressedImageFile.getAbsolutePath()+compressedImageFile.exists());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(CameraActivity.this,"拍照成功，地址为："+compressedImageFile.getAbsolutePath()+compressedImageFile.exists(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }

            @Override
            public void onVideoTaken(final File video) {
                super.onVideoTaken(video);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CameraActivity.this,"录制成功，地址为："+video.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    }
                });

                Log.i(TAG,"视频保存的地为："+video.getAbsolutePath());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraView.start();
    }
    @Override
    protected void onPause() {
        mCameraView.stop();
        super.onPause();
    }

    //开启照明
    //本方法需要修改library中的部分代码，使外面可以获取到cameraView所引用的Camera，通过gradle引用无法直接使用
    //需要照明的同学，可以下载demo 直接使用library
    public void light(View view) {
        CameraUtils.flashControl(mCameraView);
        isLight =!isLight;
    }
    //拍照/录像
    public void takephoto(View view) {
        if (isPhoto){
            mCameraView.captureImage();
        }else {
            if (!isVideoStarted) {
                Toast.makeText(this, "录像开始", Toast.LENGTH_SHORT).show();
                mCameraView.startRecordingVideo();
            }else {
                mCameraView.stopRecordingVideo();
            }
            isVideoStarted =!isVideoStarted;
        }
    }
    //切换模式
    public void type(View view) {
        ((ImageView)view).setImageResource(
                isPhoto ?
                        R.drawable.icon_photograp_photo :
                        R.drawable.icon_photograp_video);

            isPhoto =!isPhoto;
        if (isPhoto){
            Toast.makeText(this, "已经切换到拍照模式", Toast.LENGTH_SHORT).show();
        }else Toast.makeText(this, "已经切换到录像模式", Toast.LENGTH_SHORT).show();
    }
    //前后置切换
    public void font_back(View view) {
        mCameraView.setFacing(isFont? CameraKit.Constants.FACING_BACK:CameraKit.Constants.FACING_FRONT);
        isFont=!isFont;
    }
    //拍照的时候开启闪光灯
    public void flash(View view) {
        mCameraView.setFlash(isFlash ?CameraKit.Constants.FLASH_OFF:CameraKit.Constants.FLASH_ON);
        isFlash =!isFlash;
    }
    //缩放
    public void zoom(View view) {
        mCameraView.setMethod(isStandard ?CameraKit.Constants.METHOD_STILL:CameraKit.Constants.METHOD_STANDARD);
        isStandard =!isStandard;
    }
}
