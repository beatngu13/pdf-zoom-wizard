/*
  Copyright 2007-2010 Stefano Chizzolini. http://www.pdfclown.org

  Contributors:
    * Stefano Chizzolini (original code developer, http://www.stefanochizzolini.it)

  This file should be part of the source code distribution of "PDF Clown library"
  (the Program): see the accompanying README files for more info.

  This Program is free software; you can redistribute it and/or modify it under the terms
  of the GNU Lesser General Public License as published by the Free Software Foundation;
  either version 3 of the License, or (at your option) any later version.

  This Program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY,
  either expressed or implied; without even the implied warranty of MERCHANTABILITY or
  FITNESS FOR A PARTICULAR PURPOSE. See the License for more details.

  You should have received a copy of the GNU Lesser General Public License along with this
  Program (see README files); if not, go to the GNU website (http://www.gnu.org/licenses/).

  Redistribution and use, with or without modification, are permitted provided that such
  redistributions retain the above copyright notice, license and disclaimer, along with
  this list of conditions.
*/

package org.pdfclown.util.math;

import java.awt.geom.AffineTransform;

/**
  Square matrix providing fundamental operations of linear algebra.
  <h3>Remarks</h3>
  <p>This class is a specialized adaptation from the original <a href="http://math.nist.gov/javanumerics/jama/">JAMA</a>
  (Java Matrix Package) project, brought to the public domain by The MathWorks, Inc. and the National Institute of Standards
  and Technology.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.4
  @version 0.1.0
*/
public final class SquareMatrix
  implements Cloneable
{
  // <class>
  // <static>
  /**
    Gets the {@link SquareMatrix} representation of an {@link java.awt.geom.AffineTransform AffineTransform} object.
  */
  public static SquareMatrix get(
    AffineTransform transform
    )
  {
    return new SquareMatrix(
      new double[][]
      {
        {transform.getScaleX(), transform.getShearY(), 0},
        {transform.getShearX(), transform.getScaleY(), 0},
        {transform.getTranslateX(), transform.getTranslateY(), 1}}
      );
  }

  /**
    Gets the identity matrix.

    @param size Matrix dimension.
  */
  public static SquareMatrix getIdentity(
    int size
    )
  {
    SquareMatrix matrix = new SquareMatrix(size);
    double[][] matrixData = matrix.getData();
    for(int i = 0; i < size; i++)
    {
      for(int j = 0; j < size; j++)
      {matrixData[i][j] = (i == j ? 1.0 : 0.0);}
    }

    return matrix;
  }

  //TODO: implement if necessary!!!
  /*
    [return] = [matrix1] + [matrix2]
    @param matrix1 1st addend matrix.
    @param matrix2 2nd addend matrix.
  */
//   public static SquareMatrix sum(
//     SquareMatrix matrix1,
//     SquareMatrix matrix2
//     )
//   {
//     checkSize(matrix1,matrix2);
//
//     SquareMatrix sumMatrix = new SquareMatrix(matrix1.size);
//
//     sum(matrix1.data,matrix2.data,sumMatrix.data);
//
//     return sumMatrix;
//   }

//   private static void checkSize(
//     SquareMatrix matrix1,
//     SquareMatrix matrix2
//     )
//   {
//     if (matrix1.getSize() != matrix2.getSize())
//       throw new IllegalArgumentException("SquareMatrix dimensions must agree.");
//   }

//   private static void sum(
//     double[][] matrix1Data,
//     double[][] matrix2Data,
//     double[][] sumMatrixData
//     )
//   {
//     for(int i = 0; i < matrix1Data.length; i++)
//     {
//       for(int j = 0; j < matrix1Data.length; j++)
//       {sumMatrixData[i][j] = matrix1Data[i][j] + matrix2Data[i][j];}
//     }
//   }
  // </static>

  // <dynamic>
  private double[][] data;
  private int size;

  // <constructors>
  public SquareMatrix(
    int size
    )
  {
    this.size = size;
    data = new double[size][size];
  }

  /**
    Construct a matrix from a 2-D array.

    @param data Two-dimensional array of doubles.
    @exception IllegalArgumentException All rows must have the same length
  */
  public SquareMatrix(
    double[][] data
    )
  {
      size = data.length;
      for(
        int i = 0;
        i < size;
        i++
        )
      {
         if (data[i].length != size)
            throw new IllegalArgumentException("All rows must have the same length.");
      }
      this.data = data;
   }
  // </constructors>

  @Override
  public Object clone(
    )
  {
    SquareMatrix clone = new SquareMatrix(size);
    double[][] cloneData = clone.getData();
    for(int i = 0; i < size; i++)
    {
      for(int j = 0; j < size; j++)
      {cloneData[i][j] = data[i][j];}
    }

    return clone;
  }

  /**
    Gets the internal two-dimensional array.
  */
  public double[][] getData(
    )
  {return data;}

  /**
    Gets a submatrix.

    @param rowIndexes Array of row indices.
    @param startColumnIndex Initial column index.
    @exception  ArrayIndexOutOfBoundsException Submatrix indices
  */
  public SquareMatrix getMatrix(
    int[] rowIndexes,
    int startColumnIndex
    )
  {
    SquareMatrix subMatrix = new SquareMatrix(rowIndexes.length);
    double[][] subMatrixData = subMatrix.getData();
    int endColumnIndex = startColumnIndex + rowIndexes.length - 1;
    try
    {
      for(int i = 0; i < rowIndexes.length; i++)
      {
        for(int j = startColumnIndex; j <= endColumnIndex; j++)
        {subMatrixData[i][j - startColumnIndex] = data[rowIndexes[i]][j];}
      }
    }
    catch(ArrayIndexOutOfBoundsException e)
    {throw new ArrayIndexOutOfBoundsException("Submatrix indices");}

    return subMatrix;
  }

  /*
    [this] = [this] + [matrix]
    @param matrix Addend.
  */
//   public SquareMatrix sum(
//     SquareMatrix matrix
//     )
//   {
//     checkSize(matrix);
//
//     sum(data,matrix.data,data);
//
//     return this;
//   }

   /* C = data - B
   @param B    another matrix
   @return     data - B
   */
//    public SquareMatrix minus (SquareMatrix B) {
//       checkSize(B);
//       SquareMatrix X = new SquareMatrix(m,n);
//       double[][] C = X.getData();
//       for (int i = 0; i < m; i++) {
//          for (int j = 0; j < n; j++) {
//             C[i][j] = data[i][j] - B.data[i][j];
//          }
//       }
//       return X;
//    }

   /* data = data - B
   @param B    another matrix
   @return     data - B
   */
//    public SquareMatrix minusEquals (SquareMatrix B) {
//       checkSize(B);
//       for (int i = 0; i < m; i++) {
//          for (int j = 0; j < n; j++) {
//             data[i][j] = data[i][j] - B.data[i][j];
//          }
//       }
//       return this;
//    }

   /* Element-by-element multiplication, C = data.*B
   @param B    another matrix
   @return     data.*B
   */
//    public SquareMatrix arrayTimes (SquareMatrix B) {
//       checkSize(B);
//       SquareMatrix X = new SquareMatrix(m,n);
//       double[][] C = X.getData();
//       for (int i = 0; i < m; i++) {
//          for (int j = 0; j < n; j++) {
//             C[i][j] = data[i][j] * B.data[i][j];
//          }
//       }
//       return X;
//    }

   /* Element-by-element multiplication in place, data = data.*B
   @param B    another matrix
   @return     data.*B
   */
//    public SquareMatrix arrayTimesEquals (SquareMatrix B) {
//       checkSize(B);
//       for (int i = 0; i < m; i++) {
//          for (int j = 0; j < n; j++) {
//             data[i][j] = data[i][j] * B.data[i][j];
//          }
//       }
//       return this;
//    }

   /* Element-by-element right division, C = data./B
   @param B    another matrix
   @return     data./B
   */
//    public SquareMatrix arrayRightDivide (SquareMatrix B) {
//       checkSize(B);
//       SquareMatrix X = new SquareMatrix(m,n);
//       double[][] C = X.getData();
//       for (int i = 0; i < m; i++) {
//          for (int j = 0; j < n; j++) {
//             C[i][j] = data[i][j] / B.data[i][j];
//          }
//       }
//       return X;
//    }

   /* Element-by-element right division in place, data = data./B
   @param B    another matrix
   @return     data./B
   */
//    public SquareMatrix arrayRightDivideEquals (SquareMatrix B) {
//       checkSize(B);
//       for (int i = 0; i < m; i++) {
//          for (int j = 0; j < n; j++) {
//             data[i][j] = data[i][j] / B.data[i][j];
//          }
//       }
//       return this;
//    }

   /* Element-by-element left division, C = data.\B
   @param B    another matrix
   @return     data.\B
   */
//    public SquareMatrix arrayLeftDivide (SquareMatrix B) {
//       checkSize(B);
//       SquareMatrix X = new SquareMatrix(m,n);
//       double[][] C = X.getData();
//       for (int i = 0; i < m; i++) {
//          for (int j = 0; j < n; j++) {
//             C[i][j] = B.data[i][j] / data[i][j];
//          }
//       }
//       return X;
//    }

   /* Element-by-element left division in place, data = data.\B
   @param B    another matrix
   @return     data.\B
   */
//    public SquareMatrix arrayLeftDivideEquals (SquareMatrix B) {
//       checkSize(B);
//       for (int i = 0; i < m; i++) {
//          for (int j = 0; j < n; j++) {
//             data[i][j] = B.data[i][j] / data[i][j];
//          }
//       }
//       return this;
//    }

   /* Multiply a matrix by a scalar, C = s*data
   @param s    scalar
   @return     s*data
   */
//    public SquareMatrix times (double s) {
//       SquareMatrix X = new SquareMatrix(m,n);
//       double[][] C = X.getData();
//       for (int i = 0; i < m; i++) {
//          for (int j = 0; j < n; j++) {
//             C[i][j] = s*data[i][j];
//          }
//       }
//       return X;
//    }

   /* Multiply a matrix by a scalar in place, data = s*data
   @param s    scalar
   @return     replace data by s*data
   */
//    public SquareMatrix timesEquals (double s) {
//       for (int i = 0; i < m; i++) {
//          for (int j = 0; j < n; j++) {
//             data[i][j] = s*data[i][j];
//          }
//       }
//       return this;
//    }

   /* Linear algebraic matrix multiplication, data * B
   @param B    another matrix
   @return     SquareMatrix product, data * B
   @exception  IllegalArgumentException SquareMatrix inner dimensions must agree.
   */
//    public SquareMatrix times (SquareMatrix B) {
//       if (B.m != n) {
//          throw new IllegalArgumentException("SquareMatrix inner dimensions must agree.");
//       }
//       SquareMatrix X = new SquareMatrix(m,B.n);
//       double[][] C = X.getData();
//       double[] Bcolj = new double[n];
//       for (int j = 0; j < B.n; j++) {
//          for (int k = 0; k < n; k++) {
//             Bcolj[k] = B.data[k][j];
//          }
//          for (int i = 0; i < m; i++) {
//             double[] Arowi = data[i];
//             double s = 0;
//             for (int k = 0; k < n; k++) {
//                s += Arowi[k]*Bcolj[k];
//             }
//             C[i][j] = s;
//          }
//       }
//       return X;
//    }

  /**
    Gets the matrix determinant.
  */
  public double getDet(
    )
  {return (new LUDecomposition(this)).getDet();}

  /**
    Gets the matrix inverse.
  */
  public SquareMatrix getInverse(
    )
  {return solve(getIdentity(size));}

  /**
    Gets the matrix dimension.
  */
  public int getSize(
    )
  {return size;}

 /**
    Solves [this] * [return] = [target]

    @param target Resulting matrix.
    @return Solution.
  */
  public SquareMatrix solve(
    SquareMatrix target
    )
  {return (new LUDecomposition(this)).solve(target);}

  /**
    Converts this matrix into an {@link java.awt.geom.AffineTransform AffineTransform} object.
  */
  public AffineTransform toTransform(
    )
  {
    if(size != 3)
      throw new IllegalStateException("Matrix dimension MUST be 3.");

    return new AffineTransform(
      data[0][0],
      data[0][1],
      data[1][0],
      data[1][1],
      data[2][0],
      data[2][1]
      );
  }
  // </dynamic>
  // </class>
}