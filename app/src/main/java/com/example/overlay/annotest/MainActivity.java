package com.example.overlay.annotest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.annotest.anno.People;

public class MainActivity extends AppCompatActivity {

    @People(name = "zhangsan", age = 18)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            Log.e("MainActivity", ": " + getClass().getDeclaredMethod("onCreate",Bundle.class).getAnnotation(People.class).name());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}