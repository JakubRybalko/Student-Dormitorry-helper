package com.example.student_dormitorryhelper.ui.chat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.student_dormitorryhelper.R;
import com.example.student_dormitorryhelper.databinding.FragmentChatBinding;
import com.example.student_dormitorryhelper.utilities.Constants;
import com.example.student_dormitorryhelper.utilities.UserManager;
import com.facebook.AccessToken;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private TextView nameText;
    private UserManager userManager;
    private FirebaseFirestore database;

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ChatViewModel chatViewModel =
                new ViewModelProvider(this).get(ChatViewModel.class);
        binding = FragmentChatBinding.inflate(inflater, container, false);
        userManager = UserManager.getInstance();
        nameText = binding.textName;
        database = FirebaseFirestore.getInstance();
        setListeners();
        loadUserDetails();
        getToken();
        setChangeUserButton();

        return binding.getRoot();
    }

    private void setChangeUserButton() {
        if (userManager.getFbUserId().equals(Constants.KEY_RESIDENT_COUNCIL_USER_ID)) {
            binding.buttonAddChat.setVisibility(View.VISIBLE);
        } else {
            binding.buttonAddChat.setVisibility(View.INVISIBLE);
        }
    }

    private void setListeners() {
        binding.buttonAddChat.setOnClickListener(v -> {
            UsersFragment usersFragment= new UsersFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, usersFragment)
                .addToBackStack(null)
                .commit();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadUserDetails() {
        nameText.setText(userManager.getFullName());
        Picasso
            .get()
            .load("https://graph.facebook.com/"
                + userManager.getFbUserId()
                + "/picture?type=large&access_token="
                + AccessToken.getCurrentAccessToken().getToken())
            .fit()
            .centerCrop()
            .into(binding.imageProfile);
    }

    private void showToast(String msg) {
        Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token) {
        DocumentReference documentReference =
            database.collection(Constants.KEY_COLLECTION_USERS)
                .document(userManager.getFbUserId());
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
            .addOnFailureListener(e -> showToast("Unable to update token"));
    }
}