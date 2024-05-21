package com.example.MyProject.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MyProject.R;
import com.example.MyProject.data.MyNotesDatabase;
import com.example.MyProject.data.Product;
import com.example.MyProject.databinding.ActivityMainBinding;
import com.example.MyProject.ui.mynotes.MyNotesActivity;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ProductAdapter arrayAdapter;
    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private Thread update;
    private boolean threadflag = true;
    SharedPreferences sp;

    List<Product> tempList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //  handler = new Handler(Looper.getMainLooper());
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);


        sp = getSharedPreferences("SAVED", Activity.MODE_PRIVATE);
        int i = sp.getInt("DATE", -1);
        threadflag = (Calendar.getInstance().getTime().getMonth() - (i + i/11)%11) > 0;

        arrayAdapter = new ProductAdapter();

        viewModel.getProducts().observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                tempList = products;
                arrayAdapter.setProductList(products);
                if(threadflag) {
                    Toast.makeText(MainActivity.this, R.string.started_update, Toast.LENGTH_LONG).show();
                    update = new Thread(new AddNewProduct( viewModel, tempList, sp));
                    update.start();
                    threadflag = false;
                }
            }
        });





        binding.recyclerView.setAdapter(arrayAdapter);



        arrayAdapter.setOnItemProductClickListener(new ProductAdapter.OnItemProductClickListener() {
            @Override
            public void onProductClick(int position) {
                Product product = arrayAdapter.getProductList().get(position);

                Intent productIntent = new Intent(MainActivity.this, ProductActivity.class);
                productIntent.putExtra("Title", product.getTitle());
                productIntent.putExtra("Proven", product.getProven());
                productIntent.putExtra("Url", product.getUrl());
                productIntent.putExtra("Description", "");
                startActivity(productIntent);
            }
        });

        binding.editTextSort.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<Product> searchList = new LinkedList<>();
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
                        Product product = arrayAdapter.getProductList().get(position);
                        showAlert(product);

                    }
                });


        binding.btnMyNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MyNotesActivity.class));
            }
        });

//        Log.d("NNN", String.valueOf(viewModel));

    }


    public void showAlert(Product product) {
        new AlertDialog.Builder(com.example.MyProject.ui.MainActivity.this)
                .setTitle("Удаление элемента")
                .setMessage("Удалить элемент " + product.getTitle() + "?")
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        viewModel.remove(product);

                    }
                })
                .setNeutralButton("EDIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = AddNewProductActivity.newIntent(MainActivity.this,
//                                String.valueOf(product.getId()),
//                                product.getTitle(),
//                                product.getProven(),
//                                product.getPrice());
//
//                        startActivity(intent);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_delete)
                .show();
    }


//    private void addSortList() {
//
//        binding.btnSort.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//              /*  new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        tempList.sort(new Comparator<Product>() {
//                            @Override
//                            public int compare(Product o1, Product o2) {
//                                return o2.getTitle().compareTo(o1.getTitle());
//                            }
//                        });
//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                arrayAdapter.setProductList(tempList);
//                            }
//                        });
//
//                    }
//                }).start();*/
//
//
//            }
//        });
//
//    }

//    private void addListData() {
//        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = AddNewProductActivity.newIntent(MainActivity.this,
//                        String.valueOf(0), "", "", "");
//                startActivity(intent);
//            }
//        });
//
//
//    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.refreshList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}