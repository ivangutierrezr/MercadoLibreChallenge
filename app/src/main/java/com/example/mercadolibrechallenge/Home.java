package com.example.mercadolibrechallenge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mercadolibrechallenge.helpers.NavigationHelper;

public class Home extends AppCompatActivity {

    Button btnSearch;
    EditText searchInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setComponents();
    }

    public void setComponents(){
        btnSearch = findViewById(R.id.btnSearch);
        searchInput = findViewById(R.id.searchInput);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String searchText = searchInput.getText().toString();
                search(searchText);
            }
        });
    }

    public void search(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            Toast.makeText(this,"Debe agregar un texto de búsqueda", Toast.LENGTH_LONG).show();
        } else {
            NavigationHelper.navigateTo(this, SearchList.class, "", searchText, 10, 0, 1);
        }
    }
}