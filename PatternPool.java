package oop.ex6.main;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static oop.ex6.main.DataMember.*;

class PatternPool {

	//regex constants
	static final String IF_WHILE_PARAM_SPLIT = "(^.*[(]\\s*)|(\\s*&&\\s*)|(\\s*\\|\\|\\s*)|\\s*[)].*$";
	static final List<String> DATA_MEMBERS_TYPES = Arrays.asList("int", "double", "String",
			"boolean", "char", "final");


	private static final String METHOD_NAME = "([a-zA-Z]+[\\w]*)";


	static final String CATCH_PARAMETERS_REGEX = "[\\w].*(?=[(])|[^\\w\"']";


	private static final String RETURN = "\\s*return\\s*;\\s*";


	private static final String CLOSING_BRACKET = "\\s*\\}\\s*";


	private static final String OR = "|";


	private static final String OPEN_PARAM_BOX = "(\\s*[(]\\s*)";


	private static final String CLOSE_PARAM_BOX = "(\\s*[)]\\s*)";


	private static final String EMPTY_PARAM_BOX_GROUP = "(\\s*\\(\\s*\\)\\s*)";


	private static final String PARAM_NAME = DataMember.NAME_CHECK;


	private static final String DATA_MEMBER_ASSIGNMENT = "(" + BOOL_CHECK + OR + STRING_CHECK + OR +
			CHAR_CHECK + OR + PARAM_NAME + ")";


	private static final String COMMA = "(\\s*(\\,)\\s*)";


	private static final String FULL_PARAM_BOX_CALL = "(" + OPEN_PARAM_BOX + DATA_MEMBER_ASSIGNMENT +
			"(" + COMMA + DATA_MEMBER_ASSIGNMENT + ")*" + CLOSE_PARAM_BOX + ")";


	private static final String PARAM_TYPE = "((\\s*final\\s+)?\\s*(String|int|double|char|boolean)\\s+)";

	private static final String FULL_PARAM_BOX_DECLARE = "(\\s*\\((" + PARAM_TYPE + PARAM_NAME + ")" + "(" +
			COMMA + PARAM_TYPE + PARAM_NAME + ")*" + CLOSE_PARAM_BOX + ")";

	private static final String PARAM_BOX_CALL = "(" + EMPTY_PARAM_BOX_GROUP + OR + FULL_PARAM_BOX_CALL + ")";

	private static final String INVOKE_CHECK = "(\\s*" + METHOD_NAME + PARAM_BOX_CALL + ");\\s*";

	private static final String BEGIN_WITH_VOID = "^void\\s+";

	private static final String END_WITH_OPEN_BRACKET = "(\\{)";

	private static final String PARAM_BOX = "(" + EMPTY_PARAM_BOX_GROUP + OR + FULL_PARAM_BOX_DECLARE + ")";
	private static final String METHOD_DECLARE_CHECK = "(" + BEGIN_WITH_VOID + METHOD_NAME + PARAM_BOX + END_WITH_OPEN_BRACKET + ")";
	private static final String OPENING_BRACKET = "\\{";
	private static final String COMMENT_REGEX = "((//.*)" + OR + "\\s*)";
	private static final String IF_WHILE_CALL = "(" + "((if)|(while))" + OPEN_PARAM_BOX + BOOL_CHECK +
			CLOSE_PARAM_BOX + OPENING_BRACKET + ")";
	private static final String EMPTY_LINE = "()";
	private static final String FINISH_LINE = "(" + RETURN + OR + EMPTY_LINE + OR + CLOSING_BRACKET + ")";
	private static final String CONDITIONABLE = BOOL_CHECK;
	 static final String LINE_OPENER = "[(]|\\s";
	private static final String WHITESPACE_BEFORE_COMMENT = "\\s+(//).*";

	static final String PARAMETER_CATCH_INVOCATION_REGEX = "(void\\s+)|([(])|([,])|([)].*)";
	private static final String KOFIKO = "\\s*"; // FOR GOOD VIBES.

	static final Pattern methodDeclareCheck = Pattern.compile(METHOD_DECLARE_CHECK);
	static final Pattern kofiko = Pattern.compile(KOFIKO);
	static final Pattern comment = Pattern.compile(COMMENT_REGEX);
	static final Pattern ifWhileInvoke = Pattern.compile(IF_WHILE_CALL);
	static final Pattern finishLine = Pattern.compile(FINISH_LINE);
	static final Pattern conditionable = Pattern.compile(CONDITIONABLE);
	static final Pattern whitespaceBeforeComment = Pattern.compile(WHITESPACE_BEFORE_COMMENT);
	static final Pattern returnCheck = Pattern.compile(RETURN);
	static final Pattern closingBracket = Pattern.compile(CLOSING_BRACKET);
	static final Pattern invokeCheck = Pattern.compile(INVOKE_CHECK);
	static final Pattern parameterNameCheck = Pattern.compile(PARAM_NAME);
	static final Pattern trueFalse = Pattern.compile(TRUE_FALSE);
	static final Pattern intCheck = Pattern.compile(INT_CHECK);
	static final Pattern doubleCheck = Pattern.compile(DOUBLE_CHECK);

	static final Pattern charCheck = Pattern.compile(CHAR_CHECK);
	static final Pattern stringCheck = Pattern.compile(STRING_CHECK);



	static boolean checkSyntax(Pattern pattern, String syntax) {
		return pattern.matcher(syntax).matches();
	}
}
