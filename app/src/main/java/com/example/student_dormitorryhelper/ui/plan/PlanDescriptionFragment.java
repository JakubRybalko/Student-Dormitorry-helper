package com.example.student_dormitorryhelper.ui.plan;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.student_dormitorryhelper.R;
import com.example.student_dormitorryhelper.adapters.DocumentsAdapter;
import com.example.student_dormitorryhelper.databinding.FragmentPlanDescriptionBinding;
import com.example.student_dormitorryhelper.models.Document;
import com.example.student_dormitorryhelper.ui.chat.UsersFragment;
import com.example.student_dormitorryhelper.utilities.Constants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PlanDescriptionFragment extends Fragment {

  private FragmentPlanDescriptionBinding binding;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    binding = FragmentPlanDescriptionBinding.inflate(inflater, container, false);
    setListeners();
    return binding.getRoot();
  }

  public void initData(String title) {
    loading(true);
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    database
        .collection(Constants.KEY_COLLECTION_PLAN)
        .get()
        .addOnCompleteListener(task -> {
          loading(false);
          if (task.isSuccessful() && task.getResult() != null  && task.getResult().size() > 0) {
            for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
              if(queryDocumentSnapshot.getString(Constants.KEY_PLAN_TITLE).equals(title)) {
                binding.title.setText(queryDocumentSnapshot.getString(Constants.KEY_PLAN_TITLE));
                binding.descriptionText.setText(queryDocumentSnapshot.getString(Constants.KEY_PLAN_DESCRIPTION));
                binding.localizationDescription.setText(queryDocumentSnapshot.getString(Constants.KEY_PLAN_LOCALIZATION)
                    .replace("|n", System.getProperty("line.separator")));
                binding.localization.setVisibility(View.VISIBLE);

                if(!queryDocumentSnapshot.getString(Constants.KEY_PLAN_KEEPER).isEmpty()) {
                  binding.keeperDescription.setText(queryDocumentSnapshot.getString(Constants.KEY_PLAN_KEEPER)
                      .replace("|n", System.getProperty("line.separator")));
                  binding.keeper.setVisibility(View.VISIBLE);
                }
              }
            }
          } else {
            showErrorMessage();
          }
        });

  }

  private void loading(Boolean isLoading) {
    if(isLoading) {
      binding.progressBar.setVisibility(View.VISIBLE);
    } else {
      binding.progressBar.setVisibility(View.INVISIBLE);
    }
  }

  private void showErrorMessage() {
    binding.textErrorMessage.setText(String.format("%s", "Get data from database error"));
    binding.textErrorMessage.setVisibility(View.VISIBLE);
  }

  private void setListeners() {
    binding.closeDescription.setOnClickListener(v -> {
      PlanFragment planFragment = (PlanFragment) getParentFragment();
      planFragment.setVisibleGone();

      binding.title.setText("");
      binding.descriptionText.setText("");
      binding.keeperDescription.setText("");
      binding.localizationDescription.setText("");
      binding.localization.setVisibility(View.GONE);
      binding.keeper.setVisibility(View.GONE);
    });
  }
}