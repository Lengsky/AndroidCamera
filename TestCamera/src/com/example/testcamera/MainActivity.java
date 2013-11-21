package com.example.testcamera;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final String TAG = "CameraActivity";
    private SurfaceView surfaceView;
    private Camera camera;
    private boolean preview;
    private ImageButton take_picture;
    private int width,height;
	List<Size> mSupportedPreviewSizes;
   @Override
    public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Window window = getWindow();
            requestWindowFeature(Window.FEATURE_NO_TITLE);// 没有标题
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
          //  window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// 高亮

           setContentView(R.layout.camera_view);

           ButtonClickingListener buttonlistener = new ButtonClickingListener();
            surfaceView = (SurfaceView) this.findViewById(R.id.previewSV);
            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            width = display.getWidth();
            height = display.getHeight();

           take_picture = (ImageButton) findViewById(R.id.btn_takephoto_camera);//拍照
            take_picture.setOnClickListener(buttonlistener);
         //   surfaceView.getHolder().setFixedSize(width, height); // 设置分辨率
            /* 下面设置Surface不维护自己的缓冲区，而是等待屏幕的渲染引擎将内容推送到用户面前 */
            surfaceView.getHolder().addCallback(new SurfaceCallback());
    }
    //按钮监听
    private final class ButtonClickingListener implements View.OnClickListener {
            @Override
            public void onClick(View v) {
                    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                          //  Toast.makeText(MainActivity.this, R.string.sdcarderror, 1).show();
                            return;
                    }
                    try {
                            switch (v.getId()) {
                            case R.id.btn_takephoto_camera:
                                    camera.takePicture(null, null, new TakePictureCallback());
                                    break;
                            }
                    } catch (Exception e) {
                           // Toast.makeText(MainActivity.this, R.string.error, 1).show();
                            Log.e(TAG, e.toString());
                    }
            }
    }
    @Override
    protected void onDestroy() {
            // TODO Auto-generated method stub
            if(camera!=null){
                    camera.setPreviewCallback(null) ;
        camera.stopPreview();
        camera.release();
        camera = null;
            }
            super.onDestroy();
    }
    private final class SurfaceCallback implements SurfaceHolder.Callback {

           @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {}
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                    if(camera==null){
                            camera = Camera.open();//打开相机
                    }else{
                            Toast.makeText(MainActivity.this, "相机正在使用中", 1).show();
                    }
                    
                    WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                    Display display = wm.getDefaultDisplay();
                    Camera.Parameters parameters = camera.getParameters();
                	mSupportedPreviewSizes = parameters.getSupportedPreviewSizes();
                	//设置相机取景大小和获取图片大小
                	parameters.setPreviewSize(mSupportedPreviewSizes.get(3).width, mSupportedPreviewSizes.get(3).height);
            		parameters.setPictureSize(mSupportedPreviewSizes.get(3).width, mSupportedPreviewSizes.get(3).height);
            		
            		Log.i("LING", ">>>>>>>"+mSupportedPreviewSizes.get(0).width+""+mSupportedPreviewSizes.get(0).height);
            		Log.i("YI", ">>>>>>>"+mSupportedPreviewSizes.get(1).width+""+mSupportedPreviewSizes.get(1).height);
            		Log.i("ER", ">>>>>>>"+mSupportedPreviewSizes.get(2).width+""+mSupportedPreviewSizes.get(2).height);
            		Log.i("SAN", ">>>>>>>"+mSupportedPreviewSizes.get(3).width+""+mSupportedPreviewSizes.get(3).height);
            		Log.i("SI", ">>>>>>>"+mSupportedPreviewSizes.get(4).width+""+mSupportedPreviewSizes.get(4).height);
                  //  parameters.setPreviewSize(display.getWidth()/2, display.getHeight()/2);// 设置预览照片的大小
                  //  parameters.setPreviewFrameRate(3);// 每秒3帧
                    parameters.setPictureFormat(PixelFormat.JPEG);// 设置照片的输出格式
                    
                    parameters.set("jpeg-quality", 50);// 照片质量
                    //parameters.setPictureSize(display.getWidth(), display.getHeight());// 设置照片的大小
                    
                   camera.setParameters(parameters);
                  
                    camera.setPreviewCallback(new PreviewCallBack());
                    camera.setDisplayOrientation(90);
                    camera.startPreview();//开始预览
                    preview = true;
            }

           @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                    if (camera != null) {
                            if (preview)
                                    camera.stopPreview();
                            camera.release();
                    }
            } 
   }

   @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
            if (camera != null && event.getRepeatCount() == 0) {
                    switch (keyCode) {
                            case KeyEvent.KEYCODE_MENU:
                                    camera.autoFocus(null);// 自动对焦
                                    break;
                            case KeyEvent.KEYCODE_CAMERA:
                            case KeyEvent.KEYCODE_DPAD_CENTER:
                                    camera.takePicture(null, null, new TakePictureCallback());
                                    break;
                            case KeyEvent.KEYCODE_BACK:
                                    new AlertDialog.Builder(MainActivity.this).setTitle("提示")
                                    .setMessage("确定退出照相机?").setPositiveButton("确定",
                                                    new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int whichButton) {
                                                    Intent exit = new Intent(Intent.ACTION_MAIN);
                                                    exit.addCategory(Intent.CATEGORY_HOME);
                                                    exit.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(exit);
                                                    System.exit(0);
                                            }
                                    }).setNegativeButton("取消",new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int whichButton) {
                                                    // 取消按钮事件
                                                    dialog.cancel();
                                            }
                                    }).show();
                                    break;
                    }
            }
            return super.onKeyDown(keyCode, event); // 不会回到 home 页面
    }
    //预览回调接口
    private final class PreviewCallBack implements Camera.PreviewCallback {
            public void onPreviewFrame(byte[] data, Camera camera) {
                    if (data != null) {
                            int imageWidth = camera.getParameters().getPreviewSize().width;
                            int imageHeight = camera.getParameters().getPreviewSize().height;
                            int RGBData[] = new int[imageWidth * imageHeight];
                            decodeYUV420SP(RGBData, data, imageWidth, imageHeight); //解码
                            Bitmap bm = Bitmap.createBitmap(RGBData, imageWidth, imageHeight, Config.ARGB_8888);
                            bm = toGrayscale(bm);//实时滤镜效果，现在是变成黑白效果
                          //  bm = ice(bm);//冰冻效果
                            Canvas canvas = surfaceView.getHolder().lockCanvas();
                // 判断非null，才能drawBitmap.
                if (bm != null) {
                    bm = Bitmap.createScaledBitmap(bm, width, height,false);
                    canvas.drawBitmap(bm, 0, 0, null);
                }
                surfaceView.getHolder().unlockCanvasAndPost(canvas);
                    }
    }
    }
    
    static public void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int height ,int width) {
        final int frameSize = width * height;
        for (int j = 0, yp = 0; j < height; j++) {
                int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
                for (int i = 0; i < width; i++, yp++) {
                        int y = (0xff & ((int) yuv420sp[yp])) - 16;
                        if (y < 0)y = 0;
                        if ((i & 1) == 0) {
                                v = (0xff & yuv420sp[uvp++]) - 128;
                                u = (0xff & yuv420sp[uvp++]) - 128;
                        }

                        int y1192 = 1192 * y;
                        int r = (y1192 + 1634 * v);
                        int g = (y1192 - 833 * v - 400 * u);
                        int b = (y1192 + 2066 * u);

                       if (r < 0)r = 0;
                        else if (r > 262143)r = 262143;
                        if (g < 0)g = 0;
                        else if (g > 262143)g = 262143;
                        if (b < 0)b = 0;
                        else if (b > 262143)
                                b = 262143;
                        rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
                }
        }
}
    
