package com.example.MyProject.ui.mynotes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.MyProject.data.MyNote;
import com.example.MyProject.databinding.ActivityAddNewProductBinding;

public class AddNewNoteActivity extends AppCompatActivity {


    private final static String EXTRA_NOTE = "note";
    private final static String EXTRA_ID = "id";
    private static final String EXTRA_PROVEN = "proven";
    private static final String EXTRA_TITLE = "title";
    private static final String EXTRA_DESK = "description";

    private String note;
    private int id;
    private int proven;
    private String title;
    private String description;
    private AddNewNoteViewModel viewModel;
    private ActivityAddNewProductBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNewProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(AddNewNoteViewModel.class);

        viewModel.getShouldClose().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean shouldClose) {
                if (shouldClose) {
                    finish();
                }
            }
        });
        Intent intent = getIntent();
        note = intent.getStringExtra(EXTRA_NOTE);
        id = intent.getIntExtra(EXTRA_ID, 0);
        proven = intent.getIntExtra(EXTRA_PROVEN, 0);
        title = intent.getStringExtra(EXTRA_TITLE);
        description = intent.getStringExtra(EXTRA_DESK);

        binding.editTextTitle.setText(note);


        binding.btnSaveProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String note = binding.editTextTitle.getText().toString();
                MyNote product = new MyNote((title == null)? note : title, proven, "", note, description);

                if (id == 0) {
                    Log.d("LOG", "Create Product" + id + " " + product);

                    viewModel.saveProduct(product);

                } else {
                    Log.d("LOG", "UPDATE Product" + id + " " + product);

                    viewModel.updateProduct(product);
                }

            }

        });


    }

    public static Intent newIntent(Context context,
                                   int id,
                                   int proven,
                                   String note,
                                   String title,
                                   String description
    ) {
        Intent intent = new Intent(context, AddNewNoteActivity.class);
        intent.putExtra(EXTRA_ID, id);
        intent.putExtra(EXTRA_PROVEN, proven);
        intent.putExtra(EXTRA_NOTE, note);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_DESK, description);


        return intent;
    }


}