package oop.ex6.main;

import java.util.*;

import static oop.ex6.main.DataMember.*;
import static oop.ex6.main.PatternPool.*;
import static oop.ex6.main.Scope.patternPool;

public class ScopeBuilder {


	//String constants
	static final String VOID = "void";
	private static final String IF = "if";
	private static final String WHILE = "while";
	private static final String[] IF_WHILE = {IF, WHILE};

	//Exception messages constants.
	private static final String IF_WHILE_IN_MAIN = "There is an if/while in the collector scope.";
	private static final String WRONG_IF_WHILE_CONDITION = "There is a wrong if/while condition.";
	private static final String MISSING_RETURN = "Missing return statement.";
	private static final String MISSING_CLOSING_BRACKET = "Missing closing bracket.";
	private static final String CONDITION_ERROR = "ERROR: Invalid Condition Value";
	public static final String MISSING_SEMICOLON = "The line is lacking an ending semicolon.";
	private static final String WRONG_FUNCTION_NAME = "Unallowed function name.";
	private static final String WRONG_PARAMETERS = "Unallowed paramater's type in a function.";
	public static final String UNALLOWED_SYNTAX = "The code is not fully written in sjava.";
	private static final String WRONG_INVOKING = "An invoking to one of the classes is wrong.";
	private static final String FINAL_NOT_ASSIGNED = "A final variable is not directly assigned.";
	private static final String SCOPE_UNFINISHED = "A scope isn't closed properly in the code.";
	private static final String ILLEGAL_VALUE = "A code is not fully";
	private static final String UNASSIGNED_PARAMETER = "Unassigned parameter was sent to the function.";

	//int constants.
	private static final int MAIN_SCOPE = 0;
	private static final int IF_OR_WHILE = 1;
	private static final int METHOD = 2;

	//char sequences
	static final CharSequence OPEN_BRACKET = "{";
	static final CharSequence CLOSE_BRACKET = "}";

	/**
	 * The scope that is being built.
	 */
	private Scope inBuildScope;

	/**
	 * The lines in the code of this scope.
	 */
	private List<String> lines;

	/**
	 * Constructing a ScopeBuilder with its scope to be built and its lines in the code.
	 *
	 * @param inBuildScope scope to be built
	 * @param lines        The scope's lines in the code.
	 */
	ScopeBuilder(Scope inBuildScope, List<String> lines) {
		this.inBuildScope = inBuildScope;
		this.lines = lines;
	}

	/**
	 * Building a scope.
	 *
	 * @throws Exception
	 */
	public void build() throws Exception {

		int lineLimit = getLineLimit();
		int startLine = 0;
		if (lineLimit != lines.size()) startLine = 1;

		for (int i = startLine; i < lineLimit; i++) {
//			if (lines.size() == 2) break;
			String line = lines.get(i).trim();
			String firstWord = line.split(LINE_OPENER, -1)[0];

			// ignore comments:
			if (checkSyntax(comment,firstWord )) {
				if (checkSyntax(whitespaceBeforeComment,lines.get(i) )) throw new
						Exception();
				continue;
			}
			//  add data member to scope:
			if (DATA_MEMBERS_TYPES.contains(firstWord)) {
				if (isMainScope()) continue;
				if (!line.trim().endsWith(DataMember.SEMICOLON)) throw new Exception(MISSING_SEMICOLON);
				inBuildScope.addDataMembers(line);
			}

			// add if\while subScope:
			else if (Arrays.asList(IF_WHILE).contains(firstWord)) {
				isValidIfWhile(line);

				List<String> scopeLines = getScopeLines(i);
				inBuildScope.addIfWhileScope(scopeLines);
				i += scopeLines.size() - 1;

				// add method declaration subScope to scope:
			} else if (VOID.equals(firstWord)) {
				if (!isMainScope()) throw new Exception();
				List<String> scopeLines = getScopeLines(i);
				inBuildScope.addMethodScope(scopeLines);
				i += scopeLines.size() - 1;

				// check other options:
			} else if (checkSyntax(invokeCheck,line ))
				checkInvocation
					(line);
			else if (line.contains(EQUALITY)) checkDataMember(line);
			else if (checkSyntax(kofiko,line )) continue;
			else if (!checkSyntax(finishLine, line)) throw
					new
					Exception();
		}

		// validate last lines:
		checkLastLines(lines.size() - lineLimit);
	}


