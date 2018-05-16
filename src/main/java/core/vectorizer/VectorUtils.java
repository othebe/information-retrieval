package core.vectorizer;

public class VectorUtils {
    public static final double getCosineAngle(Double[] vecA, Double[] vecB) {
        int vecLength = vecA.length;

        double dotProduct = 0;

        for (int i = 0; i < vecLength; i++) {
            dotProduct += vecA[i] * vecB[i];
        }

        return dotProduct;
    }
}
