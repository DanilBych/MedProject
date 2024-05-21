package com.example.MyProject.ui.mynotes;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.MyProject.data.MyNote;
import com.example.MyProject.data.MyNotesDatabase;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AddNewNoteViewModel extends AndroidViewModel {

    private final MyNotesDatabase database;

    public LiveData<Boolean> getShouldClose() {
        return shouldClose;
    }

    private final MutableLiveData<Boolean> shouldClose = new MutableLiveData<>();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public AddNewNoteViewModel(@NonNull Application application) {
        super(application);
        database = MyNotesDatabase.newInstance(application);
    }

    public void saveProduct(MyNote product) {
        Disposable disposable = database.productDao().add(product)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Throwable {
                        shouldClose.setValue(true);
                    }
                });
        compositeDisposable.add(disposable);
    }

    public void updateProduct(MyNote product) {
        Disposable disposable = database.productDao().update(product.getTitle(), product.getNote(),
                        product.getDescription(), product.getUrl(), product.getProven(), product.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Throwable {
                        shouldClose.setValue(true);
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
