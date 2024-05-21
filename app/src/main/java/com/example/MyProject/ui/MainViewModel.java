package com.example.MyProject.ui;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.MyProject.data.Product;
import com.example.MyProject.data.ProductDatabase;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainViewModel extends AndroidViewModel {
    private final ProductDatabase database;
    private final MutableLiveData <List<Product>> products = new MutableLiveData<>();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public MainViewModel(@NonNull Application application) {
        super(application);
        database = ProductDatabase.newInstance(application);
    }
    public LiveData<List<Product>> getProducts() {
        return products;
    }


    public void refreshList(){
        Disposable disposable= database.productDao()
                .getProducts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Product>>() {
                    @Override
                    public void accept(List<Product> productDB) throws Throwable {
                        products.setValue(productDB);
                    }
                });
        compositeDisposable.add(disposable);
    }
    public void remove(Product product){
        Disposable disposable = database.productDao()
                .remove(product.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Throwable {
                        Log.d("MainViewModel", "Remove product "+ product.getId());
                    }
                });

        compositeDisposable.add(disposable);
    }
    public void saveProduct(Product product) {
        Disposable disposable = database.productDao().add(product)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Throwable {
                        Log.d("MainViewModel", "Add product "+ product);
                    }
                });

        compositeDisposable.add(disposable);
    }
    public void updateProduct(Product product) {
        Disposable disposable = database.productDao().update(product.getTitle(),
                product.getUrl(), product.getProven(), product.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Throwable {
                        Log.d("MainViewModel", "Update product "+ product);
                    }
                });

        compositeDisposable.add(disposable);
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
