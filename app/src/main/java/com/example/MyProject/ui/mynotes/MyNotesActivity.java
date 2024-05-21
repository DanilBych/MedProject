package com.example.MyProject.ui.mynotes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MyProject.data.MyNote;
import com.example.MyProject.databinding.ActivityMyNotesBinding;
import com.example.MyProject.ui.ProductActivity;
import com.example.MyProject.ui.ProductAdapter;

import java.util.LinkedList;
import java.util.List;

public class MyNotesActivity extends AppCompatActivity {

    private MyNoteAdapter arrayAdapter;
    private ActivityMyNotesBinding binding;
    private MyNotesViewModel viewModel;


    List<MyNote> tempList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyNotesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //  handler = new Handler(Looper.getMainLooper());
        viewModel = new ViewModelProvider(this).get(MyNotesViewModel.class);



        arrayAdapter = new MyNoteAdapter();
        viewModel.getProducts().observe(this, new Observer<List<MyNote>>() {
            @Override
            public void onChanged(List<MyNote> products) {
                tempList = products;
                arrayAdapter.setProductList(products);
            }
        });

        viewModel.refreshList();


        binding.recyclerView.setAdapter(arrayAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        MyNote product = (MyNote) arrayAdapter.getProductList().get(position);
                        showAlert(product);

                    }
                });
        itemTouchHelper.attachToRecyclerView(binding.recyclerView);

        arrayAdapter.setOnItemProductClickListener(new ProductAdapter.OnItemProductClickListener() {
            @Override
            public void onProductClick(int position) {
                MyNote product = arrayAdapter.getProductList().get(position);

                Intent productIntent = new Intent(MyNotesActivity.this, ProductActivity.class);
                productIntent.putExtra("Title", product.getTitle());
                productIntent.putExtra("Proven", product.getProven());
                productIntent.putExtra("Url", product.getUrl());
                productIntent.putExtra("Note", product.getNote());
                productIntent.putExtra("Description", product.getDescription());
                startActivity(productIntent);
            }
        });
        
        binding.btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.editTextSort.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<MyNote> searchList = new LinkedList<>();
                String str = s.toString().toLowerCase();
                boolean flag = true;
                int j = 0;
                while (flag) {
                    flag = false;
                    for (int i = 0; i < tempList.size(); i++) {
                        if(tempList.get(i).getTitle().length() > j + str.length()){
                            flag = true;
                            if (tempList.get(i).getTitle().substring(j, j + str.length()).toLowerCase().equals(str)) searchList.add(tempList.get(i));
                        }
                    }
                    j++;
                }
                arrayAdapter.setProductList(searchList);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        addListData();
    }


    public void showAlert(MyNote product) {
        new AlertDialog.Builder(MyNotesActivity.this)
                .setTitle("Удаление элемента")
                .setMessage("Удалить элемент?")
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        viewModel.remove(product);

                    }
                })
                .setNeutralButton("EDIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = AddNewNoteActivity.newIntent(MyNotesActivity.this,
                                product.getId(),
                                product.getProven(),
                                product.getNote(),
                                product.getTitle(),
                                product.getDescription());
                        startActivity(intent);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_delete)
                .show();
    }

    private void addListData() {
        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AddNewNoteActivity.newIntent(MyNotesActivity.this,
                        0, -1, "", null, "");
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.refreshList();
    }
}