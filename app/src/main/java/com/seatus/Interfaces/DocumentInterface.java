package com.seatus.Interfaces;

import com.seatus.Models.FireStoreUserDocument;

/**
 * Created by rah on 22-Nov-17.
 */

public interface DocumentInterface {
    void onDocumentFetchSuccess(FireStoreUserDocument document);
}