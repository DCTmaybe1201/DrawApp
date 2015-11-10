package tw.wyz1201.personalapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class MainActivity extends ActionBarActivity {

    private TabHost tabHost;
    private Button btn_loadPic, btn_resume, btn_save;
    private Button Black, Blue, Red, Green, Yellow, Gray, Cyan;
    private ImageView iv_canvas;//存檔用
    private Bitmap baseBitmap;
    private Canvas canvas;
    private Paint paint=new Paint();
    private ImageView ivLoadedPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        setTab();
        setListener();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
    }
    private void findViews(){
        tabHost=(TabHost)findViewById(R.id.tabHost);
        btn_loadPic=(Button)findViewById(R.id.btnLoadPic);
        ivLoadedPic=(ImageView)findViewById(R.id.ivLoadedPic);
        btn_save=(Button)findViewById(R.id.btn_save);
        btn_resume=(Button)findViewById(R.id.btn_resume);
        iv_canvas=(ImageView)findViewById(R.id.iv_canvas);
        Black=(Button)findViewById(R.id.btnBlack);
        Blue=(Button)findViewById(R.id.btnBlue);
        Red=(Button)findViewById(R.id.btnRed);
        Green=(Button)findViewById(R.id.btnGreen);
        Yellow=(Button)findViewById(R.id.btnYellow);
        Gray=(Button)findViewById(R.id.btnGray);
        Cyan=(Button)findViewById(R.id.btnCyan);
    }
    private void setListener(){
        btn_loadPic.setOnClickListener(click);
        btn_resume.setOnClickListener(click);
        btn_save.setOnClickListener(click);
        iv_canvas.setOnTouchListener(touch);
        Black.setOnClickListener(setColor);
        Blue.setOnClickListener(setColor);
        Red.setOnClickListener(setColor);
        Green.setOnClickListener(setColor);
        Yellow.setOnClickListener(setColor);
        Gray.setOnClickListener(setColor);
        Cyan.setOnClickListener(setColor);
    }
    private void setTab(){
        tabHost.setup();
        TabSpec tabSpec1=tabHost.newTabSpec("Tab1");
        tabSpec1.setContent(R.id.tab1);
        tabSpec1.setIndicator("放張圖片當背景吧");
        tabHost.addTab(tabSpec1);

        TabSpec tabSpec2=tabHost.newTabSpec("Tab2");
        tabSpec2.setContent(R.id.tab2);
        tabSpec2.setIndicator("自己畫一張也不錯");
        tabHost.addTab(tabSpec2);
        tabHost.setCurrentTab(0);//顯示第一個tab page
    }
    private View.OnTouchListener touch=new View.OnTouchListener() {
        float startX, startY;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN://點下去時
                    if(baseBitmap==null){
                        baseBitmap=Bitmap.createBitmap(iv_canvas.getWidth(),
                                iv_canvas.getHeight(), Bitmap.Config.ARGB_8888);//每個pixel佔8位元(最大了)
                        canvas=new Canvas(baseBitmap);
                        canvas.drawColor(Color.WHITE);
                    }
                    startX=event.getX();
                    startY=event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float stopX=event.getX();//紀錄移動點座標
                    float stopY=event.getY();
                    canvas.drawLine(startX, startY, stopX, stopY, paint);
                    startX=event.getX();//重抓現在座標
                    startY=event.getY();
                    iv_canvas.setImageBitmap(baseBitmap);
                    break;
                case MotionEvent.ACTION_UP://離開
                    break;
                default:
                    break;
            }
            return true;
        }
    };
    private View.OnClickListener setColor=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnBlack:
                    paint.setColor(Color.BLACK);
                    break;
                case R.id.btnBlue:
                    paint.setColor(Color.BLUE);
                    break;
                case R.id.btnRed:
                    paint.setColor(Color.RED);
                    break;
                case R.id.btnGreen:
                    paint.setColor(Color.GREEN);
                    break;
                case R.id.btnYellow:
                    paint.setColor(Color.YELLOW);
                    break;
                case R.id.btnGray:
                    paint.setColor(Color.GRAY);
                    break;
                case R.id.btnCyan:
                    paint.setColor(Color.CYAN);
                    break;
            }
        }
    };
    private View.OnClickListener click=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_save:
                    saveBitmap();
                    break;
                case R.id.btn_resume:
                    resumeCanvas();
                    break;
                case R.id.btnLoadPic:
                    loadPicture();
                    break;
            }
        }
    };
    private void resumeCanvas(){
        if (baseBitmap != null){
            baseBitmap=Bitmap.createBitmap(iv_canvas.getWidth(), iv_canvas.getHeight(), Bitmap.Config.ARGB_8888);
            canvas=new Canvas(baseBitmap);
            canvas.drawColor(Color.WHITE);
            iv_canvas.setImageBitmap(baseBitmap);
            Toast.makeText(MainActivity.this, "清除畫板完成", Toast.LENGTH_SHORT).show();
        }
    }
    private void saveBitmap(){
        try{
            File file=new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis()+".png");
            FileOutputStream stream=new FileOutputStream(file);
            baseBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);// 100=不壓縮 ,將bitmap壓縮成指定格式圖片並寫入檔案
            Toast.makeText(this, "圖片存檔完成", Toast.LENGTH_SHORT).show();
        }catch (FileNotFoundException e){
            Toast.makeText(this, "存檔失敗", Toast.LENGTH_SHORT).show();
        }
    }
    private void loadPicture(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            //取得圖檔的路徑位置
            Uri uri = data.getData();
            //抽象資料的接口
            ContentResolver cr = this.getContentResolver();
            try {
                //由抽象資料接口轉換圖檔路徑為Bitmap
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                // 將Bitmap設定到ImageView
                ivLoadedPic.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
