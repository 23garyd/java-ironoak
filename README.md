# DepthAI Java PersonDetector for OAK-D
This folder contains the Java implementation for using AI with OAK-D camera for teams competing in the FIRST Robotics 
Competition that need a low-cost yet effective vision solution.

## Pre-requisites
This project is based on DepthAI and Javacpp projects, which can be accessed here: https://github.com/bytedeco/javacpp-presets/tree/master/depthai

This project also makes use of NetworkTables in the ntcore project from WPILib, which can be accessed here: https://github.com/wpilibsuite/ntcore

This project requires JDK 11 and Maven 3.0+

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
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.9.8</version>
        </dependency>
        <dependency>
            <groupId>edu.wpi.first.wpilib.networktables.java</groupId>
            <artifactId>NetworkTables</artifactId>
            <version>3.1.7</version>
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
values include the coordinates of the bounding box and depth values. In order to pass the return values to the RoboRIO
on the robot, the tester uses NetworkTables. NetworkTables is a standard package from WPILibrary, a library commonly 
used by FRC teams. It functions as a dictionary that can be accessed by both the client and the server. which in this 
case is a coprocessor or Driver Station and a RoboRIO. A listener on the RoboRIO end can be used to scan for results 
from the person detector in order to take action accordingly.

```

        PersonDetectorCamera p = new PersonDetectorCamera();
        DetectResult res = p.detectPerson();
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(res);
            send(json);
        }
        catch(JsonProcessingException e) {
            e.printStackTrace();
        }


```
An example of the output from the person detector is the following:
```html
DetectResult{isDetected=true, x1=81, x2=295, y1=116, y2=300, depth=4061}
```
In order to simulate the running of the person detector without the robot's onboard RoboRIO computer, the tester uses
the WPILib Robot Simulator. When the NetworkTables values reaches the simulated RoboRIO, the person detector values are
displayed under the SmartDashboard, as shown below.

![simulation result](/doc/nt3.png)

For more information on how to set up the WPILib Robot Simulation, this link may be helpful:
https://docs.wpilib.org/en/stable/docs/software/wpilib-tools/robot-simulation/introduction.html