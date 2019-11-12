package cecs429.disk;

import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import cecs429.index.Index;
import cecs429.index.Posting;



public class DiskInvertedIndex implements Index {

    private String mPath;
    private RandomAccessFile mVocabList;
    private RandomAccessFile mPostings;
    private long[] mVocabTable;

    // Opens a disk inverted index that was constructed in the given path.
    public DiskInvertedIndex(Path path) {
        try {
            mPath = path.toString();
            mVocabList = new RandomAccessFile(new File(path.toString(), "vocab.bin"), "r");
            mPostings = new RandomAccessFile(new File(path.toString(), "postings.bin"), "r");
            mVocabTable = readVocabTable(mPath);
            //mFileNames = readFileNames(path);
        }
        catch (FileNotFoundException ex) {
            System.out.println(ex.toString());
        }
    }

    public void start() throws IOException{

    }


    // Locates the byte position of the postings for the given term.
    // For example, binarySearchVocabulary("angel") will return the byte position
    // to seek to in postings.bin to find the postings for "angel".
    private long binarySearchVocabulary(String term) {
        // do a binary search over the vocabulary, using the vocabTable and the file vocabList.
        int i = 0, j = mVocabTable.length / 2 - 1;
        while (i <= j) {
            try {
                int m = (i + j) / 2;
                long vListPosition = mVocabTable[m * 2];
                int termLength;
                if (m == mVocabTable.length / 2 - 1) {
                    termLength = (int)(mVocabList.length() - mVocabTable[m*2]);
                }
                else {
                    termLength = (int) (mVocabTable[(m + 1) * 2] - vListPosition);
                }

                mVocabList.seek(vListPosition);

                byte[] buffer = new byte[termLength];
                mVocabList.read(buffer, 0, termLength);
                String fileTerm = new String(buffer, "ASCII");

                int compareValue = term.compareTo(fileTerm);
                if (compareValue == 0) {
                    // found it!
                    return mVocabTable[m * 2 + 1];
                }
                else if (compareValue < 0) {
                    j = m - 1;
                }
                else {
                    i = m + 1;
                }
            }
            catch (IOException ex) {
                System.out.println(ex.toString());
            }
        }
        return -1;
    }

    // Reads the file vocabTable.bin into memory.
    private static long[] readVocabTable(String indexName) {
        try {
            System.out.println(indexName);
            long[] vocabTable;

            RandomAccessFile tableFile = new RandomAccessFile(
                    new File(indexName, "vocabTable.bin"),
                    "r");

 /*
            DataInputStream tableFile = new DataInputStream(
                    new BufferedInputStream(
                            new FileInputStream(indexName + "/vocabTable.bin")));

  */


            byte[] byteBuffer = new byte[4];
            tableFile.read(byteBuffer, 0, byteBuffer.length);

            int tableIndex = 0;
            //System.out.println(ByteBuffer.wrap(byteBuffer).getInt());
            vocabTable = new long[(int) tableFile.length() / 16 * 2];
            byteBuffer = new byte[8];



            while (tableFile.read(byteBuffer, 0, byteBuffer.length) > 0) { // while we keep reading 4 bytes

                vocabTable[tableIndex] = ByteBuffer.wrap(byteBuffer).getLong();
                tableIndex++;
            }
            tableFile.close();
            //System.out.println(vocabTable);
            /*
            for(long s: vocabTable){
                System.out.println(s);
            }

             */
            return vocabTable;
        }
        catch (FileNotFoundException ex) {
            System.out.println(ex.toString());
        }
        catch (IOException ex) {
            System.out.println(ex.toString());
        }
        return null;
    }



    public int getTermCount() {
        return mVocabTable.length / 2;
    }


    public List<Posting> getPostings(String term){
        List<Posting>res =  new ArrayList<>();

        


        return res;
    }


    public List<String> getVocabulary() {
        try {

            List<String> res = new ArrayList<>();
            long locHolder = 0;

            int i = 0;
            while (i <= getTermCount() - 1){

                long termLength = 0;
                locHolder = mVocabTable[i * 2];

                if (((2*i)+2) == (getTermCount())) { // we at the last term then read to EOF
                    System.out.println(mVocabList.length());
                    termLength = (int)(mVocabList.length() - locHolder); // i is at the last term
                    i = getTermCount() ;
                }else{                                // we are not at the last term read until the next term
                    int nextNum = (2*i) + 2;
                    termLength  =  mVocabTable[nextNum] -  locHolder;
                    System.out.println(locHolder);
                    System.out.println(mVocabTable[nextNum]);
                    //int y = termLength.intValue();
                    System.out.println(termLength);
                    i++;
                }
                mVocabList.seek(locHolder);

                byte[] buffer = new byte[(int) termLength]; // making  bytes
                mVocabList.read(buffer, 0,(int) termLength ); //reading from first location of the term to end of the term
                String fileTerm = new String(buffer, "ASCII"); // encoding bytes read to string
                System.out.println(fileTerm);
                res.add(fileTerm); // adding the word in our corpus to the list so we can return it



            }

            return res;

        }catch(IOException ex){
            System.out.println(ex.toString());
        }
        return null;
    }

    public void addTerm(List<String> term, int docID, int pos){ }

}