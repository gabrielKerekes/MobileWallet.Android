package com.smartwallet.android.services;

import android.content.Context;

import org.eclipse.paho.android.service.MqttAndroidClient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by ROCK LEE on 30.12.2016.
 */

public interface MQTTClientInterface {

    int QOS = 2;
    String ALL_TOPICS_NAME = "/pn/response/allShops";

    String LINK_BANK_ACCOUNT_REQUEST = "/bank/request/link_account/";
    String LINK_BANK_ACCOUNT_RESPONSE = "/bank/response/link_account/";
    String HISTORY_TOPIC_REQUEST = "/bank/request/history/";
    String HISTORY_TOPIC_RESPONSE = "/bank/response/history/";
    String BALANCE_TOPIC_REQUEST = "/bank/request/balance/";
    String BALANCE_TOPIC_RESPONSE = "/bank/response/balance/";

    String BUY_TOPIC_REQUEST = "/pn/request/buy/";
    String BUY_TOPIC_RESPONSE = "/pn/response/buy/";
    String MULTIPLE_BUY_TOPIC_REQUEST = "/pn/request/buyMultiple/";
    String MULTIPLE_BUY_TOPIC_RESPONSE = "/pn/response/buyMultiple/";
    String PRODUCT_HISTORY_REQUEST = "/pn/request/history/";
    String PRODUCT_HISTORY_RESPONSE = "/pn/response/history/";

    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
    DateFormat dateFormatHours= new SimpleDateFormat("HH:mm:ss");
    DateFormat dateFormatDay = new SimpleDateFormat("dd.MM.yyyy");

    MqttAndroidClient createClient(Context context, String ip, String port);
    void connect();
    void disconnect();
    void subscribe(final String topic);
    void unSubscribe(final String topic);
    void publish(final String topic, String payload);
    void setCallbacs();
    void addAllTopics(String response);
    void subscribeAllTopics();



}
