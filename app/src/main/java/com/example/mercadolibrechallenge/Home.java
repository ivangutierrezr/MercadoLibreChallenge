package com.example.mercadolibrechallenge;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Home extends AppCompatActivity {

    Button btnSearch;
    EditText searchInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btnSearch = findViewById(R.id.btnSearch);
        searchInput = findViewById(R.id.searchInput);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                search();
            }
        });
    }

    public void search() {
        String searchText = searchInput.getText().toString();
        if (searchText == null || searchText.matches("")) {
            Toast.makeText(this,"Debe agregar un texto de búsqueda", Toast.LENGTH_LONG).show();
        } else {
            Intent goToList = new Intent(this, SearchList.class);
            Bundle dataSearch = new Bundle();
            dataSearch.putString("stringSearch", searchText);
            dataSearch.putInt("limit", 10);
            dataSearch.putInt("offset", 0);
            dataSearch.putInt("page", 1);
            goToList.putExtras(dataSearch);
            goToList.addFlags(goToList.FLAG_ACTIVITY_CLEAR_TOP | goToList.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(goToList);
        }
    }
}