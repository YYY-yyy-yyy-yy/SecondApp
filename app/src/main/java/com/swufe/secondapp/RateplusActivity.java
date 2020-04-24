package com.swufe.secondapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;


public class RateplusActivity extends AppCompatActivity implements Runnable{
    EditText rmb;
    TextView show;
    private final String TAG="Rate";
    private float dollarRate=0.1f;
    private float euroRate=0.2f;
    private float wonRate=0.3f;
    Handler handler;
    private String updateDate="";
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        rmb=findViewById(R.id.rmb);
        show=findViewById(R.id.showOut);

        //获得sp中保存的数据
        SharedPreferences sp=getSharedPreferences("myrate", Activity.MODE_PRIVATE);
        SharedPreferences s=PreferenceManager.getDefaultSharedPreferences(this);//名字不能改
        dollarRate=sp.getFloat("dollar_rate",0.0f);
        euroRate=sp.getFloat("euro_rate",0.0f);
        wonRate=sp.getFloat("won_rate",0.0f);
        updateDate = sp.getString("update_date","");

        Log.i(TAG, "onCreate: sp dollarRate=" + dollarRate);
        Log.i(TAG, "onCreate: sp euroRate=" + euroRate);
        Log.i(TAG , "onCreate: sp wonRate=" + wonRate);

        //获取当前系统时间
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String todayStr = sdf.format(today);

        Log.i(TAG,"todayStr:"+todayStr);

        //判断时间
        if(!todayStr.equals((today))){
            Log.i(TAG,"需要更新");
            //开启子线程
            Thread t=new Thread(this);
            t.start();
        }else {
            Log.i(TAG,"不需要更新");
        }

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what==5){
                    Bundle bdl=(Bundle)msg.obj;
                    dollarRate=bdl.getFloat("dollar-rate");
                    euroRate=bdl.getFloat("euro-rate");
                    wonRate=bdl.getFloat("won-rate");
                    Log.i("handleMessage", "dollarRate=" + dollarRate);
                    Log.i("handleMessage", "euroRate=" + euroRate);
                    Log.i("handleMessage", "wonRate=" + wonRate);
                    Toast.makeText(RateplusActivity.this,"汇率更新",Toast.LENGTH_SHORT).show();

                    //保存更新的日期
                    SharedPreferences sp=getSharedPreferences("myrate", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sp.edit();
                    editor.putString("update_date",todayStr);
                    editor.putFloat("dollar_rate",dollarRate);
                    editor.putFloat("euro_rate",euroRate);
                    editor.putFloat("won_rate",wonRate);
                    editor.apply();

                }
                super.handleMessage(msg);
            }

        };//匿名类
    }

    public void onClick(View btn){
        String str=rmb.getText().toString();//获取用户输入类型
        float r;
        if(str.length()>0){
            r=Float.parseFloat(str);
        }else{
            Toast.makeText(this,"请输入金额",Toast.LENGTH_SHORT).show();
            return;
        }
        // float val=0;
        if(btn.getId()==R.id.dollar_rate){
            show.setText(String.format("%.2f",r*dollarRate));
            // val=f*(1/6.7f);//强制转换
        }
        else if(btn.getId()==R.id.euro_rate){
            show.setText(String.format("%.2f",r*euroRate));
            //val=r*(1/11f);//强制转换
        }
        else{
            show.setText(String.format("%.2f",r*wonRate));
            //val=r*500;
        }
        //show.setText(String.valueOf(val));

    }
    public void openOne(View btn){
        Intent config = openConfig();
        startActivityForResult(config,1);
    }

    private Intent openConfig() {
        Intent config = new Intent(this, ConfigActivity.class);
        config.putExtra("dollar_rate_key", dollarRate);
        config.putExtra("euro_rate_key", euroRate);
        config.putExtra("won_rate_key", wonRate);
        Log.i("openone", "dollarRate=" + dollarRate);
        Log.i("openone", "euroRate=" + euroRate);
        Log.i("openone", "wonRate=" + wonRate);
        return config;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==1 &&resultCode==2 ){
            Bundle bundle= null;
            if (data != null) {
                bundle = data.getExtras();
            }
            dollarRate=bundle.getFloat("key_dollar",0.1f);
            euroRate=bundle.getFloat("key_euro",0.1f);
            wonRate=bundle.getFloat("key_won",0.1f);
            Log.i(TAG,"onActivityResult:dollarRate="+dollarRate);
            Log.i(TAG,"onActivityResult:euroRate="+euroRate);
            Log.i(TAG,"onActivityResult:wonRate="+wonRate);
            //将新设置的汇率写到sp中
            SharedPreferences sp=getSharedPreferences("myrate", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor=sp.edit();
            editor.putFloat("dollar_rate",dollarRate);
            editor.putFloat("euro_rate",euroRate);
            editor.putFloat("won_rate",wonRate);
            editor.apply();//commit或者apply
            Log.i(TAG,"onActivityResult:数据已经保存在SP中");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rate,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.menu_set){
            Intent config = openConfig();
            //startActivity(config);
            startActivityForResult(config,1);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void run() {
        Log.i(TAG, "run: run()......");
        Bundle bundle = new Bundle();
        Document doc;
            try {
                doc = Jsoup.connect("https://www.usd-cny.com/bankofchina.htm").get();
                Log.i(TAG, "run:" + doc.title());
                Elements tds = doc.getElementsByTag("td");
                for (int i = 0; i < tds.size(); i += 6) {
                    Element td1 = tds.get(i);
                    Element td2 = tds.get(i + 5);
                    String str1 = td1.text();
                    String val = td2.text();
                    if ("美元".equals(str1)) {
                        bundle.putFloat("dollar-rate", 100f / Float.parseFloat(val));
                    } else if ("欧元".equals(str1)) {
                        bundle.putFloat("euro-rate", 100f / Float.parseFloat(val));
                    } else if ("韩元".equals(str1)) {
                        bundle.putFloat("won-rate", 100f / Float.parseFloat(val));
                    }
                    Log.i(TAG,"判断完成");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = handler.obtainMessage(5);
            msg.obj = bundle;
            handler.sendMessage(msg);
            Log.i(TAG,"HANDLE");
        }

    private String inputStream2String(InputStream inputStream) throws  IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream, "gb2312");
        for( ; ;){
            int rsz=in.read(buffer,0,buffer.length);
            if(rsz<0)
                break;
            out.append(buffer,0,rsz);

        }return out.toString();
    }

}
