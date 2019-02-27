package oop.ex6.main;

import java.util.ArrayList;
import java.util.List;

import static oop.ex6.main.DataMember.*;
import static oop.ex6.main.PatternPool.*;
import static oop.ex6.main.ScopeBuilder.*;

/**
 * class This represents a set of functional objects that collect global objects given unclassified sjava
 * code in the form of a row list. The initialization of the object creates and initializes the global
 * dataMembers list as well as the method declaration list.
 */
public class MainCollector {

	private static final String SPLIT_PARAMETERS_DECLARATION_REGEX = "(void\\s+)|(\\s*[(]\\s*)|(\\s*[,]\\s*)|(\\s*[)].*)";

	/**
	 * This class is intended to represent a group of objects that share the declaration properties of the
	 * method - a set of parameters there and given that the method executes assignment on variables also
	 * preserves the set of global variables intended for placement when calling the method
	 */
	public static class MethodDeclaration {

		String name;

		List<DataMember> parameters;
		List<DataMember> globalsToAssign;

		MethodDeclaration(String line) throws Exception {
			buildDeclaration(line);
		}

		/**
		 * Given the sjava code line corresponding to the declaration template of the method methods, the
		 * method variables and properties are built according to the code. If the Syntax is not installed,
		 * the method will throw exception
		 */
		private void buildDeclaration(String line) throws Exception {
			globalsToAssign = new ArrayList<>();
			parameters = new ArrayList<>();
			String[] lineWords = line.trim().split(SPLIT_PARAMETERS_DECLARATION_REGEX);
			List<String> words = cleanEmptyString(lineWords);
			this.name = words.get(0);
			for (int i = 1; i < words.size(); i++) {
				String word = words.get(i);
				DataMember.addLineOfDataMembers(word, parameters, null);
			}
			for (DataMember parameter :
					parameters) {
				parameter.isAssigned = true;
			}
		}

	}

	/**
	 * A list of method declaration objects
	 */
	private static List<MethodDeclaration> globalMethods;

	/**
	 * A list of dataMember objects representing the global ones
	 */
	static List<DataMember> globalDataMembers;



	/**
	 * Given the name of a method, if there is such a method the method finds it and returns the object of
	 * its declaration. If the method does not exist, an exception is thrown.
	 */
	public static MethodDeclaration getMethodDeclaration(String methodName) throws Exception {
		for (MainCollector.MethodDeclaration declaration : globalMethods) {
			if (declaration.name.equals(methodName))
				return declaration;
		}
		throw new Exception();
	}

	/**
	 * Scans the outer scope of the code and collects global variables and declares methods
	 * @throws Exception
	 */
	public static void globalScan(List<String> lines) throws Exception {
		globalDataMembers = new ArrayList<>();
		globalMethods = new ArrayList<>();

		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i).trim();
			String firstWord = line.split(LINE_OPENER, -1)[0];


			if (checkSyntax(comment,firstWord )) {
				if (checkSyntax(whitespaceBeforeComment,lines.get(i) )) throw new
						Exception();
				continue;
			}

			if (VOID.equals(firstWord)) {
				isValidMethodDeclaration(line);
				addMethodDeclaration(line);
				i += skipScope(lines, i);

			} else if (DATA_MEMBERS_TYPES.contains(firstWord)) {
				if (!line.trim().endsWith(DataMember.SEMICOLON)) throw new Exception(
						ScopeBuilder.MISSING_SEMICOLON);

				DataMember.addLineOfDataMembers(line, globalDataMembers, null);

			} else if (line.contains(EQUALITY)) assignGlobal(line);

			else throw new Exception("syntaxError in main scope");
		}
	}

	/**
	 * makes an attempt to assign global variable to a value
	 * @param line  uncompiled line thats contain an assignment sign
	 * @throws Exception when the global member is final or when the value to assign doesnt match the
	 * members type
	 */
	private static void assignGlobal(String line) throws Exception {
		String[] nameAndValue = getNameAndValue(line);
		String name = nameAndValue[0];
		String value = nameAndValue[1];

		DataMember dataMember = findGlobal(name);

		if (dataMember.isFinal) throw new IllegalAccessException();
		if (!isValueMatchType(value, dataMember)) throw new IllegalArgumentException();
		dataMember.isAssigned = true;
	}

	/**
	 * find member in globals according to name String
	 *
	 * @param toFindDataMember the name of the datamember to find
	 * @return the founded dataMember
	 * @throws Exception
	 */
	static DataMember findGlobal(String toFindDataMember) throws Exception {
		for (DataMember globalMember :
				globalDataMembers) {
			if (globalMember.name.equals(toFindDataMember)) return globalMember;
		}
		// the global has not yet declare
		throw new Exception();
	}

	/**
	 * add a method declaration to the class global method list
	 * @param line a line of uncompiled sjava code to make method declaration from it
	 * @throws Exception
	 */
	private static void addMethodDeclaration(String line) throws Exception {
		isValidMethodDeclaration(line);
		globalMethods.add(new MethodDeclaration(line));
	}

	/**
	 * checks for syntax validation of the method feclaration line
	 * @param line a line of uncompiled sjava code to check
	 * @throws Exception
	 */
	private static void isValidMethodDeclaration(String line) throws Exception {
		if (!checkSyntax(methodDeclareCheck,line )) throw new Exception
				("ERROR: Wrong Method" +
				" Declaration");
	}

	/**
	 * assuming 'i' points to the starting line of the method declaration. the method counts open and
	 * closing brackets and when reaching the last ending bracket of the scope returning the number of
	 * lines to skip
	 * @param lines a list of String lines
	 * @param i a current index for the lines list
	 * @return the number of lines to skip
	 * @throws Exception
	 */
	private static int skipScope(List<String> lines, int i) throws Exception {
		int openCount = 0;
		int closeCount = 0;
		int count = 0;

		for (int j = i; j < lines.size(); j++) {
			String line = lines.get(j);
			if (line.contains(OPEN_BRACKET)) openCount++;
			if (line.contains(CLOSE_BRACKET)) closeCount++;
			if (openCount == closeCount) {
				return count;
			}
			count++;
		}
		throw new Exception();
	}


}
