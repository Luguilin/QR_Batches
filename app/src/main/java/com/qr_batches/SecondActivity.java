package com.qr_batches;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.SeekBar;


public class SecondActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    ImageView iv_image_souce;
    ImageView iv_image_result;

    Bitmap bitmap_background = null;

    Bitmap qr_bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        iv_image_souce = (ImageView) findViewById(R.id.iv_image_souce);
        iv_image_result = (ImageView) findViewById(R.id.iv_image_result);
        ((SeekBar) findViewById(R.id.sb_up_down)).setOnSeekBarChangeListener(this);
        ((SeekBar) findViewById(R.id.sb_left_right)).setOnSeekBarChangeListener(this);
        ((SeekBar) findViewById(R.id.sb_scale)).setOnSeekBarChangeListener(this);

        bitmap_background = BitmapFactory.decodeResource(getResources(), R.drawable.qr_background);
        bitmap_background = scaleBitmap(bitmap_background, 0.3f);

        iv_image_souce.setImageBitmap(bitmap_background);

        qr_bitmap = QRCodeUtil.createQRImage4Me("000001", 400, 400);


        //获取图片的宽高
        int srcWidth = iv_image_souce.getWidth();
        int srcHeight = iv_image_souce.getHeight();

        iv_image_result.setImageBitmap(addLogo(bitmap_background, qr_bitmap, 0.8f, 0, 0));

    }

    int tempX = 0;
    int tempY = 0;
    float temp_scale = 1f;

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        switch (seekBar.getId()) {
            case R.id.sb_scale:
                temp_scale=seekBar.getProgress()/50f;
                break;
            case R.id.sb_up_down:
                int srcHeight = bitmap_background.getHeight();
                tempY = (int) ((seekBar.getProgress() / 100.f) * srcHeight);
                break;
            case R.id.sb_left_right:
                //获取图片的宽高
                int srcWidth = bitmap_background.getWidth();
                tempX = (int) ((seekBar.getProgress() / 100.f) * srcWidth);
                break;
        }
        iv_image_result.setImageBitmap(addLogo(bitmap_background, qr_bitmap, temp_scale, tempX, tempY));
    }

    /**
     * 在二维码中间添加Logo图案
     */
    public Bitmap addLogo(Bitmap src, Bitmap logo, float scale, int translateX, int translateY) {

        //获取图片的宽高
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();

        //logo大小为二维码整体大小的1/5
//        float scaleFactor = srcWidth / 2;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.RGB_565);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
//            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);
            canvas.scale(scale, scale);
            canvas.translate(translateX, translateY);
//            canvas.drawBitmap(logo, 0, (srcHeight - logoHeight) / 2, null);
            canvas.drawBitmap(logo, 0, 0, null);
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }

        return bitmap;
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.btn_down:
//
//                break;
//            case R.id.btn_left:
//                break;
//        }
//    }

    /**
     * 按比例缩放图片
     *
     * @param origin 原图
     * @param ratio  比例
     * @return 新的bitmap
     */
    private Bitmap scaleBitmap(Bitmap origin, float ratio) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(ratio, ratio);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }


}
