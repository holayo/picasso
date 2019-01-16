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
import android.net.Uri;
import android.util.Log;

class ImageViewAction extends Action<ImageView> {

  private static final String TAG = "holayo";
  Callback callback;
  Uri uri;

  ImageViewAction(Picasso picasso, ImageView imageView, Request data, int memoryPolicy,
      int networkPolicy, int errorResId, Drawable errorDrawable, String key, Object tag,
      Callback callback, boolean noFade) {
    super(picasso, imageView, data, memoryPolicy, networkPolicy, errorResId, errorDrawable, key,
        tag, noFade);
    this.callback = callback;
    this.uri = data.uri;
  }

  @Override public void complete(Bitmap result, Picasso.LoadedFrom from) {
    Log.d(TAG, "into complete for " + uri);
    if (result == null) {
      throw new AssertionError(
          String.format("Attempted to complete action with no result!\n%s", this));
    }

    ImageView target = this.target.get();
    if (target == null) {
      Log.d(TAG, "complete target imageview null for" + uri);
      return;
    }

    Log.d(TAG, "complete target not null for " + uri);
    Context context = picasso.context;
    boolean indicatorsEnabled = picasso.indicatorsEnabled;
    PicassoDrawable.setBitmap(target, context, result, from, noFade, indicatorsEnabled);

    if (callback != null) {
      callback.onSuccess();
    }
  }

  @Override public void error(Exception e) {
    Log.d(TAG, "into error for " + uri);
    ImageView target = this.target.get();
    if (target == null) {
      Log.d(TAG, "error target imageview null for" + uri, e);
      return;
    }
    Drawable placeholder = target.getDrawable();
    if (placeholder instanceof Animatable) {
      ((Animatable) placeholder).stop();
    }
    if (errorResId != 0) {
      target.setImageResource(errorResId);
    } else if (errorDrawable != null) {
      target.setImageDrawable(errorDrawable);
    }

    if (callback != null) {
      callback.onError(e);
    }
  }

  @Override void cancel() {
    Log.d(TAG, "into cancel for " + uri);
    super.cancel();
    if (callback != null) {
      callback = null;
    }
  }
}
