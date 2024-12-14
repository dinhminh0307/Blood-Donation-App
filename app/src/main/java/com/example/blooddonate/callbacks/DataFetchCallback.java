package com.example.blooddonate.callbacks;

import java.util.List;

public interface DataFetchCallback<T> {
    void onSuccess(List<T> data);
    void onFailure(Exception e);
}
