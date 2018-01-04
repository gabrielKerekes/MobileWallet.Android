package com.mobilewallet.android.services;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mobilewallet.android.ProductActivity;
import com.mobilewallet.android.pojos.BoughtProduct;
import com.mobilewallet.android.pojos.Payment;
import com.mobilewallet.android.pojos.Product;

import static com.mobilewallet.android.services.ToastMaker.makeToast;

/**
 * Created by ROCK LEE on 30.12.2016.
 *
 * ClientManager is a singleton class, which holds many important variables.
 * It also connects to an broker, sends and receives all the messages.
 * It also subscribes topics and parse responses in callbacs.
 */

public class ClientManager implements MQTTClientInterface{

    private static ClientManager clientManager = null;
    private  MqttAndroidClient client;
    private Context context;
    public static ProductActivity productActivity;
    public static String userName;
    public static String email;
    public static String accountNumber;
    public static String BIC;
    public static Boolean addedBankSuccessful = false;


    public static int buyId = 0;
    public static Map<Integer, List<Product>> waitingForBuyEvaluation = new HashMap<Integer, List<Product>>();
    public static Boolean connected = false;
    public static double balance;
    private static String lastHistoryPaymentsMessage;
    private static String lastBalanceAccountMessage;
    private List<String> availableTopics = new ArrayList<>();
    private List<String> subscribedTopics = new ArrayList<>();
    private List<Product> availableProducts = new ArrayList<>();
    private List<BoughtProduct> boughtProducts = new ArrayList<>();


    public void setContext(Context context) {
        this.context = context;
    }

