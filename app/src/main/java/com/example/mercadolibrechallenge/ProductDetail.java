package com.example.mercadolibrechallenge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.mercadolibrechallenge.component.DownloadImageTask;
import com.example.mercadolibrechallenge.helpers.NavigationHelper;
import com.example.mercadolibrechallenge.model.ProductDetails;
import com.example.mercadolibrechallenge.model.ProductDetailsAttributes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class ProductDetail extends AppCompatActivity {

    //Define colombian spanish as main language
    Locale col = new Locale("es", "CO");

    RequestQueue requestQueue;

    private final static String BASE_URL = "https://api.mercadolibre.com/items?ids=";

    private String urlSearch;
    private String results;

    private int offset;
    private int limit;
    private int currentPage;
    private String stringSearch;
    private String productId;

    private ImageView productDetailImage;
    private Button btnPrevImage;
    private Button btnNextImage;
    private TextView txtProductDetailTitle;
    private TextView txtProductDetailRegularAmount;
    private TextView txtProductDetailAmount;
    private TextView txtProductDetailDiscount;
    private TextView txtProductDetailFreeShipping;
    private TextView txtProductDetailAvailableStock;
    private LinearLayout productDetailDescriptionContent;

    private int currentPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        requestQueue = Volley.newRequestQueue(this);
        getBundles();
        setComponents();
    }

    private void getBundles() {
        Bundle dataReceived = getIntent().getExtras();
        stringSearch = dataReceived.getString("stringSearch");
        offset = dataReceived.getInt("offset");
        limit = dataReceived.getInt("limit");
        currentPage = dataReceived.getInt("page");
        productId = dataReceived.getString("productId");
        urlSearch = BASE_URL + productId;
    }

    private void setComponents() {
        productDetailImage = findViewById(R.id.productDetailImage);
        btnPrevImage = findViewById(R.id.btnPrevImage);
        btnNextImage = findViewById(R.id.btnNextImage);
        txtProductDetailTitle = findViewById(R.id.txtProductDetailTitle);
        txtProductDetailRegularAmount = findViewById(R.id.txtProductDetailRegularAmount);
        txtProductDetailAmount = findViewById(R.id.txtProductDetailAmount);
        txtProductDetailDiscount = findViewById(R.id.txtProductDetailDiscount);
        txtProductDetailFreeShipping = findViewById(R.id.txtProductDetailFreeShipping);
        txtProductDetailAvailableStock = findViewById(R.id.txtProductDetailAvailableStock);
        productDetailDescriptionContent = findViewById(R.id.productDetailDescriptionContent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.return_home:
                goSearch();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void goSearch() {
        NavigationHelper.navigateTo(this, SearchList.class, "", stringSearch, limit, offset, currentPage);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getProduct(urlSearch);
    }

    /**
     * This method get the product detail from url
     * @param urlSearch Url made with Product Id
     */
    private void getProduct(String urlSearch) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, urlSearch, null, response -> {
                    try {
                        results = response.get(0).toString();
                        JSONObject resultsObj = new JSONObject(results);
                        JSONObject data = resultsObj.getJSONObject("body");

                        String title = data.getString("title");
                        int price = data.getInt("price");
                        int base_price = 0;
                        if (!data.isNull("base_price")) {
                            base_price = data.getInt("base_price");
                        }
                        int original_price = 0;
                        if (!data.isNull("original_price")) {
                            original_price = data.getInt("original_price");
                        }
                        int available_quantity = 0;
                        if (!data.isNull("available_quantity")) {
                            available_quantity = data.getInt("available_quantity");
                        }

                        List<String> pictures  = new ArrayList();

                        JSONArray picturesArray = new JSONArray(data.getString("pictures"));

                        for (int i = 0; i < picturesArray.length(); i++) {
                            JSONObject pictureDB = picturesArray.getJSONObject(i);
                            String secure_url = pictureDB.getString("secure_url");
                            pictures.add(secure_url);
                        }

                        JSONObject shipping = data.getJSONObject("shipping");
                        Boolean free_shipping = shipping.getBoolean("free_shipping");

                        List<ProductDetailsAttributes> productAttributes  = new ArrayList();

                        JSONArray attributesArray = new JSONArray(data.getString("attributes"));

                        for (int i = 0; i < attributesArray.length(); i++) {
                            JSONObject attributeDB = attributesArray.getJSONObject(i);
                            String name = attributeDB.getString("name");
                            String value_name = attributeDB.getString("value_name");

                            productAttributes.add(new ProductDetailsAttributes(name, value_name));
                        }

                        Boolean accepts_mercadopago = data.getBoolean("accepts_mercadopago");

                        ProductDetails product = new ProductDetails(title, price, base_price, original_price, available_quantity, pictures, accepts_mercadopago, free_shipping, productAttributes);

                        renderProduct(product);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    System.out.println("error: " + error);
                });

        requestQueue.add(jsonArrayRequest);
    }

    /**
     * This method renders Product Card. Is called after product data has been downloaded
     * @param product is an object created from data that comes from API
     */
    private void renderProduct(ProductDetails product) {
        currentPicture = 0;
        new DownloadImageTask(productDetailImage)
                .execute(product.getPictures().get(0));

        txtProductDetailTitle.setText(product.getTitle());
        Currency pesos = Currency.getInstance(col);
        NumberFormat colPesoFormat = NumberFormat.getCurrencyInstance(col);
        colPesoFormat.setMaximumFractionDigits(0);
        int price;
        int regular_amount = product.getOriginal_price();
        double discountPorcDb;
        int discountPorc = 0;
        if (regular_amount > 0) {
            price = product.getBase_price();
            discountPorcDb = ((regular_amount - price)*100) / regular_amount;
            discountPorc = (int)(Math.ceil(discountPorcDb));
            txtProductDetailRegularAmount.setText(colPesoFormat.format(regular_amount));
            txtProductDetailRegularAmount.setPaintFlags(txtProductDetailRegularAmount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            txtProductDetailDiscount.setText(discountPorc+"% OFF");
        } else {
            price = product.getPrice();
            txtProductDetailRegularAmount.setText("");
            txtProductDetailDiscount.setText("");
        }
        txtProductDetailAmount.setText(colPesoFormat.format(price));

        String shipping = "";
        if (product.getFree_shipping()) {
            shipping = "Env??o gratis";
        }
        txtProductDetailFreeShipping.setText(shipping);

        String stockAvailable = "";
        if (product.getAvailable_quantity() > 0) {
            stockAvailable = "Stock disponible";
        }
        txtProductDetailAvailableStock.setText(stockAvailable);

        btnNextImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currentPicture++;
                if (currentPicture == product.getPictures().size()) {
                    currentPicture = 0;
                    new DownloadImageTask(productDetailImage)
                            .execute(product.getPictures().get(currentPicture));
                }
                new DownloadImageTask(productDetailImage)
                        .execute(product.getPictures().get(currentPicture));
            }
        });

        btnPrevImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currentPicture--;
                if (currentPicture < 0) {
                    currentPicture = product.getPictures().size()-1;
                    new DownloadImageTask(productDetailImage)
                            .execute(product.getPictures().get(currentPicture));
                }
                new DownloadImageTask(productDetailImage)
                        .execute(product.getPictures().get(currentPicture));
            }
        });

        for (ProductDetailsAttributes productAttribute : product.getProductDetailsAttributes()) {
            renderProductAttributes(productAttribute);
        }
    }

    /**
     * This method add TextViews to Relative layout with Id productDetailDescriptionContent to show every title and value of each attribute
     * @param productAttribute every attribute of product to render
     */
    public void renderProductAttributes(ProductDetailsAttributes productAttribute) {
        TextView textViewName = new TextView(this);
        TextView textViewValueName = new TextView(this);
        TextView myText = new TextView(this);
        String name = productAttribute.getName();
        String value_name = productAttribute.getValue_name();
        textViewName.setText(name);
        textViewName.setBackgroundColor(Color.parseColor("#D3E3F3"));
        textViewName.setTypeface(null, Typeface.BOLD);
        textViewValueName.setText(value_name);
        textViewValueName.setPadding(0,0,0, 10);
        productDetailDescriptionContent.addView(textViewName);
        productDetailDescriptionContent.addView(textViewValueName);
    }
}