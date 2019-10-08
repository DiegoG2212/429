package cecs429.query;

import java.util.ArrayList;
import java.util.List;

import cecs429.text.BetterTokenProcessor;

/**
 * Parses boolean queries according to the base requirements of the CECS 429 project.
 * Does not handle phrase queries, NOT queries, NEAR queries, or wildcard queries... yet.
 */
public class BooleanQueryParser {
	/**
	 * Identifies a portion of a string with a starting index and a length.
	 */
	private static class StringBounds {
		int start;
		int length;
		
		StringBounds(int start, int length) {
			this.start = start;
			this.length = length;
		}
	}
	
	/**
	 * Encapsulates a QueryComponent and the StringBounds that led to its parsing.
	 */
	private static class Literal {
		StringBounds bounds;
		QueryComponent literalComponent;
		
		Literal(StringBounds bounds, QueryComponent literalComponent) {
			this.bounds = bounds;
			this.literalComponent = literalComponent;
		}
	}
	
	/**
	 * Given a boolean query, parses and returns a tree of QueryComponents representing the query.
	 */
	public QueryComponent parseQuery(String query) {
		int start = 0;

		// General routine: scan the query to identify a literal, and put that literal into a list.
		//	Repeat until a + or the end of the query is encountered; build an AND query with each
		//	of the literals found. Repeat the scan-and-build-AND-query phase for each segment of the
		// query separated by + signs. In the end, build a single OR query that composes all of the built
		// AND subqueries.
		
		List<QueryComponent> allSubqueries = new ArrayList<>();
		do {
			// Identify the next subquery: a portion of the query up to the next + sign.
			StringBounds nextSubquery = findNextSubquery(query, start);
			// Extract the identified subquery into its own string.
			String subquery = query.substring(nextSubquery.start, nextSubquery.start + nextSubquery.length);
			int subStart = 0;
			
			// Store all the individual components of this subquery.
			List<QueryComponent> subqueryLiterals = new ArrayList<>(0);

			do {
				// Extract the next literal from the subquery.
				Literal lit = findNextLiteral(subquery, subStart);
				
				// Add the literal component to the conjunctive list.
				subqueryLiterals.add(lit.literalComponent);
				
				// Set the next index to start searching for a literal.
				subStart = lit.bounds.start + lit.bounds.length;
				
			} while (subStart < subquery.length());
			
			// After processing all literals, we are left with a conjunctive list
			// of query components, and must fold that list into the final disjunctive list
			// of components.
			
			// If there was only one literal in the subquery, we don't need to AND it with anything --
			// its component can go straight into the list.
			if (subqueryLiterals.size() == 1) {
				allSubqueries.add(subqueryLiterals.get(0));
			}
			else {
				// With more than one literal, we must wrap them in an AndQuery component.
				allSubqueries.add(new AndQuery(subqueryLiterals));
			}
			start = nextSubquery.start + nextSubquery.length;
		} while (start < query.length());
		
		// After processing all subqueries, we either have a single component or multiple components
		// that must be combined with an OrQuery.
		if (allSubqueries.size() == 1) {
			return allSubqueries.get(0);
		}
		else if (allSubqueries.size() > 1) {
			return new OrQuery(allSubqueries);
		}
		else {
			return null;
		}
	}
	
