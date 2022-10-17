public class PrefixExtractor {
    public static String getLongestCommonPrefix(String[] words){
        if (words==null || words.length == 0){
            return "";
        }

        if(words.length == 1) {
            return words[0];
        }

        int shortestWordLength = words[0].length();

        for (int i = 1; i < words.length; i++){

            if (words[i].length() < shortestWordLength){
                shortestWordLength = words[i].length();
            }
        }

        if (shortestWordLength == 0){
            return "";
        }

        char currentChar;
        int prefixLength = 0;
        boolean areDifferent;

        for (int i = 0; i < shortestWordLength; i++) {

            areDifferent = false;
            currentChar = words[0].charAt(i);
            for (int j = 1; j < words.length; j++){
                if(Character.compare(currentChar, words[j].charAt(i))!=0){
                    areDifferent = true;
                    break;
                }
            }

            if(areDifferent){
                break;
            }
            else{
                prefixLength++;
            }
        }

        String result = words[0].substring(0, prefixLength);
        return result;
    }
}
