/*
  Copyright 2007-2012 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.contents.composition;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pdfclown.documents.contents.fonts.Font;

/**
  Text fitter.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.3
  @version 0.1.2, 01/20/12
*/
final class TextFitter
{
  // <class>
  // <dynamic>
  // <fields>
  private final Font font;
  private final double fontSize;
  private final boolean hyphenation;
  private final char hyphenationCharacter;
  private final String text;
  private double width;

  private int beginIndex = 0;
  private int endIndex = -1;
  private String fittedText;
  private double fittedWidth;
  // </fields>

  // <constructors>
  TextFitter(
    String text,
    double width,
    Font font,
    double fontSize,
    boolean hyphenation,
    char hyphenationCharacter
    )
  {
    this.text = text;
    this.width = width;
    this.font = font;
    this.fontSize = fontSize;
    this.hyphenation = hyphenation;
    this.hyphenationCharacter = hyphenationCharacter;
  }
  // </constructors>

  // <interface>
  // <public>
  /**
    Fits the text inside the specified width.

    @param unspacedFitting Whether fitting of unspaced text is allowed.
    @return Whether the operation was successful.
  */
  public boolean fit(
    boolean unspacedFitting
    )
  {
    return fit(
      endIndex + 1,
      width,
      unspacedFitting
      );
    }

  /**
    Fits the text inside the specified width.

    @param index Beginning index, inclusive.
    @param width Available width.
    @param unspacedFitting Whether fitting of unspaced text is allowed.
    @return Whether the operation was successful.
    @version 0.0.4
  */
  public boolean fit(
    int index,
    double width,
    boolean unspacedFitting
    )
  {
    beginIndex = index;
    this.width = width;

    fittedText = null;
    fittedWidth = 0;

    String hyphen = "";

fitting:
    // Fitting the text within the available width...
    {
      Pattern pattern = Pattern.compile("(\\s*)(\\S*)");
      Matcher matcher = pattern.matcher(text);
      matcher.region(beginIndex,text.length());
      while(matcher.find())
      {
        // Scanning for the presence of a line break...
        /*
          NOTE: This text fitting algorithm returns everytime it finds a line break character,
          as it's intended to evaluate the width of just a single line of text at a time.
        */
        for(
          int spaceIndex = matcher.start(1),
            spaceEnd = matcher.end(1);
          spaceIndex < spaceEnd;
          spaceIndex++
          )
        {
          switch(text.charAt(spaceIndex))
          {
            case '\n':
            case '\r':
              index = spaceIndex;
              break fitting;
          }
        }

        // Add the current word!
        int wordEndIndex = matcher.end(0); // Current word's limit.
        double wordWidth = font.getWidth(matcher.group(0), fontSize); // Current word's width.
        fittedWidth += wordWidth;
        // Does the fitted text's width exceed the available width?
        if(fittedWidth > width)
        {
          // Remove the current (unfitting) word!
          fittedWidth -= wordWidth;
          wordEndIndex = index;
          if(!hyphenation
            && (wordEndIndex > beginIndex // There's fitted content.
              || !unspacedFitting // There's no fitted content, but unspaced fitting isn't allowed.
              || text.charAt(beginIndex) == ' ') // Unspaced fitting is allowed, but text starts with a space.
            ) // Enough non-hyphenated text fitted.
            break fitting;

          /*
            NOTE: We need to hyphenate the current (unfitting) word.
          */
          /*
            TODO: This hyphenation algorithm is quite primitive (to improve!).
          */
hyphenating:
          while(true)
          {
            // Add the current character!
            char textChar = text.charAt(wordEndIndex); // Current character.
            wordWidth = font.getWidth(textChar, fontSize);
            wordEndIndex++;
            fittedWidth += wordWidth;
            // Does fitted text's width exceed the available width?
            if(fittedWidth > width)
            {
              // Remove the current character!
              fittedWidth -= wordWidth;
              wordEndIndex--;
              if(hyphenation)
              {
                // Is hyphenation to be applied?
                if(wordEndIndex > index + 4) // Long-enough word chunk.
                {
                  // Make room for the hyphen character!
                  wordEndIndex--;
                  index = wordEndIndex;
                  textChar = text.charAt(wordEndIndex);
                  fittedWidth -= font.getWidth(textChar, fontSize);

                  // Add the hyphen character!
                  textChar = hyphenationCharacter;
                  fittedWidth += font.getWidth(textChar, fontSize);

                  hyphen = String.valueOf(textChar);
                }
                else // No hyphenation.
                {
                  // Removing the current word chunk...
                  while(wordEndIndex > index)
                  {
                    wordEndIndex--;
                    textChar = text.charAt(wordEndIndex);
                    fittedWidth -= font.getWidth(textChar, fontSize);
                  }
                }
              }
              else
              {
                index = wordEndIndex;
              }
              break hyphenating;
            }
          }
          break fitting;
        }
        index = wordEndIndex;
      }
    }
    fittedText = text.substring(beginIndex,index) + hyphen;
    endIndex = index;

    return (fittedWidth > 0);
  }

  /**
    Gets the begin index of the fitted text inside the available text.
  */
  public int getBeginIndex(
    )
  {return beginIndex;}

  /**
    Gets the end index of the fitted text inside the available text.
  */
  public int getEndIndex(
    )
  {return endIndex;}

  /**
    Gets the fitted text.
  */
  public String getFittedText(
    )
  {return fittedText;}

  /**
    Gets the fitted text's width.
  */
  public double getFittedWidth(
    )
  {return fittedWidth;}

  /**
    Gets the font used to fit the text.
  */
  public Font getFont(
    )
  {return font;}

  /**
    Gets the size of the font used to fit the text.
  */
  public double getFontSize(
    )
  {return fontSize;}

  /**
    Gets the character shown at the end of the line before a hyphenation break.
  */
  public char getHyphenationCharacter(
    )
  {return hyphenationCharacter;}

  /**
    Gets the available text.
  */
  public String getText(
    )
  {return text;}

  /**
    Gets the available width.
  */
  public double getWidth(
    )
  {return width;}

  /**
    Gets whether the hyphenation algorithm has to be applied.
  */
  public boolean isHyphenation(
    )
  {return hyphenation;}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}