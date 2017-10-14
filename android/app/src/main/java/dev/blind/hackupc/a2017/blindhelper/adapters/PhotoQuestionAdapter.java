package dev.blind.hackupc.a2017.blindhelper.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import dev.blind.hackupc.a2017.blindhelper.R;
import dev.blind.hackupc.a2017.blindhelper.model.Question;

public class PhotoQuestionAdapter extends RecyclerView.Adapter<PhotoQuestionAdapter.CustomViewHolder> {
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
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        Question question = questionsList.get(i);

        /*
        //Render image using Picasso library
        if (!TextUtils.isEmpty(question.getQuestionText())) {
            Picasso.with(mContext).load(question.getThumbnail())
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(customViewHolder.imageView);
        }
        */

        //Setting text view title
        customViewHolder.textView.setText(question.getQuestionText());
    }

    @Override
    public int getItemCount() {
        return (null != questionsList ? questionsList.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageView;
        protected TextView textView;

        public CustomViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.question_image);
            this.textView = (TextView) view.findViewById(R.id.question_text);
        }
    }
}