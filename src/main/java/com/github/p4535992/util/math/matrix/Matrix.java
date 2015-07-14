package com.github.p4535992.util.math.matrix;

/**
 * Created by 4535992 on 13/07/2015.
 * Class A bare-bones immutable data type for M-by-N matrices.
 * href: http://introcs.cs.princeton.edu/java/95linear/Matrix.java.html
 * @author 4535992.
 * @version 2015-07-13.
 */
@SuppressWarnings("unused")
public class Matrix {
    private final int M;             // number of rows
    private final int N;             // number of columns
    private final double[][] data;   // M-by-N array

    // create M-by-N matrix of 0's
    public Matrix(int M, int N) {
        this.M = M;
        this.N = N;
        data = new double[M][N];
    }

    // create matrix based on 2d array
    public Matrix(double[][] data) {
        M = data.length;
        N = data[0].length;
        this.data = new double[M][N];
        for (int i = 0; i < M; i++){
            // for (int j = 0; j < N; j++) this.data[i][j] = data[i][j];
            System.arraycopy(data[i], 0, this.data[i], 0, N);
        }
    }

    // copy constructor
    private Matrix(Matrix A) { this(A.data); }

    // create and return a random M-by-N matrix with values between 0 and 1
    public static Matrix random(int M, int N) {
        Matrix A = new Matrix(M, N);
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                A.data[i][j] = Math.random();
        return A;
    }

    // create and return the N-by-N identity matrix
    public static Matrix identity(int N) {
        Matrix I = new Matrix(N, N);
        for (int i = 0; i < N; i++)
            I.data[i][i] = 1;
        return I;
    }

    // swap rows i and j
    private void swap(int i, int j) {
        double[] temp = data[i];
        data[i] = data[j];
        data[j] = temp;
    }

    // create and return the transpose of the invoking matrix
    public Matrix transpose() {
        Matrix A = new Matrix(N, M);
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                A.data[j][i] = this.data[i][j];
        return A;
    }

    // return C = A + B
    public Matrix plus(Matrix B) {
        Matrix A = this;
        if (B.M != A.M || B.N != A.N) throw new RuntimeException("Illegal matrix dimensions.");
        Matrix C = new Matrix(M, N);
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                C.data[i][j] = A.data[i][j] + B.data[i][j];
        return C;
    }


    // return C = A - B
    public Matrix minus(Matrix B) {
        Matrix A = this;
        if (B.M != A.M || B.N != A.N) throw new RuntimeException("Illegal matrix dimensions.");
        Matrix C = new Matrix(M, N);
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                C.data[i][j] = A.data[i][j] - B.data[i][j];
        return C;
    }

    // does A = B exactly?
    public boolean eq(Matrix B) {
        Matrix A = this;
        if (B.M != A.M || B.N != A.N) throw new RuntimeException("Illegal matrix dimensions.");
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                if (A.data[i][j] != B.data[i][j]) return false;
        return true;
    }

    // return C = A * B
    public Matrix times(Matrix B) {
        Matrix A = this;
        if (A.N != B.M) throw new RuntimeException("Illegal matrix dimensions.");
        Matrix C = new Matrix(A.M, B.N);
        for (int i = 0; i < C.M; i++)
            for (int j = 0; j < C.N; j++)
                for (int k = 0; k < A.N; k++)
                    C.data[i][j] += (A.data[i][k] * B.data[k][j]);
        return C;
    }


    // return x = A^-1 b, assuming A is square and has full rank
    public Matrix solve(Matrix rhs) {
        if (M != N || rhs.M != N || rhs.N != 1)
            throw new RuntimeException("Illegal matrix dimensions.");

        // create copies of the data
        Matrix A = new Matrix(this);
        Matrix b = new Matrix(rhs);

        // Gaussian elimination with partial pivoting
        for (int i = 0; i < N; i++) {

            // find pivot row and swap
            int max = i;
            for (int j = i + 1; j < N; j++)
                if (Math.abs(A.data[j][i]) > Math.abs(A.data[max][i]))
                    max = j;
            A.swap(i, max);
            b.swap(i, max);

            // singular
            if (A.data[i][i] == 0.0) throw new RuntimeException("Matrix is singular.");

            // pivot within b
            for (int j = i + 1; j < N; j++)
                b.data[j][0] -= b.data[i][0] * A.data[j][i] / A.data[i][i];

            // pivot within A
            for (int j = i + 1; j < N; j++) {
                double m = A.data[j][i] / A.data[i][i];
                for (int k = i+1; k < N; k++) {
                    A.data[j][k] -= A.data[i][k] * m;
                }
                A.data[j][i] = 0.0;
            }
        }

        // back substitution
        Matrix x = new Matrix(N, 1);
        for (int j = N - 1; j >= 0; j--) {
            double t = 0.0;
            for (int k = j + 1; k < N; k++)
                t += A.data[j][k] * x.data[k][0];
            x.data[j][0] = (b.data[j][0] - t) / A.data[j][j];
        }
        return x;

    }

