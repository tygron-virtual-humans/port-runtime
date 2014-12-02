package goal.parser.mas;

import eis.iilang.Function;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;
import goal.core.kr.KRFactory;
import goal.core.mas.AgentFile;
import goal.core.mas.EntityDesc;
import goal.core.mas.EnvironmentInfo;
import goal.core.mas.Launch;
import goal.core.mas.LaunchRule;
import goal.core.mas.MASProgram;
import goal.core.mas.MultiLaunch;
import goal.core.program.validation.ValidatorError;
import goal.core.program.validation.ValidatorWarning;
import goal.core.program.validation.agentfile.GOALError;
import goal.core.program.validation.masfile.MASError;
import goal.core.program.validation.masfile.MASWarning;
import goal.parser.InputStreamPosition;
import goal.parser.WalkerHelper;
import goal.parser.WalkerInterface;
import goal.parser.antlr.MASLexer;
import goal.parser.antlr.MASParser;
import goal.parser.antlr.MASParserBaseVisitor;

import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings("rawtypes")
public class MASWalker extends MASParserBaseVisitor implements WalkerInterface {
	protected MASParser parser;
	protected WalkerHelper wh;
	protected MASProgram program;

	public MASWalker(File mas2g, MASParser parser, MASLexer lexer) {
		this.wh = new WalkerHelper(mas2g);
		parser.removeErrorListeners();
		parser.addErrorListener(this);
		this.parser = parser;
		lexer.removeErrorListeners();
		lexer.addErrorListener(this);
		this.program = null;
	}

	public MASProgram getProgram() {
		return visitMas(this.parser.mas());
	}

	public void setProgram(MASProgram program) {
		this.program = program;
	}

	public WalkerHelper getWalkerHelper() {
		return this.wh;
	}

	public void setWalkerHelper(WalkerHelper helper) {
		this.wh = helper;
	}

	public MASParser getParser() {
		return this.parser;
	}

	public void setParser(MASParser parser) {
		this.parser = parser;
	}

	@Override
	public MASProgram visitMas(MASParser.MasContext ctx) {
		if (this.program == null) {
			try {
				this.program = new MASProgram(this.wh.getPosition(ctx));
			} catch (Exception any) {
				this.wh.report(new ValidatorError(
						GOALError.EXTERNAL_OR_UNKNOWN,
						this.wh.getPosition(ctx), any.getMessage()));
				return null;
			}
		}
		if (ctx.environment() != null) {
			try {
				EnvironmentInfo info = visitEnvironment(ctx.environment());
				if (info != null) {
					this.program.setEnvironmentInfo(info);
				}
			} catch (Exception any) {
				this.wh.report(new ValidatorError(
						GOALError.EXTERNAL_OR_UNKNOWN,
						this.wh.getPosition(ctx), any.getMessage()));
				return null;
			}
		}
		if (ctx.agentFiles() != null) {
			try {
				List<AgentFile> agents = visitAgentFiles(ctx.agentFiles());
				if (agents != null) {
					this.program.setAgentFiles(agents,
							this.wh.getPosition(ctx.agentFiles()));
				}
			} catch (Exception any) {
				this.wh.report(new ValidatorError(
						GOALError.EXTERNAL_OR_UNKNOWN,
						this.wh.getPosition(ctx), any.getMessage()));
				return null;
			}
		}
		if (ctx.launchPolicy() != null) {
			try {
				List<MultiLaunch> policy = visitLaunchPolicy(ctx.launchPolicy());
				if (policy != null) {
					this.program.setLaunchPolicy(policy,
							this.wh.getPosition(ctx.launchPolicy()));
				}
			} catch (Exception any) {
				this.wh.report(new ValidatorError(
						GOALError.EXTERNAL_OR_UNKNOWN,
						this.wh.getPosition(ctx), any.getMessage()));
				return null;
			}
		}
		this.program.postProcess();
		return this.program;
	}

	// -------------------------------------------------------------
	// ENVIRONMENT section
	// -------------------------------------------------------------

