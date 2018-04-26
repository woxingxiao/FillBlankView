[![Download](https://api.bintray.com/packages/woxingxiao/maven/fillblankview/images/download.svg)](https://bintray.com/woxingxiao/maven/fillblankview/_latestVersion)

### 仿手机支付宝支付数字密码输入验证，文字信息输入验证

### Gradle
```groovy
dependencies{
    compile 'com.xw.repo:fillblankview:2.1@aar'
}
```

### Screenshot

>**password inputs**

![demo2](https://github.com/woxingxiao/FillBlankViewDemo/blob/master/screenshots/demo2.png)

>**text inputs**

![demo3](https://github.com/woxingxiao/FillBlankViewDemo/blob/master/screenshots/demo3.png)

### XML
```xml
<com.xw.repo.fillblankview.FillBlankView
    android:id="@+id/fill_blank_view4"
    android:layout_width="match_parent"
    android:layout_height="50dp"
    android:layout_marginTop="10dp"
    android:background="@color/red_light"
    android:padding="5dp"
    app:blankNum="6"
    app:blankCornerRadius="4dp"
    app:blankSolidColor="@color/yellow"
    app:blankSpace="10dp"
    app:blankStrokeColor="@color/green"
    app:blankStrokeWidth="1dp"
    app:dotColor="@color/green"
    app:dotSize="5dp"
    app:isPasswordMode="true"/>
```
```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_marginTop="10dp"
    android:text="match: Hello, world"/>

<com.xw.repo.fillblankview.FillBlankView
    android:id="@+id/fill_blank_view2"
    android:layout_width="match_parent"
    android:layout_height="40dp"
    android:padding="5dp"
    android:textSize="16sp"
    app:blankCornerRadius="4dp"
    app:blankSpace="5dp"/>
```
Check the demo for more usage.

### Attributes
attr | format
-------- | ---
blankNum|integer
blankSpace|dimension
blankSolidColor|color
blankStrokeColor|color
blankStrokeWidth|dimension
blankCornerRadius|dimension
blankFocusedStrokeColor|color
isPasswordMode|boolean
dotSize|dimension
dotColor|color
textMatchedColor|color
textNotMatchedColor|color

### License
```
The MIT License (MIT)

Copyright (c) 2017 woxingxiao

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
