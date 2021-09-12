public class LCS {
	private static String[][] memory;
	private static String str1, str2;
	public void LCS() {
	}
	public static String findLCS(String s1, String s2) {
		memory = new String[s1.length()+1][s2.length()+1];
		str1 = s1;
		str2 = s2;
		return find("","",0,0);
	}
	private static String find(String s1, String s2, int index1, int index2) {
		if(index1 == str1.length() || index2 == str2.length()) return"";
		if(str1.charAt(index1) == str2.charAt(index2)) {
			return  str1.charAt(index1) + find(s1+str1.charAt(index1), s2 + str2.charAt(index2),index1+1,index2+1);
		}
		else {
			if(memory[index1 + 1][index2] == null) memory[index1 + 1][index2] = find(s1+str1.charAt(index1),s2, index1+1,index2);
			if(memory[index1][index2 + 1] == null) memory[index1][index2 + 1] = find(s1,s2+str2.charAt(index2), index1,index2+1);
			if(memory[index1 + 1][index2].length() > memory[index1][index2 + 1].length()) {
				return memory[index1 + 1][index2];
			}
			else {
				return memory[index1][index2 + 1];
			}
		}
	}
}