	/**
	 * Checking if the last lines are valid.
	 *
	 * @param scope_case An integer representing the type of the scope.
	 * @throws Exception
	 */
	private void checkLastLines(int scope_case) throws Exception {
		switch (scope_case) {
			case MAIN_SCOPE:
				return;
			case IF_OR_WHILE:
				checkClosingBracket();
				return;
			case METHOD:
				methodHasReturn();
				checkClosingBracket();
		}
	}

	/**
	 * Checking for closing bracket.
	 *
	 * @throws Exception
	 */
	private void checkClosingBracket() throws Exception {
		if (!checkSyntax(closingBracket,lines.get(lines.size()-1) ))
			throw new Exception(MISSING_CLOSING_BRACKET);
	}

	/**
	 * Getting the limit of the lines to be run in build().
	 *
	 * @return the number of the last line.
	 */
	private int getLineLimit() {
		if (isMainScope()) return lines.size();
		else if (isIfWhile()) return lines.size() - 1;
		else return lines.size() - 2;
	}

	/**
	 * Checking if the scope is the main scope.
	 *
	 * @return true if it is, false otherwise.
	 */
	private boolean isMainScope() {
		return inBuildScope.mainScope == inBuildScope;
	}

	/**
	 * Checking if this scope is an if/while scope.
	 *
	 * @return true if it is, false otherwise.
	 */
	private boolean isIfWhile() {
		return inBuildScope.name == null && inBuildScope.mainScope != inBuildScope;
	}

	/**
	 * Checks if this scope is a valid if/while scope.
	 *
	 * @param line The condition of the if/while.
	 * @throws Exception
	 */
	private void isValidIfWhile(String line) throws
			Exception {
		checkIfWhileParams(line);
		if (isMainScope()) throw new Exception(IF_WHILE_IN_MAIN);
		if (!checkSyntax(ifWhileInvoke,line )) throw new
				Exception
				(CONDITION_ERROR);
	}

	/**
	 * Collecting the parameters in a scope.
	 *
	 * @param line The line in the code in which the parameters are given.
	 * @return The parameters.
	 */
	private List<String> ifWhileParameterCollector(String line) {
		String[] rawParams = (line.trim().split(IF_WHILE_PARAM_SPLIT, 0));
		List<String> params = new ArrayList<>();

		for (String param : rawParams) {
			if (!param.equals("")) {
				params.add(param);
			}
		}
		return params;
	}

	/**
	 * Checking if the invoke of a method is valid.
	 *
	 * @param line the invoke line in the code.
	 * @throws Exception
	 */
	private void isValidInvoke(String line) throws Exception {
		if (!patternPool.invokeCheck.matcher(line).matches()) throw new Exception
				(WRONG_INVOKING);
	}

	/**
	 * Checking the parameters given in an invoke of a method.
	 *
	 * @param invokeParameters the parameters of the invoke.
	 * @param methodParams     The parameters expected by the method.
	 * @throws Exception
	 */
	private void checkInvokeParameters(List<String> invokeParameters, List<DataMember> methodParams)
			throws Exception {
		if (invokeParameters.size() != methodParams.size()) throw new Exception();
		for (int i = 0; i < methodParams.size(); i++) {
			String invokeParameterType = invokeParameters.get(i);
			DataMember requiredParameterType = methodParams.get(i);
			if (!requiredParameterType.isTypeMatch(invokeParameterType)) throw new Exception
					(WRONG_PARAMETERS);
		}
	}

	/**
	 * Checking the parameters in an IF/WHILE scope.
	 *
	 * @param line The first line of the if/while.
	 * @throws Exception
	 */
	private void checkIfWhileParams(String line) throws Exception {
		List<String> params = ifWhileParameterCollector(line);
		DataMember paramMember;
		for (String param : params)
			if (patternPool.conditionable.matcher(param).matches()) {
				if (patternPool.parameterNameCheck.matcher(param).matches() && !patternPool.trueFalse.matcher
						(param).matches()) {
					paramMember = inBuildScope.findDataMember(param);
					if (!paramMember.isMemberTypeConditionable())
						throw new Exception(WRONG_IF_WHILE_CONDITION);
				}
			} else throw new Exception(WRONG_IF_WHILE_CONDITION);
	}


	/**
	 * Checks if the method ends with a return statement.
	 *
	 * @throws Exception
	 */
	private void methodHasReturn() throws Exception {
		int returnLine = lines.size() - 2;
		if (!patternPool.returnCheck.matcher(lines.get(returnLine)).matches())
			throw new
					Exception(MISSING_RETURN);
	}


