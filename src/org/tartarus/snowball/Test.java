package org.tartarus.snowball;
import org.tartarus.snowball.ext.englishStemmer;

public class Test {

	public static void main(String[] args) {
		englishStemmer stemmer = new englishStemmer();
		stemmer.setCurrent("swimming");
		if (stemmer.stem()){
		    System.out.println(stemmer.getCurrent());
		}
	}

}
