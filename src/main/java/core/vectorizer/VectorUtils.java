package core.vectorizer;

import java.util.Iterator;

public class VectorUtils {
    public static final double getCosineAngle(SparseVector<Double> vecA, SparseVector<Double> vecB) {
        double dotProduct = 0;

        Iterator<SparseVector<Double>.Data<Double>> vecA_it = vecA.getIterator();
        Iterator<SparseVector<Double>.Data<Double>> vecB_it = vecB.getIterator();

        if (!vecA_it.hasNext() || !vecB_it.hasNext()) return 0;

        SparseVector<Double>.Data<Double> vecA_data = vecA_it.next();
        SparseVector<Double>.Data<Double> vecB_data = vecB_it.next();

        while (vecA_it.hasNext() && vecB_it.hasNext()) {
            if (vecA_data.getNdx() == vecB_data.getNdx()) {
                dotProduct += vecA_data.getValue() * vecB_data.getValue();
                vecA_data = vecA_it.next();
                vecB_data = vecB_it.next();
            } else if (vecA_data.getNdx() < vecB_data.getNdx()) {
                vecA_data = vecA_it.next();
            } else {
                vecB_data = vecB_it.next();
            }
        }

        return dotProduct;
    }

    public static final SparseVector<Double> getUnitVector(SparseVector<Double> sparseVector) {
        double acc = 0;

        Iterator<SparseVector<Double>.Data<Double>> vecIt = sparseVector.getIterator();

        while (vecIt.hasNext()) {
            double value = vecIt.next().getValue();
            acc += (value * value);
        }

        double magnitude = Math.sqrt(acc);

        if (magnitude == 0) return sparseVector;

        SparseVector<Double> unitVector = new SparseVector<>();
        vecIt = sparseVector.getIterator();
        while (vecIt.hasNext()) {
            SparseVector<Double>.Data<Double> vecData = vecIt.next();
            unitVector.put(vecData.getNdx(), vecData.getValue() / magnitude);
        }

        return unitVector;
    }
}
