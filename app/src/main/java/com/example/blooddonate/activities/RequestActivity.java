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
import com.example.blooddonate.controllers.DonationSitesController;
import com.example.blooddonate.controllers.RequestFormController;
import com.example.blooddonate.controllers.UserController;
import com.example.blooddonate.models.BloodDonationSite;
import com.example.blooddonate.models.RequestQuestionForm;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.List;

public class RequestActivity extends AppCompatActivity {

    private TextView questionText, pageIndicator, skipText;
    private Button yesButton, noButton;

    RequestFormController requestFormController;

    DonationSitesController donationSitesController;

    UserController userController;

    private int currentQuestionIndex = 0;

    private boolean isValidated = true;

    private BloodDonationSite site;

    String siteUID;

    private List<RequestQuestionForm> questionList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_request);

        componentsInit();


    }

    private void setSiteUID(DataFetchCallback<String> callback) {
        donationSitesController.getSiteUiD(site, new DataFetchCallback<String>() {
            @Override
            public void onSuccess(List<String> data) {
                if (!data.isEmpty()) {
                    siteUID = data.get(0);
                    callback.onSuccess(data); // Pass the siteUID back through the callback
                } else {
                    callback.onFailure(new Exception("Site UID not found."));
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("RequestActivity", "Failed to fetch Site UID", e);
                callback.onFailure(e);
            }
        });
    }


    private void componentsInit() {
        questionText = findViewById(R.id.question_text);
        pageIndicator = findViewById(R.id.page_indicator);
        yesButton = findViewById(R.id.Yes_button);
        noButton = findViewById(R.id.No_button);
        skipText = findViewById(R.id.skip_text);

        site = getIntent().getParcelableExtra("site_data");

        requestFormController = new RequestFormController();
        donationSitesController = new DonationSitesController();
        userController = new UserController();

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
            loadNextQuestion();
        });

        // Handle No button click
        noButton.setOnClickListener(v -> {
            isValidated = false;
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
            if (isValidated) {
                // Retrieve Site UID before updating Firestore
                setSiteUID(new DataFetchCallback<String>() {
                    @Override
                    public void onSuccess(List<String> data) {
                        // Once siteUID is retrieved, update Firestore
                        Log.d("Request Activity", "Site UID: " + siteUID);
                        donationSitesController.updateDonationSite(siteUID, "registers", FieldValue.arrayUnion(userController.getUserId()));
                        Toast.makeText(RequestActivity.this, "All questions completed, you are qualified", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(RequestActivity.this, "Failed to retrieve Site UID.", Toast.LENGTH_SHORT).show();
                        Log.e("Request Activity", "Failed to retrieve Site UID.", e);
                    }
                });
            } else {
                Toast.makeText(this, "You are not qualified", Toast.LENGTH_SHORT).show();
                finish(); // End activity
            }
        }
    }

}