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

package org.pdfclown.objects;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pdfclown.bytes.IOutputStream;
import org.pdfclown.files.File;
import org.pdfclown.tokens.Keyword;

/**
  PDF name object [PDF:1.6:3.2.4].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2, 12/28/12
*/
public final class PdfName
  extends PdfSimpleObject<String>
{
  /*
    NOTE: As name objects are simple symbols uniquely defined by sequences of characters,
    the bytes making up the name are never treated as text, always keeping them escaped.
  */
  // <class>
  // <static>
  // <fields>
  /*
    NOTE: Name lexical conventions prescribe that the following reserved characters
    are to be escaped when placed inside names' character sequences:
      - delimiters;
      - whitespaces;
      - '#' (number sign character).
  */
  private static final Pattern EscapedPattern = java.util.regex.Pattern.compile("#([\\da-fA-F]{2})");
  private static final Pattern UnescapedPattern = java.util.regex.Pattern.compile("[\\s\\(\\)<>\\[\\]{}/%#]");

  public static final PdfName A = new PdfName("A");
  public static final PdfName a = new PdfName("a");
  public static final PdfName A85 = new PdfName("A85");
  public static final PdfName AA = new PdfName("AA");
  public static final PdfName AC = new PdfName("AC");
  public static final PdfName Action = new PdfName("Action");
  public static final PdfName AcroForm = new PdfName("AcroForm");
  public static final PdfName AHx = new PdfName("AHx");
  public static final PdfName AIS = new PdfName("AIS");
  public static final PdfName All = new PdfName("All");
  public static final PdfName AllOff = new PdfName("AllOff");
  public static final PdfName AllOn = new PdfName("AllOn");
  public static final PdfName AllPages = new PdfName("AllPages");
  public static final PdfName AN = new PdfName("AN");
  public static final PdfName Annot = new PdfName("Annot");
  public static final PdfName Annotation = new PdfName("Annotation");
  public static final PdfName Annots = new PdfName("Annots");
  public static final PdfName AnyOff = new PdfName("AnyOff");
  public static final PdfName AnyOn = new PdfName("AnyOn");
  public static final PdfName AP = new PdfName("AP");
  public static final PdfName Approved = new PdfName("Approved");
  public static final PdfName ArtBox = new PdfName("ArtBox");
  public static final PdfName AS = new PdfName("AS");
  public static final PdfName Ascent = new PdfName("Ascent");
  public static final PdfName ASCII85Decode = new PdfName("ASCII85Decode");
  public static final PdfName ASCIIHexDecode = new PdfName("ASCIIHexDecode");
  public static final PdfName AsIs = new PdfName("AsIs");
  public static final PdfName Author = new PdfName("Author");
  public static final PdfName B = new PdfName("B");
  public static final PdfName BaseEncoding = new PdfName("BaseEncoding");
  public static final PdfName BaseFont = new PdfName("BaseFont");
  public static final PdfName BaseState = new PdfName("BaseState");
  public static final PdfName BBox = new PdfName("BBox");
  public static final PdfName BC = new PdfName("BC");
  public static final PdfName BE = new PdfName("BE");
  public static final PdfName Bead = new PdfName("Bead");
  public static final PdfName BG = new PdfName("BG");
  public static final PdfName BitsPerComponent = new PdfName("BitsPerComponent");
  public static final PdfName BitsPerSample = new PdfName("BitsPerSample");
  public static final PdfName Bl = new PdfName("Bl");
  public static final PdfName BlackPoint = new PdfName("BlackPoint");
  public static final PdfName BleedBox = new PdfName("BleedBox");
  public static final PdfName Blinds = new PdfName("Blinds");
  public static final PdfName BM = new PdfName("BM");
  public static final PdfName Border = new PdfName("Border");
  public static final PdfName Bounds = new PdfName("Bounds");
  public static final PdfName Box = new PdfName("Box");
  public static final PdfName BPC = new PdfName("BPC");
  public static final PdfName BS = new PdfName("BS");
  public static final PdfName Btn = new PdfName("Btn");
  public static final PdfName BU = new PdfName("BU");
  public static final PdfName Butt = new PdfName("Butt");
  public static final PdfName C = new PdfName("C");
  public static final PdfName C0 = new PdfName("C0");
  public static final PdfName C1 = new PdfName("C1");
  public static final PdfName CA = new PdfName("CA");
  public static final PdfName ca = new PdfName("ca");
  public static final PdfName CalGray = new PdfName("CalGray");
  public static final PdfName CalRGB = new PdfName("CalRGB");
  public static final PdfName Cap = new PdfName("Cap");
  public static final PdfName CapHeight = new PdfName("CapHeight");
  public static final PdfName Caret = new PdfName("Caret");
  public static final PdfName Catalog = new PdfName("Catalog");
  public static final PdfName Category = new PdfName("Category");
  public static final PdfName CCF = new PdfName("CCF");
  public static final PdfName CCITTFaxDecode = new PdfName("CCITTFaxDecode");
  public static final PdfName CenterWindow = new PdfName("CenterWindow");
  public static final PdfName Ch = new PdfName("Ch");
  public static final PdfName CIDFontType0 = new PdfName("CIDFontType0");
  public static final PdfName CIDFontType2 = new PdfName("CIDFontType2");
  public static final PdfName CIDSystemInfo = new PdfName("CIDSystemInfo");
  public static final PdfName CIDToGIDMap = new PdfName("CIDToGIDMap");
  public static final PdfName Circle = new PdfName("Circle");
  public static final PdfName CL = new PdfName("CL");
  public static final PdfName ClosedArrow = new PdfName("ClosedArrow");
  public static final PdfName CMap = new PdfName("CMap");
  public static final PdfName CMapName = new PdfName("CMapName");
  public static final PdfName Color = new PdfName("Color");
  public static final PdfName ColorBurn = new PdfName("ColorBurn");
  public static final PdfName ColorDodge = new PdfName("ColorDodge");
  public static final PdfName Colors = new PdfName("Colors");
  public static final PdfName ColorSpace = new PdfName("ColorSpace");
  public static final PdfName Columns = new PdfName("Columns");
  public static final PdfName Comment = new PdfName("Comment");
  public static final PdfName Confidential = new PdfName("Confidential");
  public static final PdfName Configs = new PdfName("Configs");
  public static final PdfName Contents = new PdfName("Contents");
  public static final PdfName Count = new PdfName("Count");
  public static final PdfName Cover = new PdfName("Cover");
  public static final PdfName CreationDate = new PdfName("CreationDate");
  public static final PdfName Creator = new PdfName("Creator");
  public static final PdfName CreatorInfo = new PdfName("CreatorInfo");
  public static final PdfName CropBox = new PdfName("CropBox");
  public static final PdfName Crypt = new PdfName("Crypt");
  public static final PdfName CS = new PdfName("CS");
  public static final PdfName CT = new PdfName("CT");
  public static final PdfName D = new PdfName("D");
  public static final PdfName DA = new PdfName("DA");
  public static final PdfName Darken = new PdfName("Darken");
  public static final PdfName DC = new PdfName("DC");
  public static final PdfName DCT = new PdfName("DCT");
  public static final PdfName DCTDecode = new PdfName("DCTDecode");
  public static final PdfName Decode = new PdfName("Decode");
  public static final PdfName DecodeParms = new PdfName("DecodeParms");
  public static final PdfName Departmental = new PdfName("Departmental");
  public static final PdfName Desc = new PdfName("Desc");
  public static final PdfName DescendantFonts = new PdfName("DescendantFonts");
  public static final PdfName Descent = new PdfName("Descent");
  public static final PdfName Dest = new PdfName("Dest");
  public static final PdfName Dests = new PdfName("Dests");
  public static final PdfName DeviceCMYK = new PdfName("DeviceCMYK");
  public static final PdfName DeviceGray = new PdfName("DeviceGray");
  public static final PdfName DeviceRGB = new PdfName("DeviceRGB");
  public static final PdfName DeviceN = new PdfName("DeviceN");
  public static final PdfName Di = new PdfName("Di");
  public static final PdfName Diamond = new PdfName("Diamond");
  public static final PdfName Difference = new PdfName("Difference");
  public static final PdfName Differences = new PdfName("Differences");
  public static final PdfName Direction = new PdfName("Direction");
  public static final PdfName DisplayDocTitle = new PdfName("DisplayDocTitle");
  public static final PdfName Dissolve = new PdfName("Dissolve");
  public static final PdfName Dm = new PdfName("Dm");
  public static final PdfName Domain = new PdfName("Domain");
  public static final PdfName DOS = new PdfName("DOS");
  public static final PdfName DP = new PdfName("DP");
  public static final PdfName DR = new PdfName("DR");
  public static final PdfName Draft = new PdfName("Draft");
  public static final PdfName DS = new PdfName("DS");
  public static final PdfName Dur = new PdfName("Dur");
  public static final PdfName DV = new PdfName("DV");
  public static final PdfName E = new PdfName("E");
  public static final PdfName EF = new PdfName("EF");
  public static final PdfName EmbeddedFile = new PdfName("EmbeddedFile");
  public static final PdfName EmbeddedFiles = new PdfName("EmbeddedFiles");
  public static final PdfName Encode = new PdfName("Encode");
  public static final PdfName Encoding = new PdfName("Encoding");
  public static final PdfName Encrypt = new PdfName("Encrypt");
  public static final PdfName Event = new PdfName("Event");
  public static final PdfName Exclusion = new PdfName("Exclusion");
  public static final PdfName Experimental = new PdfName("Experimental");
  public static final PdfName Expired = new PdfName("Expired");
  public static final PdfName Export = new PdfName("Export");
  public static final PdfName ExportState = new PdfName("ExportState");
  public static final PdfName Extends = new PdfName("Extends");
  public static final PdfName ExtGState = new PdfName("ExtGState");
  public static final PdfName F = new PdfName("F");
  public static final PdfName Fade = new PdfName("Fade");
  public static final PdfName FB = new PdfName("FB");
  public static final PdfName FDecodeParms = new PdfName("FDecodeParms");
  public static final PdfName Ff = new PdfName("Ff");
  public static final PdfName FFilter = new PdfName("FFilter");
  public static final PdfName Fields = new PdfName("Fields");
  public static final PdfName FileAttachment = new PdfName("FileAttachment");
  public static final PdfName Filespec = new PdfName("Filespec");
  public static final PdfName Filter = new PdfName("Filter");
  public static final PdfName Final = new PdfName("Final");
  public static final PdfName First = new PdfName("First");
  public static final PdfName FirstChar = new PdfName("FirstChar");
  public static final PdfName FirstPage = new PdfName("FirstPage");
  public static final PdfName Fit = new PdfName("Fit");
  public static final PdfName FitB = new PdfName("FitB");
  public static final PdfName FitBH = new PdfName("FitBH");
  public static final PdfName FitBV = new PdfName("FitBV");
  public static final PdfName FitH = new PdfName("FitH");
  public static final PdfName FitR = new PdfName("FitR");
  public static final PdfName FitV = new PdfName("FitV");
  public static final PdfName FitWindow = new PdfName("FitWindow");
  public static final PdfName Fl = new PdfName("Fl");
  public static final PdfName Flags = new PdfName("Flags");
  public static final PdfName FlateDecode = new PdfName("FlateDecode");
  public static final PdfName Fly = new PdfName("Fly");
  public static final PdfName Fo = new PdfName("Fo");
  public static final PdfName Font = new PdfName("Font");
  public static final PdfName FontBBox = new PdfName("FontBBox");
  public static final PdfName FontDescriptor = new PdfName("FontDescriptor");
  public static final PdfName FontFile = new PdfName("FontFile");
  public static final PdfName FontFile2 = new PdfName("FontFile2");
  public static final PdfName FontFile3 = new PdfName("FontFile3");
  public static final PdfName FontName = new PdfName("FontName");
  public static final PdfName ForComment = new PdfName("ForComment");
  public static final PdfName Form = new PdfName("Form");
  public static final PdfName ForPublicRelease = new PdfName("ForPublicRelease");
  public static final PdfName FreeText = new PdfName("FreeText");
  public static final PdfName FS = new PdfName("FS");
  public static final PdfName FT = new PdfName("FT");
  public static final PdfName FullScreen = new PdfName("FullScreen");
  public static final PdfName Functions = new PdfName("Functions");
  public static final PdfName FunctionType = new PdfName("FunctionType");
  public static final PdfName FWParams = new PdfName("FWParams");
  public static final PdfName Gamma = new PdfName("Gamma");
  public static final PdfName Glitter = new PdfName("Glitter");
  public static final PdfName GoTo = new PdfName("GoTo");
  public static final PdfName GoTo3DView = new PdfName("GoTo3DView");
  public static final PdfName GoToAction = new PdfName("GoToAction");
  public static final PdfName GoToE = new PdfName("GoToE");
  public static final PdfName GoToR = new PdfName("GoToR");
  public static final PdfName Graph = new PdfName("Graph");
  public static final PdfName H = new PdfName("H");
  public static final PdfName HardLight = new PdfName("HardLight");
  public static final PdfName Height = new PdfName("Height");
  public static final PdfName Help = new PdfName("Help");
  public static final PdfName HI = new PdfName("HI");
  public static final PdfName Hide = new PdfName("Hide");
  public static final PdfName HideMenubar = new PdfName("HideMenubar");
  public static final PdfName HideToolbar = new PdfName("HideToolbar");
  public static final PdfName HideWindowUI = new PdfName("HideWindowUI");
  public static final PdfName Highlight = new PdfName("Highlight");
  public static final PdfName Hue = new PdfName("Hue");
  public static final PdfName I = new PdfName("I");
  public static final PdfName IC = new PdfName("IC");
  public static final PdfName ICCBased = new PdfName("ICCBased");
  public static final PdfName ID = new PdfName("ID");
  public static final PdfName Identity = new PdfName("Identity");
  public static final PdfName IdentityH = new PdfName("Identity-H");
  public static final PdfName IdentityV = new PdfName("Identity-V");
  public static final PdfName IF = new PdfName("IF");
  public static final PdfName Image = new PdfName("Image");
  public static final PdfName ImportData = new PdfName("ImportData");
  public static final PdfName Index = new PdfName("Index");
  public static final PdfName Indexed = new PdfName("Indexed");
  public static final PdfName Info = new PdfName("Info");
  public static final PdfName Ink = new PdfName("Ink");
  public static final PdfName InkList = new PdfName("InkList");
  public static final PdfName Insert = new PdfName("Insert");
  public static final PdfName ItalicAngle = new PdfName("ItalicAngle");
  public static final PdfName IX = new PdfName("IX");
  public static final PdfName JavaScript = new PdfName("JavaScript");
  public static final PdfName JBIG2Decode = new PdfName("JBIG2Decode");
  public static final PdfName JPXDecode = new PdfName("JPXDecode");
  public static final PdfName JS = new PdfName("JS");
  public static final PdfName K = new PdfName("K");
  public static final PdfName Key = new PdfName("Key");
  public static final PdfName Keywords = new PdfName("Keywords");
  public static final PdfName Kids = new PdfName("Kids");
  public static final PdfName L = new PdfName("L");
  public static final PdfName L2R = new PdfName("L2R");
  public static final PdfName Lab = new PdfName("Lab");
  public static final PdfName Lang = new PdfName("Lang");
  public static final PdfName Language = new PdfName("Language");
  public static final PdfName Last = new PdfName("Last");
  public static final PdfName LastChar = new PdfName("LastChar");
  public static final PdfName LastPage = new PdfName("LastPage");
  public static final PdfName Launch = new PdfName("Launch");
  public static final PdfName LC = new PdfName("LC");
  public static final PdfName LE = new PdfName("LE");
  public static final PdfName Leading = new PdfName("Leading");
  public static final PdfName Length = new PdfName("Length");
  public static final PdfName LI = new PdfName("LI");
  public static final PdfName Lighten = new PdfName("Lighten");
  public static final PdfName Limits = new PdfName("Limits");
  public static final PdfName Line = new PdfName("Line");
  public static final PdfName Link = new PdfName("Link");
  public static final PdfName ListMode = new PdfName("ListMode");
  public static final PdfName LJ = new PdfName("LJ");
  public static final PdfName LL = new PdfName("LL");
  public static final PdfName LLE = new PdfName("LLE");
  public static final PdfName Locked = new PdfName("Locked");
  public static final PdfName Luminosity = new PdfName("Luminosity");
  public static final PdfName LW = new PdfName("LW");
  public static final PdfName LZW = new PdfName("LZW");
  public static final PdfName LZWDecode = new PdfName("LZWDecode");
  public static final PdfName M = new PdfName("M");
  public static final PdfName Mac = new PdfName("Mac");
  public static final PdfName MacRomanEncoding = new PdfName("MacRomanEncoding");
  public static final PdfName Matrix = new PdfName("Matrix");
  public static final PdfName max = new PdfName("max");
  public static final PdfName MaxLen = new PdfName("MaxLen");
  public static final PdfName MCD = new PdfName("MCD");
  public static final PdfName MCS = new PdfName("MCS");
  public static final PdfName MediaBox = new PdfName("MediaBox");
  public static final PdfName MediaClip = new PdfName("MediaClip");
  public static final PdfName MediaDuration = new PdfName("MediaDuration");
  public static final PdfName MediaOffset = new PdfName("MediaOffset");
  public static final PdfName MediaPlayerInfo = new PdfName("MediaPlayerInfo");
  public static final PdfName MediaPlayParams = new PdfName("MediaPlayParams");
  public static final PdfName MediaScreenParams = new PdfName("MediaScreenParams");
  public static final PdfName Metadata = new PdfName("Metadata");
  public static final PdfName MH = new PdfName("MH");
  public static final PdfName Mic = new PdfName("Mic");
  public static final PdfName min = new PdfName("min");
  public static final PdfName MissingWidth = new PdfName("MissingWidth");
  public static final PdfName MK = new PdfName("MK");
  public static final PdfName ML = new PdfName("ML");
  public static final PdfName MMType1 = new PdfName("MMType1");
  public static final PdfName ModDate = new PdfName("ModDate");
  public static final PdfName Movie = new PdfName("Movie");
  public static final PdfName MR = new PdfName("MR");
  public static final PdfName MU = new PdfName("MU");
  public static final PdfName Multiply = new PdfName("Multiply");
  public static final PdfName N = new PdfName("N");
  public static final PdfName Name = new PdfName("Name");
  public static final PdfName Named = new PdfName("Named");
  public static final PdfName Names = new PdfName("Names");
  public static final PdfName NewParagraph = new PdfName("NewParagraph");
  public static final PdfName NewWindow = new PdfName("NewWindow");
  public static final PdfName Next = new PdfName("Next");
  public static final PdfName NextPage = new PdfName("NextPage");
  public static final PdfName NM = new PdfName("NM");
  public static final PdfName None = new PdfName("None");
  public static final PdfName Normal = new PdfName("Normal");
  public static final PdfName NotApproved = new PdfName("NotApproved");
  public static final PdfName Note = new PdfName("Note");
  public static final PdfName NotForPublicRelease = new PdfName("NotForPublicRelease");
  public static final PdfName NU = new PdfName("NU");
  public static final PdfName Nums = new PdfName("Nums");
  public static final PdfName O = new PdfName("O");
  public static final PdfName ObjStm = new PdfName("ObjStm");
  public static final PdfName OC = new PdfName("OC");
  public static final PdfName OCG = new PdfName("OCG");
  public static final PdfName OCGs = new PdfName("OCGs");
  public static final PdfName OCMD = new PdfName("OCMD");
  public static final PdfName OCProperties = new PdfName("OCProperties");
  public static final PdfName OFF = new PdfName("OFF");
  public static final PdfName Off = new PdfName("Off");
  public static final PdfName ON = new PdfName("ON");
  public static final PdfName OneColumn = new PdfName("OneColumn");
  public static final PdfName OP = new PdfName("OP");
  public static final PdfName Open = new PdfName("Open");
  public static final PdfName OpenAction = new PdfName("OpenAction");
  public static final PdfName OpenArrow = new PdfName("OpenArrow");
  public static final PdfName OpenType = new PdfName("OpenType");
  public static final PdfName Opt = new PdfName("Opt");
  public static final PdfName Order = new PdfName("Order");
  public static final PdfName Ordering = new PdfName("Ordering");
  public static final PdfName OS = new PdfName("OS");
  public static final PdfName Outlines = new PdfName("Outlines");
  public static final PdfName Overlay = new PdfName("Overlay");
  public static final PdfName P = new PdfName("P");
  public static final PdfName Page = new PdfName("Page");
  public static final PdfName PageLabel = new PdfName("PageLabel");
  public static final PdfName PageLabels = new PdfName("PageLabels");
  public static final PdfName PageLayout = new PdfName("PageLayout");
  public static final PdfName PageMode = new PdfName("PageMode");
  public static final PdfName Pages = new PdfName("Pages");
  public static final PdfName PaintType = new PdfName("PaintType");
  public static final PdfName Paperclip = new PdfName("Paperclip");
  public static final PdfName Paragraph = new PdfName("Paragraph");
  public static final PdfName Params = new PdfName("Params");
  public static final PdfName Parent = new PdfName("Parent");
  public static final PdfName Pattern = new PdfName("Pattern");
  public static final PdfName PatternType = new PdfName("PatternType");
  public static final PdfName PC = new PdfName("PC");
  public static final PdfName PDFDocEncoding = new PdfName("PdfDocEncoding");
  public static final PdfName PI = new PdfName("PI");
  public static final PdfName PID = new PdfName("PID");
  public static final PdfName PL = new PdfName("PL");
  public static final PdfName PO = new PdfName("PO");
  public static final PdfName Polygon = new PdfName("Polygon");
  public static final PdfName PolyLine = new PdfName("PolyLine");
  public static final PdfName Popup = new PdfName("Popup");
  public static final PdfName Predictor = new PdfName("Predictor");
  public static final PdfName Prev = new PdfName("Prev");
  public static final PdfName PrevPage = new PdfName("PrevPage");
  public static final PdfName Print = new PdfName("Print");
  public static final PdfName PrintState = new PdfName("PrintState");
  public static final PdfName Producer = new PdfName("Producer");
  public static final PdfName Properties = new PdfName("Properties");
  public static final PdfName Push = new PdfName("Push");
  public static final PdfName PushPin = new PdfName("PushPin");
  public static final PdfName PV = new PdfName("PV");
  public static final PdfName Q = new PdfName("Q");
  public static final PdfName QuadPoints = new PdfName("QuadPoints");
  public static final PdfName R = new PdfName("R");
  public static final PdfName r = new PdfName("r");
  public static final PdfName R2L = new PdfName("R2L");
  public static final PdfName Range = new PdfName("Range");
  public static final PdfName RBGroups = new PdfName("RBGroups");
  public static final PdfName RC = new PdfName("RC");
  public static final PdfName RClosedArrow = new PdfName("RClosedArrow");
  public static final PdfName Rect = new PdfName("Rect");
  public static final PdfName Registry = new PdfName("Registry");
  public static final PdfName Rendition = new PdfName("Rendition");
  public static final PdfName Renditions = new PdfName("Renditions");
  public static final PdfName ResetForm = new PdfName("ResetForm");
  public static final PdfName Resources = new PdfName("Resources");
  public static final PdfName RF = new PdfName("RF");
  public static final PdfName RGB = new PdfName("RGB");
  public static final PdfName RI = new PdfName("RI");
  public static final PdfName RL = new PdfName("RL");
  public static final PdfName Root = new PdfName("Root");
  public static final PdfName ROpenArrow = new PdfName("ROpenArrow");
  public static final PdfName Rotate = new PdfName("Rotate");
  public static final PdfName RT = new PdfName("RT");
  public static final PdfName RunLengthDecode = new PdfName("RunLengthDecode");
  public static final PdfName S = new PdfName("S");
  public static final PdfName Saturation = new PdfName("Saturation");
  public static final PdfName Screen = new PdfName("Screen");
  public static final PdfName Separation = new PdfName("Separation");
  public static final PdfName SetOCGState = new PdfName("SetOCGState");
  public static final PdfName Shading = new PdfName("Shading");
  public static final PdfName Sig = new PdfName("Sig");
  public static final PdfName SinglePage = new PdfName("SinglePage");
  public static final PdfName Size = new PdfName("Size");
  public static final PdfName Slash = new PdfName("Slash");
  public static final PdfName SoftLight = new PdfName("SoftLight");
  public static final PdfName Sold = new PdfName("Sold");
  public static final PdfName Sound = new PdfName("Sound");
  public static final PdfName SP = new PdfName("SP");
  public static final PdfName Speaker = new PdfName("Speaker");
  public static final PdfName Split = new PdfName("Split");
  public static final PdfName Square = new PdfName("Square");
  public static final PdfName Squiggly = new PdfName("Squiggly");
  public static final PdfName SR = new PdfName("SR");
  public static final PdfName SS = new PdfName("SS");
  public static final PdfName St = new PdfName("St");
  public static final PdfName Stamp = new PdfName("Stamp");
  public static final PdfName StandardEncoding = new PdfName("StandardEncoding");
  public static final PdfName State = new PdfName("State");
  public static final PdfName StemV = new PdfName("StemV");
  public static final PdfName StrikeOut = new PdfName("StrikeOut");
  public static final PdfName StructParent = new PdfName("StructParent");
  public static final PdfName Subject = new PdfName("Subject");
  public static final PdfName SubmitForm = new PdfName("SubmitForm");
  public static final PdfName Subtype = new PdfName("Subtype");
  public static final PdfName Supplement = new PdfName("Supplement");
  public static final PdfName SW = new PdfName("SW");
  public static final PdfName Sy = new PdfName("Sy");
  public static final PdfName T = new PdfName("T");
  public static final PdfName Tabs = new PdfName("Tabs");
  public static final PdfName Tag = new PdfName("Tag");
  public static final PdfName Text = new PdfName("Text");
  public static final PdfName TF = new PdfName("TF");
  public static final PdfName Thread = new PdfName("Thread");
  public static final PdfName Threads = new PdfName("Threads");
  public static final PdfName TilingType = new PdfName("TilingType");
  public static final PdfName Timespan = new PdfName("Timespan");
  public static final PdfName Title = new PdfName("Title");
  public static final PdfName Toggle = new PdfName("Toggle");
  public static final PdfName TopSecret = new PdfName("TopSecret");
  public static final PdfName ToUnicode = new PdfName("ToUnicode");
  public static final PdfName TP = new PdfName("TP");
  public static final PdfName Trans = new PdfName("Trans");
  public static final PdfName TrimBox = new PdfName("TrimBox");
  public static final PdfName TrueType = new PdfName("TrueType");
  public static final PdfName TwoColumnLeft = new PdfName("TwoColumnLeft");
  public static final PdfName TwoColumnRight = new PdfName("TwoColumnRight");
  public static final PdfName TwoPageLeft = new PdfName("TwoPageLeft");
  public static final PdfName TwoPageRight = new PdfName("TwoPageRight");
  public static final PdfName Tx = new PdfName("Tx");
  public static final PdfName Type = new PdfName("Type");
  public static final PdfName Type0 = new PdfName("Type0");
  public static final PdfName Type1 = new PdfName("Type1");
  public static final PdfName Type1C = new PdfName("Type1C");
  public static final PdfName Type3 = new PdfName("Type3");
  public static final PdfName U = new PdfName("U");
  public static final PdfName UC = new PdfName("UC");
  public static final PdfName Unchanged = new PdfName("Unchanged");
  public static final PdfName Uncover = new PdfName("Uncover");
  public static final PdfName Underline = new PdfName("Underline");
  public static final PdfName Unix = new PdfName("Unix");
  public static final PdfName URI = new PdfName("URI");
  public static final PdfName URL = new PdfName("URL");
  public static final PdfName Usage = new PdfName("Usage");
  public static final PdfName UseAttachments = new PdfName("UseAttachments");
  public static final PdfName UseNone = new PdfName("UseNone");
  public static final PdfName UseOC = new PdfName("UseOC");
  public static final PdfName UseOutlines = new PdfName("UseOutlines");
  public static final PdfName UseThumbs = new PdfName("UseThumbs");
  public static final PdfName V = new PdfName("V");
  public static final PdfName Version = new PdfName("Version");
  public static final PdfName Vertices = new PdfName("Vertices");
  public static final PdfName View = new PdfName("View");
  public static final PdfName ViewerPreferences = new PdfName("ViewerPreferences");
  public static final PdfName ViewState = new PdfName("ViewState");
  public static final PdfName VisiblePages = new PdfName("VisiblePages");
  public static final PdfName W = new PdfName("W");
  public static final PdfName WhitePoint = new PdfName("WhitePoint");
  public static final PdfName Widget = new PdfName("Widget");
  public static final PdfName Width = new PdfName("Width");
  public static final PdfName Widths = new PdfName("Widths");
  public static final PdfName Win = new PdfName("Win");
  public static final PdfName WinAnsiEncoding = new PdfName("WinAnsiEncoding");
  public static final PdfName Wipe = new PdfName("Wipe");
  public static final PdfName WP = new PdfName("WP");
  public static final PdfName WS = new PdfName("WS");
  public static final PdfName X = new PdfName("X");
  public static final PdfName XML = new PdfName("XML");
  public static final PdfName XObject = new PdfName("XObject");
  public static final PdfName XRef = new PdfName("XRef");
  public static final PdfName XStep = new PdfName("XStep");
  public static final PdfName XYZ = new PdfName("XYZ");
  public static final PdfName Yes = new PdfName("Yes");
  public static final PdfName YStep = new PdfName("YStep");
  public static final PdfName Z = new PdfName("Z");
  public static final PdfName Zoom = new PdfName("Zoom");

  private static final byte[] NamePrefixChunk = org.pdfclown.tokens.Encoding.Pdf.encode(Keyword.NamePrefix);
  // </fields>

  // <interface>
  // <public>
  /**
    Gets the object equivalent to the given value.
  */
  public static PdfName get(
    Object value
    )
  {return value == null ? null : get(value.toString());}

  /**
    Gets the object equivalent to the given value.
  */
  public static PdfName get(
    String value
    )
  {return value == null ? null : new PdfName(value);}
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  public PdfName(
    String value
    )
  {this(value, false);}

  /**
    <span style="color:red">For internal use only.</span>
  */
  public PdfName(
    String value,
    boolean escaped
    )
  {
    /*
      NOTE: To avoid ambiguities due to the presence of '#' characters,
      it's necessary to explicitly state when a name value has already been escaped.
      This is tipically the case of names parsed from a previously-serialized PDF file.
    */
    if(escaped)
    {setRawValue(value);}
    else
    {setValue(value);}
  }
  // </constructors>

  // <interface>
  // <public>
  @Override
  public PdfObject accept(
    IVisitor visitor,
    Object data
    )
  {return visitor.visit(this, data);}

  @Override
  public int compareTo(
    PdfDirectObject obj
    )
  {
    if(!(obj instanceof PdfName))
      throw new IllegalArgumentException("Object MUST be a PdfName");

    return getRawValue().compareTo(((PdfName)obj).getRawValue());
  }

  @Override
  public String getValue(
    )
  {return (String)super.getValue();}

  @Override
  public String toString(
    )
  {
    /*
      NOTE: The textual representation of a name concerns unescaping reserved characters.
    */
    String value = getRawValue();
    StringBuilder buffer = new StringBuilder();
    int index = 0;
    Matcher escapedMatcher = EscapedPattern.matcher(value);
    while(escapedMatcher.find())
    {
      int start = escapedMatcher.start();
      if(start > index)
      {buffer.append(value.substring(index,start));}

      buffer.append(
        (char)Integer.parseInt(
          escapedMatcher.group(1),
          16
          )
        );

      index = escapedMatcher.end();
    }
    if(index < value.length())
    {buffer.append(value.substring(index));}

    return buffer.toString();
  }

  @Override
  public void writeTo(
    IOutputStream stream,
    File context
    )
  {stream.write(NamePrefixChunk); stream.write(getRawValue());}
  // </public>

  // <protected>
  @Override
  protected void setValue(
    Object value
    )
  {
    /*
      NOTE: Before being accepted, any character sequence identifying a name MUST be normalized
      escaping reserved characters.
    */
    StringBuilder buffer = new StringBuilder();
    {
      String stringValue = (String)value;
      int index = 0;
      Matcher unescapedMatcher = UnescapedPattern.matcher(stringValue);
      while(unescapedMatcher.find())
      {
        int start = unescapedMatcher.start();
        if(start > index)
        {buffer.append(stringValue.substring(index,start));}

        buffer.append(
          '#' + Integer.toHexString(
            unescapedMatcher.group(0).charAt(0)
            )
          );

        index = unescapedMatcher.end();
      }
      if(index < stringValue.length())
      {buffer.append(stringValue.substring(index));}
    }
    setRawValue(buffer.toString());
  }
  // </protected>
  // </interface>
  // </dynamic>
  // </class>
}