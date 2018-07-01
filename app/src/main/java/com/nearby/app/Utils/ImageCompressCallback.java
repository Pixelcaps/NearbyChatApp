package com.nearby.app.Utils;

/**
 * Copyright © 2018 Deutsche Bank. All rights reserved.
 */
public interface ImageCompressCallback {

    void onCompressSuccess(String displayName, String messageBody, String messageContent, boolean fromUser);

    void onCompressError();
}
