package com.example.pethome.storeapp.ui.animals.image;
import android.graphics.Bitmap;
import com.example.pethome.storeapp.data.local.models.AnimalImage;
public interface PhotoGridUserActionsListener {
    void onItemClicked(int itemPosition, AnimalImage animalImage, @AnimalImageContract.PhotoGridSelectModeDef String gridMode);
    void onItemLongClicked(int itemPosition, AnimalImage animalImage, @AnimalImageContract.PhotoGridSelectModeDef String gridMode);
    void showSelectedImage(Bitmap bitmap, AnimalImage animalImage);
}
