package com.example.student_dormitorryhelper.ui.home;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.student_dormitorryhelper.adapters.PostsAdapter;
import com.example.student_dormitorryhelper.adapters.UsersAdapter;
import com.example.student_dormitorryhelper.databinding.FragmentHomeBinding;
import com.example.student_dormitorryhelper.models.FacebookPost;
import com.example.student_dormitorryhelper.models.User;
import com.example.student_dormitorryhelper.utilities.Constants;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private List<FacebookPost> posts;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);

//        loadData();
        loadDataFirebase();

        return binding.getRoot();
    }

    void loadDataFirebase() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database
            .collection(Constants.KEY_COLLECTION_FB_POSTS)
            .get()
            .addOnCompleteListener(task -> {
                loading(false);
                if(task.isSuccessful() && task.getResult() != null) {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                        FacebookPost facebookPost = new FacebookPost();
                        facebookPost.setId(queryDocumentSnapshot.getString(Constants.KEY_FB_POST_ID));
                        facebookPost.setMessage(queryDocumentSnapshot.getString(Constants.KEY_FB_POST_MESSAGE));
                        facebookPost.setCreatedTime(queryDocumentSnapshot.getDate(Constants.KEY_FB_POST_CREATED_TIME));
                        facebookPost.setFull_picture(queryDocumentSnapshot.getString(Constants.KEY_FB_POST_FULL_PICTURE));
                        posts.add(facebookPost);
                    }
                    if(posts.size() > 0) {
                        PostsAdapter postsAdapter = new PostsAdapter(posts);
                        binding.postsRecyclerView.setAdapter(postsAdapter);
                        binding.postsRecyclerView.setVisibility(View.VISIBLE);
                    } else {
                        showErrorMessage();
                    }
                } else {
                    showErrorMessage();
                }
            });
    }

    void loadData() {
        loading(true);
        GraphRequest graphRequest = new GraphRequest(
            AccessToken.getCurrentAccessToken(),
            "/" + Constants.KEY_FB_PAGE_ID + "/feed",
            null,
            HttpMethod.GET,
            new GraphRequest.Callback() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                public void onCompleted(GraphResponse response) {
                    loading(false);
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        String json = Objects.requireNonNull(response.getJSONObject()).getString("data");
                        posts = mapper.readValue(json,
                            mapper.getTypeFactory().constructCollectionType(List.class, FacebookPost.class));
                        posts = posts.stream()
                            .filter(i -> i.getMessage() != null
                                && i.getFrom() != null && i.getFrom().get("name").equals(Constants.KEY_RESIDENT_COUNCIL_POSTS_NAME))
                            .collect(Collectors.toList());
                        Log.d("FB", posts.get(0).getFull_picture());
                        if(posts.size() > 0) {
                            PostsAdapter postsAdapter = new PostsAdapter(posts);
                            binding.postsRecyclerView.setAdapter(postsAdapter);
                            binding.postsRecyclerView.setVisibility(View.VISIBLE);
                        } else {
                            showErrorMessage();
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        Log.d("FB", e.toString());
                        showErrorMessage();
                    }
                }
            }
        );

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, created_time, message, full_picture, from");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }

    private void loading(Boolean isLoading) {
        if(isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void showErrorMessage() {
        binding.textErrorMessage.setText(String.format("%s", "No posts available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}