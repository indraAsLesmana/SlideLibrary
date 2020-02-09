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

package com.indra.mindvalley_test

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.indra.slide.Slide
import com.indra.slide.common.Priority
import com.indra.slide.error.ANError
import com.indra.slide.interfaces.BitmapRequestListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val URL_IMAGE = "https://raw.githubusercontent.com/Tamem-Maaz/Mindvalley_Challenge_Project/master/Photos/1%20(1).jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadImageDirect()
    }

    fun loadImageDirect() {
       Slide.get(URL_IMAGE)
           .setPriority(Priority.MEDIUM)
           ?.setImageScaleType(null)
           ?.setBitmapMaxHeight(0)
           ?.setBitmapMaxWidth(0)
           ?.setBitmapConfig(Bitmap.Config.ARGB_8888)
           ?.build()?.getAsBitmap(object: BitmapRequestListener{
               override fun onResponse(response: Bitmap?) {
                   image.setImageBitmap(response)
               }

               override fun onError(anError: ANError?) {
                    val eror = anError
               }

           })
    }
}
