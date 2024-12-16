package com.example.blooddonate.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.blooddonate.R;
import com.example.blooddonate.callbacks.DataFetchCallback;
import com.example.blooddonate.controllers.RequestFormController;
import com.example.blooddonate.models.RequestQuestionForm;

import java.util.ArrayList;
import java.util.List;

public class RequestActivity extends AppCompatActivity {

    private TextView questionText, pageIndicator, skipText;
    private Button yesButton, noButton;

    RequestFormController requestFormController;

    private int currentQuestionIndex = 0;

    private List<RequestQuestionForm> questionList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_request);

        componentsInit();


    }

    private void componentsInit() {
        questionText = findViewById(R.id.question_text);
        pageIndicator = findViewById(R.id.page_indicator);
        yesButton = findViewById(R.id.Yes_button);
        noButton = findViewById(R.id.No_button);
        skipText = findViewById(R.id.skip_text);

        requestFormController = new RequestFormController();
        getQuestionList();

    }

    private void displayNoQuestionsAvailable() {
        // Default placeholder card when no data exists
        questionText.setText("No questions available.");
        pageIndicator.setText("");
        yesButton.setVisibility(View.GONE);
        noButton.setVisibility(View.GONE);
        skipText.setVisibility(View.GONE);
    }

    private void getQuestionList() {
        requestFormController.getAllQuestions(new DataFetchCallback<RequestQuestionForm>() {
            @Override
            public void onSuccess(List<RequestQuestionForm> data) {
                for(RequestQuestionForm tmp : data) {
                    Log.d("Request Activity", "Question: " + tmp.getQuestion());
                    questionList.add(tmp);
                }

                if(!questionList.isEmpty()) {
                    displayQuestion();
                } else {
                    displayNoQuestionsAvailable();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("Request Activity", "Fail to fetch data");
            }
        });
    }

    private void displayQuestion() {
        // Update the question, page indicator, and button listeners
        RequestQuestionForm currentQuestion = questionList.get(currentQuestionIndex);

        questionText.setText(currentQuestion.getQuestion());
        pageIndicator.setText((currentQuestionIndex + 1) + "/" + questionList.size());

        // Handle Yes button click
        yesButton.setOnClickListener(v -> {

            //update in question list
            currentQuestion.setQualified(true);
            questionList.remove(currentQuestion);
            questionList.add(currentQuestion);
            loadNextQuestion();
        });

        // Handle No button click
        noButton.setOnClickListener(v -> {
            currentQuestion.setQualified(false);
            questionList.remove(currentQuestion);
            questionList.add(currentQuestion);
            loadNextQuestion();
        });

        // Handle Skip text
        skipText.setOnClickListener(v -> {
            currentQuestion.setQualified(false);
            questionList.remove(currentQuestion);
            questionList.add(currentQuestion);
            loadNextQuestion();
        });
    }

    private void loadNextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < questionList.size()) {
            displayQuestion();
        } else {
            if(requestFormController.verifiedUser(questionList)) {
                Toast.makeText(this, "All questions completed, you are qualifeid", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "You are not qualified", Toast.LENGTH_SHORT).show();
            }

            finish(); // End activity or navigate to another screen
        }
    }
}