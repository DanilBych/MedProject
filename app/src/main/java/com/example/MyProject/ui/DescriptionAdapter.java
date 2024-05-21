package com.example.MyProject.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MyProject.R;
import com.example.MyProject.data.Product;
import com.example.MyProject.databinding.ListItemBinding;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class DescriptionAdapter extends BaseAdapter implements ListAdapter {
    Context context;
    private List<List<String>> descList = new ArrayList<>();
    private LayoutInflater inflater;

    public DescriptionAdapter(@NonNull Context context, @NonNull List<List<String>> objects) {
        this.context = context;
        this.descList = objects;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return descList.size();
    }

    @Override
    public Object getItem(int position) {
        return descList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        @SuppressLint({"InflateParams", "ViewHolder"})
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.desc_item, null);

        TextView textView;

        Log.d("formmmm", "getView: " + descList.get(position).toString());
        if (position == 0){
            for (int i = 0; i < descList.get(position).size(); i += 2) {
                linearLayout.setBackgroundResource(R.color.d_aquamarine);
                textView = createTextView(descList.get(position).get(i));
                textView.setTextSize(32);
                textView.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                linearLayout.addView(textView);
                textView = createTextView(descList.get(position).get(i+1));
                textView.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                linearLayout.addView(textView);
            }

        }

        else {
            textView = createTextView(descList.get(position).get(0));
            textView.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
            textView.setTextSize(24);
            linearLayout.addView(textView);
            if (descList.get(position).get(0).startsWith("Ана")) {
                int i = 2;
                for (; i < descList.get(position).size() - 3; i += 3) {
                    textView = createTextView(descList.get(position).get(i));
                    textView.setTextSize(22);
                    linearLayout.addView(textView);
                    textView = createTextView(descList.get(position).get(i + 1) + "шт "
                            + descList.get(position).get(i + 2));
                    textView.setTextSize(18);
                    linearLayout.addView(textView);
                }
                if (i == 2){
                    linearLayout.addView(createTextView(descList.get(position).get(2)));
                }
            } else if (descList.get(position).get(0).startsWith("Про")) {
                textView = createTextView(descList.get(position).get(1));
                textView.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                linearLayout.addView(textView);
                for (int i = 2; i < descList.get(position).size() - 2; i += 2) {
                    textView = createTextView(descList.get(position).get(i));
                    textView.setTextSize(22);
                    linearLayout.addView(textView);
                    textView = createTextView(descList.get(position).get(i + 1) + ' '
                            + descList.get(position).get(i + 2));
                    textView.setTextSize(18);
                    linearLayout.addView(textView);
                }
            } else if (descList.get(position).get(0).startsWith("Рис")) {
                textView = createTextView(descList.get(position).get(1));
                textView.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                int i = 0;
                switch (descList.get(position).get(1).charAt(0)){
                    case ('М'):
                        i = R.color.green;
                        break;
                    case ('У'):
                        i = R.color.yellow;
                        break;
                    case ('З'):
                        i = R.color.orange;
                        break;
                    case ('В'):
                        i = R.color.red;
                        textView.setTextColor(ContextCompat.getColor(context, R.color.white));
                }
                textView.setBackgroundResource(R.drawable.rounded);
                textView.setBackgroundTintList(
                        ContextCompat.getColorStateList(context, i));
                linearLayout.addView(textView);
                linearLayout.addView(createTextView(descList.get(position).get(2)));
            } else {
                int i = 1;

                if(descList.get(position).get(0).startsWith("Mеж")){
                    i = 2;
                }

                for (; i < descList.get(position).size(); i++) {
                    linearLayout.addView(createTextView(descList.get(position).get(i)));
                }
            }
        }




        return linearLayout;
    }

    private TextView createTextView(String text){
        @SuppressLint("InflateParams")
        TextView textView = (TextView) inflater.inflate(R.layout.desk_item_item, null);
        textView.setText(text);

        return textView;
    }
}
