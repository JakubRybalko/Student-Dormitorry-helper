package com.example.student_dormitorryhelper.utilities;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.student_dormitorryhelper.models.FacebookPost;
import com.example.student_dormitorryhelper.models.User;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class UserManager {
    private String firstName;
    private String secondName;
    private String fullName;
    private String id;
    private AccessToken accessToken;
    private User sendToUser;
    private List<FacebookPost> posts;

    private static UserManager userManager = new UserManager();
    public static UserManager getInstance() {
        if (userManager == null) {
            userManager = new UserManager();
        }
        return userManager;
    }

    public void setSendToUser(User sendToUser) {
        this.sendToUser = sendToUser;
    }

    public User getSendToUser() {
        return this.sendToUser;
    }

    public void setUserData() {
        accessToken = AccessToken.getCurrentAccessToken();
        setLoggedUserData();
        setFbPagePosts();
    }

    private void setLoggedUserData() {
        GraphRequest request = GraphRequest.newMeRequest(
            accessToken,
            new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(
                    JSONObject object,
                    GraphResponse response) {
                    try {
                        firstName = object.getString("first_name");
                        secondName = object.getString("last_name");
                        fullName = object.getString("name");
                        id = object.getString("id");
                        updateDatabase();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, name, first_name, last_name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void setFbPagePosts() {
        if (!this.id.equals(Constants.KEY_RESIDENT_COUNCIL_USER_ID)) { return; }

        GraphRequest graphRequest = new GraphRequest(
            accessToken,
            "/" + Constants.KEY_FB_PAGE_ID + "/feed",
            null,
            HttpMethod.GET,
            new GraphRequest.Callback() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                public void onCompleted(GraphResponse response) {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        String json = Objects.requireNonNull(response.getJSONObject()).getString("data");
                        posts = mapper.readValue(json,
                            mapper.getTypeFactory().constructCollectionType(List.class, FacebookPost.class));
                        posts = posts.stream()
                            .filter(i -> i.getMessage() != null
                                && i.getFrom() != null && i.getFrom().get("name").equals(Constants.KEY_RESIDENT_COUNCIL_POSTS_NAME))
                            .collect(Collectors.toList());
                        if(posts.size() > 0) {
                            FirebaseFirestore database = FirebaseFirestore.getInstance();
                            posts.forEach(it -> {
                                HashMap<String, Object> post = new HashMap<>();
                                post.put(Constants.KEY_FB_POST_ID, it.getId());
                                post.put(Constants.KEY_FB_POST_CREATED_TIME, it.getCreatedTime());
                                post.put(Constants.KEY_FB_POST_FULL_PICTURE, it.getFull_picture());
                                post.put(Constants.KEY_FB_POST_MESSAGE, it.getMessage());
                                database.collection(Constants.KEY_COLLECTION_FB_POSTS).document(it.getId()).set(post);
                            });
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        Log.d("FB", e.toString());
                    }
                }
            }
        );

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, created_time, message, full_picture, from");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }

    private void updateDatabase() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_ID, this.id);
        user.put(Constants.KEY_FIRST_NAME, this.firstName);
        user.put(Constants.KEY_SECOND_NAME, this.secondName);
        database.collection(Constants.KEY_COLLECTION_USERS).document(this.id).set(user);
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getSecondName() {
        return this.secondName;
    }

    public String getFullName() {
        return this.fullName;
    }

    public String getFbUserId() {
        return this.id;
    }


}
