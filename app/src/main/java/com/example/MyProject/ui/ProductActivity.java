package com.example.MyProject.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.MyProject.R;
import com.example.MyProject.data.Product;
import com.example.MyProject.databinding.ActivityMainBinding;
import com.example.MyProject.databinding.ActivityProductBinding;
import com.example.MyProject.ui.mynotes.AddNewNoteActivity;
import com.example.MyProject.ui.mynotes.MyNotesActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.net.ssl.SSLContext;

public class ProductActivity extends AppCompatActivity {
    private ActivityProductBinding binding;
    private String title;
    private String url;
    private int proven;
    private String note;
    private List<List<String>> description = new ArrayList<>();
    DescriptionAdapter arrayAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        url = intent.getStringExtra("Url");
        title = intent.getStringExtra("Title");
        proven = intent.getIntExtra("Proven", 0);
        note = intent.getStringExtra("Note");
        int j = 0;
        if(!intent.getStringExtra("Description").isEmpty()) {
            for (String s : intent.getStringExtra("Description").split("Ъ")) {
                description.add(new ArrayList<>());
                for (String ss : s.split("Ь")) {
                    description.get(j).add(ss);
                }
                j++;
            }
        }



        binding.proven.setTextColor(ContextCompat.getColor(this, R.color.white));
        if(proven == 1){
            binding.proven.setText("Препарат обладает доказанной эффективностью");
            binding.constraintLayout.setBackgroundTintList(
                    ContextCompat.getColorStateList(this, R.color.green));
            binding.btnExit.setBackgroundResource(R.color.green);
            binding.btnAdd.setBackgroundResource(R.color.green);
        }
        else if(proven == 0) {
            binding.proven.setText("Препарат не обладает доказанной эффективностью");
            binding.constraintLayout.setBackgroundTintList(
                    ContextCompat.getColorStateList(this,R.color.red));
            binding.btnExit.setBackgroundResource(R.color.red);
            binding.btnAdd.setBackgroundResource(R.color.red);
        }

        binding.btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String desc = "";
                for (List<String> strings : description) {
                    if (!desc.isEmpty()){
                        desc += 'Ъ';
                    }
                    for (String string : strings) {
                        if(!desc.isEmpty() && desc.charAt(desc.length() - 1) != 'Ъ'){
                            desc += 'Ь';
                        }
                        desc += string;
                    }
                }
                Intent intent = AddNewNoteActivity.newIntent(ProductActivity.this,
                        0, proven, note, title, desc);
                startActivity(intent);
            }
        });



        new Thread(new FillDescription( ProductActivity.this, url, note)).start();
        while (arrayAdapter == null){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        Log.d("getView", "onCreate: " + arrayAdapter);
        binding.listView.setAdapter(arrayAdapter);

    }


    private class FillDescription implements Runnable{
        private Context context;
        private String url;
        private String note;
        public FillDescription(Context context, String url, String note) {
            this.context = context;
            this.url = url;
            this.note = note;
        }

        @Override
        public void run() {

            getParsedEl(getByUrl("https://mediqlab.com/drugs/"+url));

            Log.d("TAG", url + "run: " + description);

            arrayAdapter = new DescriptionAdapter(context, description);
        }
        private Element getByUrl(String url) {
            try {
                return Jsoup.connect(url).get().body();
            } catch (IOException e) {
                Log.e("Open error", "Failed connection");
            }
            return null;
        }
        private void getParsedEl(Element el){

            if(el == null) {
                if(description.isEmpty()){
                    description.add(new ArrayList<>());
                }
                description.get(0).add("Ваша записка");
                description.get(0).add(note);
                return;
            }


            Elements title = el.getElementsByClass("css-14d90y3").get(0).children();

            Elements children = title.get(3).children();



            int j = 0;
            description.add(new ArrayList<>());
            description.get(j).add(children.get(1).children().get(3).text());
            description.get(j).add(children.get(1).children().get(5).text());

            if(note != null){
                description.get(j).add("Ваша заметка");
                description.get(j).add(note);
            }

            if (children.get(1).children().size() > 6) {
                j++;
                description.add(new ArrayList<>());
                description.get(j).add(children.get(1).children().get(7).children().get(1).text());
                if(description.get(j).get(0).startsWith("Фор")){
                    for (Element child : children.get(1).children().get(7).children().get(3).children()) {
                        description.get(j).add(child.text());
                    }
                }
                else {
                    description.get(j).add(children.get(1).children().get(7).children().get(2).text());
                }
            }
            boolean ncheck = true;
            for (int i = 2; i < children.size(); i++) {
                for (Element child2 : children.get(i).children()) {
                    if (ncheck && child2.children().isEmpty() && !child2.text().isEmpty()) {
                        j++;
                        description.add(new ArrayList<>());
                        description.get(j).add(child2.text());
                        ncheck = false;
                    }
                    for (Element child3 : child2.children()) {
                        if (child3.children().isEmpty() && !child3.text().isEmpty()) {
                            j++;
                            description.add(new ArrayList<>());
                            description.get(j).add(child3.text());
                        }
                        for (Element child4 : child3.children()) {
                            if (child4.children().isEmpty() && !child4.text().isEmpty()) {
                                if (ncheck) {
                                    j++;
                                    description.add(new ArrayList<>());
                                }
                                else ncheck = true;
                                description.get(j).add(child4.text());
                            }
                            for (Element child5 : child4.children())
                                if (!child5.text().isEmpty()) {
                                    if (child5.text().charAt(child5.text().length() - 1) == '₽') {
                                        if (child5.children().get(0).text().isEmpty()) {
                                            description.get(j).add(child5.children().get(1).text());
                                            description.get(j).add(child5.children().get(3).children()
                                                    .get(1).text());
                                            description.get(j).add(child5.children().get(3).children()
                                                    .get(2).text());
                                        } else {
                                            description.get(j).add(child5.children().get(0).text());
                                            description.get(j).add(child5.children().get(1).children()
                                                    .get(0).text());
                                            description.get(j).add(child5.children().get(1).children()
                                                    .get(1).text());
                                        }
                                    } else {
                                        description.get(j).add(child5.text());
                                    }
                                }
                        }
                    }
                }
            }
        }
    }

}