package com.github.romualdrousseau.shuju.ml.nn.optimizer;

import com.github.romualdrousseau.shuju.math.Scalar;
import com.github.romualdrousseau.shuju.math.Tensor2D;
import com.github.romualdrousseau.shuju.math.Tensor3D;
import com.github.romualdrousseau.shuju.math.TensorFunction;
import com.github.romualdrousseau.shuju.ml.nn.Model;
import com.github.romualdrousseau.shuju.ml.nn.Optimizer;
import com.github.romualdrousseau.shuju.ml.nn.Parameters2D;
import com.github.romualdrousseau.shuju.ml.nn.Parameters3D;
import com.github.romualdrousseau.shuju.ml.nn.LearningRateScheduler;

public class OptimizerAdaDelta extends Optimizer {
    private float b;

    public OptimizerAdaDelta(Model model, float learningRate, LearningRateScheduler scheduler, float b) {
        super(model, learningRate, scheduler);
        this.b = b;
    }

    public Tensor2D computeGradients(Parameters2D p) {
        p.V.expAvg(p.G.copy().pow(2.0f), this.b);

        Tensor2D dG = p.G.copy().map(new TensorFunction<Tensor2D>() {
            public final float apply(float a_ij, int[] ij, Tensor2D V) {
                final float m_ij = p.M.get(ij[0], ij[1]);
                final float v_ij = p.V.get(ij[0], ij[1]);
                return Scalar.sqrt(m_ij + Scalar.EPSILON) * a_ij / Scalar.sqrt(v_ij + Scalar.EPSILON);
            }
        });

        p.M.expAvg(dG.copy().pow(2.0f), this.b);

        return dG;
    }

    public Tensor3D computeGradients(Parameters3D p) {
        p.V.expAvg(p.G.copy().pow(2.0f), this.b);

        Tensor3D dG = p.G.copy().map(new TensorFunction<Tensor3D>() {
            public final float apply(float a_ij, int[] ijk, Tensor3D V) {
                final float m_ij = p.M.get(ijk[0], ijk[1], ijk[2]);
                final float v_ij = p.V.get(ijk[0], ijk[1], ijk[2]);
                return Scalar.sqrt(m_ij + Scalar.EPSILON) * a_ij / Scalar.sqrt(v_ij + Scalar.EPSILON);
            }
        });

        p.M.expAvg(dG.copy().pow(2.0f), this.b);

        return dG;
    }
}
