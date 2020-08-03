package com.seatus.Models;

/**
 * Created by rohail on 30-Oct-17.
 */

public class DocumentItem {

    public String absolute_url;

    public transient String id;
    public transient boolean isLocal = false;

    public DocumentItem(String absolute_url) {
        this.absolute_url = absolute_url;
    }

    public DocumentItem(String absolute_url, boolean isLocal) {
        this.absolute_url = absolute_url;
        this.isLocal = isLocal;
    }
}
