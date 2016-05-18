package ncl.cs.prime.archon.arch.modules.arm;

import ncl.cs.prime.archon.arch.InPort;
import ncl.cs.prime.archon.arch.Module;
import ncl.cs.prime.archon.arch.OutPort;

public class Multiplier extends ArmModule {

	public static final long TIME = 5;
	
	public static Module.Declaration getDeclaration() {
		Declaration d = new Declaration();
		d.inputNames = new String[] {"n", "m"};
		d.outputNames = new String[] {"d"};
		return d;
	}
	
	protected InPort<Integer> n = new InPort<>(this);
	protected InPort<Integer> m = new InPort<>(this);
	protected OutPort<Integer> d = new OutPort<Integer>(this, -1);
	
	@Override
	protected InPort<?>[] initInputs() {
		return new InPort<?>[] {n, m};
	}

	@Override
	protected OutPort<?>[] initOutputs() {
		return new OutPort<?>[] {d};
	}
	
	@Override
	protected String getResourceName() {
		return "mul";
	}

	@Override
	protected long update() {
		d.value = n.getValue() * m.getValue();
		return TIME;
	}

}
