package com.zhuimeng.handlertest;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView beauty;
    private int images[] = {R.mipmap.yangmi, R.mipmap.namei, R.mipmap.doubi, R.mipmap.moutain};
    private int index;
    private Button recycleMsg;
    private Button sendMsg;
    private Button method1;
    private Button method2;
    private Button method3;
    private Button method4;
    private Button uiToThread;
    private Button threadToUI;
    private TextView showMethod;
    private TextView showMsg;

    //创建主线程handler
    private Handler handler = new Handler();

    private Handler threadHandler;
    //创建主线程handler2
    private Handler handler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //主线程向子线程发送消息
            Message message = new Message();
            Log.e("tag", "Main thread");
            threadHandler.sendMessageDelayed(message, 1000);
        }
    };
    //创建主线程handler3
    private Handler handler3 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            showMethod.setText("private Handler handler3 = new Handler() {" +
                    " public void handleMessage(Message msg)new Thread() {" +
                    " + handler3.sendEmptyMessage(1);}}.start()");
            showMethod.setMovementMethod(ScrollingMovementMethod.getInstance());//实现滚动浏览
            showMsg.setText("子线程更新UI 2");
        }
    };

    private MyRunnable myRunnable = new MyRunnable();

    class MyRunnable implements Runnable {

        @Override
        public void run() {
            index++;
            index = index % 4;
            beauty.setImageResource(images[index]);
            handler.postDelayed(myRunnable, 2000);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        recycleMsg.setOnClickListener(this);
        sendMsg.setOnClickListener(this);
        uiToThread.setOnClickListener(this);
        threadToUI.setOnClickListener(this);
        method1.setOnClickListener(this);
        method2.setOnClickListener(this);
        method3.setOnClickListener(this);
        method4.setOnClickListener(this);

        handler.postDelayed(myRunnable, 2000);

        HandlerThread thread = new HandlerThread("handlerThread");
        thread.start();
        //创建子线程handler2(通过HandlerThread获取Looper)
        threadHandler = new Handler(thread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                //子线程向主线程发送消息
                Message message = new Message();
                Log.e("tag", "thread handler");
                handler2.sendMessageDelayed(message, 1000);
            }
        };

    }

    private void initView() {
        beauty = (ImageView) findViewById(R.id.iv_beauty);
        showMethod = (TextView) findViewById(R.id.tv_show_method);
        showMsg = (TextView) findViewById(R.id.tv_show_msg);
        recycleMsg = (Button) findViewById(R.id.recycle_handler_msg);
        sendMsg = (Button) findViewById(R.id.send_msg);
        uiToThread = (Button) findViewById(R.id.uiToThread);
        threadToUI = (Button) findViewById(R.id.threadToUI);
        method1 = (Button) findViewById(R.id.method1);
        method2 = (Button) findViewById(R.id.method2);
        method3 = (Button) findViewById(R.id.method3);
        method4 = (Button) findViewById(R.id.method4);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recycle_handler_msg:
                handler.removeCallbacks(myRunnable);
                break;
            case R.id.send_msg:
                myRunnable.run();
                break;
            case R.id.uiToThread:
                handler2.sendEmptyMessage(1);
                break;
            case R.id.threadToUI:
                handler2.removeMessages(1);
                break;
            case R.id.method1:
                Method1();
                break;
            case R.id.method2:
                Method2();
                break;
            case R.id.method3:
                Method3();
                break;
            case R.id.method4:
                Method4();
                break;
        }
    }

    private void Method1() {
        //创建子线程
        new Thread() {
            @Override
            public void run() {
                handler3.post(new Runnable() {
                    @Override
                    public void run() {
                        showMethod.setText(" new Thread() { + handler3.post(new Runnable() {}");
                        showMsg.setText("子线程更新UI 1");
                    }
                });
            }
        }.start();
    }

    private void Method2() {
        //创建子线程
        new Thread() {
            @Override
            public void run() {
                handler3.sendEmptyMessage(1);
            }
        }.start();
    }

    private void Method3(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showMethod.setText(" runOnUiThread(new Runnable(){};");
                showMsg.setText("子线程更新UI 3");
            }
        });
    }

    private void Method4(){
        //创建子线程
        new Thread() {
            @Override
            public void run() {
                showMethod.post(new Runnable() {
                    @Override
                    public void run() {
                        showMethod.setText(" new Thread() { + showMethod.post(new Runnable() {");
                    }
                });

                showMsg.post(new Runnable() {
                   @Override
                   public void run() {
                       showMsg.setText("子线程更新UI 4");
                   }
               });
            }
        }.start();
    }
}
