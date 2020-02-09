# How to integrate

```css
Step 1. Add the JitPack repository to your build file

allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

Step 2. Add the dependency

dependencies {
	        implementation 'com.github.indraAsLesmana:SlideLibrary:0.1.0'
	}	
```




# How Dowload Image URL to imageView

    Slide.get(imageUrl)  
    .setPriority(Priority.MEDIUM)  
    ?.setBitmapMaxHeight(0)  
    ?.setBitmapMaxWidth(0)  
    ?.setBitmapConfig(Bitmap.Config.ARGB_8888)  
    ?.setImageScaleType(ImageView.ScaleType.FIT_XY)  
    ?.build()?.getAsBitmap(object : BitmapRequestListener {  
        override fun onResponse(response: Bitmap?) {  
            imagetView.setImageBitmap(response)  
        }  
  
        override fun onError(anError: ANError?) {
        }  
    })
