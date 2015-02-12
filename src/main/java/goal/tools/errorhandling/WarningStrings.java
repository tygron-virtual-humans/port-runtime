package goal.tools.errorhandling;

import java.util.ResourceBundle;

/**
 * Collects all warning strings.
 *
 * @author W.Pasman 22may2014
 *
 */
@SuppressWarnings("javadoc")
public enum WarningStrings implements ResourceId {
	FAILED_ACK_NEW_AGENT, FAILED_ACK_DEL_AGENT, FAILED_ACK_LAUCHED_AGENT, FAILED_CALLBACK, BECAUSE, FAILED_DB_QUERY, FAILED_DEL_DBFORMULA, FAILED_DELETE_MSGBOX, FAILED_FREE_AGENT, FAILED_GB_QUERY, FAILED_ADD_DBFORMULA, FAILED_BROADCAST, FAILED_INIT_SWI3, FAILED_LAUNCH_AGENT, FAILED_LOG_TO_FILE, FAILED_MSG_RUNTIME, FAILED_PRECOMPILE_PARSE_PRECOND, FAILED_REMOVING_GOAL_FROM_GB, FAILED_RESET_AGENT, FAILED_RUNTIME_MSG, FAILED_SHUTDOWN_MSG_SERVER, FAILED_STOP_ENV, FAILED_STOP_ENV_SERV, FAILED_UPDATE_INTERNAL, FAILED_VALIDATE_USERSPEC, INTERRUPT_RESET_AGENT, INTERRUPT_STOP_RUNTIME, INTERRUPTED_DISPOSE_AGENT, MAX_REACHED_RULE_APPL, MISMATCH_ENTITY_NAME_RULE, MISMATCH_ENTITY_TYPE_RULE, NO_APPLICABLE_LAUNCH_RULE, FAILED_ENV_GET_REWARD, FAILED_ACTION_EXECUTE, FAILED_ACTION_SEND, FAILED_ACTION_AGENT_NOT_ATTACHED, FAILED_ADD_MODEL, FAILED_ENV_GET_PERCEPTS, FAILED_CREATE_ENV_PORT, FAILED_LOAD_ENV, FAILED_STOP_ENV_PORT, FAILED_START_ENV_PORT, INTERNAL_PROBLEM, FAILED_ENV_CALL, FAILED_SETTINGSFILE_CREATE, FAILED_SETTINGSFILE_FIND, FAILED_SETTINGSFILE_READ, PARSING_ERRORS, PARSING_COMPLETED, WITH_ERRORS, FOUND_ERRORS, WITH_WARNINGS, WARNING, JAVA_DETAILS, STACKDUMP, EMPTY_STACK_POINT, AT, SUPPRESS_FURTHER_WARNINGS, BY_DEEPER_EXCEPTION, FAILED_AWT_REFRESH_PANEL, FAILED_PANEL_CLOSE, FAILED_TAB_CLOSE, FAILED_FILE_FIND, FAILED_FILE_OPEN, FAILED_FILE_EDIT, FAILED_POPUP_WINDOW_CREATE, FAILED_FILE_RELOAD, FAILED_REMOVE_AFTER_RENAME, FAILED_ICON_GET, FAILED_PRINT_SETTINGS_SAVE, FAILED_OUTSTREAM_CLOSE, FAILED_INTROSPECTOR_OPEN, FAILED_ENV_START, FAILED_AGENT_RESTART, FAILED_LAF_NIMBUS, FAILED_OSXADAPTER_LOAD, FAILED_IDE_LAUNCH, FAILED_MAS_RELOAD, FAILED_GET_LINESTART, FAILED_SELECTION_NOTGOAL, FAILED_EDITACTION_NOT_AVAILABLE, FAILED_EXPORT_GOALS, FAILED_GOAL_QUERY, FAILED_EXPORT_BELIEF, FAILED_EXPORT_DATABASE, FAILED_SAVE_SELECTION, FAILED_EXPORT_NEEDS_AGENT, EXPORT_WHILE_RUNNING, FAILED_PROGANALYSIS_GENERATE, CANCELLED_ACTION, FAILED_REQUEST, HIT_GOAL_BUG, GOAL_EXCEPTION, RUNTIME_EXCEPTION, FAILED_INTROSPECT_NOT_LOCAL, FAILED_AGENT_KILL, FAILED_MAS_KILL, FAILED_KILL_ENV, FAILED_FILE_OPEN1, FAILED_IDE_FILENODE_INSERT, FAILED_FILE_OPEN2, FAILED_PAUSE_REMOTEAGT, FAILED_PAUSE, FAILED_FILE_RELOAD_NO_SELECTION, FAILED_RUN_NO_MAS, FAILED_RUN_MAS_ERRORS, FAILED_RUN_MAS, FAILED_SAVEALL, FAILED_FILE_SAVE, FAILED_ENV_PAUSE, FAILED_MEMINFO_CREATE, FAILED_BROWSER_OPEN, FAILED_CALLBACK_1, FAILED_EIS_GETTYPE1, FAILED_MSG_DELIVER_TO_ENVPORT, FAILED_CREATE_MSG_TO_INFORM_ENVPORT, FAILED_EIS_GETTYPE, FAILED_REPLY_AFTER_ACTION, FAILED_MSG_ENV, FAILED_MSG_ENV_STOPPED, FAILED_LISTENER_OFFLINE, FAILED_RULE_VALIDATE, FAILED_GET_PERCEPT, ENV_THROWS_UNCHECKED, ENV_DELETED_ENTITY;

	private static final String BUNDLE_NAME = "goal.tools.errorhandling.warningstrings"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	@Override
	public ResourceBundle getBundle() {
		return RESOURCE_BUNDLE;
	}

}
