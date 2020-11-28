// Issa Issa
// CECS 277-13
// Homework2
// 03/04/20
import java.util.Scanner;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
public class invertedIndex {
	public static void main(String args[]) {
		Scanner input = new Scanner(System.in);
		System.out.print("Enter a text to index: ");
		String word = input.next();
		HashMap<String, List<Integer>> index = new HashMap<>();
		int location = 1;
		while(input.hasNext() && !word.equals("!!quit!!")) {
			List<Integer> spots = new ArrayList<>();
			spots.add(location);
			String wordd = word.toLowerCase();
			String worddd = wordd.replaceAll("[^a-z0-9]", "");
			if(! index.containsKey(worddd)) {
				index.put(worddd, spots);
			}
			else {
				for(HashMap.Entry<String, List<Integer>> both : index.entrySet()) {
					String entry = both.getKey();
					List<Integer> values = both.getValue();
					if(worddd.equals(entry)) {
						values.add(location);
					}
				}
			}
			word = input.next();
			if(word.equals("!!quit!!")) {
				break;
			}
			location +=1;
		}
		System.out.print("Enter a word to find: ");
		String toFind = input.next();
		for(HashMap.Entry<String, List<Integer>> all : index.entrySet()) {
			String keyy= all.getKey();
			List<Integer> valuess = all.getValue();
			if(toFind.equals(keyy)) {
				System.out.println(keyy + " " + valuess);
			}
		}
		input.close();
	}
}
