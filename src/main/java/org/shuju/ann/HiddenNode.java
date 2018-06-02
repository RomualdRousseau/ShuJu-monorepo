package org.shuju.ann;

import java.util.ArrayList;

class HiddenNode extends Node
{
  public HiddenNode() {
    this.incomingEdges = new ArrayList<Edge>();
    this.outcomingEdges = new ArrayList<Edge>();
    addBias();
  }
}
