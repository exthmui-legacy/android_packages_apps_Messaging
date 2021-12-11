package com.android.messaging.ui.appsettings;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.messaging.R;
import com.android.messaging.ui.BugleActionBarActivity;
import com.android.messaging.util.CaptchaKeywordsUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class CaptchaKeywordsSettingsActivity extends BugleActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(android.R.id.content, new CaptchaKeywordsSettingsFragment());
        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public static class CaptchaKeywordsSettingsFragment extends Fragment {

        private Context mContext;
        private CaptchaKeywordsUtils mCaptchaKeywordsUtils;

        public CaptchaKeywordsSettingsFragment() {
            // Required empty constructor
        }

        @Override
        public void onAttach(@NonNull Context context) {
            super.onAttach(context);
            this.mContext = context;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View mView = inflater.inflate(R.layout.captcha_keywords_settings_container, null, false);
            this.mCaptchaKeywordsUtils = new CaptchaKeywordsUtils(this.mContext);

            RecyclerView recyclerView = mView.findViewById(R.id.keywords_container);
            recyclerView.setLayoutManager(new LinearLayoutManager(this.mContext, LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(new KeywordsRVAdapter(this.mCaptchaKeywordsUtils.getKeywordsList()));

            FloatingActionButton mAddKeywordBtn = mView.findViewById(R.id.add_keyword_btn);
            mAddKeywordBtn.setOnClickListener(v -> {
                final EditText editText = new EditText(this.mContext);
                new AlertDialog.Builder(this.mContext)
                        .setTitle(R.string.add_captcha_keyword)
                        .setMessage(R.string.captcha_dialog_tip)
                        .setView(editText)
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            this.mCaptchaKeywordsUtils.addKeywords(editText);
                        })
                        .show();
            });
            return mView;
        }

        public TextView createKeywordsView() {
            TextView textView = new TextView(this.mContext);
            TypedValue typedValue = new TypedValue();
            this.mContext.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);
            textView.setBackgroundResource(typedValue.resourceId);
            textView.setClickable(true);
            textView.setTextSize(16);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setPadding(75, 36, 75, 36);
            textView.setTextColor(Color.BLACK);
            textView.setWidth(1080);
            textView.setMinHeight(156);
            return textView;
        }

        private class KeywordsRVAdapter extends RecyclerView.Adapter<KeywordsRVAdapter.KeywordsVH> {

            private List<String> mKeywordsList;

            public KeywordsRVAdapter(List<String> keywordsList) {
                this.mKeywordsList = keywordsList;
            }

            @NonNull
            @Override
            public KeywordsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new KeywordsVH(createKeywordsView());
            }

            @Override
            public void onBindViewHolder(@NonNull KeywordsVH holder, int position) {
                String keyword = this.mKeywordsList.get(position);
                holder.textView.setText(keyword);
                holder.textView.setOnLongClickListener(view -> {
                    new AlertDialog.Builder(holder.textView.getContext()).setTitle(R.string.dialog_delete_title)
                            .setMessage(R.string.dialog_delete_message)
                            .setIconAttribute(android.R.attr.alertDialogIcon)
                            .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                                mCaptchaKeywordsUtils.removeStringFromList(keyword);
                                notifyItemRemoved(position);
                            })
                            .setNegativeButton(android.R.string.cancel, null)
                            .show();
                    return true;
                });
            }

            @Override
            public int getItemCount() {
                return mKeywordsList.size();
            }

            private class KeywordsVH extends RecyclerView.ViewHolder {

                private final TextView textView;

                public KeywordsVH(@NonNull View itemView) {
                    super(itemView);
                    this.textView = (TextView) itemView;
                }
            }

        }

    }

}