package org.ironoak;

/**
 * DetectResult.java
 * @author Gary Ding
 * @since 7/2/2022
 * This class defines the output from person detection
 */
public class DetectResult {
    boolean isDetected;
    int x1;
    int x2;
    int y1;
    int y2;
    int depth;

    public DetectResult(boolean isDetected, int x1, int x2, int y1, int y2, int depth) {
        this.isDetected = isDetected;
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.depth = depth;
    }


    public boolean isDetected() {
        return isDetected;
    }

    public void setDetected(boolean detected) {
        isDetected = detected;
    }

    public int getX1() {
        return x1;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public int getX2() {
        return x2;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public int getY1() {
        return y1;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }

    public int getY2() {
        return y2;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    @Override
    public String toString() {
        return "DetectResult{" +
                "isDetected=" + isDetected +
                ", x1=" + x1 +
                ", x2=" + x2 +
                ", y1=" + y1 +
                ", y2=" + y2 +
                ", depth=" + depth +
                '}';
    }
}
