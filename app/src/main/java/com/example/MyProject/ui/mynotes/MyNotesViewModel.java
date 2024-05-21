package com.example.MyProject.ui.mynotes;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.MyProject.data.MyNote;
import com.example.MyProject.data.MyNotesDatabase;
import com.example.MyProject.data.Product;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MyNotesViewModel extends AndroidViewModel {
        private final MyNotesDatabase database;
        private final MutableLiveData <List<MyNote>> products = new MutableLiveData<>();
        private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public MyNotesViewModel(@NonNull Application application) {
        super(application);
        database = MyNotesDatabase.newInstance(application);
    }
    public LiveData<List<MyNote>> getProducts() {
        return products;
    }


    public void refreshList(){
        Disposable disposable= database.productDao()
                .getProducts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<MyNote>>() {
                    @Override
                    public void accept(List<MyNote> productDB) throws Throwable {
                        products.setValue(productDB);
                    }
                });
        compositeDisposable.add(disposable);
    }
    public void remove(MyNote product){
        Disposable disposable = database.productDao()
                .remove(product.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Throwable {
                        refreshList();
                        Log.d("MainViewModel", "Remove product "+ product.getId());
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
