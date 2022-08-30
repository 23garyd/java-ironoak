package org.ironoak;
import org.bytedeco.javacpp.*;
import org.bytedeco.depthai.*;
import org.bytedeco.depthai.Device;
import org.bytedeco.depthai.Path;
import org.bytedeco.depthai.Rect;
import org.bytedeco.depthai.Tracklets;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_highgui.*;
import static org.bytedeco.depthai.global.depthai.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_highgui.*;

/**
 * PersonDetectorVideo.java
 * @author Gary Ding
 * @since 7/2/2022
 * This class handles person detection with video file as input for pipeline.
 */
public class PersonDetectorVideo {

    static String[] labelMap = {"background", "aeroplane", "bicycle",     "bird",  "boat",        "bottle", "bus",
                                                  "car",        "cat",       "chair",       "cow",   "diningtable", "dog",    "horse",
                                                  "motorbike",  "person",    "pottedplant", "sheep", "sofa",        "train",  "tvmonitor"};

    static Pipeline createCameraPipeline() {

        boolean fullFrameTracking = false;
        Pipeline p = new Pipeline();
        Path nnPath =  new Path("/home/demo/Public/javacpp-presets/depthai/samples/models/mobilenet-ssd_openvino_2021.4_6shave.blob");

        ColorCamera colorCam = p.createColorCamera();
        //gary added 8-27-2022

        //manip = p. self.pipeline.create(dai.node.ImageManip);
        ObjectTracker objectTracker = p.createObjectTracker();
        DetectionNetwork detectionNetwork = p.createMobileNetDetectionNetwork();


        XLinkOut xlinkOut = p.createXLinkOut();
        XLinkOut trackerOut = p.createXLinkOut();

        xlinkOut.setStreamName("preview");
        trackerOut.setStreamName("tracklets");
        // Properties
        colorCam.setPreviewSize(300, 300);
        colorCam.setResolution(ColorCameraProperties.SensorResolution.THE_1080_P);
        colorCam.setInterleaved(false);
        colorCam.setColorOrder(ColorCameraProperties.ColorOrder.BGR);
        colorCam.setFps(40);

        // Link plugins CAM -> XLINK
        //colorCam.preview().link(xlinkOut.input());
        // testing MobileNet DetectionNetwork
        detectionNetwork.setBlobPath(nnPath);
        detectionNetwork.setConfidenceThreshold(0.5f);
        detectionNetwork.input().setBlocking(false);
        objectTracker.setDetectionLabelsToTrack(new int[]{15}); // track only person
        objectTracker.setTrackerType(TrackerType.ZERO_TERM_COLOR_HISTOGRAM);   // TrackerType.ZERO_TERM_COLOR_HISTOGRAM
        // take the smallest ID when new object is tracked, possible options: SMALLEST_ID, UNIQUE_ID
        objectTracker.setTrackerIdAssignmentPolicy(TrackerIdAssignmentPolicy.SMALLEST_ID);

        // Linking
        colorCam.preview().link(detectionNetwork.input());
        objectTracker.passthroughTrackerFrame().link(xlinkOut.input());


        if(fullFrameTracking) {
            colorCam.video().link(objectTracker.inputTrackerFrame());
        } else {
            detectionNetwork.passthrough().link(objectTracker.inputTrackerFrame());
        }

        detectionNetwork.passthrough().link(objectTracker.inputDetectionFrame());
        detectionNetwork.out().link(objectTracker.inputDetections());
        objectTracker.out().link(trackerOut.input());

        return p;
    }

    public static void main(String[] args) {
        Pipeline p = createCameraPipeline();
        // Start the pipeline
        Device d = new Device();

        System.out.print("Connected cameras: ");
        IntPointer cameras = d.getConnectedCameras();
        for (int i = 0; i < cameras.limit(); i++) {
            System.out.print(cameras.get(i) + " ");
        }
        System.out.println();

        // Start the pipeline
        d.startPipeline(p);

        Mat frame;
        DataOutputQueue preview = d.getOutputQueue("preview");
        DataOutputQueue tracklets = d.getOutputQueue("tracklets");
        int counter = 0;
        float fps = 0;

        while (true) {
            ImgFrame imgFrame = preview.getImgFrame();
            Tracklets track = tracklets.getTracklets();
            frame = imgFrame.getCvFrame();

            counter++;

            System.out.println("label is not null :" + track);
            Tracklet t = track.tracklets(); //trackletsData[i];
            if(t!=null){
                Rect roi = t.roi().denormalize(frame.cols(), frame.rows());
                System.out.println("ros is" + roi.topLeft().x());
                int x1 = (int) roi.topLeft().x();
                int y1 = (int) roi.topLeft().y();
                int x2 = (int) roi.bottomRight().x();
                int y2 = (int) roi.bottomRight().y();

                int labelIndex = t.label();

                String labelStr = String.valueOf(labelIndex);
                System.out.println("label is :" + labelStr);

                if (labelIndex < labelMap.length) {
                    labelStr = labelMap[labelIndex];
                }

            }



            if (imgFrame != null) {
                System.out.printf("Frame - w: %d, h: %d\n", imgFrame.getWidth(), imgFrame.getHeight());
                frame = new Mat(imgFrame.getHeight(), imgFrame.getWidth(), CV_8UC3, imgFrame.getData());
                imshow("preview", frame);
                int key = waitKey(1);
                if (key == 'q') {
                    System.exit(0);
                }
            } else {
                System.out.println("Not ImgFrame");
            }
        }
    }
}
