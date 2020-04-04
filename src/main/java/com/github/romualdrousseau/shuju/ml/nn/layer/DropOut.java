package com.github.romualdrousseau.shuju.ml.nn.layer;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Tensor2D;
import com.github.romualdrousseau.shuju.math.Scalar;
import com.github.romualdrousseau.shuju.ml.nn.Layer;
import com.github.romualdrousseau.shuju.ml.nn.Optimizer;

public class DropOut extends Layer {

    public DropOut(int inputUnits, final int inputChannels, final float rate) {
        super(inputUnits, inputChannels, inputUnits, inputChannels, 1.0f);

        this.rate = Scalar.map(rate, 0.0f, 1.0f, -0.5f, 0.5f);
        this.U = new Tensor2D(this.inputUnits, this.inputChannels);

        if(rate < 1.0f) {
            this.scale = 1.0f / (1.0f - rate);
        } else {
            this.scale = 0.0f;
        }

        this.reset(false);
    }

    public void reset(final boolean parametersOnly) {
    }

    public Tensor2D callForward(final Tensor2D input) {
        if (this.training) {
            this.U.randomize(0.5f).if_lt_then(this.rate, 0.0f, this.scale);
            return input.mul(this.U);
        } else {
            return input;
        }
    }

    public void startBackward(final Optimizer optimizer) {
    }

    public Tensor2D callBackward(final Tensor2D d_L_d_out) {
        return d_L_d_out.mul(this.U);
    }

    public void completeBackward(final Optimizer optimizer) {
    }

    public void fromJSON(final JSONObject json) {
    }

    public JSONObject toJSON() {
        JSONObject json = JSON.newJSONObject();
        return json;
    }

    private float rate;
    private float scale;
    // cache
    private Tensor2D U;
}
