package com.github.romualdrousseau.shuju.ml.nn;

import java.util.LinkedList;
import java.util.function.Consumer;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.math.Tensor1D;
import com.github.romualdrousseau.shuju.math.Tensor2D;

public class Model {
    public Model() {
    }

    public Layer model(Tensor1D input) {
        return this.model(new Tensor2D(input, false));
    }

    public Layer model(Tensor2D input) {
        for(Layer layer : this.layers) {
            layer.output = layer.callForward(input);
            layer.lastInput = input;
            input = layer.output;
        }
        return this.layers.getLast();
    }

    public int getLastUnits() {
        return this.layers.getLast().units;
    }

    public int getLastChannels() {
        return this.layers.getLast().channels;
    }

    public void setTrainingMode(boolean training) {
        for(Layer layer : this.layers) {
            layer.training = training;
        }
    }

    public void reset() {
        for(Layer layer : this.layers) {
            layer.reset(false);
        }
    }

    public Model add(LayerBuilder<?> builder) {
        if(builder.inputUnits == 0) {
            builder.setInputUnits(this.layers.getLast().units);
        }
        if(builder.inputChannels == 0) {
            builder.setInputChannels(this.layers.getLast().channels);
        }
        return add(builder.build());
    }

    public Model add(Layer layer) {
        assert(this.layers.size() == 0 || layer.inputUnits == this.layers.getLast().units);
        assert(this.layers.size() == 0 || layer.inputChannels == this.layers.getLast().channels);
        layer.model = this;
        this.layers.add(layer);
        return this;
    }

    public void visit(Consumer<Layer> visitFunc) {
        this.layers.iterator().forEachRemaining(visitFunc);
    }

    public void visitBackward(Consumer<Layer> visitFunc) {
        this.layers.descendingIterator().forEachRemaining(visitFunc);
    }

    public void fromJSON(JSONArray json) {
        if (json.size() != this.layers.size()) {
            throw new IllegalArgumentException("model must match the model layout.");
        }
        int i = 0;
        for(Layer layer : this.layers) {
            layer.fromJSON(json.getJSONObject(i++));
        }
    }

    public JSONArray toJSON() {
        JSONArray json = JSON.newJSONArray();
        for(Layer layer : this.layers) {
            json.append(layer.toJSON());
        }
        return json;
    }

    private LinkedList<Layer> layers = new LinkedList<Layer>();
}
