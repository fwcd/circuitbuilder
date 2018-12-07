package com.fwcd.circuitbuilder.model.logic.expression;

/**
 * An operation on a {@link LogicExpression}.
 */
public interface LogicExpressionVisitor<T> {
	T visitExpression(LogicExpression expression);
	
	default T visitConjunction(Conjunction conjunction) { return visitExpression(conjunction); }
	
	default T visitDisjunction(Disjunction disjunction) { return visitExpression(disjunction); }
	
	default T visitEquivalence(Equivalence equivalence) { return visitExpression(equivalence); }
	
	default T visitImplication(Implication implication) { return visitExpression(implication); }
	
	default T visitNegation(Negation negation) { return visitExpression(negation); }
}