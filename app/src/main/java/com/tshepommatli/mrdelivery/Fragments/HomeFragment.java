package com.tshepommatli.mrdelivery.Fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import com.amigold.fundapter.BindDictionary;
import com.amigold.fundapter.FunDapter;
import com.amigold.fundapter.extractors.StringExtractor;
import com.amigold.fundapter.interfaces.DynamicImageLoader;
import com.tshepommatli.mrdelivery.IP;
import com.tshepommatli.mrdelivery.Interfaces.Products;
import com.tshepommatli.mrdelivery.Interfaces.UILConfig;
import com.tshepommatli.mrdelivery.R;
import com.kosalgeek.android.json.JsonConverter;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.ArrayList;
import java.util.HashMap;

public class HomeFragment extends Fragment implements AsyncResponse, AdapterView.OnItemClickListener {

    final static String url = IP.getIp() + "getMenu.php";
    private static final String baseUrlForImage ="http://touristtravelguide.000webhostapp.com/mrdelivery/images/";
    private ArrayList<Products> productList;
    private ListView lv;
    FunDapter<Products> adapter;
    View view;

    public static final String PREFS = "prefFile";

    public HomeFragment() {
        //Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        ImageLoader.getInstance().init(UILConfig.config(HomeFragment.this.getActivity()));


        SharedPreferences preferences = getActivity().getSharedPreferences(PREFS, 0);
        String restaurantName = preferences.getString("selectedRestaurant", null);
        //String restaurantName = "Burger King";

        HashMap postData = new HashMap();
        postData.put("selectedRestaurant", restaurantName);


        PostResponseAsyncTask taskRead = new PostResponseAsyncTask(HomeFragment.this.getActivity(), postData, this);
        taskRead.execute(url);

        return view;
    }


    @Override
    public void processFinish(String s) {

        productList = new JsonConverter<Products>().toArrayList(s, Products.class);

        BindDictionary dic = new BindDictionary();

        dic.addStringField(R.id.tvName, new StringExtractor<Products>() {
            @Override
            public String getStringValue(Products item, int position) {
                return item.name;
            }
        });

        dic.addStringField(R.id.tvDesc, new StringExtractor<Products>() {
            @Override
            public String getStringValue(Products item, int position) {
                return item.description;
            }
        }).visibilityIfNull(View.GONE);

        dic.addDynamicImageField(R.id.ivImage, new StringExtractor<Products>() {
            @Override
            public String getStringValue(Products item, int position) {
                return item.img_url;
            }
        }, new DynamicImageLoader() {
            @Override
            public void loadImage(String menuImageName, ImageView img) {
                //Set image
                ImageLoader.getInstance().displayImage(baseUrlForImage + menuImageName, img);
            }
        });

        adapter = new FunDapter<>(HomeFragment.this.getActivity(), productList, R.layout.fragment_home_row, dic);
        lv = (ListView)view.findViewById(R.id.lvProduct);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Products selectedProduct = productList.get(position);
        Fragment detailFragment = new HomeDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("productList", selectedProduct);
        detailFragment.setArguments(bundle);
        FragmentManager manager = getFragmentManager();
        manager.beginTransaction().replace(R.id.content_main, detailFragment)
                .addToBackStack("HomeDetail").commit();
    }
}
