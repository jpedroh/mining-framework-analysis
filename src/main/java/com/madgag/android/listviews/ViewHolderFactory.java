package com.madgag.android.listviews;
import android.view.View;

public interface ViewHolderFactory<T extends java.lang.Object> {
  ViewHolder<T> createViewHolderFor(View view);

  Class<
<<<<<<< /usr/src/app/output/rtyley/android-viewholder-listviews/b5a40bb804382b21052c99890298fc5afcd4b6b1/src/main/java/com/madgag/android/listviews/ViewHolderFactory.java/left.java
  ? extends ViewHolder<T>
=======
  ViewHolder<T>
>>>>>>> /usr/src/app/output/rtyley/android-viewholder-listviews/b5a40bb804382b21052c99890298fc5afcd4b6b1/src/main/java/com/madgag/android/listviews/ViewHolderFactory.java/right.java
  > getHolderClass();
}