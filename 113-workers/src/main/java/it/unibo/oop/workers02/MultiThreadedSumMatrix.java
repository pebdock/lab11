package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that gets a sum of all the matrix el.
 */
public class MultiThreadedSumMatrix implements SumMatrix {

    private final int nthread;

    /**
     * Builds a multithreaded matrix sum.
     * 
     * @param nthread no. of threads performing the sum
     */
    public MultiThreadedSumMatrix(final int nthread) {
        this.nthread = nthread;
    }

    /**
     * Makes a sum of every number in the matrix.
     */
    @Override
    public double sum(final double[][] matrix) {

        final int cols = matrix[0].length;
        final int rows = matrix.length;
        final int totEl = rows * cols;
        final int singleSize = totEl % nthread + totEl / nthread;
        final List<Worker> workers = new ArrayList<>(nthread);

        for (int start = 0; start < totEl; start += singleSize) {
            workers.add(new Worker(matrix, start, singleSize));
        }

        for (final Worker worker : workers) {
            worker.start();
        }

        double sum = 0;
        for (final Worker worker : workers) {
            try {
                worker.join();
                sum += worker.getResult();
            } catch (final InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        return sum;
    }

    private static class Worker extends Thread {

        private final double[][] matrix;
        private final int startpos;
        private final int nelem;
        private final int cols;
        private volatile double res;
        private final int endIndex;

        /**
         * Build a new worker.
         *
         * @param matrix
         *            the matrix to sum
         * @param startpos
         *            the initial position for this worker
         * @param nelem
         *            the no. of elems to sum up for this worker
         */
        Worker(final double[][] matrix, final int startpos, final int nelem) {
            super();
            this.matrix = matrix; // NOPMD
            this.startpos = startpos;
            this.nelem = nelem;
            this.cols = matrix[0].length;
            this.endIndex = Math.min(startpos + nelem, cols * matrix.length);
        }

        @Override
        public void run() {

            System.out.println("Working from position " + startpos + " to position " + (startpos + nelem - 1)); // NOPMD

            double localsum = 0;

            for (int i = startpos; i < endIndex; i++) {
                final int currentRow = i / cols;
                final int currentCol = i % cols;
                localsum += matrix[currentRow][currentCol];
            }

            this.res = localsum;
        }

        /**
         * Returns the result of summing up the integers within the list.
         *
         * @return the sum of every element in the array
         */
        public double getResult() {
            return this.res;
        }
    }
}
