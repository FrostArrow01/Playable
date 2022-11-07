package com.example.testfirebase.ui.MiEspacio;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GalleryViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public GalleryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is MiEspacio fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}