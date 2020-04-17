package com.swufe.secondapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class RateplusActivity extends AppCompatActivity {

    private final String TAG = "Rate";
    private float dollarRate =0.1f;
    private float euroRate =0.2f;
    private float wonRate =0.3f;
    EditText rmb;
    TextView show;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rateplus);
        rmb = (EditText) findViewById(R.id.rmb);
        show = (TextView) findViewById(R.id.showOut);

        SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
        dollarRate = sharedPreferences.getFloat("dollar_rate",0.0f);
        euroRate = sharedPreferences.getFloat("euro_rate",0.0f);
        wonRate = sharedPreferences.getFloat("won_rate",0.0f);
        Log.i(TAG,"sp dollar_rate:"+dollarRate);
        Log.i(TAG,"sp euro_rate:"+euroRate);
        Log.i(TAG,"sp won_rate:"+wonRate);
    }
    public void onClick(View btn){
        String str = rmb.getText().toString();
        float r = 0;
        if(str.length()>0){
            r = Float.parseFloat(str);
        }else{
            Toast.makeText(this,"请输入金额 ",Toast.LENGTH_SHORT).show();
        }
        if(btn.getId()==R.id.btn_dollar){
            show.setText(String.format("%.2f",r*dollarRate));
        }else if(btn.getId()==R.id.btn_euro){
            show.setText(String.format("%.2f",r*euroRate));
        }else if(btn.getId()==R.id.btn_won){
            show.setText(String.format("%.2f",r*wonRate));
        }
    }
    public void openOne(View btn){
        openConfig();
    }
    private void openConfig(){
        Intent config = new Intent(this,ConfigActivity.class);
        config.putExtra("dollar_rate_key",dollarRate);
        config.putExtra("euro_rate_key",euroRate);
        config.putExtra("won_rate_key",wonRate);
        Log.i(TAG,"dollar_rate_key:"+dollarRate);
        Log.i(TAG,"euro_rate_key:"+euroRate);
        Log.i(TAG,"won_rate_key:"+wonRate);
        startActivityForResult(config,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==1 && resultCode==2){
            Bundle bundle = data.getExtras();
            dollarRate = bundle.getFloat("key_dollar",0.1f);
            euroRate = bundle.getFloat("key_euro",0.1f);
            wonRate = bundle.getFloat("key_won",0.1f);

            SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat("dollar_rate",dollarRate);
            editor.putFloat("euro_rate",euroRate);
            editor.putFloat("won_rate",wonRate);
            editor.commit();
            Log.i(TAG,"数据已保存到sp中");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.menu_set){
            Intent config = new Intent(this,ConfigActivity.class);
            config.putExtra("dollar_rate_key",dollarRate);
            config.putExtra("euro_rate_key",euroRate);
            config.putExtra("won_rate_key",wonRate);
            Log.i(TAG,"dollar_rate_key:"+dollarRate);
            Log.i(TAG,"euro_rate_key:"+euroRate);
            Log.i(TAG,"won_rate_key:"+wonRate);

            startActivityForResult(config,1);
        }
        return super.onOptionsItemSelected(item);
    }
}
