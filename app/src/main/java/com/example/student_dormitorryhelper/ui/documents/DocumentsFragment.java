package com.example.student_dormitorryhelper.ui.documents;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.student_dormitorryhelper.adapters.DocumentsAdapter;
import com.example.student_dormitorryhelper.databinding.FragmentDocumentsBinding;
import com.example.student_dormitorryhelper.listeners.DocumentListener;
import com.example.student_dormitorryhelper.models.Document;
import com.example.student_dormitorryhelper.utilities.Constants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DocumentsFragment extends Fragment implements DocumentListener {

    private FragmentDocumentsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DocumentsViewModel documentsViewModel =
                new ViewModelProvider(this).get(DocumentsViewModel.class);

        binding = FragmentDocumentsBinding.inflate(inflater, container, false);
        getDocuments();

        return binding.getRoot();
    }

    private void getDocuments() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database
            .collection(Constants.KEY_COLLECTION_DOCUMENTS)
            .get()
            .addOnCompleteListener(task -> {
               loading(false);
               if (task.isSuccessful() && task.getResult() != null  && task.getResult().size() > 0) {
                   List<Document> documents = new ArrayList<>();
                   for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                       Document document = new Document();
                       document.documentName = queryDocumentSnapshot.getString(Constants.KEY_DOCUMENT_NAME);
                       document.url = queryDocumentSnapshot.getString(Constants.KEY_DOCUMENT_URL);
                       document.pathName = queryDocumentSnapshot.getString(Constants.KEY_DOCUMENT_PATH_NAME);
                       documents.add(document);
                   }
                   if (documents.size() > 0) {
                       DocumentsAdapter documentsAdapter = new DocumentsAdapter(documents, this);
                       binding.documentsRecyclerView.setAdapter(documentsAdapter);
                       binding.documentsRecyclerView.setVisibility(View.VISIBLE);
                   } else {
                       showErrorMessage();
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
        binding.textErrorMessage.setText(String.format("%s", "No documents available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDocumentClicked(Document document) {
        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(document.url)));
        }

    }
}