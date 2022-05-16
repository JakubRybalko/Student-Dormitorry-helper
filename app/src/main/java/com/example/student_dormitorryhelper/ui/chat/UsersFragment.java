package com.example.student_dormitorryhelper.ui.chat;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.student_dormitorryhelper.R;
import com.example.student_dormitorryhelper.adapters.UsersAdapter;
import com.example.student_dormitorryhelper.databinding.FragmentUsersBinding;
import com.example.student_dormitorryhelper.listeners.UserListener;
import com.example.student_dormitorryhelper.models.User;
import com.example.student_dormitorryhelper.utilities.Constants;
import com.example.student_dormitorryhelper.utilities.UserManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment implements UserListener {

  private FragmentUsersBinding binding;
  private UserManager userManager;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    binding = FragmentUsersBinding.inflate(getLayoutInflater());
    userManager = UserManager.getInstance();
    getUsers();
    setListeners();

    return binding.getRoot();
  }

  private void setListeners() {
    binding.imageBack.setOnClickListener(view -> {
      ChatFragment chatFragment= new ChatFragment();
      getActivity().getSupportFragmentManager().beginTransaction()
          .replace(R.id.nav_host_fragment_activity_main, chatFragment)
          .addToBackStack(null)
          .commit();
//    getParentFragmentManager().popBackStackImmediate();
    });
  }

  private void getUsers() {
    loading(true);
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    database
        .collection(Constants.KEY_COLLECTION_USERS)
        .get()
        .addOnCompleteListener(task -> {
          loading(false);
          String currentUserId = userManager.getFbUserId();
          if(task.isSuccessful() && task.getResult() != null) {
            List<User> users = new ArrayList<>();
            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
              if(queryDocumentSnapshot.getString(Constants.KEY_ID).equals(currentUserId)) {
                continue;
              }
              User user = new User();
              user.firstName = queryDocumentSnapshot.getString(Constants.KEY_FIRST_NAME);
              user.secondName = queryDocumentSnapshot.getString(Constants.KEY_SECOND_NAME);
              user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
              user.fbId = queryDocumentSnapshot.getString(Constants.KEY_ID);
              users.add(user);
            }
            if(users.size() > 0) {
              UsersAdapter usersAdapter = new UsersAdapter(users, this);
              binding.usersRecyclerView.setAdapter(usersAdapter);
              binding.usersRecyclerView.setVisibility(View.VISIBLE);
            } else {
              showErrorMessage();
            }
          } else {
            showErrorMessage();
          }
        });
  }

  private void showErrorMessage() {
    binding.textErrorMessage.setText(String.format("%s", "No user available"));
    binding.textErrorMessage.setVisibility(View.VISIBLE);
  }

  private void loading(Boolean isLoading) {
    if(isLoading) {
      binding.progressBar.setVisibility(View.VISIBLE);
    } else {
      binding.progressBar.setVisibility(View.INVISIBLE);
    }
  }

  @Override
  public void onUserClicked(User user) {
    userManager.setSendToUser(user);
    ChatFragment chatFragment= new ChatFragment();
    getActivity().getSupportFragmentManager().beginTransaction()
        .replace(R.id.nav_host_fragment_activity_main, chatFragment)
        .addToBackStack(null)
        .commit();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}