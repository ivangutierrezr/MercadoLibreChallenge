package com.example.mercadolibrechallenge;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
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

public class SearchList extends AppCompatActivity {

    //Define Colombia as main languaje
    Locale col = new Locale("es", "CO");

    RequestQueue requestQueue;
    private String results;
    private String pagingInfo;
    private String stringSearch;
    private String urlSearch;
    private int actualPage;
    private int offset;
    private int limit;

    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;

    private ProgressBar loadingProducts;
    private TextView txtLoading;
    private Button btnBackToSearch;
    private TextView txtNoResults;

    private TextView textActualPage;
    private Button btnPrev;
    private Button btnNext;
    private List<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_list);

        loadingProducts = findViewById(R.id.loadingProducts);
        txtLoading = findViewById(R.id.txtLoading);
        btnBackToSearch = findViewById(R.id.btnBackToSearch);
        txtNoResults = findViewById(R.id.txtNoResults);

        loadingProducts.setVisibility(View.VISIBLE);
        txtLoading.setVisibility(View.VISIBLE);
        btnBackToSearch.setVisibility(View.INVISIBLE);
        txtNoResults.setVisibility(View.INVISIBLE);

        textActualPage = findViewById(R.id.textActualPage);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);

        requestQueue = Volley.newRequestQueue(this);

        Bundle dataReceived = getIntent().getExtras();
        stringSearch = dataReceived.getString("stringSearch");
        offset = dataReceived.getInt("offset");
        limit = dataReceived.getInt("limit");
        actualPage = dataReceived.getInt("page");
        stringSearch = stringSearch.replaceAll("\\s+","%20");
        urlSearch = "https://api.mercadolibre.com/sites/MCO/search?q=" + stringSearch + "&offset=" + offset + "&limit=" + limit;

        btnBackToSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goHome();
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                offset = offset - limit;
                actualPage = actualPage - 1;
                urlSearch = "https://api.mercadolibre.com/sites/MCO/search?q=" + stringSearch + "&offset=" + offset + "&limit=" + limit;
                getProducts();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                offset = offset + limit;
                actualPage = actualPage + 1;
                urlSearch = "https://api.mercadolibre.com/sites/MCO/search?q=" + stringSearch + "&offset=" + offset + "&limit=" + limit;
                getProducts();
            }
        });
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
                goHome();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void goHome() {
        Intent goHome = new Intent(this, Home.class);
        goHome.addFlags(goHome.FLAG_ACTIVITY_CLEAR_TOP | goHome.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(goHome);
    }

    private class Installments {
        int quantity;
        int amount;
        int rate;
        String currency_id;

        public Installments(int quantity, int amount, int rate, String currency_id) {
            this.quantity = quantity;
            this.amount = amount;
            this.rate = rate;
            this.currency_id = currency_id;
        }

        public int getQuantity() {
            return quantity;
        }

        public int getAmount() {
            return amount;
        }

        public int getRate() {
            return rate;
        }

        public String getCurrency_id() {
            return currency_id;
        }
    }

    private class Address {
        String state_name;
        String city_name;

        public Address(String state_name, String city_name) {
            this.state_name = state_name;
            this.city_name = city_name;
        }

        public String getState_name() {
            return state_name;
        }

        public String getCity_name() {
            return city_name;
        }
    }

    private class Attribute {
        String value_name;
        String name;

        public Attribute(String value_name, String name) {
            this.value_name = value_name;
            this.name = name;
        }

        public String getValue_name() {
            return value_name;
        }

        public String getName() {
            return name;
        }
    }

    private class Prices {
        int amount;
        int regular_amount;

        public Prices(int amount, int regular_amount) {
            this.amount = amount;
            this.regular_amount = regular_amount;
        }

        public int getAmount() {
            return amount;
        }

        public int getRegular_amount() {
            return regular_amount;
        }
    }

    private class Product {
        String id;
        String title;
        int price;
        int available_quantity;
        String condition;
        String permalink;
        String thumbnail;
        Boolean accepts_mercadopago;
        Installments installments;
        Address address;
        List<Attribute> attributes;
        Prices prices;
        Boolean free_shipping;

        public Product(String id, String title, int price, int available_quantity, String condition, String permalink, String thumbnail, Boolean accepts_mercadopago, Installments installments, Address address, List<Attribute> attributes, Prices prices, Boolean free_shipping) {
            this.id = id;
            this.title = title;
            this.price = price;
            this.available_quantity = available_quantity;
            this.condition = condition;
            this.permalink = permalink;
            this.thumbnail = thumbnail;
            this.accepts_mercadopago = accepts_mercadopago;
            this.installments = installments;
            this.address = address;
            this.attributes = attributes;
            this.prices = prices;
            this.free_shipping = free_shipping;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public int getPrice() {
            return price;
        }

        public int getAvailable_quantity() {
            return available_quantity;
        }

        public String getCondition() {
            return condition;
        }

        public String getPermalink() {
            return permalink;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public Boolean getAccepts_mercadopago() {
            return accepts_mercadopago;
        }

        public Installments getInstallments() {
            return installments;
        }

        public Address getAddress() {
            return address;
        }

        public List<Attribute> getAttributes() {
            return attributes;
        }

        public Prices getPrices() {
            return prices;
        }

        public Boolean getFree_shipping() {
            return free_shipping;
        }
    }

    public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductViewHolder> {
        private List<Product> products;

        public class ProductViewHolder extends RecyclerView.ViewHolder {
            public ImageView product_image;
            public TextView product_title;
            public TextView product_regular_amount;
            public TextView product_amount;
            public TextView product_discount;
            public TextView product_installments;
            public TextView product_free_shipping;
            public RelativeLayout product_list_content;

            public ProductViewHolder(View v) {
                super(v);
                product_image = (ImageView)v.findViewById(R.id.productDetailImage);
                product_title = (TextView)v.findViewById(R.id.product_title);
                product_regular_amount = (TextView)v.findViewById(R.id.product_regular_amount);
                product_amount = (TextView)v.findViewById(R.id.product_amount);
                product_discount = (TextView)v.findViewById(R.id.product_discount);
                product_installments = (TextView)v.findViewById(R.id.product_installments);
                product_free_shipping = (TextView)v.findViewById(R.id.product_free_shipping);
                product_list_content = (RelativeLayout)v.findViewById(R.id.product_list_content);
            }
        }

        public ProductsAdapter(List<Product> products) {
            this.products = products;
        }

        @Override
        public int getItemCount() {
            return products.size();
        }

        @Override
        public ProductViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.product_card_list, viewGroup, false);
            return new ProductViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ProductViewHolder viewHolder, int i) {
            viewHolder.product_title.setText(products.get(i).getTitle());
            Currency pesos = Currency.getInstance(col);
            NumberFormat colPesoFormat = NumberFormat.getCurrencyInstance(col);
            colPesoFormat.setMaximumFractionDigits(0);
            int price;
            int regular_amount = products.get(i).getPrices().getRegular_amount();
            double discountPorcDb;
            int discountPorc = 0;
            if (regular_amount > 0) {
                price = products.get(i).getPrices().getAmount();
                discountPorcDb = ((regular_amount - price)*100) / regular_amount;
                discountPorc = (int)(Math.ceil(discountPorcDb));
                viewHolder.product_regular_amount.setText(colPesoFormat.format(regular_amount));
                viewHolder.product_regular_amount.setPaintFlags(viewHolder.product_regular_amount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                viewHolder.product_discount.setText(discountPorc+"% OFF");
            } else {
                price = products.get(i).getPrice();
                viewHolder.product_regular_amount.setText("");
                viewHolder.product_discount.setText("");
            }
            viewHolder.product_amount.setText(colPesoFormat.format(price));
            if (products.get(i).getInstallments().getQuantity() > 0) {
                int quantity = products.get(i).getInstallments().getQuantity();
                int amountInstallment = products.get(i).getInstallments().getAmount();
                viewHolder.product_installments.setText("En " + quantity + "x " + colPesoFormat.format(amountInstallment));
            }
            if (products.get(i).getFree_shipping()) {
                viewHolder.product_free_shipping.setText("Envío gratis");
            }
            new DownloadImageTask(viewHolder.product_image)
                    .execute(products.get(i).getThumbnail());
            viewHolder.product_list_content.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    goToProductDetail(products.get(i));
                }
            });
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

    private void goToProductDetail(Product product) {
        Intent goProductDetail = new Intent(this, ProductDetail.class);
        Bundle dataBundle = new Bundle();
        dataBundle.putString("productId", product.getId());
        dataBundle.putString("stringSearch", stringSearch);
        dataBundle.putInt("limit", limit);
        dataBundle.putInt("offset", offset);
        dataBundle.putInt("page", actualPage);
        goProductDetail.putExtras(dataBundle);
        goProductDetail.addFlags(goProductDetail.FLAG_ACTIVITY_CLEAR_TOP | goProductDetail.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(goProductDetail);
    }

    private void getProducts() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, urlSearch, null, response -> {
                    try {
                        results = response.get("results").toString();
                        pagingInfo = response.get("paging").toString();
                        System.out.println("pagingInfo: " + pagingInfo);
                        JSONArray resultsArray = new JSONArray(results);
                        JSONObject infoPage = new JSONObject(pagingInfo);
                        int totalResults = infoPage.getInt("total");
                        loadingProducts.setVisibility(View.INVISIBLE);
                        txtLoading.setVisibility(View.INVISIBLE);
                        products = new ArrayList();
                        if (resultsArray.length() == 0) {
                            btnBackToSearch.setVisibility(View.VISIBLE);
                            txtNoResults.setVisibility(View.VISIBLE);
                        } else {
                            if (offset == 0) {
                                btnPrev.setVisibility(View.INVISIBLE);
                            } else {
                                btnPrev.setVisibility(View.VISIBLE);
                            }
                            if (offset+limit >= totalResults) {
                                btnNext.setVisibility(View.INVISIBLE);
                            } else {
                                btnNext.setVisibility(View.VISIBLE);
                            }
                            int totalPages = (int)(Math.floor(totalResults/limit));
                            textActualPage.setText(actualPage + " / " + totalPages);
                            for (int i =0; i < resultsArray.length(); i++) {
                                JSONObject row = resultsArray.getJSONObject(i);

                                // Product main info
                                String id = row.getString("id");
                                String title = row.getString("title");
                                int price = row.getInt("price");
                                int available_quantity = row.getInt("available_quantity");
                                String condition = row.getString("condition");
                                String permalink = row.getString("permalink");
                                String thumbnail = row.getString("thumbnail");
                                Boolean accepts_mercadopago = row.getBoolean("accepts_mercadopago");

                                // Installments info
                                JSONObject installmentsBD = new JSONObject(row.getString("installments"));
                                int quantity = installmentsBD.getInt("quantity");
                                int amount = installmentsBD.getInt("amount");
                                int rate = installmentsBD.getInt("rate");
                                String currency_id = installmentsBD.getString("currency_id");
                                Installments installments = new Installments(quantity, amount, rate, currency_id);

                                // Address info
                                JSONObject addressBD = new JSONObject(row.getString("address"));
                                String state_name = addressBD.getString("state_name");
                                String city_name = addressBD.getString("city_name");
                                Address address = new Address(state_name, city_name);

                                // Attributes info
                                JSONArray attributesArray = new JSONArray(row.getString("attributes"));
                                List<Attribute> attributes = new ArrayList();

                                for (int j = 0; j < attributesArray.length(); j++) {
                                    JSONObject attributeDB = attributesArray.getJSONObject(j);
                                    String value_name = attributeDB.getString("value_name");
                                    String name = attributeDB.getString("name");

                                    attributes.add(new Attribute(value_name, name));
                                }

                                // Prices info
                                int amountDB = price;
                                int regular_amountDB = 0;
                                if (row.has("prices")) {
                                    JSONObject pricesBD = new JSONObject(row.getString("prices"));
//                                    System.out.println("pricesBD: " + pricesBD);
                                    JSONArray pricesArray = new JSONArray(pricesBD.getString("prices"));
                                    if (pricesArray.length() > 0) {
                                        JSONObject lastPrice = pricesArray.getJSONObject(pricesArray.length()-1);
//                                        System.out.println("lastPrice: " + lastPrice);
                                        amountDB = lastPrice.getInt("amount");
                                        if (!lastPrice.isNull("regular_amount")) {
                                            regular_amountDB = lastPrice.getInt("regular_amount");
                                        }
                                    }
                                }
//                                System.out.println("regular_amountDB: " + regular_amountDB);

                                Prices prices = new Prices(amountDB, regular_amountDB);

                                //Shipping info
                                JSONObject free_shippingBD = new JSONObject(row.getString("shipping"));
                                Boolean free_shipping = free_shippingBD.getBoolean("free_shipping");

                                products.add(new Product(id, title, price, available_quantity, condition, permalink, thumbnail, accepts_mercadopago, installments, address, attributes, prices, free_shipping));
                            }
                            recycler = (RecyclerView) findViewById(R.id.rvProductsContainer);
                            recycler.setHasFixedSize(true);

                            lManager = new LinearLayoutManager(this);
                            recycler.setLayoutManager(lManager);
                            adapter = new ProductsAdapter(products);
                            recycler.setAdapter(adapter);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    System.out.println("error: " + error);
                });

        requestQueue.add(jsonObjectRequest);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getProducts();
    }
}