public class DataCenter {
    public static int getCommunicatingServersCount(int[][] map) {
        final int ROWS = map.length;
        final int COLS = map[0].length;
        int result = 0;
        boolean[][] visited = new boolean[ROWS][COLS];

        for(int i = 0; i < ROWS - 1; i++){

            if(map[i][0] == 1 &&
               map[i+1][0] == 1){

                if(visited[i][0] == true ||
                   visited[i + 1][0] == true){
                    result++;
                }
                else{
                    result += 2;
                }
                visited[i][0] = true;
                visited[i + 1][0] = true;

            }
        }

        for(int i = 0; i < COLS - 1; i++){

            if(map[0][i + 1] == 1 &&
                    map[0][i + 1] == 1){

                if(visited[0][i] == true ||
                        visited[0][i + 1] == true){
                    result++;
                }
                else{
                    result += 2;
                }
                visited[0][i] = true;
                visited[0][i + 1] = true;

            }
        }

        for(int i=1; i < ROWS; i++){
            for(int j=1; j < COLS; j++){
                if(map[i][j] == 1 &&
                   map[i - 1][j] ==1){
                    if(visited[i - 1][j] == true){
                        result++;
                    }
                    else{
                        result += 2;
                    }
                    visited[i][j] = true;
                    continue;
                }

                if(map[i][j] == 1 &&
                   map[i][j - 1] ==1){
                    if(visited[i][j - 1] == true){
                        result++;
                    }
                    else{
                        result += 2;
                    }
                    visited[i][j] = true;
                }
            }
        }
        return result;
    }
    }