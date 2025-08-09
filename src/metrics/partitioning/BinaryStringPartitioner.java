package metrics.partitioning;

import representation.BinaryString;
import representation.base.Representation;

import java.util.Arrays;
import java.util.HashMap;

public class BinaryStringPartitioner implements SearchSpacePartitioner {
    HashMap<String, Integer> map4 = new HashMap<>();

    int length;
    int slotCount;
    int chunkSize;
    private HashMap<String, Integer> map= new HashMap<>();

    public BinaryStringPartitioner(int chunkSize,int slotCount) {
        this.slotCount= slotCount;
        this.chunkSize = chunkSize;
        initMap();

    }

    void initMap() {
        map4 = new HashMap<>();
        map4.put("0000", 13);
        map4.put("0001", 12);
        map4.put("0010", 10);
        map4.put("0011", 11);
        map4.put("0100", 8);
        map4.put("0101", 7);
        map4.put("0110", 9);
        map4.put("0111", 6);
        map4.put("1000", 14);
        map4.put("1001", 3);
        map4.put("1010", 1);
        map4.put("1011", 2);
        map4.put("1100", 15);
        map4.put("1101", 4);
        map4.put("1110", 0);
        map4.put("1111", 5);

        map = GrayCodeMapper.generateGrayCodeMap(chunkSize);
    }


    public int[] mapBinaryString(String binary) {
        assert length == binary.length() :"The binary string length does not match!";
        int numChunks = (int) Math.ceil((double) length / chunkSize);
        int[] result = new int[numChunks];

        for (int i = 0; i < numChunks; i++) {
            int start = i * chunkSize;
            int end = Math.min(start + chunkSize, length);
            String chunk = binary.substring(start, end);

            // Pad with zeros if chunk is less than 4 digits
            while (chunk.length() < 4) {
                chunk += "0";
            }

            result[i] =  chunkSize ==4 ? map4.getOrDefault(chunk, -1) :
                                         map.getOrDefault(chunk,-1); // -1 for unmapped chunks
        }

        return result;
    }

    @Override
    public String idOf(Representation rep) {
        assert rep instanceof BinaryString : "BinaryString is required!";
        BinaryString code= (BinaryString) rep;

        int[] mapped = mapBinaryString(code.toString());


        return IntegerVectorPartitioner.idOf(mapped,0,(int)Math.pow(2,chunkSize)-1,slotCount);
    }

    @Override
    public long locationCount() {
        int numChunks = (int) Math.ceil((double) length / chunkSize);
        return (long) Math.pow(slotCount,numChunks);
    }

    public static void main(String[] args) {
        BinaryStringPartitioner bsp = new BinaryStringPartitioner(16,8);
        String st = "00011010101101101010110111010101101100010100100100000101011010111";

        int[] arr = bsp.mapBinaryString(st);

        System.out.println(Arrays.toString(arr));

        System.out.println(bsp.idOf(BinaryString.of(st)));
    }



}
