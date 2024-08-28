package com.ericsson.ci.simnet;

import java.util.List;

import javax.inject.Inject;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.ericsson.ci.simnet.operators.OperatorInterface;
import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.DataDriven;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.annotations.Output;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.guice.OperatorRegistry;
import com.ericsson.cifwk.taf.handlers.netsim.domain.Simulation;

public class SimulationTest extends TorTestCaseHelper implements TestCase {
	
	@Inject
	OperatorRegistry <OperatorInterface> operatorRegistry;

	OperatorInterface apiOperator;
	Simulation simulation;
	List <String> cellNames;
	int totalNodes = 160;
	
	@BeforeSuite
	public void initialise()
	{
		apiOperator = operatorRegistry.provide(OperatorInterface.class);
		simulation = apiOperator.getSimulation();
		cellNames = apiOperator.getListOfCellNames(simulation);
	}
		
	@TestId(id = "OSS-69727", title = "Correct Number of Nodes in Simulation")
	@Context(context = { Context.API })
	@Test(groups = { "Acceptance" })
	public void verifyTestSimulationHasCorrectNumberOfNodes() 
	{
		System.out.println("Counting nodes on " + simulation.getName());
		Assert.assertEquals(simulation.getAllNEs().size(), totalNodes);
	}

	@TestId(id = "OSS-69727", title = "Correct Number of Started Nodes in Simulation")
	@Context(context = { Context.API })
	@Test(groups = { "Acceptance" })
	public void verifyTestSimulationHasAllNodesStarted() 
	{
		System.out.println("Counting started nodes on " + simulation.getName());
		Assert.assertEquals(simulation.getStartedNEs().size(), totalNodes);
	}
	
	@TestId(id = "OSS-69727", title = "Correct Cells in Simulation")
	@Context(context = { Context.API })
	@Test(groups = { "Acceptance" })
	@DataDriven(name = "APIScriptExecutionTest")
	public void verifySimulationHasAllCells(@Input("cellNumber") final Integer cellNumber, @Output("cellName") final String cellName) 
	{
		System.out.println("Counting cells in " + simulation.getName());
		Assert.assertEquals(cellNames.get(cellNumber - 1), cellName);
	}
}
