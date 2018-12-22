# LoginLibrary

For using this OTP Login Library  in your app, you need to get head_number, sid, sub_url1, sub_url2 by contacting me.  
You can find me with: mohammad.samadi1373[at]gmail.com

Add dependencies
=====

**Step 1.** Add the JitPack maven repository to the list of repositories (build.gradle (Project)):

```gradle
    allprojects {
    repositories {
        google()
        jcenter()
        maven { url 'https://jitpack.io' }

    }
  }
```
**Step 2.**  Add the dependency information (build.gradle (Module:app)):


```
dependencies {
    ....
    implementation 'com.github.mosamadi:LoginLibrary:1'

}
```

**Step 3.**  Initiate these strings camp_name,sid,welcome,farsi_name_str,head_number,sub_url1,sub_url2,first_activity,package_name with proper values. Then Add this part of code to onCreated method of your first activity (first_activity is name of your first activity in string format for example "MainActivity") :


```java
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ...

        String signup = getIntent().getStringExtra("signup");
        if (signup == null) {

            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("camp_name",camp_name );
            intent.putExtra("sid", sid);
            intent.putExtra("welcome", welcome);
            intent.putExtra("farsi_name_str",farsi_name_str );
            intent.putExtra("head_number", head_number);
            intent.putExtra("sub_url1", sub_url1);
            intent.putExtra("sub_url2", sub_url2);
            intent.putExtra("first_activity", first_activity);

            intent.putExtra("package_name", package_name);

//        intent.putse("first_activity",MainActivity.class);
            startActivity(intent);
            finish();
        }else if (signup.equals("Exit")) {
            
            finish();
            System.exit(0);
        }
        ....


    }
        
```

```
**Step 4.**  Add this permission to your application manifests:


```xml
 <uses-permission android:name="android.permission.INTERNET" />
    
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.REORDER_TASKS" />

<uses-permission android:name="android.permission.READ_SMS" />

<uses-permission android:name="android.permission.RECEIVE_SMS" />

```

