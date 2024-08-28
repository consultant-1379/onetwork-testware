package com.ericsson.ci.simnet.operators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.Operator;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.execution.TestExecutionEvent;
import com.ericsson.cifwk.taf.handlers.netsim.CommandOutput;
import com.ericsson.cifwk.taf.handlers.netsim.NetSimCommandHandler;
import com.ericsson.cifwk.taf.handlers.netsim.NetSimResult;
import com.ericsson.cifwk.taf.handlers.netsim.commands.DumpmotreeCommand;
import com.ericsson.cifwk.taf.handlers.netsim.commands.NetSimCommands;
import com.ericsson.cifwk.taf.handlers.netsim.domain.NetworkElement;
import com.ericsson.cifwk.taf.handlers.netsim.domain.Simulation;
import com.ericsson.cifwk.taf.handlers.netsim.domain.SimulationGroup;
import com.ericsson.nms.host.HostConfigurator;
import com.google.inject.Singleton;

@Operator(context = Context.API)
@Singleton
public class NetsimHandlerService implements OperatorInterface {

	NetSimCommandHandler service;
	Host host = null;
	DumpmotreeCommand command;
	DumpmotreeCommand findNPPCICommand;
	
	SimulationGroup simsGroupList;
	Map<String, Simulation> simulations;
	
	//TODO move this to config file.
	String simName = "LTEE1220x160-TAF_Test-LTE01";
	int totalNodes = 160;
	
	String moIDPrefix = "ManagedElement=1, ENodeBFunction=1";
	String FDDCell = "EUtranCellFDD";
	String TDDCell = "EUtranCellTDD";
	
	SimulationGroup simGroup;
	Simulation simulation;
	
	@Override
	public Simulation getSimulation() 
	{
		host = HostConfigurator.getNetsim();
		System.out.println("Obtained host: " + host.getHostname());
		
		NetSimCommandHandler
				.setContextClosePolicy(TestExecutionEvent.ON_EXECUTION_FINISH);
		service = NetSimCommandHandler.getInstance(host);
		
		command = NetSimCommands.dumpmotree();
		command.setPrintattrs(false);
		command.setScope(2);
		command.setMotypes(new String[]{"EUtranCellFDD", "EUtranCellTDD"});
		
		simsGroupList = service.getSimulations(simName);
		return simsGroupList.get(simName);
	}
	
	@Override
	public List <String> getListOfCellNames(Simulation sim)
	{
		System.out.println("Checking for cells on " + simName);
		command.setMoid(moIDPrefix);
		
		List <String> cellNames = new ArrayList <String> ();
		List <NetworkElement> networkElements = getSimulation().getAllNEs();
		
		for(NetworkElement NE : networkElements)
		{
			System.out.println("Checking for cells on node: " + NE.getName());
			
			NetSimResult result = NE.exec(command);
			CommandOutput[] commandOutput = result.getOutput();
			List <String> results = returnCellNamesIfPresent(commandOutput);
			
			if(results.size() != 0)
			{
				cellNames.addAll(results);
			}
		}
		cellNames = removeCellTypeFromOutput(cellNames);
		
		return sortCellNames(cellNames);
	}
	
	
		
//---------------------------------------------------------------------------------------
//-----------------------------------PRIVATE METHODS-------------------------------------
//---------------------------------------------------------------------------------------
	
	
	private List <String> returnCellNamesIfPresent(CommandOutput[] commandOutput)
	{
		List <String> listOfResults = new ArrayList <String> ();
		if (commandOutput.length == 0)
		{
			return listOfResults;
		}
		
		for (int i = 0; i < commandOutput.length; i++)
		{
			List <String> output = commandOutput[i].asList();
			
			if (output.size() > 1)
			{
				output.remove(output.size() - 1);
			}
			
			listOfResults.addAll(output);
		}
		
		return listOfResults;
	}
	
	private List <String> removeCellTypeFromOutput( List <String> cellNamesOutput)
	{
		List <String> result = new ArrayList <String>();
		for(String cellName : cellNamesOutput)
		{
			result.add(cellName.split("=")[1]);
		}
		
		return result;
	}
	
	private List <String> sortCellNames (List <String> cellNames)
	{
		Collections.sort(cellNames,new Comparator<String>() {

	        @Override
	        public int compare(String o1, String o2) {
	        	String[] temp1 = o1.split("-");
	        	String[] temp2 = o2.split("-");
	        	
	        	if (temp1[0].equals(temp2[0]))
	        	{
	        		Integer integer1 = Integer.valueOf(temp1[1]);
	                Integer integer2 = Integer.valueOf(temp2[1]);
	        		return integer1.compareTo(integer2);
	        	}
	        	else
	        	{
	        		return o1.compareTo(o2);
	        	}
	        }
	    });
		
		return cellNames;
	}
}
