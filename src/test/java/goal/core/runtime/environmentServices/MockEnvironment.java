package goal.core.runtime.environmentServices;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;

import eis.EIDefaultImpl;
import eis.exceptions.ActException;
import eis.exceptions.EntityException;
import eis.exceptions.ManagementException;
import eis.exceptions.NoEnvironmentException;
import eis.exceptions.PerceiveException;
import eis.iilang.Action;
import eis.iilang.EnvironmentState;
import eis.iilang.Parameter;
import eis.iilang.Percept;

final class MockEnvironment extends EIDefaultImpl {
	/**
	 *
	 */
	private static final long serialVersionUID = 8751467335725577307L;

	@Override
	public void init(Map<String, Parameter> parameters)
			throws ManagementException {
		super.init(parameters);

		setState(EnvironmentState.PAUSED);

		setState(EnvironmentState.RUNNING);

		try {
			this.addEntity("existingEntity");
		} catch (EntityException e) {
			// FIXME: add entity is internal to the EIS.
			// Should not throw exceptions.
			throw new ManagementException("...", e);
		}

	}

	@Override
	public void reset(Map<String, Parameter> parameters)
			throws ManagementException {
		super.reset(parameters);

		setState(EnvironmentState.RUNNING);
	}

	@Override
	public String queryProperty(String property) {

		if (property.startsWith("REWARD")) {
			return new Double(42.0).toString();
		}

		return null;
	}

	@Override
	public String requiredVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Percept performEntityAction(String entity, Action arg1)
			throws ActException {

		if (!entity.equals("existingEntity")) {
			throw new ActException("No such entity");
		}

		return new Percept(arg1.getName());

	}

	@Override
	protected boolean isSupportedByType(Action arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean isSupportedByEnvironment(Action arg0) {
		return true;
	}

	@Override
	protected boolean isSupportedByEntity(Action arg0, String arg1) {
		return true;
	}

	@Override
	protected LinkedList<Percept> getAllPerceptsFromEntity(String arg0)
			throws PerceiveException, NoEnvironmentException {
		return new LinkedList<Percept>(Arrays.asList(new Percept(arg0)));
	}
}