    // print matrix to standard output
    public void show() {
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++)
                System.out.printf("%9.4f ", data[i][j]);
            System.out.println();
        }
    }



    // test client
    /*public static void main(String[] args) {
        double[][] d = { { 1, 2, 3 }, { 4, 5, 6 }, { 9, 1, 3} };

        *//*
        1,0000    2,0000    3,0000
        4,0000    5,0000    6,0000
        9,0000    1,0000    3,0000
        *//*
        Matrix D = new Matrix(d);
        D.show();
        System.out.println();

       *//*
        0,9794    0,8753    0,5271    0,4165    0,0451
        0,2815    0,2901    0,5538    0,0958    0,3053
        0,2665    0,4943    0,1368    0,2911    0,3043
        0,7716    0,9129    0,3734    0,4831    0,4232
        0,8946    0,8551    0,6859    0,2140    0,2347
        *//*
        Matrix A = Matrix.random(5, 5);
        A.show();
        System.out.println();

        *//*
         0,9794    0,8753    0,5271    0,4165    0,0451
         0,2665    0,4943    0,1368    0,2911    0,3043
         0,2815    0,2901    0,5538    0,0958    0,3053
         0,7716    0,9129    0,3734    0,4831    0,4232
         0,8946    0,8551    0,6859    0,2140    0,2347
         *//*
        A.swap(1, 2);
        A.show();
        System.out.println();

        *//*
        0,9794    0,2665    0,2815    0,7716    0,8946
        0,8753    0,4943    0,2901    0,9129    0,8551
        0,5271    0,1368    0,5538    0,3734    0,6859
        0,4165    0,2911    0,0958    0,4831    0,2140
        0,0451    0,3043    0,3053    0,4232    0,2347
         *//*
        Matrix B = A.transpose();
        B.show();
        System.out.println();

        *//*
        1,0000    0,0000    0,0000    0,0000    0,0000
        0,0000    1,0000    0,0000    0,0000    0,0000
        0,0000    0,0000    1,0000    0,0000    0,0000
        0,0000    0,0000    0,0000    1,0000    0,0000
        0,0000    0,0000    0,0000    0,0000    1,0000
         *//*
        Matrix C = Matrix.identity(5);
        C.show();
        System.out.println();

        *//*
        1,9588    1,1418    0,8087    1,1881    0,9397
        1,1418    0,9886    0,4269    1,2040    1,1594
        0,8087    0,4269    1,1075    0,4692    0,9912
        1,1881    1,2040    0,4692    0,9661    0,6373
        0,9397    1,1594    0,9912    0,6373    0,4694
         *//*
        A.plus(B).show();
        System.out.println();

        *//*
         2,5053    2,5401    1,6104    1,0767    0,7478
         2,5401    2,6593    1,6171    1,1602    0,8655
         1,6104    1,6171    1,2131    0,6396    0,5535
         1,0767    1,1602    0,6396    0,5465    0,3913
         0,7478    0,8655    0,5535    0,3913    0,4221
         *//*
        B.times(A).show();
        System.out.println();

        // shouldn't be equal since AB != BA in general
        //false
        System.out.println(A.times(B).eq(B.times(A)));
        System.out.println();

        *//*
         0,5327
         0,7442
         0,3566
         0,8271
         0,3218
         *//*
        Matrix b = Matrix.random(5, 1);
        b.show();
        System.out.println();

        *//*
         -2,2760
         1,7086
         0,9079
         1,9585
         -0,6178
         *//*
        Matrix x = A.solve(b);
        x.show();
        System.out.println();

        *//*
        0,5327
        0,7442
        0,3566
        0,8271
        0,3218
        *//*
        A.times(x).show();

    }*/
}
