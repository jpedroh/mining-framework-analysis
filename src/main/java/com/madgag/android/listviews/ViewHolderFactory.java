/*
 * Copyright 2011 Roberto Tyley
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.madgag.android.listviews;


import android.view.View;

public interface ViewHolderFactory<T> {

    ViewHolder<T> createViewHolderFor(View view);

<<<<<<< /usr/src/app/output/rtyley/android-viewholder-listviews/b5a40bb804382b21052c99890298fc5afcd4b6b1/src/main/java/com/madgag/android/listviews/ViewHolderFactory.java/left.java
    Class<? extends ViewHolder<T>> getHolderClass();
||||||| /usr/src/app/output/rtyley/android-viewholder-listviews/b5a40bb804382b21052c99890298fc5afcd4b6b1/src/main/java/com/madgag/android/listviews/ViewHolderFactory.java/base.java
=======
    Class<ViewHolder<T>> getHolderClass();
>>>>>>> /usr/src/app/output/rtyley/android-viewholder-listviews/b5a40bb804382b21052c99890298fc5afcd4b6b1/src/main/java/com/madgag/android/listviews/ViewHolderFactory.java/right.java
}
