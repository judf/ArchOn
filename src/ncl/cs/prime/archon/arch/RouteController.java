package ncl.cs.prime.archon.arch;

import java.util.LinkedList;
import java.util.Random;

public class RouteController {

	private Architecture arch;
	private Estimation est = null;
	
	public boolean syncNext = false;
	public boolean randomOrder = false;
	public boolean timeOrder = false;
	private static Random random = new Random();
	
	public RouteController(Architecture arch) {
		this.arch = arch;
	}
	
	public void setEstimation(Estimation est) {
		this.est = est;
		est.init(arch);
	}
	
	public void configure(int destId, int config) {
		arch.getModuleById(destId).setConfig(config);
	}
	
	public void connect(int destId, int srcId) throws ArchitectureException {
		// TODO check if connection allowed
		arch.getModuleById(destId).getInputs()[Architecture.portFromId(destId)].connect(
				arch.getModuleById(srcId).getOutputs()[Architecture.portFromId(srcId)]
			);
	}
	
	public void connect(int destId, int srcId, int srcFlag, boolean neg) throws ArchitectureException {
		// TODO check if connection allowed
		arch.getModuleById(destId).getInputs()[Architecture.portFromId(destId)].connect(
				arch.getModuleById(srcId).getOutputs()[Architecture.portFromId(srcId)],
				arch.getModuleById(srcId).getFlags()[srcFlag],
				neg
			);
	}
	
	public void disconnect(int destId) {
		arch.getModuleById(destId).getInputs()[Architecture.portFromId(destId)].disconnect();
	}
	
	public void disconnectAll() {
		for(InPort<?> p : arch.inputs) {
			p.disconnect();
		}
	}
	
	/**
	 * @return false if nothing changed (ineffective simulation)
	 */
	private boolean pullData() {
		boolean res = false;
		for(InPort<?> p : arch.inputs) {
			if(p.pullData())
				res = true;
		}
		return res;
	}
	
	private void recompute() {
		if(est!=null)
			est.beginCycle();
		
		if(randomOrder) {
			LinkedList<Integer> indices = new LinkedList<>();
			int n = arch.modules.size();
			for(int i=0; i<n; i++)
				indices.add(i);
			while(n>0) {
				int i = indices.remove(random.nextInt(n));
				arch.modules.get(i).recompute(est);
				n--;
			}
		}
		else if(timeOrder) {
			long min = -1L;
			for(Module m : arch.modules) {
				if(m.getTime()<min || min<0)
					min = m.getTime();
			}
			for(Module m : arch.modules) {
				if(m.getTime()==min)
					m.recompute(est);
			}
		}
		else {
			for(Module m : arch.modules) {
				m.recompute(est);
			}
		}
		if(syncNext)
			arch.syncTime();
		if(est!=null)
			est.endCycle();
	}
	
	public boolean next() {
		pullData();
		recompute();
		return true;
	}
	
	public boolean checkCondition(int destId) {
		return arch.getModuleById(destId).getFlags()[Architecture.portFromId(destId)].value;
	}
	
	public void setup(int destId, String keyValues) {
		arch.getModuleById(destId).setup(keyValues);
	}

	public void nameModule(int destId, String name) {
		arch.getModuleById(destId).setName(name);
	}

	public void debugSetIntValue(int destId, int v) {
		Module module = arch.getModuleById(destId);
		if(module==null) {
			System.err.println("Warning: module "+((destId & Architecture.MODULE_MASK)>>8)+" is null");
			return;
		}
		OutPort<?> port = module.getOutputs()[Architecture.portFromId(destId)];
		port.debugSetIntValue(v);
	}
	
	public int debugGetIntValue(int destId) {
		return arch.getModuleById(destId).getOutputs()[Architecture.portFromId(destId)].debugGetIntValue(); 
	}
	
}
