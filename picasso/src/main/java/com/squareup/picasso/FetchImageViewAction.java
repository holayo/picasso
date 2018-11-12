/*
 * Copyright (C) 2013 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.squareup.picasso;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

class FetchImageViewAction extends Action<ImageView> {

  ImageView imageView;
  Callback callback;

  FetchImageViewAction(Picasso picasso, ImageView imageView, Request data, int memoryPolicy,
      int networkPolicy, int errorResId, Drawable errorDrawable, String key, Object tag,
      Callback callback, boolean noFade) {
    super(picasso, null, data, memoryPolicy, networkPolicy, errorResId, errorDrawable, key,
        tag, noFade);
    this.imageView = imageView;
    this.callback = callback;
  }

  @Override public void complete(Bitmap result, Picasso.LoadedFrom from) {
    if (result == null) {
      throw new AssertionError(
          String.format("Attempted to complete action with no result!\n%s", this));
    }

    Context context = picasso.context;
    boolean indicatorsEnabled = picasso.indicatorsEnabled;
    PicassoDrawable.setBitmap(imageView, context, result, from, noFade, indicatorsEnabled);

    if (callback != null) {
      callback.onSuccess();
    }
  }

  @Override public void error(Exception e) {
    Drawable placeholder = imageView.getDrawable();
    if (placeholder instanceof Animatable) {
      ((Animatable) placeholder).stop();
    }
    if (errorResId != 0) {
      imageView.setImageResource(errorResId);
    } else if (errorDrawable != null) {
      imageView.setImageDrawable(errorDrawable);
    }

    if (callback != null) {
      callback.onError(e);
    }
  }

  @Override void cancel() {
    super.cancel();
    if (callback != null) {
      callback = null;
    }
  }
}