public static Bitmap toGrayscale(Bitmap bmp) {
    int height = bmp.getHeight();
    int width = bmp.getWidth();

   Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
    Canvas c = new Canvas(bmpGrayscale);
    Paint paint = new Paint();
    ColorMatrix cm = new ColorMatrix();
    cm.setSaturation(0);
    ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
    paint.setColorFilter(f);
    c.drawBitmap(bmp, 0, 0, paint);
    return bmpGrayscale;
}

public static Bitmap ice(Bitmap bmp) {
    int width = bmp.getWidth();
    int height = bmp.getHeight();
    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
    int dst[] = new int[width * height];
    bmp.getPixels(dst, 0, width, 0, 0, width, height);
    int R, G, B, pixel;
    int pos, pixColor;
    for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                    pos = y * width + x;
                    pixColor = dst[pos]; // 获取图片当前点的像素值
                    R = Color.red(pixColor); // 获取RGB三原色
                    G = Color.green(pixColor);
                    B = Color.blue(pixColor);
                    pixel = R - G - B;
                    pixel = pixel * 3 / 2;

                    if (pixel < 0)
                            pixel = -pixel;
                    if (pixel > 255)
                            pixel = 255;

                    R = pixel; // 计算后重置R值，以下类同
                    pixel = G - B - R;
                    pixel = pixel * 3 / 2;

                    if (pixel < 0)
                            pixel = -pixel;
                    if (pixel > 255)
                            pixel = 255;

                    G = pixel;
                    pixel = B - R - G;
                    pixel = pixel * 3 / 2;

                    if (pixel < 0)
                            pixel = -pixel;
                    if (pixel > 255)
                            pixel = 255;
                    B = pixel;
                    dst[pos] = Color.rgb(R, G, B); // 重置当前点的像素值
                    } // x
            } // y
    bitmap.setPixels(dst, 0, width, 0, 0, width, height);
    return bitmap;
}

private final class TakePictureCallback implements PictureCallback {
    public void onPictureTaken(byte[] data, Camera camera) {
            try {
            	
    			Bitmap mBitmap = null;
    			if(null != data){
    				mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
    				camera.stopPreview();	
    			}
    			Matrix matrix = new Matrix();
    			matrix.postRotate((float)90.0);
    			Bitmap rotaBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(),
    					mBitmap.getHeight(), matrix, false);
    			if(null != rotaBitmap){
    			
    				saveJpeg(toGrayscale(rotaBitmap));
    			}
    			camera.startPreview();
            	
            } catch (Exception e) {
                    Log.e(TAG, e.toString());
            }
    }
}

public void saveJpeg(Bitmap bm){
	String savePath = "/mnt/sdcard/PhotoHaha/";
	File folder = new File(savePath);
	if(!folder.exists())
	{
		folder.mkdir();
	}
	long dataTake = System.currentTimeMillis();
	String jpegName = savePath + dataTake +".jpg";
	try {
		FileOutputStream fout = new FileOutputStream(jpegName);
		BufferedOutputStream bos = new BufferedOutputStream(fout);

		bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
		bos.flush();
		bos.close();

	} catch (IOException e) {
		// TODO Auto-generated catch block
	}
}

}