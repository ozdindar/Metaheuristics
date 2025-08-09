package metrics.partitioning;

import java.util.HashMap;

public class GrayCodeMapper {

    public static HashMap<String, Integer> generateGrayCodeMap(int n) {
        HashMap<String, Integer> grayMap = new HashMap<>();
        int total = 1 << n; // 2^n combinations

        for (int i = 0; i < total; i++) {
            int gray = i ^ (i >> 1); // Gray code formula
            String binaryString = String.format("%" + n + "s", Integer.toBinaryString(i)).replace(' ', '0');
            grayMap.put(binaryString, gray);
        }

        return grayMap;
    }

    // Example usage
    public static void main(String[] args) {
        HashMap<String, Integer> grayMap = generateGrayCodeMap(8);
        for (String key : grayMap.keySet()) {
            System.out.println(key + " -> " + grayMap.get(key));
        }
    }
}
