package com.tshepommatli.mrdelivery.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.tshepommatli.mrdelivery.IP;
import com.tshepommatli.mrdelivery.Interfaces.Products;
import com.tshepommatli.mrdelivery.Interfaces.UILConfig;
import com.tshepommatli.mrdelivery.R;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.HashMap;


public class HomeDetailFragment extends Fragment {
    public static final String PREFS = "prefFile";
    final String LOG = "HomeDetailFragment";

    private static final String baseUrlForImage ="http://touristtravelguide.000webhostapp.com/mrdelivery/images/";
    private static final String saveCart = IP.getIp() + "saveCart.php";
    TextView tvName;
    TextView tvDesc;
    TextView tvPrice;
    String img_url;
    ImageView imageView;
    Button btnAddtocart;
    String userEmailAddress;

    public HomeDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home_detail, container, false);

        ImageLoader.getInstance().init(UILConfig.config(HomeDetailFragment.this.getActivity()));

        Products product = (Products) getArguments().getSerializable("productList");

        tvName = (TextView) v.findViewById(R.id.tvName);
        tvDesc = (TextView) v.findViewById(R.id.tvDesc);
        tvPrice = (TextView) v.findViewById(R.id.tvPrice);
        img_url = baseUrlForImage + product.img_url;
        imageView = (ImageView)v.findViewById(R.id.ivImage);
        btnAddtocart = (Button)v.findViewById(R.id.btnCheckout);

        SharedPreferences preferences = getActivity().getSharedPreferences(PREFS, 0);
        final String customer = preferences.getString("username", null);
        userEmailAddress = customer;

        final TextView name, desc, price;
        final String qty, imurl;

        //Load all the products
        if(product !=null)
        {
            tvName.setText(product.name);
            tvDesc.setText(product.description);

            tvPrice.setText("" + String.format("%.2f",product.price));
            ImageLoader.getInstance().displayImage(img_url, imageView);
        }


        name = tvName;
        desc = tvDesc;
        qty = ""+1;
        price = tvPrice;
        imurl = img_url;

        String editUrl = img_url;
        String[] output = editUrl.split("/");

        final String  imageName = output[5].toString();

        btnAddtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final HashMap postData = new HashMap();

                postData.put("itemName", name.getText().toString());
                postData.put("itemDesc", desc.getText().toString());
                postData.put("quantity", qty);
                postData.put("itemPrice",price.getText().toString());
                postData.put("total", price.getText().toString());
                postData.put("menuImage", imageName);
                postData.put("userEmail", userEmailAddress);

                PostResponseAsyncTask insertTask = new PostResponseAsyncTask(
                        HomeDetailFragment.this.getActivity(), postData, new AsyncResponse() {
                    @Override
                    public void processFinish(String result) {
                        Log.d(LOG, result);
                        String aName = name.toString();
                        String aQty = qty.toString();

                        if(result.contains("Item added to cart successfully."))
                        {
                            if(postData.equals(aName))
                            {
                                aQty += 1;
                            }
                            Toast.makeText(HomeDetailFragment.this.getActivity(), "Added to Cart", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                insertTask.execute(saveCart);
            }
        });

        return v;
    }
}