package com.example.student_dormitorryhelper.ui.chat;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.student_dormitorryhelper.R;
import com.example.student_dormitorryhelper.adapters.ChatAdapter;
import com.example.student_dormitorryhelper.databinding.FragmentResidentsCouncilChatBinding;
import com.example.student_dormitorryhelper.models.ChatMessage;
import com.example.student_dormitorryhelper.models.User;
import com.example.student_dormitorryhelper.utilities.Constants;
import com.example.student_dormitorryhelper.utilities.UserManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ResidentsCouncilChatFragment extends Fragment {

  private FragmentResidentsCouncilChatBinding binding;
  private List<ChatMessage> chatMessageList;
  UserManager userManager;
  private ChatAdapter chatAdapter;
  private FirebaseFirestore database;


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    binding = FragmentResidentsCouncilChatBinding.inflate(getLayoutInflater());
    init();
    return binding.getRoot();
  }

  private void init() {
    userManager = UserManager.getInstance();
    chatMessageList = new ArrayList<>();
    chatAdapter = new ChatAdapter(
        chatMessageList,
        userManager.getFbUserId()
    );
    binding.chateRecyclerView.setAdapter(chatAdapter);
    database = FirebaseFirestore.getInstance();
    loadReceiverDetails();
    setListeners();
    listenMessages();
  }

  private void sendMessage() {
    HashMap<String, Object> message = new HashMap<>();
    message.put(Constants.KEY_SENDER_ID, userManager.getFbUserId());
    message.put(Constants.KEY_RECEIVER_ID, userManager.getSendToUser().fbId);
    message.put(Constants.KEY_MESSAGE, binding.inputMessage.getText().toString());
    message.put(Constants.KEY_TIMESTAMP, new Date());
    database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
    binding.inputMessage.setText(null);
  }

  private void listenMessages() {
    database.collection(Constants.KEY_COLLECTION_CHAT)
        .whereEqualTo(Constants.KEY_SENDER_ID, userManager.getFbUserId())
        .whereEqualTo(Constants.KEY_RECEIVER_ID, userManager.getSendToUser().fbId)
        .addSnapshotListener(eventListener);
    database.collection(Constants.KEY_COLLECTION_CHAT)
        .whereEqualTo(Constants.KEY_SENDER_ID, userManager.getSendToUser().fbId)
        .whereEqualTo(Constants.KEY_RECEIVER_ID, userManager.getFbUserId())
        .addSnapshotListener(eventListener);
  }

  private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
    if(error != null) {
      return;
    }
    if (value != null) {
      int count = chatMessageList.size();
      for (DocumentChange documentChange : value.getDocumentChanges()) {
        if(documentChange.getType() == DocumentChange.Type.ADDED) {
          ChatMessage chatMessage = new ChatMessage();
          chatMessage.senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
          chatMessage.receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
          chatMessage.message = documentChange.getDocument().getString(Constants.KEY_MESSAGE);
          chatMessage.dateTime = getReadableDateTime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
          chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
          chatMessageList.add(chatMessage);
        }
      }
      Collections.sort(chatMessageList, (obj1, obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
      if (count == 0) {
        chatAdapter.notifyDataSetChanged();
      } else {
        chatAdapter.notifyItemRangeInserted(chatMessageList.size(), chatMessageList.size());
        binding.chateRecyclerView.smoothScrollToPosition(chatMessageList.size() - 1);
      }
      binding.chateRecyclerView.setVisibility(View.VISIBLE);
    }
    binding.progressBar.setVisibility(View.GONE);
  };

  private void loadReceiverDetails() {
      binding.inputMessage.setHint(
          "Napisz do " +
          userManager.getSendToUser().firstName +
              " " +
              userManager.getSendToUser().secondName);
  }

  private void setListeners() {
    binding.layoutDownload.setOnClickListener(v -> sendMessage());
  }

  private String getReadableDateTime(Date date) {
    return new SimpleDateFormat("dd MMMM, yyyy - hh:mm a", Locale.getDefault()).format(date);
  }
}