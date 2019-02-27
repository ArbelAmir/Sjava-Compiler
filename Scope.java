package oop.ex6.main;

import static oop.ex6.main.MainCollector.*;

import java.util.*;
import java.util.List;


public class Scope {
	private static final String VOID = "void";
	private static final String WHITESPACE = "\\s+";

	static PatternPool patternPool;

	/**
	 * The declaration of the method in which this scope is in.
	 */
	MainCollector.MethodDeclaration methodDeclaration;

	/**
	 * A list of the DataMembers in this scope.
	 */
	List<DataMember> dataMembers;

	/**
	 * The subscopes of this Scope.
	 */
	private Scope parent;

	/**
	 * The mainScope of this scope
	 */
	Scope mainScope;

	/**
	 * The name of this scope.
	 */
	String name;

	/**
	 * Constructing a Scope given its parent scope and its lines in the sjava file.
	 * @param parent The parent scope of the scope constructed.
	 * @param scopeLines the lines of the scope in the sjava file.
	 * @throws Exception
	 */
	public Scope(Scope parent, List<String> scopeLines) throws Exception {

		if (parent == null) this.mainScope = this;
		else this.mainScope = parent.mainScope;

		// Collecting the global variables.
		if (mainScope == this) {
			MainCollector.globalScan(scopeLines);
			patternPool = new PatternPool();
		}

		assignName(scopeLines.get(0));
		this.dataMembers = new ArrayList<>();

		// handling a function scope.
		if (parent == mainScope) {
			this.methodDeclaration = getMethodDeclaration(this.name);
			this.dataMembers.addAll(methodDeclaration.parameters);
		}

		// handling an if/while scope.
		else if(parent!=null) this.methodDeclaration = parent.methodDeclaration;

		else{ this.methodDeclaration = null;}
		this.parent = parent;

		ScopeBuilder builder = new ScopeBuilder(this, scopeLines);
		builder.build();

	}

	/**
	 * Assign a name to the scope.
	 * @param line The first line of this scope.
	 */
	private void assignName(String line) {
		if (mainScope.equals(this)) {
			name = null;
			return;
		}

		String[] words = line.trim().split(WHITESPACE);
		String firstWord = words[0];
		if (firstWord.equals(VOID)) name = words[1].split("\\(")[0];
		else name = null;
	}

	/**
	 * Adding DataMembers to the scope.
	 * @param line A line in the code of assignment or declaration of DataMembers.
	 * @throws Exception
	 */
	public void addDataMembers(String line) throws Exception {
		DataMember.addLineOfDataMembers(line, dataMembers, this);
	}

	/**
	 * Adding a method subScope.
	 * @param scopeLines The line of the code of the subScope.
	 * @throws Exception
	 */
	public void addMethodScope(List<String> scopeLines) throws Exception {
		new Scope(this, scopeLines);
	}

	/**
	 * Adding a method subScope.
	 * @param scopeLines The line of the code of the subScope.
	 * @throws Exception
	 */
	public void addIfWhileScope(List<String> scopeLines) throws Exception {
		new Scope(this, scopeLines);
	}


	/**
	 * Trying to find a dataMember in this scope, its parents and in the globals.
	 * @param dataMemberName The name of the DataMember to be searched.
	 * @return The DataMember that was searched for.
	 * @throws Exception
	 */

	public DataMember findDataMember(String dataMemberName) throws Exception {
		for (DataMember member : dataMembers)
			if (member.name.equals(dataMemberName)) return member;

		if (parent == mainScope) {
			for (DataMember globalDataMember : MainCollector.globalDataMembers) {
				if (globalDataMember.name.equals(dataMemberName)) {

					methodDeclaration.globalsToAssign.add(globalDataMember);

					return new DataMember(globalDataMember, this);
				}
			}
			throw new Exception();
		} else return parent.findDataMember(dataMemberName);
	}
}