package org.ironoak;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.bytedeco.depthai.*;
import org.bytedeco.depthai.global.depthai;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.opencv.opencv_core.Mat;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import static org.bytedeco.depthai.global.depthai.TrackerIdAssignmentPolicy;
import static org.bytedeco.depthai.global.depthai.TrackerType;
import static org.bytedeco.opencv.global.opencv_highgui.waitKey;
import org.ironoak.*;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * PersonDetectorCameraTester.java
 * @author Gary Ding
 * @since 7/2/2022
 * This class test output
 */
public class PersonDetectorCameraTester {
    public static void main(String[] args) {
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

        System.out.println("test finished.");
    }

    public static void send(String s)
    {
        NetworkTable.setClientMode();
        NetworkTable.setTeam(111);
        NetworkTable.setIPAddress("192.168.1.252");
        NetworkTable.initialize();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        NetworkTable.getTable("SmartDashboard").putString("person-detector", s);
        //table.putString("person-detector", s);
    }


}
