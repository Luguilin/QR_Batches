package com.qr_batches;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 要记得读写权限
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView iv_preview;
    Button btn_preview;
    Button btn_print;

    EditText et_width;
    EditText et_height;

    EditText et_start;
    EditText et_step;
    EditText et_end;

    TextView tv_now;
    TextView tv_quality;

    ProgressBar progress_bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv_preview = (ImageView) findViewById(R.id.iv_preview);
        tv_now = (TextView) findViewById(R.id.tv_now);
        tv_quality = (TextView) findViewById(R.id.tv_quality);

        btn_preview = (Button) findViewById(R.id.btn_preview);
        btn_print = (Button) findViewById(R.id.btn_print);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);

        btn_preview.setOnClickListener(this);
        btn_print.setOnClickListener(this);

        et_height = (EditText) findViewById(R.id.et_height);
        et_width = (EditText) findViewById(R.id.et_width);

        et_start = (EditText) findViewById(R.id.et_start);
        et_step = (EditText) findViewById(R.id.et_step);
        et_end = (EditText) findViewById(R.id.et_end);
    }

    int height = 800;
    int width = 800;

    int start = 1;
    long end = 10;
    int step = 1;


    ExecutorService cachedThreadPool;

    @Override
    public void onClick(View v) {

        if (et_width.getText().toString().length() > 0) {
            width = Integer.parseInt(et_width.getText().toString().trim());
        }
        if (et_height.getText().toString().length() > 0) {
            height = Integer.parseInt(et_width.getText().toString().trim());
        }
        switch (v.getId()) {
            case R.id.btn_preview:
                Bitmap bitmap = QRCodeUtil.createQRImage4Me("666666", width, height);
                Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.qr_background);
                Bitmap resultBitmap = QRCodeUtil.addLogo(background, bitmap, 0, 0);
                iv_preview.setImageBitmap(resultBitmap);
                break;
            case R.id.btn_print:

                cachedThreadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                btn_print.setEnabled(false);

                if (et_start.getText().toString().length() > 0) {
                    start = Integer.parseInt(et_start.getText().toString().trim());
                }
                if (et_step.getText().toString().length() > 0) {
                    step = Integer.parseInt(et_step.getText().toString().trim());
                }
                if (et_end.getText().toString().length() > 0) {
                    end = Integer.parseInt(et_end.getText().toString().trim());
                }

                progress_bar.setMax((int) end);
                progress_bar.setProgress(0);

                final String path = FileHelper.getExternalSdCardPath() + File.separator + "0_QR";
                File parent_file = new File(path);
                if (parent_file.exists()) {
                    deleteDir(parent_file);
                }
                parent_file.mkdirs();

                for (int i = 0; i < end; i++) {
                    final int finalI = i;
                    cachedThreadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            DecimalFormat df = new DecimalFormat("000000");
                            String str2 = df.format(finalI);
                            Bitmap bitmap = QRCodeUtil.createQRImage4Me(str2, width, height);
                            try {
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, new FileOutputStream(path + File.separator + str2 + ".jpg"));
                            } catch (FileNotFoundException e) {

                            }
                            Log.e(Thread.currentThread().getName()+"========", finalI + "");
                            handler.sendEmptyMessage(0);
                        }
                    });
                }
                break;
        }
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            progress_bar.setProgress(progress_bar.getProgress() + 1);
            if (progress_bar.getMax() == progress_bar.getProgress()) {
                tv_now.setText(progress_bar.getProgress() + "全部完成");
                tv_now.setTextColor(0xffff0000);
                btn_print.setEnabled(true);
            } else {
                tv_now.setText(progress_bar.getProgress() + "");
            }
            return true;
        }
    });

    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
//            递归删除目录中的子目录下
            if (children == null) return true;
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }
}
