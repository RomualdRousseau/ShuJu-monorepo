package com.github.romualdrousseau.shuju.ml.nn.initializer;

import com.github.romualdrousseau.shuju.math.Scalar;
import com.github.romualdrousseau.shuju.math.Tensor2D;
import com.github.romualdrousseau.shuju.math.Tensor3D;
import com.github.romualdrousseau.shuju.ml.nn.InitializerFunc;

public class LecunUniformInitializer implements InitializerFunc {
    public Tensor2D apply(Tensor2D matrix) {
        return matrix.randomize(Scalar.sqrt(6.0f / Scalar.sqrt(matrix.rowCount())));
    }

    public Tensor3D apply(Tensor3D matrix) {
        return matrix.randomize(Scalar.sqrt(6.0f / Scalar.sqrt(matrix.shape[1])));
    }
  }
