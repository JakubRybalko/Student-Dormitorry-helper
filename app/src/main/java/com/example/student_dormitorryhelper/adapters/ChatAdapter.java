package com.example.student_dormitorryhelper.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.student_dormitorryhelper.databinding.ItemConstainerReceivedMessageBinding;
import com.example.student_dormitorryhelper.databinding.ItemContainerSentMessageBinding;
import com.example.student_dormitorryhelper.models.ChatMessage;
import com.example.student_dormitorryhelper.utilities.UserManager;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

  private final List<ChatMessage> chatMessageList;
  private final String senderId;

  public static final int VIEW_TYPE_SENT = 1;
  public static final int VIEW_TYPE_RECEIVED = 2;

  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    if(viewType == VIEW_TYPE_SENT) {
      return new SentMessageViewHolder(
          ItemContainerSentMessageBinding.inflate(
              LayoutInflater.from(parent.getContext()),
              parent,
              false
          )
      );
    } else {
      return new ReceivedMessageViewHolder(
          ItemConstainerReceivedMessageBinding.inflate(
              LayoutInflater.from(parent.getContext()),
              parent,
              false
          )
      );
    }
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    if(getItemViewType(position) == VIEW_TYPE_SENT) {
      ((SentMessageViewHolder) holder).setData(chatMessageList.get(position));
    } else {
      ((ReceivedMessageViewHolder) holder).setData(chatMessageList.get(position), UserManager.getInstance().getSendToUser().fbId);
    }
  }

  @Override
  public int getItemCount() {
    return chatMessageList.size();
  }

  @Override
  public int getItemViewType(int position) {
    if(chatMessageList.get(position).senderId.equals(senderId)) {
      return VIEW_TYPE_SENT;
    }
    return VIEW_TYPE_RECEIVED;
  }

  public ChatAdapter(List<ChatMessage> chatMessageList, String senderId) {
    this.chatMessageList = chatMessageList;
    this.senderId = senderId;
  }

  static class SentMessageViewHolder extends RecyclerView.ViewHolder {

    private final ItemContainerSentMessageBinding binding;

    SentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding) {
      super(itemContainerSentMessageBinding.getRoot());
      binding = itemContainerSentMessageBinding;
    }

    void setData(ChatMessage chatMessage) {
      binding.textMassage.setText(chatMessage.message);
      binding.textDataTime.setText(chatMessage.dateTime);
    }
  }

  static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {

    private final ItemConstainerReceivedMessageBinding binding;

    ReceivedMessageViewHolder(ItemConstainerReceivedMessageBinding itemConstainerReceivedMessageBinding) {
      super(itemConstainerReceivedMessageBinding.getRoot());
      binding = itemConstainerReceivedMessageBinding;
    }

    void setData(ChatMessage chatMessage, String userFbId) {
      binding.textMassage.setText(chatMessage.message);
      binding.textDateTime.setText(chatMessage.dateTime);
      Picasso
          .get()
          .load("https://graph.facebook.com/"
              + userFbId
              + "/picture?type=large&access_token=")
          .fit()
          .centerCrop()
          .into(binding.imageProfile);
    }
  }
}
