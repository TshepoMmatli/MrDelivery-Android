package com.tshepommatli.mrdelivery;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amigold.fundapter.BindDictionary;
import com.amigold.fundapter.FunDapter;
import com.amigold.fundapter.extractors.StringExtractor;
import com.amigold.fundapter.interfaces.DynamicImageLoader;
import com.kosalgeek.android.json.JsonConverter;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tshepommatli.mrdelivery.Fragments.CartFragment;
import com.tshepommatli.mrdelivery.Interfaces.Cart;

import java.util.ArrayList;
import java.util.HashMap;

public class CardActivity extends AppCompatActivity {
    public static final String PREFS = "prefFile";
    final static String url = IP.getIp() + "addPayment.php";
    final static String emptyCartURL = IP.getIp() + "emptyCart.php";
    final static String placeOrderURL = IP.getIp() + "placeOrder.php";
    Spinner cardSpinner, monthSpinner, yearSpinner;
    EditText Cardno, Cvc, cHolder;
    CheckBox saveCard;
    Context ctx;
    String customer;
    private ArrayList<Cart> itemList;
    private ListView lv;
    FunDapter<Cart> adapter;
    private static final String baseUrlForImage ="http://touristtravelguide.000webhostapp.com/mrdelivery/images/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        SharedPreferences preferences = CardActivity.this.getApplicationContext().getSharedPreferences(PREFS, 0);
        customer = preferences.getString("username", null);

        saveCard = (CheckBox)findViewById(R.id.Cardsave);

        Button payButton = (Button) findViewById(R.id.btnPay);

        Cardno = (EditText) findViewById(R.id.Cardno);
        Cvc = (EditText) findViewById(R.id.Cvc);
        cHolder = (EditText)findViewById(R.id.Cardholdername);

        ctx = getApplicationContext();

        cardSpinner = (Spinner) findViewById(R.id.spinnercardtype);

        ArrayAdapter<String> myAdaptor = new ArrayAdapter<String>(CardActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.names));

        myAdaptor.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        cardSpinner.setAdapter(myAdaptor);

        cardSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String Text = parent.getSelectedItem().toString();
                TextView Cvctxt = (TextView) findViewById(R.id.Cvctxt);
                if (Text.equals("Maestro")) {
                    Cvc.setVisibility(View.INVISIBLE);
                    Cvctxt.setVisibility(View.INVISIBLE);
                } else
                {
                    Cvc.setVisibility(View.VISIBLE);
                    Cvctxt.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        monthSpinner = (Spinner) findViewById(R.id.spinnerMonth);

        ArrayAdapter<String> myAdaptormonth = new ArrayAdapter<String>(CardActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.month));

        myAdaptor.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        monthSpinner.setAdapter(myAdaptormonth);

        yearSpinner = (Spinner) findViewById(R.id.spinnerYear);

        ArrayAdapter<String> myAdaptoryear = new ArrayAdapter<String>(CardActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.year));

        myAdaptor.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        yearSpinner.setAdapter(myAdaptoryear);

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Cardno.getText().toString().length() != 16 || Cvc.getText().toString().length() != 5 || cHolder.getText().toString().isEmpty()) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(CardActivity.this);
                    dialog.setMessage("Please check the details entered is correct")
                            .setCancelable(false)
                            .setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog box = dialog.create();
                    box.setTitle("ERROR MESSAGE");
                    box.show();
                }
                else
                {
                    ProcessPayment();
                    Intent intent = new Intent(CardActivity.this, ThanksActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    public void ProcessPayment(){
        final HashMap postData = new HashMap();
        postData.put("userId", customer);
        postData.put("cardType", cardSpinner.getSelectedItem().toString());
        postData.put("cardHolder", cHolder.getText().toString());
        postData.put("cardNumber", Cardno.getText().toString());
        postData.put("ExpDate", yearSpinner.getSelectedItem().toString() + "-" +
                monthSpinner.getSelectedItem().toString()+ "-" +
                "01");
        postData.put("cvcCode", Cvc.getText().toString());

        PostResponseAsyncTask cardTask = new PostResponseAsyncTask(CardActivity.this, postData, new AsyncResponse() {
            @Override
            public void processFinish(String result) {
                if(result.contains("Successful"))
                {
                    PlaceOrder();
                    Toast.makeText(CardActivity.this, "Payment Successful", Toast.LENGTH_SHORT).show();
                }
                else if (result.contains("Unsuccessful"))
                {
                    Toast.makeText(CardActivity.this, "Payment Unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cardTask.execute(url);
    }

    public void PlaceOrder(){
        final HashMap postData = new HashMap();
        postData.put("userID", customer);
        PostResponseAsyncTask orderTask = new PostResponseAsyncTask(CardActivity.this, postData, new AsyncResponse() {
            @Override
            public void processFinish(String result) {
                if(result.contains("Successful"))
                {
                    //Place order
                    HashMap postData = new HashMap();
                    postData.put("userID", customer);
                    postData.put("itemName", "s");
                    postData.put("itemDesc", "s");
                    postData.put("itemPrice", 12);
                    postData.put("quantity", 12);
                    postData.put("total", 12);
                    postData.put("status", "Order Placed");
                    PostResponseAsyncTask taskPlaceOrder = new PostResponseAsyncTask(CardActivity.this.getApplicationContext(), postData, this);
                    taskPlaceOrder.execute(placeOrderURL);

                    itemList = new JsonConverter<Cart>().toArrayList(result, Cart.class);
                    for(int i = 0; i < itemList.size(); i++){
                        System.out.println(itemList.get(i).customer);
                    }

                    //Delete the user's cart
                    postData = new HashMap();
                    postData.put("userID", customer);
                    PostResponseAsyncTask emptyCartTask = new PostResponseAsyncTask(CardActivity.this.getApplicationContext(), postData, this);
                    emptyCartTask.execute(emptyCartURL);
                }
                else if (result.contains("Unsuccessful"))
                {
                    Toast.makeText(CardActivity.this, "Payment Unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }
        });

        orderTask.execute(placeOrderURL);

    }
}


