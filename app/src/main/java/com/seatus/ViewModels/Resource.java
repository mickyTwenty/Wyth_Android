package com.seatus.ViewModels;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.seatus.ViewModels.Resource.Status.error;
import static com.seatus.ViewModels.Resource.Status.loading;
import static com.seatus.ViewModels.Resource.Status.success;

/**
 * Created by rohail on 27-Oct-17.
 */

public class Resource<T> {

    @NonNull
    public final Status status;
    @Nullable
    public final T data;

    private Resource(@NonNull Status status, @Nullable T data) {
        this.status = status;
        this.data = data;
    }

    public static <T> Resource<T> response(Status status, @NonNull T data) {
        return new Resource<>(status, data);
    }

    public static <T> Resource<T> loading() {
        return new Resource<>(loading, null);
    }

    public enum Status {
        initial,
        loading,
        success,
        error,
        connection_error,
        action_validation,
        action_need_signup,
        action_card_not_added,
        add_bank_account
    }
}