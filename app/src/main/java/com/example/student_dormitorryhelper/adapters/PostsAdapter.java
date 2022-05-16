package com.example.student_dormitorryhelper.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.student_dormitorryhelper.databinding.ItemContainerDocumentsBinding;
import com.example.student_dormitorryhelper.databinding.ItemContainerFbPostBinding;
import com.example.student_dormitorryhelper.models.Document;
import com.example.student_dormitorryhelper.models.FacebookPost;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.security.acl.Group;
import java.text.SimpleDateFormat;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder>{

  private final List<FacebookPost> posts;

  @NonNull
  @Override
  public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    ItemContainerFbPostBinding itemContainerFbPostBinding = ItemContainerFbPostBinding.inflate(
        LayoutInflater.from(parent.getContext()),
        parent,
        false
    );
    return new PostViewHolder(itemContainerFbPostBinding);
  }

  @Override
  public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
    holder.setPostsData(posts.get(position));
  }

  @Override
  public int getItemCount() {
    return posts.size();
  }

  public PostsAdapter(List<FacebookPost> posts) {
    this.posts = posts;
  }

  class PostViewHolder extends RecyclerView.ViewHolder {

    ItemContainerFbPostBinding binding;

    PostViewHolder(ItemContainerFbPostBinding itemContainerFbPostBinding) {
      super(itemContainerFbPostBinding.getRoot());
      binding = itemContainerFbPostBinding;
    }

    void setPostsData(FacebookPost facebookPost) {
      SimpleDateFormat simpleDate =  new SimpleDateFormat("dd/MM/yyyy");
      binding.messageDate.setText(simpleDate.format(facebookPost.getCreatedTime()));
//      binding.messageDate.setText("XDDD");
      binding.messageText.setText(facebookPost.getMessage());

      Picasso.get()
          .load(facebookPost.getFull_picture())
          .into(binding.postImage, new Callback() {
            @Override
            public void onSuccess() {
              binding.postImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }

            @Override
            public void onError(Exception e) {

            }
          });
    }

  }
}
