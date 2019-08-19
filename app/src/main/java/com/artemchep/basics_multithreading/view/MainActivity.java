package com.artemchep.basics_multithreading.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.view.View;

import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.artemchep.basics_multithreading.R;
import com.artemchep.basics_multithreading.cipher.CipherUtil;
import com.artemchep.basics_multithreading.domain.Message;
import com.artemchep.basics_multithreading.domain.WithMillis;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int WAT_SENT_TO_BG_THREAD = 24;
    private static final int WAT_RETURN_TO_UI_THREAD = 42;

    private List<WithMillis<Message>> mList = new ArrayList<>();

    private MessageAdapter mAdapter = new MessageAdapter(mList);

    private HandlerThread backgroundThread = new HandlerThread("bg_thread");
    private Handler backgroundThreadHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);

//        showWelcomeDialog();

        backgroundThread.start();
        backgroundThreadHandler = new Handler(backgroundThread.getLooper(), backgroundThreadCallback);
    }

    @Override
    protected void onDestroy() {
        backgroundThread.quit();
        super.onDestroy();
    }

    private void showWelcomeDialog() {
        new AlertDialog.Builder(this)
                .setMessage("What are you going to need for this task: Thread, Handler.\n" +
                        "\n" +
                        "1. The main thread should never be blocked.\n" +
                        "2. Messages should be processed sequentially.\n" +
                        "3. The elapsed time SHOULD include the time message spent in the queue.")
                .show();
    }

    public void onPushBtnClick(View view) {
        Message message = Message.generate();
        insert(new WithMillis<>(message));
    }

    @UiThread
    public void insert(final WithMillis<Message> message) {
        mList.add(message);
        mAdapter.notifyItemInserted(mList.size() - 1);

        backgroundThreadHandler.obtainMessage(WAT_SENT_TO_BG_THREAD, message).sendToTarget();
    }

    @UiThread
    public void update(final WithMillis<Message> message) {
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).value.key.equals(message.value.key)) {
                mList.set(i, message);
                mAdapter.notifyItemChanged(i);
                return;
            }
        }

        throw new IllegalStateException();
    }

    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what == WAT_RETURN_TO_UI_THREAD) {
                update((WithMillis<Message>) msg.obj);
            }
        }
    };

    private final Handler.Callback backgroundThreadCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(android.os.Message msg) {
            if (msg.what == WAT_SENT_TO_BG_THREAD) {
                long time = System.currentTimeMillis();

                WithMillis<Message> messageWithMillis = (WithMillis<Message>) msg.obj;
                Message message = messageWithMillis.value;

                final Message messageNew = message.copy(CipherUtil.encrypt(message.plainText));
                long elapsedTime = System.currentTimeMillis() - time;

                final WithMillis<Message> messageNewWithMillis = new WithMillis<>(messageNew, elapsedTime);

                mainThreadHandler.obtainMessage(WAT_RETURN_TO_UI_THREAD, messageNewWithMillis).sendToTarget();

                return true;
            }
            return false;
        }
    };
}