	@Override
	public EnvironmentInfo visitEnvironment(MASParser.EnvironmentContext ctx) {
		EnvironmentInfo info = new EnvironmentInfo(this.wh.getPosition(ctx));
		if (ctx.environmentFile() != null) {
			String filename = visitEnvironmentFile(ctx.environmentFile());
			if (filename != null) {
				info.addJar(filename);
			} else {
				this.wh.report(new ValidatorError(
						MASError.ENVIRONMENT_CANNOT_FIND, this.wh
								.getPosition(ctx.environmentFile()), ctx
								.environmentFile().getText()));
			}
		} else {
			this.wh.report(new ValidatorError(MASError.ENVIRONMENT_CANNOT_FIND,
					this.wh.getPosition(ctx), ctx.getText()));
		}
		if (ctx.initParams() != null) {
			Map<String, Parameter> params = visitInitParams(ctx.initParams());
			if (params != null) {
				info.setInitParameters(params);
			}
		}
		return info;
	}

	@Override
	public String visitEnvironmentFile(MASParser.EnvironmentFileContext ctx) {
		String path = null;
		if (ctx.DOUBLESTRING() != null) {
			path = ctx.DOUBLESTRING().getText().trim().replace("\"", "");
		}
		return path;
	}

	@Override
	public Map<String, Parameter> visitInitParams(
			MASParser.InitParamsContext ctx) {
		Map<String, Parameter> params = new HashMap<>(ctx.initParam().size());
		for (MASParser.InitParamContext initparam : ctx.initParam()) {
			Map.Entry<String, Parameter> param = visitInitParam(initparam);
			if (param != null) { // elements are non-null as well
				String key = param.getKey();
				Parameter value = param.getValue();
				if (params.containsKey(key)) {
					this.wh.report(new ValidatorError(
							MASError.INIT_DUPLICATE_PARAMETER, this.wh
									.getPosition(initparam), initparam
									.getText()));
				} else {
					params.put(key, value);
				}
			} else {
				this.wh.report(new ValidatorError(
						MASError.INIT_UNRECOGNIZED_PARAMETER, this.wh
								.getPosition(initparam), initparam.getText()));
			}
		}
		return params;
	}

	@Override
	public Map.Entry<String, Parameter> visitInitParam(
			MASParser.InitParamContext ctx) {
		String key = null;
		Parameter value = null;
		if (ctx.ID() != null) {
			key = ctx.ID().getText().trim();
		}
		if (ctx.initValue() != null) {
			value = visitInitValue(ctx.initValue());
		}
		if (key == null || value == null) {
			return null;
		} else {
			return new AbstractMap.SimpleEntry<>(key, value);
		}
	}

	@Override
	public List<Parameter> visitInitValues(MASParser.InitValuesContext ctx) {
		List<Parameter> values = new ArrayList<>(ctx.initValue().size());
		for (MASParser.InitValueContext initvalue : ctx.initValue()) {
			Parameter value = visitInitValue(initvalue);
			if (value != null) {
				values.add(value);
			} else {
				this.wh.report(new ValidatorError(
						MASError.INIT_UNRECOGNIZED_PARAMETER, this.wh
								.getPosition(initvalue), initvalue.getText()));
			}
		}
		return values;
	}

	@Override
	public Parameter visitInitValue(MASParser.InitValueContext ctx) {
		Parameter returned = null;
		if (ctx.simpleInitValue() != null) {
			returned = visitSimpleInitValue(ctx.simpleInitValue());
		} else if (ctx.functionInitValue() != null) {
			returned = visitFunctionInitValue(ctx.functionInitValue());
		} else if (ctx.listInitValue() != null) {
			returned = visitListInitValue(ctx.listInitValue());
		}
		return returned;
	}

	@Override
	public Parameter visitSimpleInitValue(MASParser.SimpleInitValueContext ctx) {
		Parameter returned = null;
		if (ctx.FLOAT() != null) {
			String text = ctx.FLOAT().getText().trim();
			try {
				returned = new Numeral(Double.parseDouble(text));
			} catch (Exception e) {
			}
		} else if (ctx.INT() != null) {
			String text = ctx.INT().getText().trim();
			try {
				returned = new Numeral(Integer.parseInt(text));
			} catch (Exception e) {
			}
		} else if (ctx.ID() != null) {
			String text = ctx.ID().getText().trim();
			returned = new Identifier(text);
		} else if (ctx.DOUBLESTRING() != null) {
			String text = ctx.DOUBLESTRING().getText();
			try {
				String[] parts = text.split("(?<!\\\\)\"", 0);
				String string = parts[1].replace("\\\"", "\"");
				returned = new Identifier(string.trim());
			} catch (Exception e) {
			}
		} else if (ctx.SINGLESTRING() != null) {
			String text = ctx.SINGLESTRING().getText();
			try {
				String[] parts = text.split("(?<!\\\\)'", 0);
				String string = parts[1].replace("\\'", "'");
				returned = new Identifier(string.trim());
			} catch (Exception e) {
			}
		}
		return returned;
	}

