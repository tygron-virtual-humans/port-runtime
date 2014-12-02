package goal.parser;

import goal.core.program.validation.ValidatorError;
import goal.core.program.validation.ValidatorMessage;
import goal.core.program.validation.ValidatorWarning;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

public class WalkerHelper {
	protected final File file;
	protected final List<ValidatorError> errors;
	protected final List<ValidatorWarning> warnings;

	public WalkerHelper(File file) {
		this.file = file;
		this.errors = new LinkedList<>();
		this.warnings = new LinkedList<>();
	}

	public File getFile() {
		return this.file;
	}

	public InputStreamPosition getPosition(ParserRuleContext context) {
		return new InputStreamPosition(context.getStart(),
				context.getStop() == null ? context.getStart()
						: context.getStop(), this.file);
	}

	public InputStreamPosition getPosition(TerminalNode context) {
		return new InputStreamPosition(context.getSymbol(),
				context.getSymbol(), this.file);
	}

	public InputStreamPosition getPosition(List<TerminalNode> context) {
		if (context != null && !context.isEmpty()) {
			return getPosition(context.get(0));
		} else {
			return null;
		}
	}

	public void report(ValidatorMessage problem) {
		if (problem instanceof ValidatorError) {
			this.errors.add((ValidatorError) problem);
		} else if (problem instanceof ValidatorWarning) {
			this.warnings.add((ValidatorWarning) problem);
		} else {
			throw new RuntimeException("Unrecognized message type: "
					+ problem.getClass());
		}
	}

	public void merge(WalkerHelper otherHelper) {
		this.errors.addAll(otherHelper.getErrors());
		this.warnings.addAll(otherHelper.getWarnings());
	}

	public List<ValidatorError> getErrors() {
		return Collections.unmodifiableList(this.errors);
	}

	public List<ValidatorWarning> getWarnings() {
		return Collections.unmodifiableList(this.warnings);
	}

}
