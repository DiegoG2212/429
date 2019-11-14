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
    private RandomAccessFile mDocWeights;
    private long[] mVocabTable;

    // Opens a disk inverted index that was constructed in the given path.
    public DiskInvertedIndex(Path path) {
        try {
            mPath = path.toString();
            mVocabList = new RandomAccessFile(new File(path.toString(), "vocab.bin"), "r");
            mPostings = new RandomAccessFile(new File(path.toString(), "postings.bin"), "r");
            mDocWeights = new RandomAccessFile(new File(path.toString(), "docWeights.bin"), "r");
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
                String fileTerm = new String(buffer, "UTF-8");

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

            RandomAccessFile tableFile = new RandomAccessFile(
                    new File(indexName, "vocabTable.bin"),
                    "r");

            int tableIndex = 0;
            long[] vocabTable = new long[(int) tableFile.length() / 16 * 2];
            byte[] byteBuffer = new byte[8];



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


    public List<Posting> getPostings(String term) {
        List<Posting> res = new ArrayList<>();

        try {

            long position = binarySearchVocabulary(term);
            System.out.println(position);

            byte[] buffer = new byte[4]; // making  bytes

            if (position < 0) {
                return Collections.emptyList();
            } else {
                mPostings.seek(position);

                mPostings.read(buffer, 0,4 ); //reading from first location of the term to end of the term
                int numberOfDocs = ByteBuffer.wrap(buffer).getInt();

                position = position + 4; // to go to the next number

                int i =0;
                int lastDocId = 0;
                while (i < numberOfDocs){
                    //mPostings.seek(position);
                    byte[] buffer2 = new byte[4]; // making  bytes
                    mPostings.read(buffer2, 0,4 ); //reading from first location of the term to end of the term
                    position = position + 4; // keeping track where i am
                    int DocId  = ByteBuffer.wrap(buffer2).getInt() + lastDocId;
                    lastDocId = DocId;

                    //position += 4;
                    //mPostings.seek(position);
                    mPostings.read(buffer2, 0,4 );
                    int termFreq = ByteBuffer.wrap(buffer2).getInt(); // getting number of positions of that term
                    position = position + 4;
                  //  position += 4;
                    //mPostings.seek(position);
                    mPostings.read(buffer2, 0,4 );
                    int termPos = ByteBuffer.wrap(buffer2).getInt(); // getting the locations of the term in the doc.
                    position = position + 4;

                    Posting p = new Posting(DocId, termPos); // creates the posting so
                                                             // i just add the locations in the for loop
                    position += (long) (termFreq -1)*4;

                    res.add(p); // adds posting to the list
                    i++; // adds 1  to i;

                    mPostings.seek(position);
                }

            }


            return res;


        } catch (IOException ex) {
            System.out.println(ex.toString());
        }

        return Collections.emptyList();
    }

    public List<Posting> getPositionalPostings(String term) {

        List<Posting> res = new ArrayList<>();

        try {

            long position = binarySearchVocabulary(term);
            System.out.println(position);

            byte[] buffer = new byte[4]; // making  bytes

            if (position < 0) {
                return Collections.emptyList();
            } else {
                mPostings.seek(position);

                mPostings.read(buffer, 0, 8); //reading from first location of the term to end of the term
                int numberOfDocs = ByteBuffer.wrap(buffer).getInt();

                //position = position + 4; // to go to the next number

                int i =0;
                int lastDocId = 0;
                while (i < numberOfDocs){
                    //mPostings.seek(position);
                    byte[] buffer2 = new byte[4]; // making  bytes
                    mPostings.read(buffer2, 0,4 ); //reading from first location of the term to end of the term
                    int DocId  = ByteBuffer.wrap(buffer2).getInt() + lastDocId;
                    lastDocId = DocId;
                    
                    //position += 4;
                    //mPostings.seek(position);
                    mPostings.read(buffer2, 0,4 );
                    int termFreq = ByteBuffer.wrap(buffer2).getInt(); // getting number of positions of that term

                    //position += 4;
                    //mPostings.seek(position);
                    mPostings.read(buffer2, 0,4 );
                    int termPos = ByteBuffer.wrap(buffer2).getInt(); // getting the locations of the term in the doc.

                    Posting p = new Posting(DocId, termPos); // creates the posting so
                                                             // i just add the locations in the for loop
                    for(int j = 0; j < termFreq - 1; j++){
                        mPostings.read(buffer2, 0,4 );
                        termPos = ByteBuffer.wrap(buffer2).getInt(); // getting the locations of the term in the doc.

                        p.addPos(termPos); // otherwise is just add the location on to it

                        //position += 4;// goes to the next location


                    }
                    res.add(p); // adds posting to the list
                    i++; // adds 1  to i;
                    //position += 4; // makes it go to the next doc id.
                }

            }


            return res;


        } catch (IOException ex) {
            System.out.println(ex.toString());
        }

        return Collections.emptyList();
    }


    public List<String> getVocabulary() {
        try {

            List<String> res = new ArrayList<>();
            long locHolder = 0;

            int i = 0;
            while (i <= mVocabTable.length - 1){

                long termLength = 0;
                locHolder = mVocabTable[i * 2];

                if (((2*i)+1) == (mVocabTable.length - 1)) { // we at the last term then read to EOF
                    int nextNum = mVocabTable.length - 1;
                    System.out.println(mVocabList.length());
                    termLength = mVocabTable[nextNum] - locHolder; // i is at the last term

                    i = mVocabTable.length ;
                } else {                                // we are not at the last term read until the next term
                    int nextNum = (2*i) + 2;
                    termLength  =  mVocabTable[nextNum] -  locHolder;
                    System.out.println(locHolder);
                    System.out.println(mVocabTable[nextNum]);
                    //int y = termLength.intValue();
                    System.out.println((termLength));
                    i++;
                }
                mVocabList.seek(locHolder);

                byte[] buffer = new byte[(int) termLength]; // making  bytes
                mVocabList.read(buffer, 0,(int) termLength ); //reading from first location of the term to end of the term
                String fileTerm = new String(buffer, "UTF-8"); // encoding bytes read to string
                System.out.println(fileTerm);
                res.add(fileTerm); // adding the word in our corpus to the list so we can return it



            }

            return res;

        }catch(IOException ex){
            System.out.println(ex.toString());
        }
        return null;
    }

    @Override
    public void addTerm(List<String> term, int docID, int pos) {}

    public List<Double> getLds() {
        try {
            List<Double> Lds = Collections.emptyList();
            byte[] buffer = new byte[8];

            for (int i = 0; i < mDocWeights.length(); i++) {
                mDocWeights.read(buffer, 0, 8);

                double docLength = ByteBuffer.wrap(buffer).getDouble();

                Lds.add(docLength);
            }
            return Lds;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}