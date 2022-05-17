package com.example.student_dormitorryhelper.adapters;

import android.icu.number.CompactNotation;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.student_dormitorryhelper.databinding.ItemContainerUserBinding;
import com.example.student_dormitorryhelper.listeners.UserListener;
import com.example.student_dormitorryhelper.models.User;
import com.example.student_dormitorryhelper.utilities.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder>{

  private final List<User> users;
  private final UserListener userListener;

  public UsersAdapter(List<User> users, UserListener userListener) {
    this.users = users;
    this.userListener = userListener;
  }

  @NonNull
  @Override
  public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding.inflate(
        LayoutInflater.from(parent.getContext()),
        parent,
        false
    );
    return new UserViewHolder(itemContainerUserBinding);
  }

  @Override
  public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
    holder.setUserData(users.get(position));
  }

  @Override
  public int getItemCount() {
    return users.size();
  }

  class UserViewHolder extends RecyclerView.ViewHolder {

    ItemContainerUserBinding binding;

    UserViewHolder(ItemContainerUserBinding itemContainerUserBinding) {
      super(itemContainerUserBinding.getRoot());
      binding = itemContainerUserBinding;
    }

    void setUserData(User user) {
      binding.textFirstName.setText(user.firstName);
      binding.textSecondName.setText(user.secondName);
      Picasso
          .get()
          .load("https://graph.facebook.com/"
              + user.fbId
              + "/picture?type=small&access_token=")
          .fit()
          .centerCrop()
          .into(binding.imageProfile);
      binding.getRoot().setOnClickListener(v -> userListener.onUserClicked(user));
    }
  }
}
