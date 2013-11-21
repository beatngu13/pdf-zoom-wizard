/*
  Copyright 2006-2011 Stefano Chizzolini. http://www.pdfclown.org

  Contributors:
    * Stefano Chizzolini (original code developer, http://www.stefanochizzolini.it):
      - porting and adaptation (extension to any bit depth other than 8) of [JT]
        predictor-decoding implementation.
    * Joshua Tauberer (code contributor, http://razor.occams.info):
      - predictor-decoding contributor on .NET implementation.

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

package org.pdfclown.bytes.filters;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;

/**
  zlib/deflate [RFC:1950,1951] filter [PDF:1.6:3.3.3].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @author Joshua Tauberer (http://razor.occams.info)
  @since 0.0.2
  @version 0.1.1, 04/25/11
*/
@PDF(VersionEnum.PDF12)
public final class FlateFilter
  extends Filter
{
  // <class>
  // <dynamic>
  // <constructors>
  FlateFilter(
    )
  {}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public byte[] decode(
    byte[] data,
    int offset,
    int length,
    PdfDictionary parameters
    )
  {
    try
    {
      InflaterInputStream inputFilter = new InflaterInputStream(
        new ByteArrayInputStream(data, offset, length)
        );
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      transform(inputFilter, outputStream);
      return decodePredictor(outputStream.toByteArray(), parameters);
    }
    catch(IOException e)
    {throw new RuntimeException(e);}
  }

  @Override
  public byte[] encode(
    byte[] data,
    int offset,
    int length,
    PdfDictionary parameters
    )
  {
    try
    {
      ByteArrayInputStream inputStream = new ByteArrayInputStream(data, offset, length);
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      DeflaterOutputStream outputFilter = new DeflaterOutputStream(outputStream);
      transform(inputStream, outputFilter);
      return outputStream.toByteArray();
    }
    catch(IOException e)
    {throw new RuntimeException(e);}
  }
  // </public>

  // <private>
  private byte[] decodePredictor(
    byte[] data,
    PdfDictionary parameters
    ) throws IOException
  {
    if(parameters == null)
      return data;

    int predictor = (parameters.containsKey(PdfName.Predictor) ? ((PdfInteger)parameters.get(PdfName.Predictor)).getRawValue() : 1);
    if(predictor == 1) // No predictor was applied during data encoding.
      return data;

    int sampleComponentBitsCount = (parameters.containsKey(PdfName.BitsPerComponent) ? ((PdfInteger)parameters.get(PdfName.BitsPerComponent)).getRawValue() : 8);
    int sampleComponentsCount = (parameters.containsKey(PdfName.Colors) ? ((PdfInteger)parameters.get(PdfName.Colors)).getRawValue() : 1);
    int rowSamplesCount = (parameters.containsKey(PdfName.Columns) ? ((PdfInteger)parameters.get(PdfName.Columns)).getRawValue() : 1);

    InputStream input = new ByteArrayInputStream(data);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    switch (predictor)
    {
      case 2: // TIFF Predictor 2 (component-based).
      {
        int[] sampleComponentPredictions = new int[sampleComponentsCount];
        int sampleComponentDelta = 0;
        int sampleComponentIndex = 0;
        while((sampleComponentDelta = input.read()) != -1)
        {
          int sampleComponent = sampleComponentDelta + sampleComponentPredictions[sampleComponentIndex];
          output.write(sampleComponent);

          sampleComponentPredictions[sampleComponentIndex] = sampleComponent;

          sampleComponentIndex = ++sampleComponentIndex % sampleComponentsCount;
        }
        break;
      }
      default: // PNG Predictors [RFC 2083] (byte-based).
      {
        int sampleBytesCount = (int)Math.ceil(sampleComponentBitsCount * sampleComponentsCount / 8); // Number of bytes per pixel (bpp).
        int rowSampleBytesCount = (int)Math.ceil(sampleComponentBitsCount * sampleComponentsCount * rowSamplesCount / 8) + sampleBytesCount; // Number of bytes per row (comprising a leading upper-left sample (see Paeth method)).
        int[] previousRowBytePredictions = new int[rowSampleBytesCount];
        int[] currentRowBytePredictions = new int[rowSampleBytesCount];
        int[] leftBytePredictions = new int[sampleBytesCount];
        int predictionMethod;
        while((predictionMethod = input.read()) != -1)
        {
          System.arraycopy(currentRowBytePredictions, 0, previousRowBytePredictions, 0, currentRowBytePredictions.length);
          Arrays.fill(leftBytePredictions, 0, leftBytePredictions.length, 0);
          for(
            int rowSampleByteIndex = sampleBytesCount; // Starts after the leading upper-left sample (see Paeth method).
            rowSampleByteIndex < rowSampleBytesCount;
            rowSampleByteIndex++
            )
          {
            int byteDelta = input.read();

            int sampleByteIndex = rowSampleByteIndex % sampleBytesCount;

            int sampleByte;
            switch(predictionMethod)
            {
              case 0: // None (no prediction).
                sampleByte = byteDelta;
                break;
              case 1: // Sub (predicts the same as the sample to the left).
                sampleByte = byteDelta + leftBytePredictions[sampleByteIndex];
                break;
              case 2: // Up (predicts the same as the sample above).
                sampleByte = byteDelta + previousRowBytePredictions[rowSampleByteIndex];
                break;
              case 3: // Average (predicts the average of the sample to the left and the sample above).
                sampleByte = byteDelta + (int)Math.floor(((leftBytePredictions[sampleByteIndex] + previousRowBytePredictions[rowSampleByteIndex])) / 2);
                break;
              case 4: // Paeth (a nonlinear function of the sample above, the sample to the left, and the sample to the upper left).
              {
                int paethPrediction;
                {
                  int leftBytePrediction = leftBytePredictions[sampleByteIndex];
                  int topBytePrediction = previousRowBytePredictions[rowSampleByteIndex];
                  int topLeftBytePrediction = previousRowBytePredictions[rowSampleByteIndex - sampleBytesCount];
                  int initialPrediction = leftBytePrediction + topBytePrediction - topLeftBytePrediction;
                  int leftPrediction = Math.abs(initialPrediction - leftBytePrediction);
                  int topPrediction = Math.abs(initialPrediction - topBytePrediction);
                  int topLeftPrediction = Math.abs(initialPrediction - topLeftBytePrediction);
                  if(leftPrediction <= topPrediction
                    && leftPrediction <= topLeftPrediction)
                  {paethPrediction = leftBytePrediction;}
                  else if(topPrediction <= topLeftPrediction)
                  {paethPrediction = topBytePrediction;}
                  else
                  {paethPrediction = topLeftBytePrediction;}
                }
                sampleByte = byteDelta + paethPrediction;
                break;
              }
              default:
                throw new UnsupportedOperationException("Prediction method " + predictionMethod + " unknown.");
            }
            output.write(sampleByte);

            leftBytePredictions[sampleByteIndex] = currentRowBytePredictions[rowSampleByteIndex] = sampleByte;
          }
        }
        break;
      }
    }
    return output.toByteArray();
  }

  private void transform(
    InputStream input,
    OutputStream output
    ) throws IOException
  {
    byte[] buffer = new byte[8192]; int bufferLength;
    while((bufferLength = input.read(buffer, 0, buffer.length)) != -1)
    {output.write(buffer, 0, bufferLength);}

    input.close(); output.close();
  }
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}