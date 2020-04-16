package com.swufe.secondapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class JifenActivity extends AppCompatActivity {
    //重新上传
    TextView score;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jifen);
        score = (TextView) findViewById(R.id.score);

    }
    public void btnAdd1(View click){
        showScore(1);
    }
    public void btnAdd2(View click){
        showScore(2);
    }
    public void btnAdd3(View click){
        showScore(3);
    }
    public void btnReset(View click){
        score.setText("0");
    }
    private void showScore(int inc){
        Log.i("show","inc="+inc);
        String oldScore = (String) score.getText();
        int newScore=Integer.parseInt(oldScore) + inc;
        score.setText(""+newScore);

    }
}
