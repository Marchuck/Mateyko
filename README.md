# Mateyko
Lightweight image loader based on Retrofit and RxJava

#Dependicies
    compile 'com.squareup.retrofit:retrofit:1.9.0'
    compile 'io.reactivex:rxandroid:0.25.0'
#Usage
Add class Mateyko.java into your project, then simply invoke method, similar to Glide or Picasso:
Mateyko.with(activity).load(url).into(imageView);

Note: you need internet permission :)

Enjoy
