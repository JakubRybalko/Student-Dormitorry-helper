package com.example.student_dormitorryhelper.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.student_dormitorryhelper.databinding.ItemContainerDocumentsBinding;
import com.example.student_dormitorryhelper.listeners.DocumentListener;
import com.example.student_dormitorryhelper.models.Document;

import java.util.List;

public class DocumentsAdapter extends RecyclerView.Adapter<DocumentsAdapter.DocumentViewHolder> {

  private final List<Document> documents;
  private final DocumentListener documentListener;

  public DocumentsAdapter(List<Document> documents, DocumentListener documentListener) {
    this.documents = documents;
    this.documentListener = documentListener;
  }

  @NonNull
  @Override
  public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    ItemContainerDocumentsBinding itemContainerDocumentsBinding = ItemContainerDocumentsBinding.inflate(
        LayoutInflater.from(parent.getContext()),
        parent,
        false
    );
    return new DocumentViewHolder(itemContainerDocumentsBinding);
  }

  @Override
  public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
    holder.setDocumentData(documents.get(position));
  }

  @Override
  public int getItemCount() {
    return documents.size();
  }

  class DocumentViewHolder extends RecyclerView.ViewHolder {

    ItemContainerDocumentsBinding binding;

    DocumentViewHolder(ItemContainerDocumentsBinding itemContainerDocumentsBinding) {
      super(itemContainerDocumentsBinding.getRoot());
      binding = itemContainerDocumentsBinding;
    }

    void setDocumentData(Document document) {
      binding.documentName.setText(document.documentName);
      binding.getRoot().setOnClickListener(v -> documentListener.onDocumentClicked(document));
    }
  }
}
