/*
  Copyright 2006-2012 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.contents.objects;

import java.util.ArrayList;
import java.util.List;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.bytes.IOutputStream;
import org.pdfclown.documents.Document;
import org.pdfclown.files.File;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.tokens.Chunk;

/**
  Content stream instruction [PDF:1.6:3.7.1].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.2
  @version 0.1.2, 08/23/12
*/
@PDF(VersionEnum.PDF10)
public abstract class Operation
  extends ContentObject
{
  // <class>
  // <static>
  // <interface>
  // <public>
  /**
    Gets an operation.

    @param operator Operator.
    @param operands List of operands.
  */
  public static Operation get(
    String operator,
    List<PdfDirectObject> operands
    )
  {
    if(operator == null)
      return null;

    if(operator.equals(SaveGraphicsState.Operator))
      return SaveGraphicsState.Value;
    else if(operator.equals(SetFont.Operator))
      return new SetFont(operands);
    else if(operator.equals(SetStrokeColor.Operator)
      || operator.equals(SetStrokeColor.ExtendedOperator))
      return new SetStrokeColor(operator, operands);
    else if(operator.equals(SetStrokeColorSpace.Operator))
      return new SetStrokeColorSpace(operands);
    else if(operator.equals(SetFillColor.Operator)
      || operator.equals(SetFillColor.ExtendedOperator))
      return new SetFillColor(operator, operands);
    else if(operator.equals(SetFillColorSpace.Operator))
      return new SetFillColorSpace(operands);
    else if(operator.equals(SetDeviceGrayStrokeColor.Operator))
      return new SetDeviceGrayStrokeColor(operands);
    else if(operator.equals(SetDeviceGrayFillColor.Operator))
      return new SetDeviceGrayFillColor(operands);
    else if(operator.equals(SetDeviceRGBStrokeColor.Operator))
      return new SetDeviceRGBStrokeColor(operands);
    else if(operator.equals(SetDeviceRGBFillColor.Operator))
      return new SetDeviceRGBFillColor(operands);
    else if(operator.equals(SetDeviceCMYKStrokeColor.Operator))
      return new SetDeviceCMYKStrokeColor(operands);
    else if(operator.equals(SetDeviceCMYKFillColor.Operator))
      return new SetDeviceCMYKFillColor(operands);
    else if(operator.equals(RestoreGraphicsState.Operator))
      return RestoreGraphicsState.Value;
    else if(operator.equals(BeginSubpath.Operator))
      return new BeginSubpath(operands);
    else if(operator.equals(CloseSubpath.Operator))
      return CloseSubpath.Value;
    else if(operator.equals(PaintPath.CloseStrokeOperator))
      return PaintPath.CloseStroke;
    else if(operator.equals(PaintPath.FillOperator)
      || operator.equals(PaintPath.FillObsoleteOperator))
      return PaintPath.Fill;
    else if(operator.equals(PaintPath.FillEvenOddOperator))
      return PaintPath.FillEvenOdd;
    else if(operator.equals(PaintPath.StrokeOperator))
      return PaintPath.Stroke;
    else if(operator.equals(PaintPath.FillStrokeOperator))
      return PaintPath.FillStroke;
    else if(operator.equals(PaintPath.FillStrokeEvenOddOperator))
      return PaintPath.FillStrokeEvenOdd;
    else if(operator.equals(PaintPath.CloseFillStrokeOperator))
      return PaintPath.CloseFillStroke;
    else if(operator.equals(PaintPath.CloseFillStrokeEvenOddOperator))
      return PaintPath.CloseFillStrokeEvenOdd;
    else if(operator.equals(PaintPath.EndPathNoOpOperator))
      return PaintPath.EndPathNoOp;
    else if(operator.equals(ModifyClipPath.NonZeroOperator))
      return ModifyClipPath.NonZero;
    else if(operator.equals(ModifyClipPath.EvenOddOperator))
      return ModifyClipPath.EvenOdd;
    else if(operator.equals(TranslateTextToNextLine.Operator))
      return TranslateTextToNextLine.Value;
    else if(operator.equals(ShowSimpleText.Operator))
      return new ShowSimpleText(operands);
    else if(operator.equals(ShowTextToNextLine.SimpleOperator)
      || operator.equals(ShowTextToNextLine.SpaceOperator))
      return new ShowTextToNextLine(operator, operands);
    else if(operator.equals(ShowAdjustedText.Operator))
      return new ShowAdjustedText(operands);
    else if(operator.equals(TranslateTextRelative.SimpleOperator)
      || operator.equals(TranslateTextRelative.LeadOperator))
      return new TranslateTextRelative(operator, operands);
    else if(operator.equals(SetTextMatrix.Operator))
      return new SetTextMatrix(operands);
    else if(operator.equals(ModifyCTM.Operator))
      return new ModifyCTM(operands);
    else if(operator.equals(PaintXObject.Operator))
      return new PaintXObject(operands);
    else if(operator.equals(PaintShading.Operator))
      return new PaintShading(operands);
    else if(operator.equals(SetCharSpace.Operator))
      return new SetCharSpace(operands);
    else if(operator.equals(SetLineCap.Operator))
      return new SetLineCap(operands);
    else if(operator.equals(SetLineDash.Operator))
      return new SetLineDash(operands);
    else if(operator.equals(SetLineJoin.Operator))
      return new SetLineJoin(operands);
    else if(operator.equals(SetLineWidth.Operator))
      return new SetLineWidth(operands);
    else if(operator.equals(SetMiterLimit.Operator))
      return new SetMiterLimit(operands);
    else if(operator.equals(SetTextLead.Operator))
      return new SetTextLead(operands);
    else if(operator.equals(SetTextRise.Operator))
      return new SetTextRise(operands);
    else if(operator.equals(SetTextScale.Operator))
      return new SetTextScale(operands);
    else if(operator.equals(SetTextRenderMode.Operator))
      return new SetTextRenderMode(operands);
    else if(operator.equals(SetWordSpace.Operator))
      return new SetWordSpace(operands);
    else if(operator.equals(DrawLine.Operator))
      return new DrawLine(operands);
    else if(operator.equals(DrawRectangle.Operator))
      return new DrawRectangle(operands);
    else if(operator.equals(DrawCurve.FinalOperator)
      || operator.equals(DrawCurve.FullOperator)
      || operator.equals(DrawCurve.InitialOperator))
      return new DrawCurve(operator, operands);
    else if(operator.equals(EndInlineImage.Operator))
      return EndInlineImage.Value;
    else if(operator.equals(BeginText.Operator))
      return BeginText.Value;
    else if(operator.equals(EndText.Operator))
      return EndText.Value;
    else if(operator.equals(BeginMarkedContent.SimpleOperator)
      || operator.equals(BeginMarkedContent.PropertyListOperator))
      return new BeginMarkedContent(operator, operands);
    else if(operator.equals(EndMarkedContent.Operator))
      return EndMarkedContent.Value;
    else if(operator.equals(MarkedContentPoint.SimpleOperator)
      || operator.equals(MarkedContentPoint.PropertyListOperator))
      return new MarkedContentPoint(operator, operands);
    else if(operator.equals(BeginInlineImage.Operator))
      return BeginInlineImage.Value;
    else if(operator.equals(EndInlineImage.Operator))
      return EndInlineImage.Value;
    else if(operator.equals(ApplyExtGState.Operator))
      return new ApplyExtGState(operands);
    else // No explicit operation implementation available.
      return new GenericOperation(operator, operands);
  }
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <fields>
  protected String operator;
  protected List<PdfDirectObject> operands;
  // </fields>

  // <constructors>
  protected Operation(
    String operator
    )
  {this.operator = operator;}

  protected Operation(
    String operator,
    PdfDirectObject operand
    )
  {
    this.operator = operator;
    this.operands = new ArrayList<PdfDirectObject>();
    this.operands.add(operand);
  }

  protected Operation(
    String operator,
    PdfDirectObject... operands
    )
  {
    this.operator = operator;
    this.operands = new ArrayList<PdfDirectObject>();
    for(PdfDirectObject operand : operands)
    {this.operands.add(operand);}
  }

  protected Operation(
    String operator,
    List<PdfDirectObject> operands
    )
  {
    this.operator = operator;
    this.operands = operands;
  }
  // </constructors>

  // <interface>
  // <public>
  public String getOperator(
    )
  {return operator;}

  public List<PdfDirectObject> getOperands(
    )
  {return operands;}

  @Override
  public String toString(
    )
  {
    return "{"
      + operator + " "
      + (operands == null ? "" : operands.toString())
      + "}";
  }

  @Override
  public void writeTo(
    IOutputStream stream,
    Document context
    )
  {
    if(operands != null)
    {
      File fileContext = context.getFile();
      for(PdfDirectObject operand : operands)
      {operand.writeTo(stream, fileContext); stream.write(Chunk.Space);}
    }
    stream.write(operator); stream.write(Chunk.LineFeed);
  }
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}