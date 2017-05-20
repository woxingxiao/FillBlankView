### 仿手机支付宝支付数字密码输入验证，文字信息输入验证

### Gradle
```groovy
dependencies{
    compile 'com.xw.repo:fillblankview:1.1@aar'
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
    app:blankCornerRadius="4dp"
    app:blankSolidColor="@color/yellow"
    app:blankSpace="10dp"
    app:blankStrokeColor="@color/green"
    app:blankStrokeWidth="1dp"
    app:dotColor="@color/green"
    app:dotSize="5dp"
    app:hideText="true"/>
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

### Attributes
attr | format
-------- | ---
blankNum|integer
blankSpace|dimension
blankSolidColor|color
blankStrokeColor|color
blankStrokeWidth|dimension
blankCornerRadius|dimension
hideText|boolean
dotSize|dimension
dotColor|color
textMatchedColor|color
textNotMatchedColor|color