	/**
	 * Locates the start index and length of the next subquery in the given query string,
	 * starting at the given index.
	 */
	private StringBounds findNextSubquery(String query, int startIndex) {
		int lengthOut;
		// Find the start of the next subquery by skipping spaces and + signs.
		char test = query.charAt(startIndex);
		// part of extended query parser as it needs to read to see if there is a parentesis first other wise split in the +
		if (test == '('){

			// the following just grabs what ever is inside the parenthesis with the parenthesis
			//and sends it as a su query
			int nextPar = query.indexOf(')', startIndex+1);
			if (nextPar  < 0 ) {// making sure that there is a closing parenthesis
				lengthOut = query.length() - startIndex;
			} else {
				lengthOut = 1 + nextPar - startIndex;
			}

		}else { // if no parenthesis divide base on space an the plus


			while (test == ' ' || test == '+') {
				test = query.charAt(++startIndex);
			}

			// Find the end of the next subquery.
			int nextPlus = query.indexOf('+', startIndex + 1);
			int nextPar = query.indexOf('(',startIndex+1);

			if ((nextPlus < 0) && (nextPar < 0)) {
				// If there is no other + sign, then this is the final subquery in the
				// query string.
				lengthOut = query.length() - startIndex;
			}else if ((nextPlus < 0) && (nextPar > 0) ){

				lengthOut = nextPar - startIndex - 1;
			}else {
				// If there is another + sign, then the length of this subquery goes up
				// to the next + sign.

				// Move nextPlus backwards until finding a non-space non-plus character.
				test = query.charAt(nextPlus);
				while (test == ' ' || test == '+') {
					test = query.charAt(--nextPlus);
				}

				lengthOut = 1 + nextPlus - startIndex;
			}
		}
		// startIndex and lengthOut give the bounds of the subquery.
		return new StringBounds(startIndex , lengthOut);
	}
	
	/**
	 * Locates and returns the next literal from the given subquery string.
	 */
	private Literal findNextLiteral(String subquery, int startIndex) {
		int subLength = subquery.length();
		int lengthOut;

		// Skip past white space.

		while (subquery.charAt(startIndex) == ' ') {
			++startIndex;
		}


		// Locate the next space to find the end of this literal.
		int nextSpace = subquery.indexOf(' ', startIndex);
		if (nextSpace < 0) {
			// No more literals in this subquery.
			lengthOut = subLength - startIndex;


		} else {
			lengthOut = nextSpace - startIndex;

		}

		if (subquery.charAt(startIndex) == '"') {
			
			/*
			TODO:
			Instead of assuming that we only have single-term literals, modify this method so it will create a PhraseLiteral
			object if the first non-space character you find is a double-quote ("). In this case, the literal is not ended
			by the next space character, but by the next double-quote character.
			 */

			// Find ending quotation mark


			String holder = " ";
			while (subquery.charAt(startIndex) == '"') {
				startIndex++;
			}
			// Substring to get all words between quotation marks as a single string

			int fallSpace = subquery.indexOf('"', startIndex);

			if (fallSpace < 0) {
				lengthOut = subquery.length() - startIndex;
			} else {
				lengthOut = fallSpace - startIndex +1;
			}

			holder = subquery.substring(startIndex , lengthOut);

			String[] phrase = holder.split(" ");// split that into a list of individual strings
			List<String>  tem = new ArrayList<>();
			List<String> phr = new ArrayList<>();

			for (String i : phrase) { // process tokens before going in to the phrase literal. Following golden rule.
				System.out.println(i);
				tem = new BetterTokenProcessor().processToken(i);
				for (String t : tem) {
					phr.add(t);
				}
			}


			return new Literal(
					new StringBounds(startIndex, lengthOut),// Construct a new Literal object,
					new PhraseLiteral(phr)); // but the second parameter will be a PhraseLiteral constructed with the list of strings.
		}else if(subquery.charAt(startIndex) == '('){ // expanded query parser

			String parSub = " ";	//checking to see if there is a parenthesis
			while (subquery.charAt(startIndex) == '(') { //if there is get whats inside
				startIndex++;
			}
			// Substring to get all words between the parenthesis as a single string

			int exitPar = subquery.indexOf(')', startIndex); //substring it and send it as a query thru the query
			System.out.println(startIndex);						  //parser
			System.out.println(exitPar);
			System.out.print(subquery);
			if (exitPar < 0) { //amking sure of closing parenthesis
				lengthOut = subquery.length() - startIndex;
			} else {
				lengthOut = exitPar - startIndex + 1;
			}

			parSub = subquery.substring(startIndex, lengthOut);




			return new Literal( // send new literal with parse query as a recursive call
					new StringBounds(startIndex, lengthOut),
					this.parseQuery(parSub));


		} else {
		
		// This is a term literal containing a single term.
			List<String> tem = new BetterTokenProcessor().processToken(subquery.substring(startIndex, startIndex + lengthOut)); //process token before 
																															    // sending to the term literal
			return new Literal(
			 new StringBounds(startIndex, lengthOut),
			 new TermLiteral(tem.get(0)));
		}

		
		
		
	}
}
