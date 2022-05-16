package com.example.student_dormitorryhelper.ui.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChatViewModel extends ViewModel {

    private MutableLiveData<String> headerName;


    public LiveData<String> getText() {
        return headerName;
    }

}