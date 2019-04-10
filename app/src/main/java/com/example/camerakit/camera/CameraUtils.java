package com.example.camerakit.camera;

import android.hardware.Camera;

import com.flurgle.camerakit.CameraView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/*************************************************************************************************
 * 作   者： 高永好
 * 完成日期：2017-06-20 15:34
 * 说明：
 ************************************************************************************************/

public class CameraUtils {

    private static Camera m_Camera;

    public static File getFile(byte[] bfile, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if(!dir.exists()||dir.isDirectory()){//判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(filePath+"/"+fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            return file;
        }
    }

    public static boolean flashControl(CameraView cameraView) {
        Camera.Parameters parameters =cameraView.getCamera().getParameters();
        if (Camera.Parameters.FLASH_MODE_OFF.equals(parameters.getFlashMode())) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            cameraView.getCamera().setParameters(parameters);
            return true;
        } else if (Camera.Parameters.FLASH_MODE_TORCH.equals(parameters.getFlashMode())) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            cameraView.getCamera().setParameters(parameters);
        }
        return false;
    }
}
