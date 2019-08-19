package com.artemchep.basics_multithreading.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.artemchep.basics_multithreading.R;
import com.artemchep.basics_multithreading.domain.Message;
import com.artemchep.basics_multithreading.domain.WithMillis;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapterViewHolder> {
    private final List<WithMillis<Message>> mList;

    public MessageAdapter(List<WithMillis<Message>> list) {
        mList = list;
    }

    @NonNull
    @Override
    public MessageAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_message, parent, false);
        return new MessageAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapterViewHolder holder, int position) {
        WithMillis<Message> message = mList.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
