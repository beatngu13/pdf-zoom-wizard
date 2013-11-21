/*
  Copyright 2009-2010 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.contents.fonts;

/**
  Adobe standard Latin-text encoding [PDF:1.6:D].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.8
  @version 0.1.0
*/
final class StandardEncoding
  extends Encoding
{
  public StandardEncoding(
    )
  {
    put(0101,"A");
    put(0341,"AE");
    put(0102,"B");
    put(0103,"C");
    put(0104,"D");
    put(0105,"E");
    put(0106,"F");
    put(0107,"G");
    put(0110,"H");
    put(0111,"I");
    put(0112,"J");
    put(0113,"K");
    put(0114,"L");
    put(0350,"Lslash");
    put(0115,"M");
    put(0116,"N");
    put(0117,"O");
    put(0352,"OE");
    put(0351,"Oslash");
    put(0120,"P");
    put(0121,"Q");
    put(0122,"R");
    put(0123,"S");
    put(0124,"T");
    put(0125,"U");
    put(0126,"V");
    put(0127,"W");
    put(0130,"X");
    put(0131,"Y");
    put(0132,"Z");
    put(0141,"a");
    put(0302,"acute");
    put(0361,"ae");
    put(046,"ampersand");
    put(0136,"asciicircum");
    put(0176,"asciitilde");
    put(052,"asterisk");
    put(0100,"at");
    put(0142,"b");
    put(0134,"backslash");
    put(0174,"bar");
    put(0173,"braceleft");
    put(0175,"braceright");
    put(0133,"bracketleft");
    put(0135,"bracketright");
    put(0306,"breve");
    put(0267,"bullet");
    put(0143,"c");
    put(0317,"caron");
    put(0313,"cedilla");
    put(0242,"cent");
    put(0303,"circumflex");
    put(072,"colon");
    put(054,"comma");
    put(0250,"currency");
    put(0144,"d");
    put(0262,"dagger");
    put(0263,"daggerdbl");
    put(0310,"dieresis");
    put(044,"dollar");
    put(0307,"dotaccent");
    put(0365,"dotlessi");
    put(0145,"e");
    put(070,"eight");
    put(0274,"ellipsis");
    put(0320,"emdash");
    put(0261,"endash");
    put(075,"equal");
    put(041,"exclam");
    put(0241,"exclamdown");
    put(0146,"f");
    put(0256,"fi");
    put(065,"five");
    put(0257,"fl");
    put(0246,"florin");
    put(064,"four");
    put(0244,"fraction");
    put(0147,"g");
    put(0373,"germandbls");
    put(0301,"grave");
    put(076,"greater");
    put(0253,"guillemotleft");
    put(0273,"guillemotright");
    put(0254,"guilsinglleft");
    put(0255,"guilsinglright");
    put(0150,"h");
    put(0315,"hungarumlaut");
    put(055,"hyphen");
    put(0151,"i");
    put(0152,"j");
    put(0153,"k");
    put(0154,"l");
    put(074,"less");
    put(0370,"lslash");
    put(0155,"m");
    put(0305,"macron");
    put(0156,"n");
    put(071,"nine");
    put(043,"numbersign");
    put(0157,"o");
    put(0372,"oe");
    put(0316,"ogonek");
    put(061,"one");
    put(0343,"ordfeminine");
    put(0353,"ordmasculine");
    put(0371,"oslash");
    put(0160,"p");
    put(0266,"paragraph");
    put(050,"parenleft");
    put(051,"parenright");
    put(045,"percent");
    put(056,"period");
    put(0264,"periodcentered");
    put(0275,"perthousand");
    put(053,"plus");
    put(0161,"q");
    put(077,"question");
    put(0277,"questiondown");
    put(042,"quotedbl");
    put(0271,"quotedblbase");
    put(0252,"quotedblleft");
    put(0272,"quotedblright");
    put(0140,"quoteleft");
    put(047,"quoteright");
    put(0270,"quotesinglbase");
    put(0251,"quotesingle");
    put(0162,"r");
    put(0312,"ring");
    put(0163,"s");
    put(0247,"section");
    put(073,"semicolon");
    put(067,"seven");
    put(066,"six");
    put(057,"slash");
    put(040,"space");
    put(0243,"sterling");
    put(0164,"t");
    put(063,"three");
    put(0304,"tilde");
    put(062,"two");
    put(0165,"u");
    put(0137,"underscore");
    put(0166,"v");
    put(0167,"w");
    put(0170,"x");
    put(0171,"y");
    put(0245,"yen");
    put(0172,"z");
    put(060,"zero");
  }
}