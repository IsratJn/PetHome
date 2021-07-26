

package com.example.pethome.storeapp.data.local.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;


public class Animal implements Parcelable {


    public static final Creator<Animal> CREATOR = new Creator<Animal>() {

        @Override
        public Animal createFromParcel(Parcel in) {
            return new Animal(in);
        }


        @Override
        public Animal[] newArray(int size) {
            return new Animal[size];
        }
    };
    private final int mId;
    private String mName;
    private String mSku;
    private String mDescription;
    private String mCategory;
    private ArrayList<AnimalImage> mAnimalImages;
    private ArrayList<AnimalAttribute> mAnimalAttributes;


    private Animal(int id, String name, String sku, String description, String category,
                    ArrayList<AnimalImage> animalImages,
                    ArrayList<AnimalAttribute> animalAttributes) {
        mId = id;
        mName = name;
        mSku = sku;
        mDescription = description;
        mCategory = category;
        mAnimalImages = animalImages;
        mAnimalAttributes = animalAttributes;
    }


    protected Animal(Parcel in) {
        mId = in.readInt();
        mName = in.readString();
        mSku = in.readString();
        mDescription = in.readString();
        mCategory = in.readString();
        mAnimalImages = in.createTypedArrayList(AnimalImage.CREATOR);
        mAnimalAttributes = in.createTypedArrayList(AnimalAttribute.CREATOR);
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mName);
        dest.writeString(mSku);
        dest.writeString(mDescription);
        dest.writeString(mCategory);
        dest.writeTypedList(mAnimalImages);
        dest.writeTypedList(mAnimalAttributes);
    }


    @Override
    public int describeContents() {
        return 0;
    }


    public int getId() {
        return mId;
    }


    public String getName() {
        return mName;
    }


    public void setName(String name) {
        mName = name;
    }


    public String getSku() {
        return mSku;
    }


    public void setSku(String sku) {
        mSku = sku;
    }


    public String getDescription() {
        return mDescription;
    }


    public void setDescription(String description) {
        mDescription = description;
    }


    public String getCategory() {
        return mCategory;
    }


    public void setCategory(String category) {
        mCategory = category;
    }


    public ArrayList<AnimalImage> getAnimalImages() {
        return mAnimalImages;
    }


    public void setAnimalImages(ArrayList<AnimalImage> animalImages) {
        mAnimalImages = animalImages;
    }


    public ArrayList<AnimalAttribute> getAnimalAttributes() {
        return mAnimalAttributes;
    }


    public void setAnimalAttributes(ArrayList<AnimalAttribute> animalAttributes) {
        mAnimalAttributes = animalAttributes;
    }


    public static class Builder {
        private int mId;
        private String mName;
        private String mSku;
        private String mDescription;
        private String mCategory;
        private ArrayList<AnimalImage> mAnimalImages;
        private ArrayList<AnimalAttribute> mAnimalAttributes;


        public Builder setId(int id) {
            mId = id;
            return this;
        }


        public Builder setName(String name) {
            mName = name;
            return this;
        }


        public Builder setSku(String sku) {
            mSku = sku;
            return this;
        }


        public Builder setDescription(String description) {
            mDescription = description;
            return this;
        }


        public Builder setCategory(String category) {
            mCategory = category;
            return this;
        }


        public Builder setAnimalImages(ArrayList<AnimalImage> animalImages) {
            mAnimalImages = animalImages;
            return this;
        }


        public Builder setAnimalAttributes(ArrayList<AnimalAttribute> animalAttributes) {
            mAnimalAttributes = animalAttributes;
            return this;
        }


        public Animal createAnimal() {
            if (mAnimalImages == null) mAnimalImages = new ArrayList<>();
            if (mAnimalAttributes == null) mAnimalAttributes = new ArrayList<>();
            return new Animal(mId, mName, mSku, mDescription, mCategory, mAnimalImages, mAnimalAttributes);
        }
    }
}
