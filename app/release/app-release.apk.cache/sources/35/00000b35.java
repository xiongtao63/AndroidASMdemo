package com.xiongtao.asmdemo;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import java.io.PrintStream;

/* loaded from: classes.dex */
public class MainActivity extends AppCompatActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(com.example.mywechat.R.layout.activity_main);
        testApp();
    }

    private void testApp() {
        long currentTimeMillis = System.currentTimeMillis();
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long currentTimeMillis2 = System.currentTimeMillis();
        PrintStream printStream = System.out;
        printStream.println("execute testApp :" + (currentTimeMillis2 - currentTimeMillis) + " ms.");
    }
}