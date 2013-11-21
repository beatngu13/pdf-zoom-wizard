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
  Mac OS standard latin encoding [PDF:1.6:D].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.8
  @version 0.1.0
*/
final class MacRomanEncoding
  extends Encoding
{
  public MacRomanEncoding(
    )
  {
    put(0101,"A");
    put(0256,"AE");
    put(0347,"Aacute");
    put(0345,"Acircumflex");
    put(0200,"Adieresis");
    put(0313,"Agrave");
    put(0201,"Aring");
    put(0314,"Atilde");
    put(0102,"B");
    put(0103,"C");
    put(0202,"Ccedilla");
    put(0104,"D");
    put(0105,"E");
    put(0203,"Eacute");
    put(0346,"Ecircumflex");
    put(0350,"Edieresis");
    put(0351,"Egrave");
    put(0106,"F");
    put(0107,"G");
    put(0110,"H");
    put(0111,"I");
    put(0352,"Iacute");
    put(0353,"Icircumflex");
    put(0354,"Idieresis");
    put(0355,"Igrave");
    put(0112,"J");
    put(0113,"K");
    put(0114,"L");
    put(0115,"M");
    put(0116,"N");
    put(0204,"Ntilde");
    put(0117,"O");
    put(0316,"OE");
    put(0356,"Oacute");
    put(0357,"Ocircumflex");
    put(0205,"Odieresis");
    put(0361,"Ograve");
    put(0257,"Oslash");
    put(0315,"Otilde");
    put(0120,"P");
    put(0121,"Q");
    put(0122,"R");
    put(0123,"S");
    put(0124,"T");
    put(0125,"U");
    put(0362,"Uacute");
    put(0363,"Ucircumflex");
    put(0206,"Udieresis");
    put(0364,"Ugrave");
    put(0126,"V");
    put(0127,"W");
    put(0130,"X");
    put(0131,"Y");
    put(0331,"Ydieresis");
    put(0132,"Z");
    put(0141,"a");
    put(0207,"aacute");
    put(0211,"acircumflex");
    put(0253,"acute");
    put(0212,"adieresis");
    put(0276,"ae");
    put(0210,"agrave");
    put(046,"ampersand");
    put(0214,"aring");
    put(0136,"asciicircum");
    put(0176,"asciitilde");
    put(052,"asterisk");
    put(0100,"at");
    put(0213,"atilde");
    put(0142,"b");
    put(0134,"backslash");
    put(0174,"bar");
    put(0173,"braceleft");
    put(0175,"braceright");
    put(0133,"bracketleft");
    put(0135,"bracketright");
    put(0371,"breve");
    put(0245,"bullet");
    put(0143,"c");
    put(0377,"caron");
    put(0215,"ccedilla");
    put(0374,"cedilla");
    put(0242,"cent");
    put(0366,"circumflex");
    put(072,"colon");
    put(054,"comma");
    put(0251,"copyright");
    put(0333,"currency");
    put(0144,"d");
    put(0240,"dagger");
    put(0340,"daggerdbl");
    put(0241,"degree");
    put(0254,"dieresis");
    put(0326,"divide");
    put(044,"dollar");
    put(0372,"dotaccent");
    put(0365,"dotlessi");
    put(0145,"e");
    put(0216,"eacute");
    put(0220,"ecircumflex");
    put(0221,"edieresis");
    put(0217,"egrave");
    put(070,"eight");
    put(0311,"ellipsis");
    put(0321,"emdash");
    put(0320,"endash");
    put(075,"equal");
    put(041,"exclam");
    put(0301,"exclamdown");
    put(0146,"f");
    put(0336,"fi");
    put(065,"five");
    put(0337,"fl");
    put(0304,"florin");
    put(064,"four");
    put(0332,"fraction");
    put(0147,"g");
    put(0247,"germandbls");
    put(0140,"grave");
    put(076,"greater");
    put(0307,"guillemotleft");
    put(0310,"guillemotright");
    put(0334,"guilsinglleft");
    put(0335,"guilsinglright");
    put(0150,"h");
    put(0375,"hungarumlaut");
    put(055,"hyphen");
    put(0151,"i");
    put(0222,"iacute");
    put(0224,"icircumflex");
    put(0225,"idieresis");
    put(0223,"igrave");
    put(0152,"j");
    put(0153,"k");
    put(0154,"l");
    put(074,"less");
    put(0302,"logicalnot");
    put(0155,"m");
    put(0370,"macron");
    put(0265,"mu");
    put(0156,"n");
    put(071,"nine");
    put(0226,"ntilde");
    put(043,"numbersign");
    put(0157,"o");
    put(0227,"oacute");
    put(0231,"ocircumflex");
    put(0232,"odieresis");
    put(0317,"oe");
    put(0376,"ogonek");
    put(0230,"ograve");
    put(061,"one");
    put(0273,"ordfeminine");
    put(0274,"ordmasculine");
    put(0277,"oslash");
    put(0233,"otilde");
    put(0160,"p");
    put(0246,"paragraph");
    put(050,"parenleft");
    put(051,"parenright");
    put(045,"percent");
    put(056,"period");
    put(0341,"periodcentered");
    put(0344,"perthousand");
    put(053,"plus");
    put(0261,"plusminus");
    put(0161,"q");
    put(077,"question");
    put(0300,"questiondown");
    put(042,"quotedbl");
    put(0343,"quotedblbase");
    put(0322,"quotedblleft");
    put(0323,"quotedblright");
    put(0324,"quoteleft");
    put(0325,"quoteright");
    put(0342,"quotesinglbase");
    put(047,"quotesingle");
    put(0162,"r");
    put(0250,"registered");
    put(0373,"ring");
    put(0163,"s");
    put(0244,"section");
    put(073,"semicolon");
    put(067,"seven");
    put(066,"six");
    put(057,"slash");
    put(040,"space");
    put(0243,"sterling");
    put(0164,"t");
    put(063,"three");
    put(0367,"tilde");
    put(0252,"trademark");
    put(062,"two");
    put(0165,"u");
    put(0234,"uacute");
    put(0236,"ucircumflex");
    put(0237,"udieresis");
    put(0235,"ugrave");
    put(0137,"underscore");
    put(0166,"v");
    put(0167,"w");
    put(0170,"x");
    put(0171,"y");
    put(0330,"ydieresis");
    put(0264,"yen");
    put(0172,"z");
    put(060,"zero");
  }
}