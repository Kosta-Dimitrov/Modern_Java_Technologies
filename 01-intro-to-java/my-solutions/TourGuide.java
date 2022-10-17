public class TourGuide {
    public static int getBestSightseeingPairScore(int[] places){
        int bestScore = 0;

        for(int i=0; i < places.length - 1; i++ ){

            for(int j = i+1; j < places.length; j++  ){

                if(places[i] + places[j] + i - j > bestScore ) {
                    bestScore = places[i] + places[j] + i - j;
                }
            }
        }
        return bestScore;
    }
}
