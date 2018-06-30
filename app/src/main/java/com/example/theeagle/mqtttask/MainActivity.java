package com.example.theeagle.mqtttask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText messageEditText, subscribeEditText, unsubscribeEditText;
    private Button messageBtn, subscribeBtn, unsubscribeBtn, helloBtn, byeBtn;
    private MqttAndroidClient mqttAndroidClient;
    private MQTTClint mqttClint;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initClint();
        initViews();
        listeners();

    }

    /**
     * in this method the initialization of the clint
     */
    private void initClint() {
        mqttClint = new MQTTClint();
        mqttAndroidClient = mqttClint.getMqttClient(this, Constants.MQTT_BROKER_URL, Constants.CLIENT_ID);
    }

    /**
     * in this method the initialization of the views
     */
    private void initViews() {
        messageEditText = findViewById(R.id.message);
        subscribeEditText = findViewById(R.id.subscribe);
        unsubscribeEditText = findViewById(R.id.unsubscribe);
        messageBtn = findViewById(R.id.send_message);
        subscribeBtn = findViewById(R.id.subscribe_btn);
        unsubscribeBtn = findViewById(R.id.unsubscribe_btn);
        helloBtn = findViewById(R.id.send_hello);
        byeBtn = findViewById(R.id.send_bye);
        textView=findViewById(R.id.text);
    }

    /**
     * in this method the adding of the on click listeners on the buttons
     */
    private void listeners() {
        messageBtn.setOnClickListener(this);
        subscribeBtn.setOnClickListener(this);
        unsubscribeBtn.setOnClickListener(this);
        helloBtn.setOnClickListener(this);
        byeBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        startService(new Intent(this, MQTTService.class));
        switch (v.getId()) {
            case R.id.send_hello:
                String hello = getString(R.string.hello);
                try {
                    mqttClint.publishMessage(mqttAndroidClient, hello, 1, Constants.PUBLISH_TOPIC);
                } catch (MqttException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.send_bye:
                String bye = getString(R.string.bye);
                try {
                    mqttClint.publishMessage(mqttAndroidClient, bye, 1, Constants.PUBLISH_TOPIC);
                } catch (MqttException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.send_message:
                String message = messageEditText.getText().toString().trim();
                if (!message.isEmpty()) {
                    try {
                        mqttClint.publishMessage(mqttAndroidClient, message, 1, Constants.PUBLISH_TOPIC);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.subscribe_btn:
                String subscribeTopic = subscribeEditText.getText().toString().trim();
                if (!subscribeTopic.isEmpty()) {
                    try {
                        mqttClint.subscribe(mqttAndroidClient, subscribeTopic, 1);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.unsubscribe_btn:
                String unsubscribeTopic = unsubscribeEditText.getText().toString().trim();
                if (!unsubscribeTopic.isEmpty()) {
                    try {
                        mqttClint.unSubscribe(mqttAndroidClient, unsubscribeTopic);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            textView.setText(message);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(MainActivity.this)
                .registerReceiver(broadcastReceiver, new IntentFilter("NOW"));
    }
}

