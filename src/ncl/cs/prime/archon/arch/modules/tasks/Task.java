package ncl.cs.prime.archon.arch.modules.tasks;

import ncl.cs.prime.archon.arch.Estimation;
import ncl.cs.prime.archon.arch.InPort;
import ncl.cs.prime.archon.arch.Module;
import ncl.cs.prime.archon.arch.OutPort;

public class Task extends Module {

	public static Module.Declaration getDeclaration() {
		Declaration d = new Declaration();
		d.inputNames = new String[] {"req", "nextAck"};
		d.outputNames = new String[] {"ack", "nextReq"};
		return d;
	}	

	public String taskType = null;
	
	public double power = 1f;
	public long preDelay = 0L;
	public long postDelay = 0L;
	
	protected InPort.Int req = new InPort.Int(this);
	protected OutPort.Int ack = new OutPort.Int(this, null);
	protected OutPort.Int nextReq = new OutPort.Int(this, null);
	protected InPort.Int nextAck = new InPort.Int(this);

	@Override
	public void setup(String key, String value) {
		if("preDelay".equals(key))
			preDelay = Long.parseLong(value);
		else if("postDelay".equals(key))
			postDelay = Long.parseLong(value);
		else if("power".equals(key))
			power = Double.parseDouble(value);
		else if("type".equals(key))
			taskType = value.trim().toLowerCase();
	}

	@Override
	protected InPort<?>[] initInputs() {
		return new InPort<?>[] {req, nextAck};
	}

	@Override
	protected OutPort<?>[] initOutputs() {
		return new OutPort<?>[] {ack, nextReq};
	}

	@Override
	protected long update() {
		return 0;
	}
	
	protected long sendTime = 0L;
	
	protected Integer sendValue(Integer value) {
		return value;
	}
	
	protected Integer returnValue(Integer value) {
		return value;
	}
	
	@Override
	protected long update(Estimation est) {
		TaskEstimation e = (TaskEstimation) est;

		long t = 0L;
		if(req.getValue()==null) {
			ack.value = returnValue(nextAck.getValue());
			nextReq.value = null;
			if(ack.value!=null) {
				t = postDelay;
			}
		}
		else {
			//e.countException(name, 0);
			e.countTaskType(taskType);
			ack.value = null;
			nextReq.value = sendValue(req.getValue());
			t = preDelay;
			sendTime = getTime()+t;
		}

		e.useEnergy(name, true, power * t / 1000.0);
		return t;
	}

}
