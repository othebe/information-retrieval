package core.vectorizer;

public class VectorUtils {
    public static final double getCosineAngle(double[] vecA, double[] vecB) {
        int vecLength = vecA.length;

        double dotProduct = 0;

        for (int i = 0; i < vecLength; i++) {
            dotProduct += vecA[i] * vecB[i];
        }

        return dotProduct;
    }
}