	@Override
	public Function visitFunctionInitValue(
			MASParser.FunctionInitValueContext ctx) {
		Function returned = null;
		if (ctx.ID() != null) {
			String functionName = ctx.ID().getText().trim();
			List<Parameter> functionParams = visitInitValues(ctx.initValues());
			Parameter[] fParams = new Parameter[functionParams.size()];
			fParams = functionParams.toArray(fParams);
			returned = new Function(functionName, fParams);
		}
		return returned;
	}

	@Override
	public ParameterList visitListInitValue(MASParser.ListInitValueContext ctx) {
		List<Parameter> listParams = visitInitValues(ctx.initValues());
		return new ParameterList(listParams);
	}

	// -------------------------------------------------------------
	// AGENTFILES section
	// -------------------------------------------------------------

	@Override
	public List<AgentFile> visitAgentFiles(MASParser.AgentFilesContext ctx) {
		List<AgentFile> agents = new ArrayList<>(ctx.agentFile().size());
		for (MASParser.AgentFileContext file : ctx.agentFile()) {
			AgentFile agent = visitAgentFile(file);
			if (agent != null) {
				agents.add(agent);
			} else {
				this.wh.report(new ValidatorError(
						MASError.AGENTFILE_CANNOT_FIND, this.wh
								.getPosition(file), file.getText()));
			}
		}
		return agents;
	}

	@Override
	public AgentFile visitAgentFile(MASParser.AgentFileContext ctx) {
		AgentFile agent = null;
		if (ctx.DOUBLESTRING() == null) {
			return null;
		}
		String path = ctx.DOUBLESTRING().getText().trim().replace("\"", "");
		if (!path.endsWith(".goal")) {
			this.wh.report(new ValidatorWarning(
					MASWarning.AGENTFILE_OTHEREXTENSION, this.wh
							.getPosition(ctx.DOUBLESTRING()), path));
			return null;
		}
		agent = new AgentFile(path, wh.getFile().getParentFile()
				.getAbsolutePath(), this.wh.getPosition(ctx));
		if (ctx.agentFileParameters() != null) {
			Map<String, String> params = visitAgentFileParameters(ctx
					.agentFileParameters());
			for (Map.Entry<String, String> entry : params.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				if (key.equalsIgnoreCase("name")) {
					agent.setName(value);
				} else if (key.equalsIgnoreCase("language")) {
					agent.setKRLang(KRFactory.get(value));
				}
			}
		}
		return agent;
	}

	@Override
	public Map<String, String> visitAgentFileParameters(
			MASParser.AgentFileParametersContext ctx) {
		Map<String, String> params = new HashMap<>(ctx.agentFileParameter()
				.size());
		for (MASParser.AgentFileParameterContext fileparam : ctx
				.agentFileParameter()) {
			Map.Entry<String, String> param = visitAgentFileParameter(fileparam);
			if (param != null) { // elements are non-null as well
				String key = param.getKey();
				String value = param.getValue();
				if (params.containsKey(key)) {
					this.wh.report(new ValidatorError(
							MASError.AGENTFILE_DUPLICATE_PARAMETER, this.wh
									.getPosition(fileparam), fileparam
									.getText()));
				} else {
					params.put(key, value);
				}
			} else {
				this.wh.report(new ValidatorError(
						MASError.AGENTFILE_UNRECOGNIZED_PARAMETER, this.wh
								.getPosition(fileparam), fileparam.getText()));
			}
		}
		return params;
	}

	@Override
	public Map.Entry<String, String> visitAgentFileParameter(
			MASParser.AgentFileParameterContext ctx) {
		Map.Entry<String, String> returned = null;
		if (ctx.ID() != null) {
			String ID = ctx.ID().getText().trim();
			if (ctx.NAME() != null) {
				String key = ctx.NAME().getText().trim();
				returned = new AbstractMap.SimpleEntry<>(key, ID);
			} else if (ctx.LANGUAGE() != null) {
				String key = ctx.LANGUAGE().getText().trim();
				returned = new AbstractMap.SimpleEntry<>(key, ID);
			}
		}
		return returned;
	}

