package com.example.MyProject.ui;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.MyProject.R;
import com.example.MyProject.data.Product;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.net.ssl.SSLContext;

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
        int j = sp.getInt("NUMS", 1);


        for (; i < chars.size(); i++) {
            ch = "/alphabetical/"+chars.get(i).text();

            do {
                doc = getByUrl(url+ch);
            }while(doc == null);

            lists = doc.getElementsByClass("css-2imjyh");

            nums = lists.get(1).children();

            sp.edit().putInt("CHARS", i).apply();

            for (; j <= nums.size(); j++) {
                n = ch+'/'+j;
                Log.d("TAG", "update: " + n);

                do {
                    doc = getByUrl(url+n);
                }while(doc == null);

                lists = doc.getElementsByClass("css-178yklu");

                names = lists.get(1).children();

                sp.edit().putInt("NUMS", j).apply();

                for (int g = 1; g < names.size(); g++) {
//                    try {
//                        wait(10);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
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
                viewModel.refreshList();
            }
            j = 1;

        }
    }

//    private String[] getParsedEl(Element el){
//        String titl, desc = "", qual = "";
//
//        Elements title = el.getElementsByClass("css-14d90y3").get(0).children();
//
//        titl = title.get(3).children().get(1).children().get(3).text();
//
//        desc += title.get(1).text() + ",,";
//
//        if(title.get(1).text().equals("Препарат не обладает доказанной эффективностью")){
//            for (Element child : title.get(3).children().get(1).children()) {
//                if(!child.text().isEmpty()){
//                    desc += child.text() + ",,";
//                }
//            }
//            for (Element child : title.get(3).children().get(3).children().get(1).children()) {
//                if (!child.text().isEmpty()) {
//                    qual += child.text() + ",,";
//                }
//
//            }
//        }
//        else {
//            Elements children = title.get(3).children();
//            for (Element child : children.get(1).children()) {
//                if (child.children().isEmpty() && !child.text().isEmpty()) {
//                    desc += child.text() + ";;";
//                }
//                for (Element child2 : child.children()) {
//                    if (!child2.text().isEmpty()) {
//                        desc += child2.text() + ",,";
//                    }
//                }
//            }
//
//            for (int i = 2; i < children.size(); i++) {
//                for (Element child2 : children.get(i).children())
//                    for (Element child3 : child2.children()){
//                        for (Element child4 : child3.children()) {
//                            if (child4.children().isEmpty() && !child4.text().isEmpty()) {
//                                qual += child4.text() + ";;";
//                            }
//                            for (Element child5 : child4.children())
//                                if (!child5.text().equals("")) {
//                                    qual += child5.text() + ",,";
//                                }
//                        }
//                    }
//            }
//        }
//        desc = desc.substring(0, desc.length()-2);
//        qual = qual.substring(0, qual.length()-2);
//
//        return new String[]{titl, desc, qual};
//    }


}