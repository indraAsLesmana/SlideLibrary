/*
 * Copyright 2020 indra953@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.indra.mindvalley_test.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class DynamicHeightImageView extends AppCompatImageView {

  private double mHeightRatio;

  public DynamicHeightImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public DynamicHeightImageView(Context context) {
    super(context);
  }

  //Here we will set the aspect ratio
  public void setHeightRatio(double ratio) {
    if (ratio != mHeightRatio) {
      mHeightRatio = ratio;
      requestLayout();
    }
  }

  public double getHeightRatio() {
    return mHeightRatio;
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    if (mHeightRatio > 0.0) {
      // set the image views size
      int width = MeasureSpec.getSize(widthMeasureSpec);
      int height = (int) (width * mHeightRatio);
      setMeasuredDimension(width, height);
    } else {
      super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
  }
}
