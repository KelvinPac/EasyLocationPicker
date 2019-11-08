package com.loginext.easylocationpicker;

public interface EasyLocationCallbacks {
    void onSuccess(SelectedLocation location);

    void onFailed(String reason);
}
