package com.purificadora.activity;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.purificadora.R;
import com.purificadora.adepter.ReletedItemAdp;
import com.purificadora.database.DatabaseHelper;
import com.purificadora.database.MyCart;
import com.purificadora.fragment.ItemListFragment;
import com.purificadora.model.Price;
import com.purificadora.model.ProductItem;
import com.purificadora.model.ReletedProduct;
import com.purificadora.retrofit.APIClient;
import com.purificadora.retrofit.GetResult;
import com.purificadora.utils.CustPrograssbar;
import com.purificadora.utils.SessionManager;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class ItemDetailsActivity extends BaseActivity implements GetResult.MyListener, ReletedItemAdp.ItemClickListener {
    ProductItem productItem;
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.img_cart)
    ImageView imgCart;
    @BindView(R.id.txt_tcount)
    TextView txtTcount;
    @BindView(R.id.lvl_cart)
    RelativeLayout lvlCart;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.txt_desc)
    TextView txtDesc;
    @BindView(R.id.lvl_pricelist)
    LinearLayout lvlPricelist;

    ArrayList<Price> priceslist;
    DatabaseHelper databaseHelper;
    SessionManager sessionManager;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.txt_price)
    TextView txtPrice;
    @BindView(R.id.txt_item_offer)
    TextView txtItemOffer;
    @BindView(R.id.txt_seler)
    TextView txtSeler;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.tabview)
    TabLayout tabview;
    @BindView(R.id.recycler_releted)
    RecyclerView recyclerReleted;
    CustPrograssbar custPrograssbar;
    public static ItemDetailsActivity itemDetailsActivity = null;

    public static ItemDetailsActivity getInstance() {
        return itemDetailsActivity;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        itemDetailsActivity = null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        ButterKnife.bind(this);
        itemDetailsActivity = this;
        LinearLayoutManager mLayoutManager1 = new LinearLayoutManager(ItemDetailsActivity.this);
        mLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerReleted.setLayoutManager(mLayoutManager1);
        custPrograssbar = new CustPrograssbar();
        sessionManager = new SessionManager(this);
        databaseHelper = new DatabaseHelper(ItemDetailsActivity.this);
        productItem = (ProductItem) getIntent().getParcelableExtra("MyClass");
        priceslist = getIntent().getParcelableArrayListExtra("MyList");
        recyclerReleted.setItemAnimator(new DefaultItemAnimator());







        // sequence example
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, "SHOWCASE_ID");

        sequence.setConfig(config);

        sequence.addSequenceItem(txtTitle,
                "Bienvenido, este es el nombre del producto a elegir.", "TOCA AQUI PARA CONTINUAR");
        sequence.addSequenceItem(lvlPricelist,
                "Desde este boton puede agregar que tantas unidades quiere ordenar.", "TOCA AQUI PARA CONTINUAR");

        sequence.addSequenceItem(lvlCart,
                "Ya que eligio cuantas unidades, la orden se agrega al carrito para continuar con su compra correctamente.", "TOCA AQUI PARA FINALIZAR");



        sequence.start();


        onSetdata();
    }


    private void onSetdata() {

        if (productItem != null) {
            txtTitle.setText("" + productItem.getProductName());
            txtDesc.setText("" + productItem.getShortDesc());
            txtSeler.setText("" + productItem.getSellerName());
            List<String> arrayList = new ArrayList<>();
            for (int i = 0; i < priceslist.size(); i++) {
                arrayList.add(priceslist.get(i).getProductType());
            }
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, arrayList);
            spinner.setAdapter(dataAdapter);
            updateItem();
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (productItem.getmDiscount() > 0) {
                    double res = (Double.parseDouble(priceslist.get(position).getProductPrice()) / 100.0f) * productItem.getmDiscount();
                    res = Double.parseDouble(priceslist.get(position).getProductPrice()) - res;
                    txtItemOffer.setText(sessionManager.getStringData(SessionManager.currncy) + priceslist.get(position).getProductPrice());
                    txtItemOffer.setPaintFlags(txtItemOffer.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    txtPrice.setText(sessionManager.getStringData(SessionManager.currncy) + res);
                    txtItemOffer.setText(sessionManager.getStringData(SessionManager.currncy) + priceslist.get(position).getProductPrice());
                } else {
                    txtItemOffer.setVisibility(View.GONE);
                    txtPrice.setText(sessionManager.getStringData(SessionManager.currncy) + priceslist.get(position).getProductPrice());
                }
                setJoinPlayrList(lvlPricelist, productItem, priceslist.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        List<String> myList = new ArrayList<>();
        myList.add(productItem.getProductImage());
        if (productItem.getProductRelatedImage() != null && productItem.getProductRelatedImage().length() != 0) {
            myList.addAll(Arrays.asList(productItem.getProductRelatedImage().split(",")));
            tabview.setupWithViewPager(viewPager, true);
        }
        MyCustomPagerAdapter myCustomPagerAdapter = new MyCustomPagerAdapter(this, myList);
        viewPager.setAdapter(myCustomPagerAdapter);
        reletedProduct();
    }

    public void updateItem() {
        Cursor res = databaseHelper.getAllData();
        if (res.getCount() == 0) {
            txtTcount.setText("0");
        } else {
            txtTcount.setText("" + res.getCount());
        }
    }

    @OnClick({R.id.img_back, R.id.lvl_cart })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.lvl_cart:
                fragment();
                break;

            default:
                break;
        }
    }

    private void setJoinPlayrList(LinearLayout lnrView, ProductItem datum, Price price) {

        lnrView.removeAllViews();
        final int[] count = {0};
        DatabaseHelper helper = new DatabaseHelper(lnrView.getContext());
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.custome_additem, null);
        TextView txtcount = view.findViewById(R.id.txtcount);
        LinearLayout lvlAddremove = view.findViewById(R.id.lvl_addremove);
        LinearLayout lvlAddcart = view.findViewById(R.id.lvl_addcart);
        LinearLayout imgMins = view.findViewById(R.id.img_mins);
        LinearLayout imgPlus = view.findViewById(R.id.img_plus);
        MyCart myCart = new MyCart();
        myCart.setPid(datum.getId());
        myCart.setImage(datum.getProductImage());
        myCart.setTitle(datum.getProductName());
        myCart.setWeight(price.getProductType());
        myCart.setCost(price.getProductPrice());
        myCart.setDiscount(datum.getmDiscount());
        myCart.setMqty(datum.getMqty());
        int qrt = helper.getCard(myCart.getPid(), myCart.getCost());
        if (qrt != -1) {
            count[0] = qrt;
            txtcount.setText("" + count[0]);
            lvlAddremove.setVisibility(View.VISIBLE);
            lvlAddcart.setVisibility(View.GONE);
        } else {
            lvlAddremove.setVisibility(View.GONE);
            lvlAddcart.setVisibility(View.VISIBLE);

        }
        imgMins.setOnClickListener(v -> {

            count[0] = Integer.parseInt(txtcount.getText().toString());

            count[0] = count[0] - 1;
            if (count[0] <= 0) {
                lvlAddremove.setVisibility(View.GONE);
                lvlAddcart.setVisibility(View.VISIBLE);
                txtcount.setText("0");
                helper.deleteRData(myCart.getPid(), myCart.getCost());
            } else {
                txtcount.setVisibility(View.VISIBLE);
                txtcount.setText("" + count[0]);
                myCart.setQty(String.valueOf(count[0]));
                helper.insertData(myCart);
            }
            updateItem();
            if (ItemListFragment.itemListFragment != null)
                ItemListFragment.itemListFragment.updateItem();
        });

        imgPlus.setOnClickListener(v -> {
            if (Integer.parseInt(txtcount.getText().toString()) < datum.getMqty()) {

                count[0] = Integer.parseInt(txtcount.getText().toString());

                count[0] = count[0] + 1;
                txtcount.setText("" + count[0]);
                myCart.setQty(String.valueOf(count[0]));
                Log.e("INsert", "--> " + helper.insertData(myCart));
                updateItem();
                if (ItemListFragment.itemListFragment != null)
                    ItemListFragment.itemListFragment.updateItem();
            } else {
                Toast.makeText(this, getString(R.string.excelimit), Toast.LENGTH_SHORT).show();

            }
        });
        lvlAddcart.setOnClickListener(v -> {
            lvlAddcart.setVisibility(View.GONE);
            lvlAddremove.setVisibility(View.VISIBLE);
            count[0] = Integer.parseInt(txtcount.getText().toString());

            count[0] = count[0] + 1;
            txtcount.setText("" + count[0]);
            myCart.setQty(String.valueOf(count[0]));
            Log.e("INsert", "--> " + helper.insertData(myCart));
            updateItem();
            if (ItemListFragment.itemListFragment != null)
                ItemListFragment.itemListFragment.updateItem();
        });
        lnrView.addView(view);

    }

    public void fragment() {
        SessionManager.iscart = true;
        finish();

    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            custPrograssbar.closePrograssBar();
            if (callNo.equalsIgnoreCase("1")) {
                Gson gson = new Gson();
                ReletedProduct reletedProduct = gson.fromJson(result.toString(), ReletedProduct.class);
                if (reletedProduct.getResult().equalsIgnoreCase("true")) {
                    ReletedItemAdp reletedItemAdp = new ReletedItemAdp(ItemDetailsActivity.this, reletedProduct.getData(), this);
                    recyclerReleted.setAdapter(reletedItemAdp);
                }
            }
        } catch (Exception e) {
            e.toString();
        }
    }

    @Override
    public void onItemClick(ProductItem productItem, int position) {
        this.productItem = productItem;
        priceslist = productItem.getPrice();
        onSetdata();
    }

    @Override
    public void onItemClick() {
        updateItem();

    }

    public class MyCustomPagerAdapter extends PagerAdapter {
        Context context;
        List<String> imageList;
        LayoutInflater layoutInflater;

        public MyCustomPagerAdapter(Context context, List<String> bannerDatumList) {
            this.context = context;
            this.imageList = bannerDatumList;
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return imageList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = layoutInflater.inflate(R.layout.item_image, container, false);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
            Glide.with(ItemDetailsActivity.this).load(APIClient.baseUrl + "/" + imageList.get(position)).placeholder(R.drawable.empty).into(imageView);
            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }

    private void reletedProduct() {

        custPrograssbar.prograssCreate(ItemDetailsActivity.this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cid", productItem.getCatId());
            jsonObject.put("sid", productItem.getSubcatId());
            jsonObject.put("pid", productItem.getId());
            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().related((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
