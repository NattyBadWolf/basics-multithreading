package com.artemchep.basics_multithreading.view;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.artemchep.basics_multithreading.R;
import com.artemchep.basics_multithreading.domain.Message;
import com.artemchep.basics_multithreading.domain.WithMillis;

public class MessageAdapterViewHolder extends RecyclerView.ViewHolder {
    private TextView mPlainText;
    private TextView mCipherText;
    private TextView mElapsedTimeText;

    MessageAdapterViewHolder(@NonNull View itemView) {
        super(itemView);
        mPlainText = itemView.findViewById(R.id.plainText);
        mCipherText = itemView.findViewById(R.id.cipherText);
        mElapsedTimeText = itemView.findViewById(R.id.elapsedTime);
    }

    void bind(@NonNull WithMillis<Message> message) {
        mPlainText.setText(message.value.plainText);

        if (message.value.cipherText != null) {
            mCipherText.setText(message.value.cipherText);
            mCipherText.setVisibility(View.VISIBLE);

            mElapsedTimeText.setText(message.elapsedMillis + " ms.");
            mElapsedTimeText.setVisibility(View.VISIBLE);
        } else {
            mCipherText.setVisibility(View.GONE);
            mElapsedTimeText.setVisibility(View.GONE);
        }
    }
}
