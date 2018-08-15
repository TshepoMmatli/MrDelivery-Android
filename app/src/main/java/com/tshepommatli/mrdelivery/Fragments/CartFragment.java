package com.tshepommatli.mrdelivery.Fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amigold.fundapter.BindDictionary;
import com.amigold.fundapter.FunDapter;
import com.amigold.fundapter.extractors.StringExtractor;
import com.amigold.fundapter.interfaces.DynamicImageLoader;
import com.amigold.fundapter.interfaces.ItemClickListener;
import com.tshepommatli.mrdelivery.CardActivity;
import com.tshepommatli.mrdelivery.IP;
import com.tshepommatli.mrdelivery.Interfaces.Cart;
import com.tshepommatli.mrdelivery.Interfaces.UILConfig;
import com.tshepommatli.mrdelivery.R;
import com.kosalgeek.android.json.JsonConverter;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;

public class CartFragment extends Fragment implements AdapterView.OnItemClickListener, AsyncResponse{

    public static final String PREFS = "prefFile";

    final String LOG = "CartFragment";
    final static String url = IP.getIp() + "getCart.php";
    final static String deleteItem = IP.getIp() + "deleteItem.php";
    final static String increaseQty = IP.getIp() + "increaseQty.php";
    final static String decreaseQty = IP.getIp() + "decreaseQty.php";
    private static final String baseUrlForImage ="http://touristtravelguide.000webhostapp.com/mrdelivery/images/";

    View view;

    private ArrayList<Cart> itemList;
    private ListView lv;
    FunDapter<Cart> adapter;

    Button btnCheckout;

    public CartFragment() {
        // Required empty public constructor
        System.out.println(LOG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_cart, container, false);

        ImageLoader.getInstance().init(UILConfig.config(CartFragment.this.getActivity()));

        btnCheckout = (Button)view.findViewById(R.id.btnCheckout);

        SharedPreferences preferences = CartFragment.this.getActivity().getSharedPreferences(PREFS, 0);
        String customer = preferences.getString("username", null);

        HashMap postData = new HashMap();
        postData.put("txtUsername", customer);
        PostResponseAsyncTask taskRead = new PostResponseAsyncTask(CartFragment.this.getActivity(), postData, this);
        taskRead.execute(url);

        return view;
    }

    @Override
    public void processFinish(String s)
    {
        itemList = new JsonConverter<Cart>().toArrayList(s, Cart.class);

        BindDictionary dic = new BindDictionary();

        dic.addStringField(R.id.tvName, new StringExtractor<Cart>() {
            @Override
            public String getStringValue(Cart item, int position) {
                return item.orderName;
            }
        });

        dic.addStringField(R.id.tvDesc, new StringExtractor<Cart>() {
            @Override
            public String getStringValue(Cart item, int position) {
                return item.description;
            }
        }).visibilityIfNull(View.GONE);

        dic.addStringField(R.id.edQty, new StringExtractor<Cart>() {
            @Override
            public String getStringValue(Cart item, int position) {
                return ""+ item.quantity;
            }
        });

        dic.addStringField(R.id.tvPrice, new StringExtractor<Cart>() {
            @Override
            public String getStringValue(Cart item, int position) {
                double price = item.price;
                int qty = item.quantity;
                price = price * qty;
                String s = String.format("%.2f",price);
                return  s;
            }
        });


        dic.addDynamicImageField(R.id.ivImage, new StringExtractor<Cart>() {
            @Override
            public String getStringValue(Cart item, int position) {
                return item.img_url;
            }
        }, new DynamicImageLoader() {
            @Override
            public void loadImage(String url, ImageView img) {
                //Set image
                ImageLoader.getInstance().displayImage(baseUrlForImage  + url, img);
            }
        });

        //Remove Item from the basket
        dic.addBaseField(R.id.btnRemove).onClick(new ItemClickListener() {

            @Override
            public void onClick(Object item, int position, View view) {

                final Cart selectedItem = itemList.get(position);
                HashMap postData = new HashMap();
                postData.put("itemID", "" + selectedItem.id);
                PostResponseAsyncTask taskRemove = new PostResponseAsyncTask(CartFragment.this.getActivity(),
                        postData, new AsyncResponse() {
                    @Override
                    public void processFinish(String result) {
                        Log.d(LOG, result);
                        if(result.contains("Item successfully removed."))
                        {
                            itemList.remove(selectedItem);
                            lv.refreshDrawableState();

                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.detach(CartFragment.this).attach(CartFragment.this).commit();

                            adapter.notifyDataSetChanged();
                            Toast.makeText(CartFragment.this.getActivity(), "Item Removed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                taskRemove.execute(deleteItem);
            }
        });

        //increase the item quantity
        dic.addBaseField(R.id.qty_increase).onClick(new ItemClickListener() {
            @Override
            public void onClick(Object item, int position, final View view) {
                final Cart selectedItem = itemList.get(position);
                HashMap postData = new HashMap();
                postData.put("itemID", ""+ selectedItem.id);

                final PostResponseAsyncTask incTask = new PostResponseAsyncTask(CartFragment.this.getActivity(),
                        postData, new AsyncResponse() {
                    @Override
                    public void processFinish(String result) {

                        if(result.contains("Successful"))
                        {
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.detach(CartFragment.this).attach(CartFragment.this).commit();
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
                incTask.execute(increaseQty);
            }
        });

        //Decrease the item quantity
        dic.addBaseField(R.id.qty_decrease).onClick(new ItemClickListener() {
            @Override
            public void onClick(Object item, int position, final View view) {
                final Cart selectedItem = itemList.get(position);
                HashMap postData = new HashMap();
                int qty = selectedItem.quantity;

                postData.put("itemID", ""+ selectedItem.id);

                if(qty > 1)
                {
                    final PostResponseAsyncTask incTask = new PostResponseAsyncTask(CartFragment.this.getActivity(),
                            postData, new AsyncResponse() {
                        @Override
                        public void processFinish(String result) {

                            if(result.contains("Successful"))
                            {
                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ft.detach(CartFragment.this).attach(CartFragment.this).commit();

                                adapter.notifyDataSetChanged();
                            }
                        }

                    });
                    incTask.execute(decreaseQty);
                }
            }
        });

        adapter = new FunDapter<>(CartFragment.this.getActivity(), itemList, R.layout.cart_row, dic);
        lv = (ListView)view.findViewById(R.id.lvCart);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(this);

        if(lv.getCount() == 0)
        {
            TextView total = (TextView)view.findViewById(R.id.tvTotal);
            TextView empty = (TextView)view.findViewById(R.id.tvEmpty);

            btnCheckout.setVisibility(View.GONE);
            total.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        }

        double v = totalPrice(lv);

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartFragment.this.getActivity(), CardActivity.class);
                startActivity(intent);
            }
        });

    }
    private double totalPrice(ListView listView)
    {
        double sum = 0;
        TextView total = (TextView)view.findViewById(R.id.tvTotal);
        int count = listView.getCount();
        for(int i = 0; i < count; i++)
        {
            View v = listView.getAdapter().getView(i, null, null);
            TextView tv = (TextView) v.findViewById(R.id.tvPrice);
            sum = sum + Double.parseDouble(tv.getText().toString());
        }

        String s = "" + String.format("%.2f",sum);
        total.setText("Sub Total: " +s);
        return sum;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

    }
}
