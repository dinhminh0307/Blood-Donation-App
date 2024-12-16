package com.example.blooddonate.controllers;

import com.example.blooddonate.callbacks.DataFetchCallback;
import com.example.blooddonate.models.RequestQuestionForm;
import com.example.blooddonate.services.RequestFormService;

import java.util.List;

public class RequestFormController {
    RequestFormService requestFormService;

    public RequestFormController() {
        this.requestFormService = new RequestFormService();
    }

    public void getAllQuestions(DataFetchCallback<RequestQuestionForm> callback) {
        requestFormService.getAllQuestions(callback);
    }

    public boolean verifiedUser(List<RequestQuestionForm> ques) {
        return this.requestFormService.verifiedUser(ques);
    }
}
