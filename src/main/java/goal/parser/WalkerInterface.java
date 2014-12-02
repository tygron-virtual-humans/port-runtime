package goal.parser;

import goal.core.program.validation.ValidatorError;
import goal.core.program.validation.ValidatorWarning;

import java.util.List;

import org.antlr.v4.runtime.ANTLRErrorListener;

public interface WalkerInterface extends ANTLRErrorListener {
	public List<ValidatorError> getErrors();

	public List<ValidatorWarning> getWarnings();
}
