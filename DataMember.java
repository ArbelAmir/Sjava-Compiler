package oop.ex6.main;

import java.util.Arrays;
import java.util.List;

import static oop.ex6.main.MainCollector.findGlobal;
import static oop.ex6.main.MainCollector.globalDataMembers;
import static oop.ex6.main.PatternPool.*;


public class DataMember {


	private static final String COMMA = ",";
	private static final String WRONG_DATA_MEMBER_NAME = "There is a wrong data member name.";
	private static final String WRONG_DATA_MEMBER_VALUE = "There is a wrong data member value.";
	private static final String FINAL_NOT_ASSIGNED = "The final variable is not assigned at declaration time.";
	private static final String SAME_NAME_EXCEPTION = "There are two variables with the same name in the " +
			"same block.";

	static final String DOUBLE_CHECK = "(-?[0-9]+[.]?[0-9]*|-?[0-9]*[.]?[0-9]+)";
	static final String TRUE_FALSE = "\\s*(true|false)\\s*";

	private static final String OR = "|";
	private static final String PURE_BOOL_VALUE = TRUE_FALSE + OR + DOUBLE_CHECK;
	static final String STRING_CHECK = "(\\\".*\\\")";
	static final String CHAR_CHECK = "(\\'.\\')";
	static final String NAME_CHECK = "(\\s*((_+[a-zA-Z\\d][\\w]*)|([a-zA-Z][\\w]*)\\s*))";
	static final String INT_CHECK = "(-?[0-9]+)";
	private static final String LOGICALS = "(\\s*((&&)|([|]{2}))\\s*)";
	private static final String BOOL_VALUE = "(" + NAME_CHECK + OR + TRUE_FALSE + OR + DOUBLE_CHECK + ")";
	private static final String LOGICAL_EXPR = "(" + BOOL_VALUE + "(" + LOGICALS + BOOL_VALUE + ")*)";
	static final String BOOL_CHECK = "(" + BOOL_VALUE + OR + LOGICAL_EXPR + ")";
	private static final String WHITESPACE = "\\s+";
	private static final String EQUALITY_REGEX = "(\\s*(=)\\s*)";

	static final String SEMICOLON = ";";
	private static final CharSequence DOUBLE_EQUALITY = "==";
	static final String INT = "int";
	static final String STRING = "String";
	static final String BOOL = "boolean";
	static final String DOUBLE = "double";
	static final String CHAR = "char";
	private static final String FINAL = "final";
	private static String[] reservedWordsArray = {INT, DOUBLE, BOOL, CHAR, FINAL, STRING, "void", "if",
			"while", "true",  "false", "return"};
	static final List<String> reservedWords = Arrays.asList(reservedWordsArray);

	private static final int FINAL_TYPE_POSITION = 1;
	private static final int TYPE_POSITION = 0;
	private static final int NAME_POSITION = 0;
	private static final int VALUE_POSITION = 1;
	static final String EQUALITY = "=";

	/**
	 * The name of this DataMember.
	 */
	public String name;

	/**
	 * The type of this DataMember.
	 */
	String type;

	/**
	 * A boolean representing whether this DataMember is final.
	 */
	boolean isFinal;

	/**
	 * A boolean representing whether this DataMember is assigned.
	 */
	boolean isAssigned;

	/**
	 * The scope of this DataMember.
	 */
	private oop.ex6.main.Scope parentScope;


	/**
	 * constructor a clone of assigned global data member
	 * @param member Assigned global DataMember.
	 * @param scope The scope of this DataMember.
	 */
	DataMember(DataMember member, Scope scope){
		this.type = member.type;
		this.name = member.name;
		this.parentScope = scope;
		this.isFinal = member.isFinal;
		this.isAssigned = member.isAssigned;
	}

	/**
	 * Creating a DataMember.
	 * @param line The code of its declaration.
	 * @param type The type of the dataMember.
	 * @param isFinal A boolean determining whether it is final or not.
	 * @param parentScope The scope to whom this DataMember belongs.
	 * @throws Exception
	 */
	private DataMember(String line, String type, boolean isFinal, Scope parentScope) throws Exception {
		this.type = type;
		this.parentScope = parentScope;
		this.isFinal = isFinal;

		initDataMember(line);
	}

	/**
	 * Determining whther this DataMember is assigned in its creation and its name.
	 * @param line The code of its creation.
	 * @throws Exception
	 */
	private void initDataMember(String line) throws Exception {
		String[] partsOfLine;
		if (line.contains(EQUALITY)) {
			if (line.contains(DOUBLE_EQUALITY)) throw new Exception(WRONG_DATA_MEMBER_VALUE);
			isAssigned = true;
			partsOfLine = line.split(EQUALITY_REGEX);
			if (checkName(partsOfLine[NAME_POSITION])) this.name = partsOfLine[NAME_POSITION].trim();
			else throw new Exception(WRONG_DATA_MEMBER_NAME);
			if (!checkValue(partsOfLine[VALUE_POSITION].trim()))
				throw new Exception(WRONG_DATA_MEMBER_VALUE);
		} else {
			isAssigned = false;
			if (!checkName(line)) throw new Exception(WRONG_DATA_MEMBER_NAME);
			if (isFinal) throw new Exception(FINAL_NOT_ASSIGNED);
			else this.name = line.trim();
		}
	}