	/**
	 * Getting the lines of the scope.
	 *
	 * @param i the first line of the scope.
	 * @return The lines of the scope.
	 * @throws Exception
	 */
	private List<String> getScopeLines(int i) throws Exception {
		int openCount = 0;
		int closeCount = 0;

		List<String> scopeLines = new ArrayList<>();
		for (int j = i; j < lines.size(); j++) {
			String line = lines.get(j);

			if (line.contains(OPEN_BRACKET)) openCount++;
			if (line.contains(CLOSE_BRACKET)) closeCount++;
			scopeLines.add(line);

			if (openCount == closeCount) {
				return scopeLines;
			}
		}
		throw new Exception(SCOPE_UNFINISHED);
	}


	/**
	 * Checking if a DataMember assignment is valid.
	 *
	 * @param line the line in the code of this data member.
	 * @throws Exception
	 */
	private void checkDataMember(String line) throws Exception {
		String[] nameAndValue = getNameAndValue(line);
		String name = nameAndValue[0];
		String value = nameAndValue[1];

		//checking it is not global.
		if (!isMainScope()) {
			DataMember dataMember = inBuildScope.findDataMember(name);
			try {
				addLineOfDataMembers(line, inBuildScope.dataMembers, inBuildScope);
			}
			// preventing dataMember duplication.
			catch (Exception ignored) {
			}

			if (dataMember.isFinal) throw new Exception(FINAL_NOT_ASSIGNED);
			if (!dataMember.checkValue(value)) throw new Exception(ILLEGAL_VALUE);
			dataMember.isAssigned = true;
		}
	}

	public void arbel(){

	}

	/**
	 * Checking if a method invocation is valid.
	 *
	 * @param line The line of the invocation.
	 * @throws Exception
	 */
	private void checkInvocation(String line) throws Exception {
		isValidInvoke(line);

		String methodName = getInvocationName(line).trim();
		List<String> invokeParameterTypes = getInvokeParametersTypes(line);
		List<DataMember> requiredParameters = MainCollector.getMethodDeclaration(methodName).parameters;
		checkInvokeParameters(invokeParameterTypes, requiredParameters);

		assignGlobals();
	}

	/**
	 * Getting the name of the invocation.
	 *
	 * @param line the line of the invocation.
	 * @return the name of the invocation.
	 * @throws Exception
	 */
	private String getInvocationName(String line) throws Exception {
		String[] lineWords = line.split(PARAMETER_CATCH_INVOCATION_REGEX);
		List<String> words = cleanEmptyString(lineWords);
		if (DataMember.reservedWords.contains(words.get(0))) throw new Exception(WRONG_FUNCTION_NAME);
		return words.get(0);
	}

	/**
	 * Assignment of globals inside method.
	 */
	private void assignGlobals() {
		for (DataMember globalToAssign : inBuildScope.methodDeclaration.globalsToAssign) {
			for (DataMember scopeMember : inBuildScope.dataMembers) {
				if (globalToAssign.name.equals(scopeMember.name)) {
					scopeMember.isAssigned = true;
				}
			}
		}
	}

	/**
	 * Getting the types of the parameters given in an invocation of a method.
	 *
	 * @param line The line of the invocation.
	 * @return A list of these types.
	 * @throws Exception
	 */
	private List<String> getInvokeParametersTypes(String line) throws Exception {
		List<String> params = cleanEmptyString(line.split(CATCH_PARAMETERS_REGEX));
		List<String> paramTypes = new ArrayList<>();
		for (String param : params) {

			if (checkSyntax(intCheck, param)) paramTypes.add(INT);
			else if (checkSyntax(doubleCheck, param)) paramTypes.add(DOUBLE);
			else if (checkSyntax(trueFalse, param)) paramTypes.add(BOOL);
			else if (checkSyntax(charCheck, param)) paramTypes.add(CHAR);
			else if (checkSyntax(stringCheck, param)) paramTypes.add(STRING);

				// parameter is an assignment of a DataMember
			else if (checkSyntax(parameterNameCheck, param)) {
				DataMember invokeMember = inBuildScope.findDataMember(param);
				if (!invokeMember.isAssigned) throw new Exception(UNASSIGNED_PARAMETER);
				paramTypes.add(invokeMember.type);

			} else throw new Exception(WRONG_PARAMETERS);
		}

		return paramTypes;
	}


	/**
	 * Cleaning an array from empty strings.
	 *
	 * @param array An array to be cleaned.
	 * @return The array cleaned.
	 */
	static List<String> cleanEmptyString(String[] array) {
		List<String> list = new ArrayList<>();
		for (String string :
				array) {
			if (!string.equals("")) list.add(string);
		}
		return list;
	}

}