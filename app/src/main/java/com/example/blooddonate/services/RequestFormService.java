package com.example.blooddonate.services;

import com.example.blooddonate.callbacks.DataFetchCallback;
import com.example.blooddonate.models.BloodDonationSite;
import com.example.blooddonate.models.RequestQuestionForm;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class RequestFormService {
    private FirebaseFirestore firestore;

    public RequestFormService() {
        firestore = FirebaseFirestore.getInstance();
    }

    public void getAllQuestions(DataFetchCallback<RequestQuestionForm> callback) {
        firestore.collection("requestForm")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<RequestQuestionForm> forms = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        RequestQuestionForm form = doc.toObject(RequestQuestionForm.class);
                        if (form != null) {
                            forms.add(form);
                        }
                    }
                    // Notify success with the fetched data
                    callback.onSuccess(forms);
                })
                .addOnFailureListener(e -> {
                    // Notify failure
                    callback.onFailure(e);
                });
    }

    public static boolean verifiedUser(List<RequestQuestionForm> ques) {
        for(RequestQuestionForm validate : ques) {
            if(!validate.isQualified()) {
                validate.setQualified(true);
                return false;
            }
        }
        return  true;
    }
}
