package com.datn.client.services;

import androidx.annotation.NonNull;

import com.datn.client.action.IApiCallable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ApiManager {
    private final ExecutorService executor = Executors.newFixedThreadPool(3); // TODO number

    private void goToLoginScreen() {

    }

    @NonNull
    private List<Callable<String>> convertToCallable(@NonNull List<IApiCallable> tasks) {
        List<Callable<String>> callableTasks = new ArrayList<>();
        for (IApiCallable task : tasks) {
            callableTasks.add(task::call);
        }
        return callableTasks;
    }

    public void callAPIsSafely(List<IApiCallable> tasks) {
        try {
            List<Future<String>> futures = executor.invokeAll(convertToCallable(tasks));
            for (Future<String> future : futures) {
                String result = future.get();
                System.out.println(future.isDone());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            goToLoginScreen();
        }
    }
}
