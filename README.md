# DepthAI Java PersonDetector for OAK-D
This folder contains Java Implementation for using AI with OAK-D camera.

## Pre-requisites
This project is based on DepthAI and Javacpp project. This project requires JDK 8+ and Maven 3.0+

```
<dependencies>
<dependency>
<groupId>org.bytedeco</groupId>
<artifactId>depthai-platform</artifactId>
<version>2.17.3-1.5.8-SNAPSHOT</version>
</dependency>
<dependency>
<groupId>org.bytedeco</groupId>
<artifactId>opencv-platform</artifactId>
<version>4.6.0-1.5.8-SNAPSHOT</version>
</dependency>
</dependencies>

```


## Compile

`mvn clean install`

## Run

`mvn exec:java -Dexec.mainClass="org.ironoak.PersonDetector"
`
## How it works
When run, the tester will begin the person detection through the OAK-D camera until a person is spotted. The return 
values include the coordinates of the bounding box and depth values.

```

        PersonDetectorCamera p = new PersonDetectorCamera();
        DetectResult res = p.detectPerson();
        System.out.println(res);

```
the output is
```html
DetectResult{isDetected=true, x1=81, x2=295, y1=116, y2=300, depth=4061}
```