	/**
	 * removing a semicolon in the end of a line if exists.
	 * @param command The command in the code.
	 * @return The command without a semicolon.
	 */
	private static String removeSemicolon(String command) {
		if (command.endsWith(SEMICOLON)) return command.substring(0, command.length() - 1).trim();
		else return command;
	}

	/**
	 * Checking whether the value given to a DataMember is valid.
	 * @param value The value given to the DataMember.
	 * @return true if the value is valid, false otherwise.
	 * @throws Exception
	 */
	public boolean checkValue(String value) throws Exception {
		if (isValueMatchType(value, this)) return true;
		if (parentScope == null) {
			DataMember globalMember = findGlobal(value);
			return checkGlobalDataMembers(globalMember);
		}
		else{
		DataMember referenced = parentScope.findDataMember(value);
		return referenced.type.equals(type) && referenced.isAssigned;}
	}


	/**
	 * Checking whether a global DataMember matches the type of this DataMember and it is assigned.
	 * @param member A global DataMember
	 * @return true if it matches, false otherwise.
	 */
	private boolean checkGlobalDataMembers(DataMember member) {
		for (DataMember globalMember : globalDataMembers) {
			if(globalMember.name.equals(member.name))
				return isTypeMatch(member.type) && globalMember.isAssigned;
		}
		return false;
	}

	/**
	 * Checking whether a value is valid and matches the type of the DataMember given.
	 * @param value A value to be assigned.
	 * @param member A dataMember to be assigned to.
	 * @return true if it matches, false otherwise.
	 */
	static boolean isValueMatchType(String value, DataMember member) {
		if (value.matches(PURE_BOOL_VALUE + OR + STRING_CHECK + OR + CHAR_CHECK)) {
			switch (member.type) {
				case INT:
					return checkSyntax(intCheck,value);
				case BOOL:
					return value.matches(BOOL_CHECK);
				case DOUBLE:
					return checkSyntax(doubleCheck,value);
				case STRING:
					return checkSyntax(stringCheck,value );
				case CHAR:
					return checkSyntax(charCheck,value );
			}
		}
		return false;
	}

	/**
	 * Checking if the name is valid.
	 * @param name The name given to the DataMember.
	 * @return true if valid, false otherwise.
	 */
	private boolean checkName(String name) {
		return name.matches(NAME_CHECK) && !reservedWords.contains(name);
	}

	/**
	 * Processing a line of dataMembers assignment or declaration.
	 * @param line The line given.
	 * @param dataMembers the DataMember list to be added to.
	 * @param scope The scope of the DataMembers.
	 * @throws Exception
	 */
	public static void addLineOfDataMembers(String line, List<DataMember> dataMembers, Scope scope)
			throws Exception {

		// Checking if it is final.
		line = removeSemicolon(line.trim());
		String[] lineWords = line.split(WHITESPACE);
		String type;
		boolean isFinal;
		if (lineWords[TYPE_POSITION].equals(FINAL)) {
			type = lineWords[FINAL_TYPE_POSITION];
			isFinal = true;
		} else {
			type = lineWords[TYPE_POSITION];
			isFinal = false;
		}

		// Splitting to the different DataMembers.
		lineWords = line.split(type, 2); // Divide to before type and after type.
		if(line.endsWith(COMMA)) throw new Exception(ScopeBuilder.UNALLOWED_SYNTAX);
		String[] variables = lineWords[1].trim().split(COMMA); // take the part after type.

		// Handling each DataMember.
		for (String newMember : variables) {
			String memberName = newMember.split(EQUALITY)[NAME_POSITION].trim();
			for (DataMember dataMember : dataMembers) {
				if (dataMember.name.equals(memberName)) throw new Exception(SAME_NAME_EXCEPTION);
			}
			dataMembers.add(new DataMember(newMember, type, isFinal, scope));
		}
	}


	/**
	 * Getting the name and value of a DataMember assignemnt.
	 * @param line The line of the assignment in the code.
	 * @return An array containing its name and value.
	 * @throws Exception
	 */
	static String[] getNameAndValue(String line){
		String[] wordLines = line.split(DataMember.EQUALITY);
		String name = wordLines[DataMember.NAME_POSITION].trim();
		String value = wordLines[DataMember.VALUE_POSITION].trim();
		value = removeSemicolon(value);
		return new String[]{name, value};
	}

	/**
	 * Checking if the the type of the parameter matches the type of this DataMember
	 * @param callParamType the type of the parameter
	 * @return True if matches, false otherwise.
	 */
	public boolean isTypeMatch(String callParamType) {
		return (
				// parameters are equal
				(callParamType.equals(type))

				||

				// method param is boolean and call param is int or double
				(type.equals(DataMember.BOOL) &&
						(callParamType.equals(DataMember.INT) || callParamType.equals(DataMember.DOUBLE)))

				||

				// method param is double and call param is int
				(type.equals(DataMember.DOUBLE) && (callParamType.equals(DataMember.INT))));
	}


	/**
	 * checks if this members type is conditionable
	 * @return boolean according to conditionability of the member
	 */
	boolean isMemberTypeConditionable() {
		return isAssigned && (type.equals(INT) ||
				type.equals(DOUBLE) || type.equals(BOOL));
	}

}
