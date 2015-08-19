package com.deguet.gutils.vote;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import com.deguet.gutils.permutation.FactorialBase;
import com.deguet.gutils.permutation.PrimeBase;

/**
 * Represents a preferential vote establish with ranks.
 * Two candidates can have the same rank.
 * The same candidate cannot exist twice.
 * @author joris
 *
 * @param <T> must implements hashCode and equals in a way.
 */
public class RankedVote<T> {

	private final Map<T, Integer> ranks;

	transient Comparator<T> comparator = new Comparator<T>(){
		public int compare(T o1, T o2) {
			return ranks.get(o1).compareTo(ranks.get(o2));
		}};

		public RankedVote(){
			ranks = new HashMap<T, Integer>();
		}

		public RankedVote<T> addAtRank(int rank, T... candidates){
			if (rank < 1 ) throw new IllegalArgumentException("ranks starts from 1 to whatever you want");
			for (T c : candidates){
				if (ranks.containsKey(c) && !ranks.get(c).equals(rank))
					throw new IllegalArgumentException("try to add the same candidate twice with different ranks "+c);
				ranks.put(c,rank);
			}
			return this;
		}

		public Integer rankFor(T c){
			return this.ranks.get(c);
		}

		public Set<T> candidates() {
			return this.ranks.keySet();
		}

		public int score(T a, T b){
			if (this.isPreferred(a, b)) return 1;
			if (this.isPreferred(b, a)) return -1;
			return 0;
		}

		public boolean isPreferred(T a, T b){		
			if (rankFor(a) == null && rankFor(a) == null) return false;
			if (rankFor(b) == null) return true;
			if (rankFor(a) == null) return false;
			return rankFor(a) < rankFor(b);
		}

		public List<Set<T>> asListOfSet(){
			List<Set<T>> result = new ArrayList<Set<T>>();
			List<T> candidates = new ArrayList<T>(this.ranks.keySet());
			Collections.sort(candidates, comparator);
			long rank = rankFor(candidates.get(0));
			Set<T> set = new HashSet<T>();
			result.add(set);
			for (T c : candidates){
				if (rankFor(c) == rank){
					set.add(c);
				}
				else{
					set = new HashSet<T>();
					result.add(set);
					set.add(c);
					rank = rankFor(c);
				}
			}
			return result;
		}
		
		public static <T> RankedVote<T> fromListOfSet(List<Set<T>> list){
			RankedVote<T> result = new RankedVote<T>();
			int count = 1;
			for (Set<T> set : list){
				for (T elt : set){
					result = result.addAtRank(count, elt);
				}
				count++;
			}
			return result;
		}

		/**
		 * Encodes one vote on the format described at http://www.cs.wustl.edu/~legrand/rbvote/
		 * @param votes
		 * @return
		 */
		public String toCondense(){
			List<T> candidates = new ArrayList<T>(this.ranks.keySet());
			Collections.sort(candidates, comparator);
			StringBuilder sb = new StringBuilder();
			for (int i = 0 ; i < candidates.size() ; i++){
				T a = candidates.get(i);
				if (a.toString().contains(">") || (a.toString().contains("=")))
					throw new IllegalArgumentException("Ambiguous string contains either = or > "+a);
				T b = i < candidates.size()-1?candidates.get(i+1):null;
				sb.append(a);
				if (b != null){
					int aa = rankFor(a);
					int bb = rankFor(b);
					//System.out.println(" condense " + a+ " @ " +aa + "    and  " +b + "  @" +bb);
					//System.out.println(aa<bb);
					if (aa < bb) {sb.append(">");}
					else {sb.append("=");}
				}
			}
			return sb.toString();
		}
		
		/**
		 * parse votes following the format from http://www.cs.wustl.edu/~legrand/rbvote/
		 * Votes can be separated by either a new line or a |
		 * @param votes
		 * @return
		 */
		public static List<RankedVote<String>> fromCondenseList(String votes){
			String[] lines = votes.split("\n");
			List<RankedVote<String>> result = new ArrayList<RankedVote<String>>();
			for (String line : lines){
				int n = 1;
				if (line.contains("|")){
					String[] split = line.split("\\|");
					n = Integer.parseInt(split[0]);
					line = split[1];
				}
				RankedVote<String> vote = fromCondense(line);
				for (int i = 0 ; i < n ; i++){
					result.add(vote);
				}
			}
			return result;
		}
		
		/**
		 * parse a single vote following the format from http://www.cs.wustl.edu/~legrand/rbvote/
		 * a>b=d>c meaning that we prefer a over b and d equally, c is the least favored
		 * @param vote
		 * @return
		 */
		public static RankedVote<String> fromCondense(String vote){
			String[] rankeds = vote.split(">");
			//System.out.println("rankeds " + Arrays.toString(rankeds));
			RankedVote<String> result  = new RankedVote<String>();
			int rank = 1;
			for (String ranked : rankeds){
				String[] members = ranked.split("=");
				//System.out.println("   members " + Arrays.toString(members));
				for (String member : members){
					result.addAtRank(rank, member);
				}
				rank++;
			}
			return result;
		}

		/**
		 * Encode a preferential vote into one single long
		 * CANNOT REALLY USE WHEN NUMBER OF CANDIDATES GOES BEYOND 5
		 * could be used for 5 people of love like neutral 
		 * @param candidateSet an array to determine indexes of candidates
		 * @return
		 */
		public Long encode(T... candidateSet){
			PrimeBase b = new PrimeBase();
			int[] toEncode = new int[candidateSet.length+1];
			for (int i  = 0 ; i < candidateSet.length ; i++){
				Integer nullable = this.rankFor(candidateSet[i]);
				int real = nullable==null?0:nullable;
				//System.out.println("Encode candidate " + i + " avec exposant " +real);
				// got to shift by 1 cause exponents on 1 are useless
				toEncode[i+1] = real;
			}
			//System.out.println(Arrays.toString(toEncode));
			return b.from(toEncode);
		}

		
		/**
		 * decode a prefered vote when encoded with PrimeBase
		 * @param coded
		 * @param candidateSet
		 * @return
		 */
		public static <T> RankedVote<T> decode(Long coded, T... candidateSet){
			PrimeBase b = new PrimeBase();
			int[] longs = b.to(coded);
			//System.out.println(Arrays.toString(longs));
			RankedVote<T> result = new RankedVote<T>();
			for (int i = 1 ; i < longs.length ; i++){
				long l = longs[i];
				if (l != 0){
					// compensate the shift
					result.addAtRank((int)l, candidateSet[i-1]);
				}
			}
			return result;
		}

		public static <T> RankedVote<T> atRandom(Random r, T... candidateSet){
			int size = candidateSet.length;
			RankedVote<T> result = new RankedVote<T>();
			for (int i = 0 ; i < size ; i++){
				result.addAtRank(1+r.nextInt(size-1), candidateSet[i]);
			}
			return result;
		}

		@Override
		public String toString(){
			return toCondense();
		}
		
		public RankedVote<T> without(T... candidates){
			RankedVote<T> result = new RankedVote<T>();
			List<T> list = Arrays.asList(candidates);
			for (T t : this.candidates()){
				if (!list.contains(t)){
					result = result.addAtRank(this.rankFor(t), t);
				}
			}
			return result;
		}

		@Override
		public int hashCode() {
			return this.toCondense().hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			RankedVote<T> other = (RankedVote<T>) obj;
			if (ranks == null) {
				if (other.ranks != null)
					return false;
			} else if (!toCondense().equals(other.toCondense()))
				return false;
			return true;
		}
}
