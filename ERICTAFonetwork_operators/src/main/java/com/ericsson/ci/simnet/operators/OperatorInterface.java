package com.ericsson.ci.simnet.operators;

import java.util.List;

import com.ericsson.cifwk.taf.handlers.netsim.domain.Simulation;

public interface OperatorInterface {
   
    public Simulation getSimulation();
    
    public List <String> getListOfCellNames(Simulation sim);

}