	// -------------------------------------------------------------
	// LAUNCHPOLICY section
	// -------------------------------------------------------------

	@Override
	public List<MultiLaunch> visitLaunchPolicy(MASParser.LaunchPolicyContext ctx) {
		List<MultiLaunch> launchRules = new ArrayList<>(ctx.launchRule().size());
		for (MASParser.LaunchRuleContext launchRule : ctx.launchRule()) {
			MultiLaunch launch = visitLaunchRule(launchRule);
			if (launch != null) {
				if (launchRules.contains(launch)) {
					this.wh.report(new ValidatorError(
							MASError.LAUNCH_DUPLICATE, this.wh
									.getPosition(launchRule), launchRule
									.getText()));
				} else {
					launchRules.add(launch);
				}
			} else {
				this.wh.report(new ValidatorError(
						MASError.LAUNCH_INVALID_DEFINITION, this.wh
								.getPosition(launchRule), launchRule.getText()));
			}
		}
		return launchRules;
	}

	@Override
	public MultiLaunch visitLaunchRule(MASParser.LaunchRuleContext ctx) {
		MultiLaunch returned = null;
		if (ctx.simpleLaunchRule() != null) {
			returned = visitSimpleLaunchRule(ctx.simpleLaunchRule());
		} else if (ctx.conditionalLaunchRule() != null) {
			returned = visitConditionalLaunchRule(ctx.conditionalLaunchRule());
		}
		return returned;
	}

	@Override
	public MultiLaunch visitSimpleLaunchRule(
			MASParser.SimpleLaunchRuleContext ctx) {
		MultiLaunch multi = null;
		if (ctx.launchRuleComponents() != null) {
			List<Launch> launches = visitLaunchRuleComponents(ctx
					.launchRuleComponents());
			if (launches != null) {
				multi = new MultiLaunch();
				multi.addLaunches(launches);
			}
		}
		return multi;
	}

	@Override
	public List<Launch> visitLaunchRuleComponents(
			MASParser.LaunchRuleComponentsContext ctx) {
		List<Launch> rules = new ArrayList<>(ctx.launchRuleComponent().size());
		for (MASParser.LaunchRuleComponentContext rule : ctx
				.launchRuleComponent()) {
			rules.add(visitLaunchRuleComponent(rule));
		}
		return rules;
	}

	@Override
	public Launch visitLaunchRuleComponent(
			MASParser.LaunchRuleComponentContext ctx) {
		Launch launch = new Launch(this.wh.getPosition(ctx));
		if (ctx.ID() != null) {
			String name = ctx.ID().getText().trim();
			launch.setAgentBaseName(name);
		} else {
			launch.setAgentBaseName("*");
		}
		if (ctx.INT() != null) {
			String number = ctx.INT().getText().trim();
			launch.setAgentNumber(Integer.parseInt(number));
		}
		if (ctx.AGENTFILENAME() != null) {
			String file = ctx.AGENTFILENAME().getText().trim();
			launch.setAgentFileRef(file.substring(1).trim(),
					this.wh.getPosition(ctx));
		} else {
			launch.setAgentFileRef(launch.getAgentBaseName(),
					this.wh.getPosition(ctx));
		}
		return launch;
	}

	@Override
	public LaunchRule visitConditionalLaunchRule(
			MASParser.ConditionalLaunchRuleContext ctx) {
		LaunchRule rule = new LaunchRule();
		EntityDesc desc = visitEntityDescription(ctx.entityDescription());
		rule.setEntityDesc(desc); // not-null
		MultiLaunch simple = visitSimpleLaunchRule(ctx.simpleLaunchRule());
		if (simple != null) {
			rule.addLaunches(simple.getLaunches());
			return rule;
		} else {
			return null;
		}

	}

	@Override
	public EntityDesc visitEntityDescription(
			MASParser.EntityDescriptionContext ctx) {
		EntityDesc desc = new EntityDesc();
		if (ctx.entityConstraints() != null) {
			Map<String, String> constraints = visitEntityConstraints(ctx
					.entityConstraints()); // contains only valid values
			for (Map.Entry<String, String> constraint : constraints.entrySet()) {
				String key = constraint.getKey();
				String value = constraint.getValue();
				if (key.equalsIgnoreCase("name")) {
					desc.setName(value);
				} else if (key.equalsIgnoreCase("type")) {
					desc.setType(value);
				} else if (key.equalsIgnoreCase("max")) {
					desc.setMax(Integer.parseInt(value));
				}
			}
		}
		return desc;
	}

