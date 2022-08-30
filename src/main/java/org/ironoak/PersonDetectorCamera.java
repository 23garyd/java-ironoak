package org.ironoak;
import org.bytedeco.depthai.*;
import org.bytedeco.depthai.global.depthai;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.opencv.opencv_core.Mat;
import static org.bytedeco.depthai.global.depthai.TrackerIdAssignmentPolicy;
import static org.bytedeco.depthai.global.depthai.TrackerType;
import static org.bytedeco.opencv.global.opencv_highgui.imshow;
import static org.bytedeco.opencv.global.opencv_highgui.waitKey;
import org.ironoak.DetectResult;

/**
 * PersonDetectorCamera.java
 * @author Gary Ding
 * @since 7/2/2022
 * This class handles person detection with oak-D as input for pipeline.
 */
public class PersonDetectorCamera {

    static String[] labelMap = {"background", "aeroplane", "bicycle",     "bird",  "boat",        "bottle", "bus",
                                                  "car",        "cat",       "chair",       "cow",   "diningtable", "dog",    "horse",
                                                  "motorbike",  "person",    "pottedplant", "sheep", "sofa",        "train",  "tvmonitor"};

    static boolean isDetected;
    static int x1;
    static int x2;
    static int y1;
    static int y2;
    static int depth;
    static Pipeline createCameraPipeline() {

        boolean fullFrameTracking = false;
        Pipeline p = new Pipeline();
        Path nnPath =  new Path("/home/demo/Public/javacpp-presets/depthai/samples/models/mobilenet-ssd_openvino_2021.4_6shave.blob");

        ColorCamera colorCam = p.createColorCamera();
        //gary added 8-27-2022
        SpatialDetectionNetwork detectionNetwork = p.createMobileNetSpatialDetectionNetwork();
        StereoDepth stereo = p.createStereoDepth();
        ObjectTracker objectTracker = p.createObjectTracker();
        MonoCamera monoLeft = p.createMonoCamera();
        MonoCamera monoRight = p.createMonoCamera();

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

        monoLeft.setResolution(MonoCameraProperties.SensorResolution.THE_400_P);
        monoLeft.setBoardSocket(depthai.CameraBoardSocket.LEFT);
        monoRight.setResolution(MonoCameraProperties.SensorResolution.THE_400_P);
        monoRight.setBoardSocket(depthai.CameraBoardSocket.RIGHT);

        stereo.setDefaultProfilePreset(StereoDepth.PresetMode.HIGH_DENSITY);
        stereo.setDepthAlign(depthai.CameraBoardSocket.RGB);
        stereo.setOutputSize(monoLeft.getResolutionWidth(), monoLeft.getResolutionHeight());

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
        monoLeft.out().link(stereo.left());
        monoRight.out().link(stereo.right());

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
        stereo.depth().link(detectionNetwork.inputDepth());

        return p;
    }

    public static DetectResult detectPerson() {
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

            Tracklet t = track.tracklets(); //trackletsData[i];
            if(t!=null){
                Rect roi = t.roi().denormalize(frame.cols(), frame.rows());
                x1 = (int) roi.topLeft().x();
                y1 = (int) roi.topLeft().y();
                x2 = (int) roi.bottomRight().x();
                y2 = (int) roi.bottomRight().y();
                int labelIndex = t.label();
                if (labelIndex == 15) {
                    isDetected = true;
                    depth = (int) t.spatialCoordinates().z();
                    System.out.println("depth is: "+ depth);
                    return new DetectResult(isDetected,x1,x2,y1,y2,depth);
                }
            }

            else {
                x1=0;
                y1=0;
                x2=0;
                y2=0;
                depth=0;
                isDetected=false;
                System.out.println(isDetected);
            }

            if (imgFrame != null) {
                //System.out.printf("Frame - w: %d, h: %d\n", imgFrame.getWidth(), imgFrame.getHeight());
                //frame = new Mat(imgFrame.getHeight(), imgFrame.getWidth(), CV_8UC3, imgFrame.getData());
                //Imgproc.rectangle (frame, new Point(x1, y1), new Point(x2, y2), new Scalar(64, 64, 64), 10);
                //HighGui.imshow("preview", frame);
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
