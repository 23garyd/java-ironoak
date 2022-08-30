package org.ironoak;
import org.bytedeco.depthai.*;
import org.bytedeco.depthai.global.depthai;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.opencv.opencv_core.Mat;

import static org.bytedeco.depthai.global.depthai.TrackerIdAssignmentPolicy;
import static org.bytedeco.depthai.global.depthai.TrackerType;
import static org.bytedeco.opencv.global.opencv_highgui.waitKey;
import org.ironoak.*;

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
        System.out.println(res);
    }
}