	@Override
	public Map<String, String> visitEntityConstraints(
			MASParser.EntityConstraintsContext ctx) {
		Map<String, String> constraints = new HashMap<>(ctx.entityConstraint()
				.size());
		for (MASParser.EntityConstraintContext entityConstraint : ctx
				.entityConstraint()) {
			Map.Entry<String, String> constraint = visitEntityConstraint(entityConstraint);
			if (constraint != null) { // elements are non-null as well
				String key = constraint.getKey();
				String value = constraint.getValue();
				if (constraints.containsKey(key)) {
					this.wh.report(new ValidatorError(
							MASError.CONSTRAINT_DUPLICATE, this.wh
									.getPosition(entityConstraint),
							entityConstraint.getText()));
				} else {
					constraints.put(key, value);
				}
			} else {
				this.wh.report(new ValidatorError(
						MASError.CONSTRAINT_INVALID_DEFINITION, this.wh
								.getPosition(entityConstraint),
						entityConstraint.getText()));
			}
		}
		return constraints;
	}

	@Override
	public Map.Entry<String, String> visitEntityConstraint(
			MASParser.EntityConstraintContext ctx) {
		Map.Entry<String, String> returned = null;
		if (ctx.ID() != null) {
			String ID = ctx.ID().getText().trim();
			if (ctx.NAME() != null) {
				String key = ctx.NAME().getText().trim();
				returned = new AbstractMap.SimpleEntry<>(key, ID);
			} else if (ctx.TYPE() != null) {
				String key = ctx.TYPE().getText().trim();
				returned = new AbstractMap.SimpleEntry<>(key, ID);
			}
		} else if (ctx.MAX() != null && ctx.INT() != null) {
			String key = ctx.MAX().getText().trim();
			String digits = ctx.INT().getText().trim();
			returned = new AbstractMap.SimpleEntry<>(key, digits);
		}
		return returned;
	}

	// -------------------------------------------------------------
	// Initialization helpers
	// -------------------------------------------------------------
	public static MASWalker getWalker(File file) throws Exception {
		return getWalker(file, null);
	}

	public static MASWalker getWalker(File file, InputStreamPosition pos)
			throws Exception {
		ANTLRFileStream antlrStream = new ANTLRFileStream(file.getPath());
		MASLexer lexer = new MASLexer(antlrStream);
		CommonTokenStream stream = new CommonTokenStream(lexer);
		MASParser parser = new MASParser(stream);
		MASWalker walker = new MASWalker(file, parser, lexer);
		return walker;
	}

	// -------------------------------------------------------------
	// WalkerInterface implementation
	// -------------------------------------------------------------

	@Override
	public List<ValidatorError> getErrors() {
		return this.wh.getErrors();
	}

	@Override
	public List<ValidatorWarning> getWarnings() {
		return this.wh.getWarnings();
	}

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer,
			Object offendingSymbol, int line, int charPositionInLine,
			String msg, RecognitionException e) {
		int start = recognizer.getInputStream().index();
		int stop = start;
		if (offendingSymbol != null) {
			CommonToken token = (CommonToken) offendingSymbol;
			start = token.getStartIndex();
			stop = token.getStopIndex();
		}
		final InputStreamPosition pos = new InputStreamPosition(line,
				charPositionInLine, start, stop, this.wh.getFile());
		if (msg == null || msg.isEmpty()) {
			msg = e.getMessage();
		} // TODO: custom errors?!
		this.wh.report(new ValidatorError(GOALError.EXTERNAL_OR_UNKNOWN, pos,
				msg));
	}

	@Override
	public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex,
			int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
	}

	@Override
	public void reportAttemptingFullContext(Parser recognizer, DFA dfa,
			int startIndex, int stopIndex, BitSet conflictingAlts,
			ATNConfigSet configs) {
	}

	@Override
	public void reportContextSensitivity(Parser recognizer, DFA dfa,
			int startIndex, int stopIndex, int prediction, ATNConfigSet configs) {
	}
}
