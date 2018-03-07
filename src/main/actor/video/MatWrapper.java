package main.actor.video;
import java.io.Serializable;

import org.opencv.core.Mat;

public class MatWrapper implements Serializable {
    int rows;
    int cols;
    int type;
    byte[] data;

    public MatWrapper() {
    }

    public MatWrapper(Mat mat)
    {
        if (mat.isContinuous()) {

            int elemSize = (int) mat.elemSize();
            rows = mat.rows();
            cols = mat.cols();
            type = mat.type();

            data = new byte[cols * rows * elemSize];
            mat.get(0, 0, data);
        }
    }

    public Mat toMat()
    {
        Mat mat = new Mat(rows, cols, type);
        mat.put(0, 0, data);
        return mat;
    }
}