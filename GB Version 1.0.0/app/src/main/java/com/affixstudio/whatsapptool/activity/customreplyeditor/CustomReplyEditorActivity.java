package com.affixstudio.whatsapptool.activity.customreplyeditor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.activity.BaseActivity;
import com.affixstudio.whatsapptool.activity.autoReplyTest;
import com.affixstudio.whatsapptool.model.CustomRepliesData;
import com.affixstudio.whatsapptool.model.preferences.PreferencesManager;
import com.affixstudio.whatsapptool.viewmodel.SwipeToKillAppDetectViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.zip.Inflater;

public class CustomReplyEditorActivity extends BaseActivity {
    TextInputEditText autoReplyText;
    Button saveAutoReplyTextBtn;
    CustomRepliesData customRepliesData;
    PreferencesManager preferencesManager;
    Button watoMessageLinkBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_reply_editor);

        setTitle(R.string.mainAutoReplyLabel);

        new ViewModelProvider(this).get(SwipeToKillAppDetectViewModel.class);

        customRepliesData = CustomRepliesData.getInstance(this);
        preferencesManager = PreferencesManager.getPreferencesInstance(this);

        autoReplyText = findViewById(R.id.autoReplyTextInputEditText);
        saveAutoReplyTextBtn = findViewById(R.id.saveCustomReplyBtn);
        watoMessageLinkBtn = findViewById(R.id.tip_wato_message);

        RecyclerView recyclerViewQuick=findViewById(R.id.recyclerViewQuick);

        String [] quickMessage=getResources().getStringArray(R.array.autoQuickMessage);


        recyclerViewQuick.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.quick_ms_cat_list,null)) {
                    @Override
                    public String toString() {
                        return super.toString();
                    }
                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

               TextView messages= holder.itemView.findViewById(R.id.messages);
               messages.setText(quickMessage[position]);
               messages.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       autoReplyText.setText(autoReplyText.getText().toString()+" "+messages.getText().toString());
                   }
               });
            }

            @Override
            public int getItemCount() {
                return quickMessage.length;
            }
        });


        findViewById(R.id.testAutoBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(CustomReplyEditorActivity.this, autoReplyTest.class);
               // Bundle b= ActivityOptions.makeSceneTransitionAnimation(CustomReplyEditorActivity.this).toBundle();
                startActivity(i);
            }
        });


        Intent intent = getIntent();
        Uri data = intent.getData();

        autoReplyText.setText((data != null)
                ? data.getQueryParameter("message")
                : customRepliesData.get());

        autoReplyText.requestFocus();
        autoReplyText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Disable save button if text does not satisfy requirements
                saveAutoReplyTextBtn.setEnabled(CustomRepliesData.isValidCustomReply(editable));
            }
        });

        saveAutoReplyTextBtn.setOnClickListener(view -> {
            String setString = customRepliesData.set(autoReplyText.getText());
            if (setString != null) {
                this.onNavigateUp();
            }
        });

        watoMessageLinkBtn.setOnClickListener(view -> {
            String url = getString(R.string.watomatic_wato_message_url);
            startActivity(
                    new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url))
            );
        });

    }
}