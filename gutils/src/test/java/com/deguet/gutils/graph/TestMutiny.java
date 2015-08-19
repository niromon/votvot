package com.deguet.gutils.graph;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Random;
import java.util.Set;

import org.junit.Test;

import com.deguet.gutils.graph.DGraph;
import com.deguet.gutils.graph.DGraphAdja;
import com.deguet.gutils.graph.DGraphMatrix;
import com.deguet.gutils.graph.DGraphNaive;
import com.deguet.gutils.graph.DGraphTiny;
import com.deguet.gutils.graph.DGraphs;
import com.deguet.gutils.graph.mutable.MuTiny;
import com.deguet.gutils.nuplets.Duo;

public class TestMutiny{	
	
	@Test
	public void testSimple(){
		MuTiny<Integer,Integer> graph = new MuTiny<Integer,Integer>();
		graph.addEdge(1, 2, 3);
		System.out.println(graph.toDot("test"));
	} 

}
