package com.example.MyProject.ui;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.MyProject.data.Product;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddNewProduct implements Runnable{
    MainViewModel viewModel;
    List<Product> products;
    SharedPreferences sp;
    private final String url="https://mediqlab.com";
    public AddNewProduct(MainViewModel viewModel, List<Product> products, SharedPreferences sp) {
        this.viewModel = viewModel;
        this.products = products;
        if(products == null) {
            this.products = new ArrayList<>();
        }
        this.sp = sp;
    }

    public void run() {



        update();

        sp.edit().putInt("DATE", (int) Calendar.getInstance().getTime().getTime())
                .putInt("CHARS", 1).putInt("NUMS", 1).apply();
        return;
    }

    private Element getByUrl(String url) {

        try {
            return Jsoup.connect(url).get().body();
        } catch (IOException e) {
            Log.e("Update error", "Failed connection");
        }
        return null;
    }

    private void update() {
        Element doc;
        do {
            doc = getByUrl(url + "/alphabetical");
        }while(doc == null);

        Elements lists = doc.getElementsByClass("css-2imjyh");
        Elements chars = lists.get(0).children(), nums, names, children;
        Elements drug;
        String ch, n, drug_url;
        int h = 0;

        int i = sp.getInt("CHARS", 1);
        int j = sp.getInt("NUMS", 0);


        for (; i < chars.size(); i++) {
            if(chars.get(i).hasAttr("href")){
                ch = chars.get(i).attribute("href").getValue().substring(19);
            }
            else {
                ch = "/alphabetical/" + chars.get(i).text();
            }

            do {
                doc = getByUrl(url+ch);
            }while(doc == null);

            lists = doc.getElementsByClass("css-2imjyh");

            nums = lists.get(1).children();

            sp.edit().putInt("CHARS", i).apply();

            for (; j < nums.size(); j++) {
                if(nums.get(j).hasAttr("href")){
                    n = nums.get(j).attribute("href").getValue().substring(19);
                }
                else {
                    n = ch + '/' + (j + 1);
                }
                Log.d("TAG", "update: " + n);

                do {
                    doc = getByUrl(url+n);
                }while(doc == null);

                lists = doc.getElementsByClass("css-178yklu");

                names = lists.get(1).children();

                sp.edit().putInt("NUMS", j).apply();

                for (int g = 1; g < names.size(); g++) {
                    drug_url = names.get(g).children().get(0).attribute("href").getValue();

                    do {
                        doc = getByUrl(url+drug_url);
                    }while(doc == null);

                    drug = doc.getElementsByClass("css-14d90y3").get(0).children();


                    String title = drug.get(3).children().get(1).children().get(3).text();
                    int proven = (drug.get(1).text().charAt(9) == 'н')? 0 : 1;

                    Product product = new Product(title, proven, drug_url.substring(7));



                    if(h >= products.size()){
                        viewModel.saveProduct(product);
                    }
                    else if(!products.get(h).getTitle().equals(product.getTitle())){
                        viewModel.saveProduct(product);
                    }


                    h++;
                }
            }
            j = 0;

        }
    }


}
