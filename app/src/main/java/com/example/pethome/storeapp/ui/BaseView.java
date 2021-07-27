

package com.example.pethome.storeapp.ui;

public interface BaseView<T extends BasePresenter> {
    void setPresenter(T presenter);
}
