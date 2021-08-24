package com.example.mercadolibrechallenge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class ProductDetail extends AppCompatActivity {

    //Define Colombia as main languaje
    Locale col = new Locale("es", "CO");

    RequestQueue requestQueue;

    private String urlSearch;
    private String results;

    private int offset;
    private int limit;
    private int actualPage;
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

    //Auxiliar variables
    private int currentPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        Bundle dataReceived = getIntent().getExtras();
        stringSearch = dataReceived.getString("stringSearch");
        offset = dataReceived.getInt("offset");
        limit = dataReceived.getInt("limit");
        actualPage = dataReceived.getInt("page");
        productId = dataReceived.getString("productId");

        productDetailImage = findViewById(R.id.productDetailImage);
        btnPrevImage = findViewById(R.id.btnPrevImage);
        btnNextImage = findViewById(R.id.btnNextImage);
        txtProductDetailTitle = findViewById(R.id.txtProductDetailTitle);
        txtProductDetailRegularAmount = findViewById(R.id.txtProductDetailRegularAmount);
        txtProductDetailAmount = findViewById(R.id.txtProductDetailAmount);
        txtProductDetailDiscount = findViewById(R.id.txtProductDetailDiscount);
        txtProductDetailFreeShipping = findViewById(R.id.txtProductDetailFreeShipping
        );
        txtProductDetailAvailableStock = findViewById(R.id.txtProductDetailAvailableStock);

        productDetailDescriptionContent = findViewById(R.id.productDetailDescriptionContent);

        urlSearch = "https://api.mercadolibre.com/items?ids=" + productId;

        requestQueue = Volley.newRequestQueue(this);
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
        Intent goSearch = new Intent(this, SearchList.class);
        Bundle dataBundle = new Bundle();
        dataBundle.putString("stringSearch", stringSearch);
        dataBundle.putInt("limit", limit);
        dataBundle.putInt("offset", offset);
        dataBundle.putInt("page", actualPage);
        goSearch.putExtras(dataBundle);
        goSearch.addFlags(goSearch.FLAG_ACTIVITY_CLEAR_TOP | goSearch.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(goSearch);
    }

    private class ProductAttributes {
        String name;
        String value_name;

        public ProductAttributes(String name, String value_name) {
            this.name = name;
            this.value_name = value_name;
        }

        public String getName() {
            return name;
        }

        public String getValue_name() {
            return value_name;
        }
    }

    private class Product {
        String title;
        int price;
        int base_price;
        int original_price;
        int available_quantity;
        List<String> pictures;
        Boolean accepts_mercadopago;
        Boolean free_shipping;
        List<ProductAttributes> productAttributes;

        public Product(String title, int price, int base_price, int original_price, int available_quantity, List<String> pictures, Boolean accepts_mercadopago, Boolean free_shipping, List<ProductAttributes> productAttributes) {
            this.title = title;
            this.price = price;
            this.base_price = base_price;
            this.original_price = original_price;
            this.available_quantity = available_quantity;
            this.pictures = pictures;
            this.accepts_mercadopago = accepts_mercadopago;
            this.free_shipping = free_shipping;
            this.productAttributes = productAttributes;
        }

        public String getTitle() {
            return title;
        }

        public int getPrice() {
            return price;
        }

        public int getBase_price() {
            return base_price;
        }
        public int getOriginal_price() {
            return original_price;
        }

        public int getAvailable_quantity() {
            return available_quantity;
        }

        public List<String> getPictures() {
            return pictures;
        }

        public Boolean getAccepts_mercadopago() {
            return accepts_mercadopago;
        }

        public Boolean getFree_shipping() {
            return free_shipping;
        }

        public List<ProductAttributes> getProductAttributes() {
            return productAttributes;
        }
    }
    private void renderProduct(Product product) {
        currentPicture = 0;
        new DownloadImageTask(productDetailImage)
                .execute(product.getPictures().get(0));

        txtProductDetailTitle.setText(product.getTitle());
//        txtProductDetailRegularAmount.setText(product.getOriginal_price());
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
//        txtProductDetailRegularAmount.setText("");
//        txtProductDetailAmount.setText(product.getPrice());
        txtProductDetailAmount.setText(colPesoFormat.format(price));
//        txtProductDetailDiscount.setText("");

        String shipping = "";
        if (product.getFree_shipping()) {
            shipping = "Envío gratis";
        }
        txtProductDetailFreeShipping.setText(shipping);

        String stockAvailable = "";
        if (product.getAvailable_quantity() > 0) {
            stockAvailable = "Stock disponible";
        }
        txtProductDetailAvailableStock.setText(stockAvailable);

        btnNextImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println(product.getPictures().size());
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

        for (int i = 0; i < product.getProductAttributes().size() ; i++) {
            ProductAttributes productAttribute = product.getProductAttributes().get(i);
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

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
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

                        List<ProductAttributes> productAttributes  = new ArrayList();

                        JSONArray attributesArray = new JSONArray(data.getString("attributes"));

                        for (int i = 0; i < attributesArray.length(); i++) {
                            JSONObject attributeDB = attributesArray.getJSONObject(i);
                            String name = attributeDB.getString("name");
                            String value_name = attributeDB.getString("value_name");

                            productAttributes.add(new ProductAttributes(name, value_name));
                        }

                        Boolean accepts_mercadopago = data.getBoolean("accepts_mercadopago");

                        Product product = new Product(title, price, base_price, original_price, available_quantity, pictures, accepts_mercadopago, free_shipping, productAttributes);

                        renderProduct(product);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    System.out.println("error: " + error);
                });

        requestQueue.add(jsonArrayRequest);
    }

}