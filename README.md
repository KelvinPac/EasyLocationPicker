# EasyLocationPicker

[![Release](https://jitpack.io/v/KelvinPac/EasyLocationPicker.svg)](https://jitpack.io/#KelvinPac/EasyLocationPicker)

An Android library helps you pick user location easily via a callback
+ Fully customizable using the Builder
+ Location is precise up to 7 decimal places (highest precision)
+ Geocoder to give you address and street names
+ User can drag or click on map
+ Integrated Places Autocomplete
+ No need to add any permissions in manifest manually
+ No need to add google play services location lib in gradle manually
+ Uses Google location services API internally - so you're in safe hands
+ Simple plug and play design
+ **Full Kotlin support**

<!--###### (method counts ~50, size ~50KB)-->

# Screenshots

|   |  |
| ------------- | ------------- |
| ![alt text](https://github.com/KelvinPac/EasyLocationPicker/blob/master/github_assets/s1.jpg "Logo")  | ![alt text](https://github.com/KelvinPac/EasyLocationPicker/blob/master/github_assets/s2.jpg "Logo")  |
| ![alt text](https://github.com/KelvinPac/EasyLocationPicker/blob/master/github_assets/s3.jpg "Logo")  | ![alt text](https://github.com/KelvinPac/EasyLocationPicker/blob/master/github_assets/s4.jpg "Logo")  |
| ![alt text](https://github.com/KelvinPac/EasyLocationPicker/blob/master/github_assets/s5.jpg "Logo")  | ![alt text](https://github.com/KelvinPac/EasyLocationPicker/blob/master/github_assets/s6.jpg "Logo")  |

# Usage

+ Declare EasyLocation in your activity
+ Override `onActivityResult` and call `easyLocation.onActivityResult` inside it

Example:

```java
public class MainActivity extends AppCompatActivity {

    private EasyLocation easyLocation;
    
    ...
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        easyLocation.onActivityResult(requestCode, resultCode, data);
    }

}
```

+ Everytime you want to fetch user's current location, simply initialize `easyLocation` variable:
+ Customize the builder as per your request
```java
   easyLocation = new EasyLocation.Builder(MainActivity.this,"<PLACES_API_KEY>")
                        .showCurrentLocation(true)
                        .useGeoCoder(true)
                        .setResultOnBackPressed(false)
                        .setCallbacks(new EasyLocationCallbacks() {
                            @Override
                            public void onSuccess(SelectedLocation location) {
                                Toast.makeText(MainActivity.this, location.toString(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailed(String reason) {
                                Toast.makeText(MainActivity.this, reason, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .build()
```

# Setup

Add this line in your root build.gradle at the end of repositories:

```gradle
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' } // this line
  }
}
  ```
Add this line in your app build.gradle:
```gradle
dependencies {
  implementation 'com.github.KelvinPac:EasyLocationPicker:LATEST_VERSION' // this line
}
```
where LATEST_VERSION is [![](https://jitpack.io/v/KelvinPac/EasyLocationPicker.svg)](https://jitpack.io/#KelvinPac/EasyLocationPicker)

# Kotlin Support
This library fully supports Kotlin out of the box (because Kotlin is 100% interoperable)
Hence the setup remains the same, and usage becomes:
```kotlin
Code sample coming soon
```


Thank you :)

