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
  Windows ANSI encoding (Windows Code Page 1252) [PDF:1.6:D].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.8
  @version 0.1.0
*/
final class WinAnsiEncoding
  extends Encoding
{
  public WinAnsiEncoding(
    )
  {
    put(0101,"A");
    put(0306,"AE");
    put(0301,"Aacute");
    put(0302,"Acircumflex");
    put(0304,"Adieresis");
    put(0300,"Agrave");
    put(0305,"Aring");
    put(0303,"Atilde");
    put(0102,"B");
    put(0103,"C");
    put(0307,"Ccedilla");
    put(0104,"D");
    put(0105,"E");
    put(0311,"Eacute");
    put(0312,"Ecircumflex");
    put(0313,"Edieresis");
    put(0310,"Egrave");
    put(0320,"Eth");
    put(0200,"Euro");
    put(0106,"F");
    put(0107,"G");
    put(0110,"H");
    put(0111,"I");
    put(0315,"Iacute");
    put(0316,"Icircumflex");
    put(0317,"Idieresis");
    put(0314,"Igrave");
    put(0112,"J");
    put(0113,"K");
    put(0114,"L");
    put(0115,"M");
    put(0116,"N");
    put(0321,"Ntilde");
    put(0117,"O");
    put(0214,"OE");
    put(0323,"Oacute");
    put(0324,"Ocircumflex");
    put(0326,"Odieresis");
    put(0322,"Ograve");
    put(0330,"Oslash");
    put(0325,"Otilde");
    put(0120,"P");
    put(0121,"Q");
    put(0122,"R");
    put(0123,"S");
    put(0212,"Scaron");
    put(0124,"T");
    put(0336,"Thorn");
    put(0125,"U");
    put(0332,"Uacute");
    put(0333,"Ucircumflex");
    put(0334,"Udieresis");
    put(0331,"Ugrave");
    put(0126,"V");
    put(0127,"W");
    put(0130,"X");
    put(0131,"Y");
    put(0335,"Yacute");
    put(0237,"Ydieresis");
    put(0132,"Z");
    put(0216,"Zcaron");
    put(0141,"a");
    put(0341,"aacute");
    put(0342,"acircumflex");
    put(0264,"acute");
    put(0344,"adieresis");
    put(0346,"ae");
    put(0340,"agrave");
    put(046,"ampersand");
    put(0345,"aring");
    put(0136,"asciicircum");
    put(0176,"asciitilde");
    put(052,"asterisk");
    put(0100,"at");
    put(0343,"atilde");
    put(0142,"b");
    put(0134,"backslash");
    put(0174,"bar");
    put(0173,"braceleft");
    put(0175,"braceright");
    put(0133,"bracketleft");
    put(0135,"bracketright");
    put(0246,"brokenbar");
    put(0225,"bullet");
    put(0143,"c");
    put(0347,"ccedilla");
    put(0270,"cedilla");
    put(0242,"cent");
    put(0210,"circumflex");
    put(072,"colon");
    put(054,"comma");
    put(0251,"copyright");
    put(0244,"currency");
    put(0144,"d");
    put(0206,"dagger");
    put(0207,"daggerdbl");
    put(0260,"degree");
    put(0250,"dieresis");
    put(0367,"divide");
    put(044,"dollar");
    put(0145,"e");
    put(0351,"eacute");
    put(0352,"ecircumflex");
    put(0353,"edieresis");
    put(0350,"egrave");
    put(070,"eight");
    put(0205,"ellipsis");
    put(0227,"emdash");
    put(0226,"endash");
    put(075,"equal");
    put(0360,"eth");
    put(041,"exclam");
    put(0241,"exclamdown");
    put(0146,"f");
    put(065,"five");
    put(0203,"florin");
    put(064,"four");
    put(0147,"g");
    put(0337,"germandbls");
    put(0140,"grave");
    put(076,"greater");
    put(0253,"guillemotleft");
    put(0273,"guillemotright");
    put(0213,"guilsinglleft");
    put(0233,"guilsinglright");
    put(0150,"h");
    put(055,"hyphen");
    put(0151,"i");
    put(0355,"iacute");
    put(0356,"icircumflex");
    put(0357,"idieresis");
    put(0354,"igrave");
    put(0152,"j");
    put(0153,"k");
    put(0154,"l");
    put(074,"less");
    put(0254,"logicalnot");
    put(0155,"m");
    put(0257,"macron");
    put(0265,"mu");
    put(0327,"multiply");
    put(0156,"n");
    put(071,"nine");
    put(0361,"ntilde");
    put(043,"numbersign");
    put(0157,"o");
    put(0363,"oacute");
    put(0364,"ocircumflex");
    put(0366,"odieresis");
    put(0234,"oe");
    put(0362,"ograve");
    put(061,"one");
    put(0275,"onehalf");
    put(0274,"onequarter");
    put(0271,"onesuperior");
    put(0252,"ordfeminine");
    put(0272,"ordmasculine");
    put(0370,"oslash");
    put(0365,"otilde");
    put(0160,"p");
    put(0266,"paragraph");
    put(050,"parenleft");
    put(051,"parenright");
    put(045,"percent");
    put(056,"period");
    put(0267,"periodcentered");
    put(0211,"perthousand");
    put(053,"plus");
    put(0261,"plusminus");
    put(0161,"q");
    put(077,"question");
    put(0277,"questiondown");
    put(042,"quotedbl");
    put(0204,"quotedblbase");
    put(0223,"quotedblleft");
    put(0224,"quotedblright");
    put(0221,"quoteleft");
    put(0222,"quoteright");
    put(0202,"quotesinglbase");
    put(047,"quotesingle");
    put(0162,"r");
    put(0256,"registered");
    put(0163,"s");
    put(0232,"scaron");
    put(0247,"section");
    put(073,"semicolon");
    put(067,"seven");
    put(066,"six");
    put(057,"slash");
    put(040,"space");
    put(0243,"sterling");
    put(0164,"t");
    put(0376,"thorn");
    put(063,"three");
    put(0276,"threequarters");
    put(0263,"threesuperior");
    put(0230,"tilde");
    put(0231,"trademark");
    put(062,"two");
    put(0262,"twosuperior");
    put(0165,"u");
    put(0372,"uacute");
    put(0373,"ucircumflex");
    put(0374,"udieresis");
    put(0371,"ugrave");
    put(0137,"underscore");
    put(0166,"v");
    put(0167,"w");
    put(0170,"x");
    put(0171,"y");
    put(0375,"yacute");
    put(0377,"ydieresis");
    put(0245,"yen");
    put(0172,"z");
    put(0236,"zcaron");
    put(060,"zero");
  }
}