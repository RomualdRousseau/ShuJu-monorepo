package com.github.romualdrousseau.shuju.ml.nn.layer;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Tensor2D;
import com.github.romualdrousseau.shuju.math.Scalar;
import com.github.romualdrousseau.shuju.math.Tensor1D;
import com.github.romualdrousseau.shuju.ml.nn.Layer;
import com.github.romualdrousseau.shuju.ml.nn.Optimizer;
import com.github.romualdrousseau.shuju.ml.nn.Parameters2D;

public class BatchNormalizer extends Layer {

    public BatchNormalizer(int inputUnits) {
        super(inputUnits, inputUnits, 1.0f);

        this.norms = new Parameters2D(2);
        this.mu = 0.0f;
        this.var = 0.0f;
        this.mu_run = 0.0f;
        this.var_run = 0.0f;

        this.reset(false);
    }

    public void reset(final boolean parametersOnly) {
        if (parametersOnly) {
            this.norms.M.zero();
            this.norms.V.zero();
        } else {
            this.norms.reset();
            this.norms.W.set(0, 0, 1.0f);
        }
    }

    public Tensor2D callForward(final Tensor2D input) {
        final float gamma = this.norms.W.get(0, 0), beta = this.norms.W.get(1, 0);
        float mu_curr = 0.0f, var_curr = 1.0f;

        if (this.training) {
            this.mu = input.avg(0, 0);
            this.var = input.var(0, 0);
            this.mu_run = this.mu_run * 0.9f + this.mu * (1.0f - 0.9f);
            this.var_run = this.var_run * 0.9f + this.var * (1.0f - 0.9f);
            mu_curr = this.mu;
            var_curr = this.var;
        } else {
            mu_curr = this.mu_run;
            var_curr = this.var_run;
        }

        var_inv = 1.0f / Scalar.sqrt(var_curr + Scalar.EPSILON);
        x_mu = input.copy().sub(mu_curr);
        x_hat = x_mu.copy().mul(var_inv);
        return x_hat.copy().mul(gamma).add(beta);
    }

    public void startBackward(final Optimizer optimizer) {
        this.norms.G.zero();
    }

    public Tensor2D callBackward(final Tensor2D d_L_d_out) {
        final float gamma = this.norms.W.get(0, 0);
        final float N = d_L_d_out.rowCount();

        Tensor2D dG = new Tensor2D(2, 1);
        dG.set(0, 0, d_L_d_out.flatten(0, 0));
        dG.set(1, 0, x_hat.copy().mul(d_L_d_out).flatten(0, 0));
        this.norms.G.add(dG);

        final Tensor2D dva2 = d_L_d_out.copy().mul(gamma);
        final float dvar_inv = x_mu.copy().mul(dva2).flatten(0, 0);
        final float dvar = -0.5f * dvar_inv * Scalar.pow(var_inv, 3);
        final Tensor2D dxmu = (Tensor2D) dva2.copy().mul(var_inv).add(x_mu.copy().mul(2.0f * dvar / N));
        final float dmu = -dxmu.flatten(0, 0) / N;
        return dxmu.add(dmu);
    }

    public void completeBackward(final Optimizer optimizer) {
        this.norms.W.sub(optimizer.computeGradients(this.norms));
    }

    public void fromJSON(final JSONObject json) {
        this.norms.fromJSON(json.getJSONObject("norms"));
        this.mu_run = json.getFloat("mu_r");
        this.var_run = json.getFloat("var_r");
    }

    public JSONObject toJSON() {
        final JSONObject json = JSON.newJSONObject();
        json.setJSONObject("norms", this.norms.toJSON());
        json.setFloat("mu_r", this.mu_run);
        json.setFloat("var_r", this.var_run);
        return json;
    }

    private final Parameters2D norms;
    private float mu_run;
    private float var_run;
    // cache
    private float mu;
    private float var;
    private float var_inv;
    private Tensor2D x_mu;
    private Tensor2D x_hat;
}
