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

/**
  LU matrix decomposition.
  <p>The LU decomposition is a lower triangular matrix L, an upper triangular matrix U,
   and a permutation <code>size</code>-long pivot vector.</p>
  <h3>Remarks</h3>
  <p>This class is a specialized adaptation from the original <a href="http://math.nist.gov/javanumerics/jama/">JAMA</a>
  (Java Matrix Package) project, brought to the public domain by The MathWorks, Inc. and the National Institute of Standards
  and Technology.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.4
  @version 0.1.0
*/
public final class LUDecomposition
{
  // <class>
  // <dynamic>
  /** Array for internal storage of decomposition. */
  private double[][] data;

  /** Matrix size. */
  private int size;

  /** Pivot sign. */
  private int pivsign;

  /** Internal storage of pivot vector. */
  private int[] piv;

  // <constructors>
  /**
    @param matrix Matrix to decompose.
  */
  public LUDecomposition(
    SquareMatrix matrix
    )
  {
    /*
      NOTE: Use a "left-looking", dot-product, Crout/Doolittle algorithm.
    */
    this.data = ((SquareMatrix)matrix.clone()).getData();
    this.size = matrix.getSize();

    this.piv = new int[size];
    for(int i = 0; i < size; i++)
    {piv[i] = i;}

    pivsign = 1;
    double[] LUrowi;
    double[] LUcolj = new double[size];

    for(int j = 0; j < size; j++)
    {
      // Making a copy of the j-th column to localize references...
      for(int i = 0; i < size; i++)
      {LUcolj[i] = data[i][j];}

      // Applying previous transformations...
      for(int i = 0; i < size; i++)
      {
        LUrowi = data[i];

        int kmax = Math.min(i,j);
        double s = 0.0;
        for(int k = 0; k < kmax; k++)
        {s += LUrowi[k] * LUcolj[k];}

        LUrowi[j] = LUcolj[i] -= s;
      }

      // Finding pivot and exchanging if necessary...
      int p = j;
      for(int i = j+1; i < size; i++)
      {
        if (Math.abs(LUcolj[i]) > Math.abs(LUcolj[p]))
        {p = i;}
      }

      if(p != j)
      {
        for(int k = 0; k < size; k++)
        {double t = data[p][k]; data[p][k] = data[j][k]; data[j][k] = t;}

        int k = piv[p]; piv[p] = piv[j]; piv[j] = k;

        pivsign = -pivsign;
      }

      // Computing multipliers...
      if(j < size
        & data[j][j] != 0.0)
      {
        for(
          int i = j+1;
          i < size;
          i++
          )
        {data[i][j] /= data[j][j];}
      }
    }
  }
  // </constructors>

  /**
    Gets the determinant.
  */
  public double getDet(
    )
  {
    double d = (double) pivsign;
    for(int j = 0; j < size; j++)
    {d *= data[j][j];}

    return d;
  }

  /**
    Gets the lower triangular factor.
  */
  public SquareMatrix getL(
    )
  {
    SquareMatrix X = new SquareMatrix(size);
    double[][] L = X.getData();
    for(int i = 0; i < size; i++)
    {
      for(int j = 0; j < size; j++)
      {
        if(i > j)
        {L[i][j] = data[i][j];}
        else if(i == j)
        {L[i][j] = 1.0;}
        else
        {L[i][j] = 0.0;}
      }
    }

    return X;
  }

  /**
    Gets the upper triangular factor.
  */
  public SquareMatrix getU(
    )
  {
    SquareMatrix X = new SquareMatrix(size);
    double[][] U = X.getData();
    for(int i = 0; i < size; i++)
    {
      for(int j = 0; j < size; j++)
      {
        if(i <= j)
        {U[i][j] = data[i][j];}
        else
        {U[i][j] = 0.0;}
      }
    }

    return X;
  }

  /**
    Gets the pivot permutation vector.
  */
  public int[] getPivot(
    )
  {
    int[] pivot = new int[size];
    for(int i = 0; i < size; i++)
    {pivot[i] = piv[i];}

    return pivot;
  }

  /**
    Gets whether the matrix is non-singular.
  */
  public boolean isNonsingular(
    )
  {
    for(int j = 0; j < size; j++)
      if (data[j][j] == 0)
        return false;

    return true;
  }

  /**
    Solves [this] * [return] = [target]
    @param target Resulting matrix.
    @return Solution.
    @exception IllegalArgumentException Matrix sizes must agree.
    @exception RuntimeException Matrix is singular.
  */
  public SquareMatrix solve(
    SquareMatrix target
    )
  {
      if (target.getSize() != size)
         throw new IllegalArgumentException("Matrix size must agree.");
      if (!this.isNonsingular())
         throw new RuntimeException("Matrix is singular.");

      // Copy right hand side with pivoting
      SquareMatrix Xmat = target.getMatrix(piv,0);
      double[][] X = Xmat.getData();

      // Solve L*Y = target(piv,:)
      for(int k = 0; k < size; k++)
      {
        for(int i = k+1; i < size; i++)
        {
          for(int j = 0; j < size; j++)
          {X[i][j] -= X[k][j] * data[i][k];}
        }
      }

      // Solve U*X = Y;
      for(int k = size - 1; k >= 0; k--)
      {
         for(int j = 0; j < size; j++)
         {X[k][j] /= data[k][k];}

         for(int i = 0; i < k; i++)
         {
            for(int j = 0; j < size; j++)
            {X[i][j] -= X[k][j] * data[i][k];}
         }
      }

      return Xmat;
   }
  // </dynamic>
  // </class>
}