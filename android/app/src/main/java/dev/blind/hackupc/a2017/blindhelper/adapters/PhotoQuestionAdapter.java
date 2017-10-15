package dev.blind.hackupc.a2017.blindhelper.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.util.List;

import dev.blind.hackupc.a2017.blindhelper.PhotoAnswerActivity;
import dev.blind.hackupc.a2017.blindhelper.R;
import dev.blind.hackupc.a2017.blindhelper.controllers.BackendController;
import dev.blind.hackupc.a2017.blindhelper.model.Question;

public class PhotoQuestionAdapter extends RecyclerView.Adapter<PhotoQuestionAdapter.CustomViewHolder> implements BackendController.ResponseServerCallback {
    private List<Question> questionsList;
    private Context mContext;

    public PhotoQuestionAdapter(Context context, List<Question> questionsList) {
        this.questionsList = questionsList;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_photoanswer_fragment, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, int i) {
        final Question question = questionsList.get(i);

        //Setting text view title
        customViewHolder.textView.setText(question.getQuestionText());
        customViewHolder.button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.e("TT", customViewHolder.editView.getText().toString());
                if (!customViewHolder.editView.getText().toString().equals("")) {
                    String text = customViewHolder.editView.getText().toString();

                    //BackendController.addAnswer("usuario666", question.getId(), text, this);
                    ((PhotoAnswerActivity)mContext).addAnswer("usuario666", question.getId(), text);

                    DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();

                    TextView answerTextView = new TextView(mContext);
                    answerTextView.setText(text);
                    answerTextView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.shape_bg_outgoing_bubble));
                    answerTextView.setPadding((int)(metrics.density*10+0.5f),(int)(metrics.density*7+0.5f),
                            (int)(metrics.density*20+0.5f), (int)(metrics.density*7+0.5f));
                    answerTextView.setTextColor(Color.WHITE);
                    answerTextView.setTextSize(18);
                    answerTextView.setTypeface(answerTextView.getTypeface(), Typeface.BOLD);

                    LinearLayout lLayout = new LinearLayout(mContext);
                    lLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT));
                    lLayout.setOrientation(LinearLayout.VERTICAL);
                    lLayout.setPadding((int)(metrics.density*30+0.5f),(int)(metrics.density*0+0.5f),
                            (int)(metrics.density*0+0.5f), (int)(metrics.density*10+0.5f));
                    lLayout.addView(answerTextView);

                    customViewHolder.linearLayout.addView(lLayout);
                    customViewHolder.editView.setText("");
                }
            }
        });

        Picasso.with(mContext).load(BackendController.GET_QUESTION_URL + question.getId()).into(customViewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return (null != questionsList ? questionsList.size() : 0);
    }

    @Override
    public void onResponseServer(String petition, String id, String text) {
        if (petition.equals(BackendController.ADD_ANSWER_URL)) {
        }
    }

    @Override
    public void onResponseGetAnswer(JSONArray jsonArray) {

    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageView;
        protected TextView textView;
        protected EditText editView;
        protected Button button;
        protected LinearLayout linearLayout;

        public CustomViewHolder(View view) {
            super(view);
            this.imageView = view.findViewById(R.id.question_image);
            this.textView = view.findViewById(R.id.question_text);
            this.editView = view.findViewById(R.id.question_answer_text);
            this.button = view.findViewById(R.id.question_answer_send_button);
            this.linearLayout = view.findViewById(R.id.question_answers_layout);
        }
    }
}