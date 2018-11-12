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

import android.graphics.Bitmap;
import android.util.Log;
import android.net.Uri;
import android.graphics.drawable.Drawable;

final class FetchTargetAction extends Action<Target> {

  private static final String TAG = "holayo";
  private Target target;
  private boolean hasPlaceholder;
  private Uri uri;

  FetchTargetAction(Picasso picasso, Target target, Request data, int memoryPolicy, int networkPolicy,
      Drawable errorDrawable, String key, Object tag, int errorResId, boolean hasPlaceholder, Uri uri) {
    super(picasso, null, data, memoryPolicy, networkPolicy, errorResId, errorDrawable, key, tag,
        false);
    this.target = target;
    this.hasPlaceholder = hasPlaceholder;
    this.uri = uri;
  }

  @Override void complete(Bitmap result, Picasso.LoadedFrom from) {
    if (result == null) {
      throw new AssertionError(
          String.format("Attempted to complete action with no result!\n%s", this));
    }

    if (target != null) {
      target.onBitmapLoaded(result, from);
      if (result.isRecycled()) {
        throw new IllegalStateException("Target callback must not recycle bitmap!");
      }
    }
  }

  @Override void error(Exception e) {
    if (target != null) {
      if (!hasPlaceholder) {
        if (errorResId != 0) {
          target.onBitmapFailed(e, picasso.context.getResources().getDrawable(errorResId));
        } else {
          target.onBitmapFailed(e, errorDrawable);
        }
      } else {
        Log.d(TAG, "already got a place holder for " + uri, e);
      }
    }
  }
}