    public void setClient(MqttAndroidClient client) {
        this.client = client;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    private List<Payment> payments = new ArrayList<>();

    public List<String> getAvailableTopics() {
        return availableTopics;
    }

    public static int getAndUpdateBuyId() {
        return buyId++;
    }

    public List<BoughtProduct> getBoughtProducts() {
        return boughtProducts;
    }

    public List<Product> getAvailableProducts() {
        return availableProducts;
    }

    public List<String> getSubscribedTopics() {
        return subscribedTopics;
    }


    private ClientManager() {
    }

    public static ClientManager getInstance() {
        if (clientManager == null) {
            clientManager = new ClientManager();
        }
        return clientManager;
    }

    public void setTestingData() {
        availableTopics.add("Billa");
        availableTopics.add("Lidl");
        availableTopics.add("Tesco");

        Product p1 = new Product(1,"Pomarance", 5, 5,"Billa");
        Product p2 = new Product(2,"Citrony", 12.5, 5,"Lidl");
        Product p3 = new Product(3,"Paradajky", 5, 12,"Tesco");

        availableProducts.add(p1);
        availableProducts.add(p2);
        availableProducts.add(p3);

        //boughtProducts.add(p1);
        //boughtProducts.add(p2);

        Payment pa1 = new Payment(1,20,new Date().toString());
        Payment pa2 = new Payment(1,20,new Date().toString());
        Payment pa3 = new Payment(1,20,new Date().toString());
        payments.add(pa1);
        payments.add(pa2);
        payments.add(pa3);

        balance = 1500;
    }

    public void setTestNotifications(Context context) {

//        Notification confirmIdentity = new Notification(new Date(), "content", ClientManager.accountNumber, NotificationStatus.PENDING);
//        Notification confirmTransaction = new Notification(new Date(), "content", ClientManager.accountNumber, NotificationStatus.PENDING, 55, "");
//
//        Gson gson = new Gson();
//
//        final SharedPreferences sharedPreferences = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
//        final SharedPreferences.Editor editor = sharedPreferences.edit();
//        //Set<String> existing = sharedPreferences.getStringSet("notifications", new HashSet<String>()); this is good for updating notifications
//        Set<String> edited = new HashSet<>();
//        edited.add(gson.toJson(confirmIdentity));
//        edited.add(gson.toJson(confirmTransaction));
//        editor.putStringSet("notifications", edited);
//        editor.apply();
//
//        Notification confirmTransaction2 = new Notification(new Date(), "content", ClientManager.accountNumber, NotificationStatus.REJECTED, 1424, "");
//        SharedPreferencesHelper.addToNotifications(context, confirmTransaction2);
//
//        //SharedPreferencesHelper.removeFromNotifications(confirmTransaction2, context);
//        SharedPreferencesHelper.editNotificationStatus(context, confirmTransaction.getId(), NotificationStatus.CONFIRMED);

    }

    @Override
    public MqttAndroidClient createClient(Context context, String ip, String port) {
        String clientId = MqttClient.generateClientId();
        return new MqttAndroidClient(
                context,
                "tcp://"+ip+":" + port,
                clientId);
    }

    @Override
    public void connect() {
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    connected = true;
                    askForBalance();
                    askForHistory();
                    askForProductHistory(-1); // -1 stands for getting all
                    subscribeAllTopics();
                    Log.d("connection", "onSuccess");
                    makeToast("Connection succesful",context);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d("connection", "onFailure");
                    makeToast("Failed connect",context);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        try {
            IMqttToken disconToken = client.disconnect();
            disconToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // we are now successfully disconnected
                    connected = false;
                    Log.d("disconnections", "onSuccess");
                    makeToast("Disconnection succesful",context);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // something went wrong, but probably we are disconnected anyway
                    Log.d("disconnection", "onFailure");
                    makeToast("Failed to disconnect",context);
                }
            });
        }
        catch (MqttException e){
            e.printStackTrace();
        }
    }


    @Override
    public void unSubscribe(final String topic) {
        try {
            IMqttToken unsubToken = client.unsubscribe(topic);
            unsubToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("unsubscribe", "unsubscribed topic: " + topic);
                    subscribedTopics.remove(topic);
                    // The subscription could successfully be removed from the client
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    Log.d("unsubscribe", "could not unsubscribe topic: " + topic);
                    // some error occurred, this is very unlikely as even if the client
                    // did not had a subscription to the topic the unsubscribe action
                    // will be successfully
                }
            });
        }
        catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void subscribe(final String topic) {
        int qos = QOS;
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The message was published
                    Log.d("subscribe", "subscribed topic: " + topic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards
                    Log.d("subscribe", "could not subscribe topic: " + topic);
                }
            });
        } catch (MqttException e) {
            System.out.println(e.toString());
        }
    }


    @Override
    public void publish(final String topic, String payload) {
        try {
            byte[] encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            IMqttToken pubToken = client.publish(topic, message);
            pubToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("publish", "published topic: " + topic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("publish", "could not publish topic: " + topic);
                }
            });

        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setCallbacs() {
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.d("connection", "CONNECTION WAS LOST!!!");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

                final String payload = message.toString();

                Log.d("messageArrived","You received a message with topic: "+topic);
                Log.d("messageArrived","payload message: " + message.toString());

                // Message by broker sending the names of all suppliers
                if (topic.equals(ALL_TOPICS_NAME)) {
                    addAllTopics(payload);
                    Log.d("allTopics", "Received all topics");
                }

                // Message from supplier offering products
                else if (subscribedTopics.contains(topic)){
                    parseProducts(topic, payload);
                }

                // Message from process node - response about buying a product
                else if (topic.equals(BUY_TOPIC_RESPONSE + userName)) {
                    parseBoughtProduct(payload);
                }

                // Message from process node - response about buying a multiple products
                else if (topic.equals(MULTIPLE_BUY_TOPIC_RESPONSE+userName)) {
                    parseMultipleBuy(payload);
                }

                // Message from bank - sending a payments history
                else if (topic.equals(HISTORY_TOPIC_RESPONSE + BIC + "/" + accountNumber)) {
                    if (payload.equals(lastHistoryPaymentsMessage)) {
                        Log.d("payment history", "History is same as was before.");
                    } else {
                        lastHistoryPaymentsMessage = payload;
                        parsePaymentsHistory(lastHistoryPaymentsMessage);
                    }
                }
                else if (topic.equals(BALANCE_TOPIC_RESPONSE + BIC + "/" + accountNumber)) {
                    if (payload.equals(lastBalanceAccountMessage)) {
                        Log.d("balance","Balance is same as was before");
                    } else {
                        lastBalanceAccountMessage = payload;
                        parseBalance(lastBalanceAccountMessage);
                    }
                }
                else if (topic.equals(PRODUCT_HISTORY_RESPONSE + userName)) {
                    boughtProducts.clear();
                    parseProductHistory(payload);
                }
                else if (topic.contains(LINK_BANK_ACCOUNT_RESPONSE + BIC)) {
                    JSONObject json = new JSONObject(payload);
                    int success = json.getInt("success");
                    if (success == 1)
                        addedBankSuccessful = true;
                    else addedBankSuccessful = false;
                }
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    @Override
    public void addAllTopics(String response) {
        try {

            JSONObject jsonObject = new JSONObject(response);
            JSONArray allTopics = jsonObject.getJSONArray("allTopics");

            for (int i = 0; i < allTopics.length(); i++) {
                JSONObject oneTopic = new JSONObject(allTopics.getString(i));
                String topicName = oneTopic.getString("topic");
                if (!availableTopics.contains(topicName) && !subscribedTopics.contains(topicName)) {
                    availableTopics.add(topicName);
                }
            }

        } catch (JSONException e) {
            Log.d("allTopics","Chyba pri parsovani JSON");
            e.printStackTrace();
        }
    }

    @Override
    public void subscribeAllTopics() {
        try {
            IMqttToken subToken = client.subscribe(ALL_TOPICS_NAME, QOS);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The message was published
                    Log.d("subscribeAllTopics", "subscribed topic: " + ALL_TOPICS_NAME);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    Log.d("subscribeAllTopics", "could not subscribe topic: " + ALL_TOPICS_NAME);
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void parseProductHistory(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray allProducts = jsonObject.getJSONArray("products");
            for (int i = 0; i < allProducts.length(); i++) {

                JSONObject oneProduct = new JSONObject(allProducts.getString(i));
                String name = oneProduct.getString("product");
                double price = oneProduct.getDouble("price");
                int amount = oneProduct.getInt("amount");
                String date = oneProduct.getString("date");
                String topicName = oneProduct.getString("merchant");
                BoughtProduct newProduct = new BoughtProduct(name, price, amount, date, topicName);
                if (!boughtProducts.contains(newProduct)) {
                    boughtProducts.add(newProduct);
                }
            }
        } catch (JSONException e) {
            Log.d("productHistory","Exception");
            e.printStackTrace();
        }
    }


    private void parseProducts(String topic, String response) {
        try {

            JSONObject jsonObject = new JSONObject(response);
            JSONArray allProducts = jsonObject.getJSONArray(topic);

            for (int i = 0; i < allProducts.length(); i++) {

                JSONObject oneProduct = new JSONObject(allProducts.getString(i));
                int id = oneProduct.getInt("id");
                String name = oneProduct.getString("name");
                double price = oneProduct.getDouble("price");
                int available = oneProduct.getInt("available");
                Product newProduct = new Product(id, name, price, available, topic);
                Product existingProduct = findProduct(id, name, price);
                if (existingProduct != null && available != existingProduct.getAvailable()) {
                    Log.d("parseProducts","Updating product: " + name );
                    availableProducts.set(availableProducts.indexOf(existingProduct),newProduct);
                }
                else if (existingProduct == null) {
                    Log.d("parseProducts","Adding product: " + name );
                    availableProducts.add(newProduct);
                }
            }

        } catch (JSONException e) {
            Log.d("parseProducts", "Chyba pri parsovani JSON");
            e.printStackTrace();
        }
    }

    private void parseBoughtProduct(String response) {
        int success;
        String message;
        int id;
        try {

            JSONObject oneProduct = new JSONObject(response);
            id = oneProduct.getInt("id");
            success = oneProduct.getInt("success");
            message = oneProduct.getString("message");

            if (success == 0) {
                Log.d("buy", "Success: 0. Could not buy product with id " + id +". Message : " + message);
                productActivity.createUnsuccessfullTransactionNotification(message);
            }
            else if (success == 1) {
                Log.d("buy", "Success: 1. Bought product with id " + id + ". Message: " + message);
                if (waitingForBuyEvaluation.containsKey(id)) {
                    productActivity.createBoughtProductNotification(waitingForBuyEvaluation.get(id));
                    removeFromWaitingList(id);
                    askForProductHistory(-1); //ask for all
                    askForBalance();
                    askForHistory();
                    Log.d("buy","Made operations with bought product");
                }
                else {
                    Log.d("buy","Bought product wasn't found. This should never happen!");
                }
            }
        }
        catch (JSONException e) {
            Log.d("parseBoughtProduct", "Chyba pri parsovani JSON");
            e.printStackTrace();
        }
    }

    private void parseMultipleBuy(String response) {
        parseBoughtProduct(response);
    }


    private void parseBalance(String response) {
        try {
            JSONObject balanceObject = new JSONObject(response);
            balance = balanceObject.getDouble("balance");
            Log.d("parseBalance", "Message from bank: balance is " + balance);

        } catch (JSONException e) {
            Log.d("parseBalance", "JSON exception");
            e.printStackTrace();
        }
    }
    private void parsePaymentsHistory(String response) {
        try {
            payments.clear();
            JSONObject responseJson = new JSONObject(response);
            Double bankId = responseJson.getDouble("bankId");
            JSONArray paymentsArray = responseJson.getJSONArray("paymentOrders");
            for (int i = 0; i < paymentsArray.length(); i++) {

                JSONObject payment = new JSONObject(paymentsArray.getString(i));
                String dateCreated = payment.getString("time_sent");
                double amount = payment.getDouble("amount");

                Payment newPayment = new Payment(bankId, amount, dateCreated);
                payments.add(newPayment);

            }
            Log.d("parsePaymentsHistory", "Received payments history");

        } catch (JSONException e) {
            Log.d("parsePaymentsHistory", "Exception");
            e.printStackTrace();
        }
    }

    private Product findProduct(int id, String name, Double price) {
        for (Product product: availableProducts) {
            if (product.getId() == id && product.getName().equals(name) && product.getPrice() == price) {
                return product;
            }
        }
        return null;
    }

    private Payment findPayment(Payment newPayment) {
        for (Payment payment: payments) {
            if (payment.toString().equals(newPayment.toString())) {
                return payment;
            }
        }
        return null;
    }

    private void askForProductHistory(int amount){
        JSONObject json = new JSONObject();
        if (amount == -1) {
            try {
                json.put("last", "all");
                clientManager.publish(PRODUCT_HISTORY_REQUEST+userName, json.toString() );
                clientManager.subscribe(PRODUCT_HISTORY_RESPONSE+userName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                json.put("last", amount);
                clientManager.publish(PRODUCT_HISTORY_REQUEST+userName, json.toString() );
                clientManager.subscribe(PRODUCT_HISTORY_RESPONSE+userName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void askForHistory(){
        clientManager.publish(HISTORY_TOPIC_REQUEST + BIC + "/" + accountNumber,  "{'accountNumber':" + accountNumber + "}" );
        clientManager.subscribe(HISTORY_TOPIC_RESPONSE + BIC + "/" + accountNumber);
    }

    private void askForBalance(){
        clientManager.publish(MQTTClientInterface.BALANCE_TOPIC_REQUEST + BIC + "/" + accountNumber,  "{'accountNumber':" + accountNumber + "}" );
        clientManager.subscribe(MQTTClientInterface.BALANCE_TOPIC_RESPONSE + BIC + "/" + accountNumber);
    }

    public void deleteProductsFromUnsubscribedTopic(String topic) {
        List<Product> toRemove = new ArrayList<>();
        for (int i = 0; i < availableProducts.size(); i++) {
            if (availableProducts.get(i).getTopicName().equals(topic)) {
                toRemove.add(availableProducts.get(i));
            }
        }
        if (availableProducts.removeAll(toRemove)) {
            Log.d("deletingProducts", "Successfully deleted products from unsubscribed topic");
        }
    }

    private void removeFromWaitingList(Integer id) {
        if (waitingForBuyEvaluation.containsKey(id)) {
            waitingForBuyEvaluation.remove(id);
            Log.d("removeFromWaitingList", "Id " + id + " successfully removed.");
        }
        else {
            Log.d("removeFromWaitingList", "Id wasn't found in waiting list. THIS SHOULD NEVER HAPPEN");
        }
    }
}
