package com.qr_batches;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Hashtable;

/**
 * 二维码生成工具类
 */

public class QRCodeUtil {

    /**
     *  因为二维码边框设置那里不起作用，不管设置多少，都会生成白边，所以根据网上的例子进行修改，自定义控制白边宽度，
     该方法生成自定义白边框后的bitMatrix；
     * @param matrix
     * @param margin
     * @return
     */
    private BitMatrix updateBit(BitMatrix matrix, int margin){
        int tempM = margin*2;
        int[] rec = matrix.getEnclosingRectangle();   //获取二维码图案的属性
        int resWidth = rec[2] + tempM;
        int resHeight = rec[3] + tempM;
        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight); // 按照自定义边框生成新的BitMatrix
        resMatrix.clear();
        for(int i= margin; i < resWidth- margin; i++){   //循环，将二维码图案绘制到新的bitMatrix中
            for(int j=margin; j < resHeight-margin; j++){
                if(matrix.get(i-margin + rec[0], j-margin + rec[1])){
                    resMatrix.set(i,j);
                }
            }
        }
        return resMatrix;
    }


    public static Bitmap createQRImage4Me(String content, int widthPix, int heightPix) {
        if (content == null || "".equals(content)) {
//                return false;
            return null;
        }

        //配置参数
        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        //容错级别
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        //设置空白边距的宽度
//            hints.put(EncodeHintType.MARGIN, 2); //default is 4
        hints.put(EncodeHintType.MARGIN, 1);

        // 图像数据转换，使用了矩阵转换
        BitMatrix bitMatrix = null;
        try {
            bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, widthPix, heightPix, hints);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        int[] pixels = new int[widthPix * heightPix];
        // 下面这里按照二维码的算法，逐个生成二维码的图片，
        // 两个for循环是图片横列扫描的结果
        for (int y = 0; y < heightPix; y++) {
            for (int x = 0; x < widthPix; x++) {
                if (bitMatrix.get(x, y)) {
                    pixels[y * widthPix + x] = 0xff000000;
                } else {
                    pixels[y * widthPix + x] = 0xffffffff;
                }
            }
        }
        // 生成二维码图片的格式，使用ARGB_8888
        Bitmap bitmap = Bitmap.createBitmap(widthPix, heightPix, Bitmap.Config.RGB_565);
        bitmap.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix);

        //必须使用compress方法将bitmap保存到文件中再进行读取。直接返回的bitmap是没有任何压缩的，内存消耗巨大！
        return addNumber(bitmap,content);//!= null && bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(filePath));
    }

    public static int text_size=0;
    /**
     * 在二维码中间添加Logo图案
     */
    private static Bitmap addNumber(Bitmap src,String testString) {
        //获取图片的宽高
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();


        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(srcWidth/4);
        textPaint.setStyle(Paint.Style.FILL);
        //该方法即为设置基线上那个点究竟是left,center,还是right  这里我设置为center
        textPaint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();

        int text_height= (int) (fontMetrics.bottom - fontMetrics.top);


        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight+text_height-text_height/4, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(src, 0, 0, null);

        Rect rect = new Rect(0,srcHeight,srcWidth,srcHeight+text_height-text_height/4);//画一个矩形
        Paint rectPaint = new Paint();
        rectPaint.setColor(Color.WHITE);
        rectPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(rect, rectPaint);

        float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
        float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom

//        int baseLineY = (int) (rect.centerY() - top/2 - bottom/2);//基线中间点的y轴计算公式
//        canvas.drawText(testString,rect.centerX(),baseLineY,textPaint);

        canvas.drawText(testString,rect.centerX(),rect.centerY()+text_height/8,textPaint);
//

//        int text_height=(fontMetrics.bottom - fontMetrics.top);

        // 转载请注明出处：http://blog.csdn.net/hursing
//            int baseline = (srcHeight - fontMetrics.bottom - fontMetrics.top) / 2;
//        int baseline =srcHeight-(fontMetrics.bottom - fontMetrics.top)/2;

//        int text_height=(fontMetrics.bottom - fontMetrics.top);
        try {

            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }
        return bitmap;
    }




    /**
     * 在二维码中间添加Logo图案
     */
    public static Bitmap addLogo(Bitmap src, Bitmap logo,int x,int y) {
        if (src == null) {
            return null;
        }

        if (logo == null) {
            return src;
        }

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
            canvas.translate(350,-550);
            canvas.scale(1.5f,1.5f);
            canvas.drawBitmap(logo,0, (srcHeight - logoHeight) / 2, null);
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }

        return bitmap;
    }
}
