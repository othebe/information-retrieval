package core.vectorizer;

public class VectorUtils {
    public static final double getCosineAngle(SparseVector<Double> vecA, SparseVector<Double> vecB) {
        long vecLength = vecA.getLength();

        double dotProduct = 0;

        for (long i = 0; i < vecLength; i++) {
            if (vecA.has(i) && vecB.has(i)) {
                dotProduct += vecA.get(i) * vecB.get(i);
            }
        }

        return dotProduct;
    }

    public static final SparseVector<Double> getUnitVector(SparseVector<Double> sparseVector) {
        double acc = 0;

        for (long i = 0; i < sparseVector.getLength(); i++) {
            acc += (sparseVector.get(i) * sparseVector.get(i));
        }

        double magnitude = Math.sqrt(acc);

        SparseVector<Double> unitVector = new SparseVector<>(sparseVector.getLength(), sparseVector.getDefaultValue());
        for (long i = 0; i < sparseVector.getLength(); i++) {
            double component = sparseVector.has(i) ? sparseVector.get(i) / magnitude : 0;
            unitVector.put(i, component);
        }

        return unitVector;
    }
}
