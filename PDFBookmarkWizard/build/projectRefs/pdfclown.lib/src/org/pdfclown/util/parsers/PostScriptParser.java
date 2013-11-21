/*
  Copyright 2011-2012 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.util.parsers;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;

import org.pdfclown.bytes.Buffer;
import org.pdfclown.bytes.IInputStream;
import org.pdfclown.tokens.Keyword;
import org.pdfclown.tokens.Symbol;

/**
  PostScript (non-procedural subset) parser [PS].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.1
  @version 0.1.2, 12/02/12
*/
public class PostScriptParser
  implements Closeable
{
  // <class>
  // <classes>
  public enum TokenTypeEnum // [PS:3.3].
  {
    Keyword,
    Boolean,
    Integer,
    Real,
    Literal,
    Hex,
    Name,
    Comment,
    ArrayBegin,
    ArrayEnd,
    DictionaryBegin,
    DictionaryEnd,
    Null
  }
  // </classes>

  // <static>
  // <interface>
  // <protected>
  protected static int getHex(
    int c
    )
  {
    if(c >= '0' && c <= '9')
      return (c - '0');
    else if(c >= 'A' && c <= 'F')
      return (c - 'A' + 10);
    else if(c >= 'a' && c <= 'f')
      return (c - 'a' + 10);
    else
      return -1;
  }

  /**
    Evaluates whether a character is a delimiter.
  */
  protected static boolean isDelimiter(
    int c
    )
  {
    return c == Symbol.OpenRoundBracket
      || c == Symbol.CloseRoundBracket
      || c == Symbol.OpenAngleBracket
      || c == Symbol.CloseAngleBracket
      || c == Symbol.OpenSquareBracket
      || c == Symbol.CloseSquareBracket
      || c == Symbol.Slash
      || c == Symbol.Percent;
  }

  /**
    Evaluates whether a character is an EOL marker.
  */
  protected static boolean isEOL(
    int c
    )
  {return c == 10 || c == 13;}

  /**
    Evaluates whether a character is a white-space.
  */
  protected static boolean isWhitespace(
    int c
    )
  {return c == 32 || isEOL(c) || c == 0 || c == 9 || c == 12;}
  // </protected>
  // </interface>
  // </static>

  // <dynamic>
  // <fields>
  private IInputStream stream;

  private Object token;
  private TokenTypeEnum tokenType;
  // </fields>

  // <constructors>
  public PostScriptParser(
    IInputStream stream
    )
  {this.stream = stream;}

  public PostScriptParser(
    byte[] data
    )
  {this.stream = new Buffer(data);}
  // </constructors>

  // <interface>
  // <public>
  public long getLength(
    )
  {return stream.getLength();}

  public long getPosition(
    )
  {return stream.getPosition();}

  public IInputStream getStream(
    )
  {return stream;}

  /**
    Gets the currently-parsed token.
  */
  public Object getToken(
    )
  {return token;}

  /**
    Gets a token after moving to the given offset.

    @param offset Number of tokens to skip before reaching the intended one.
    @see #getToken()
  */
  public Object getToken(
    int offset
    )
  {moveNext(offset); return getToken();}

  /**
    Gets the currently-parsed token type.
  */
  public TokenTypeEnum getTokenType(
    )
  {return tokenType;}

  @Override
  public int hashCode(
    )
  {return stream.hashCode();}

  /**
    Moves the pointer to the token at the given offset.

    @param offset Number of tokens to skip before reaching the intended one.
    @return Whether a new token was found.
  */
  public boolean moveNext(
    int offset
    )
  {
    for(
      int index = 0;
      index < offset;
      index++
      )
    {
      if(!moveNext())
        return false;
    }
    return true;
  }

  /**
    Moves the pointer to the next token.
    <p>To properly parse the current token, the pointer MUST be just before its starting
    (leading whitespaces are ignored). When this method terminates, the pointer IS
    at the last byte of the current token.</p>

    @return Whether a new token was found.
  */
  public boolean moveNext(
    )
  {
    StringBuilder buffer = null;
    token = null;
    int c = 0;

    // Skip leading white-space characters.
    try
    {
      do
      {c = stream.readUnsignedByte();}
      while(isWhitespace(c)); // Keep goin' till there's a white-space character...
    }
    catch(EOFException e)
    {return false;}

    // Which character is it?
    switch(c)
    {
      case Symbol.Slash: // Name.
      {
        tokenType = TokenTypeEnum.Name;

        /*
          NOTE: As name objects are simple symbols uniquely defined by sequences of characters,
          the bytes making up the name are never treated as text, so here they are just
          passed through without unescaping.
        */
        buffer = new StringBuilder();
        try
        {
          while(true)
          {
            c = stream.readUnsignedByte();
            if(isDelimiter(c) || isWhitespace(c))
              break;

            buffer.append((char)c);
          }
          stream.skip(-1); // Restores the first byte after the current token.
        }
        catch(EOFException e)
        {/* NOOP */}
      } break;
      case '0':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
      case '.':
      case '-':
      case '+': // Number.
      {
        if(c == '.')
        {tokenType = TokenTypeEnum.Real;}
        else // Digit or signum.
        {tokenType = TokenTypeEnum.Integer;} // By default (it may be real).

        // Building the number...
        buffer = new StringBuilder();
        try
        {
          while(true)
          {
            buffer.append((char)c);
            c = stream.readUnsignedByte();
            if(c == '.')
            {tokenType = TokenTypeEnum.Real;}
            else if(c < '0' || c > '9')
              break;
          }
          stream.skip(-1); // Restores the first byte after the current token.
        }
        catch(EOFException e)
        {/* NOOP */}
      } break;
      case Symbol.OpenSquareBracket: // Array (begin).
        tokenType = TokenTypeEnum.ArrayBegin;
        break;
      case Symbol.CloseSquareBracket: // Array (end).
        tokenType = TokenTypeEnum.ArrayEnd;
        break;
      case Symbol.OpenAngleBracket: // Dictionary (begin) | Hexadecimal string.
      {
        try
        {c = stream.readUnsignedByte();}
        catch(EOFException e)
        {throw new ParseException("Unexpected EOF (isolated opening angle-bracket character).", e);}
        // Is it a dictionary (2nd angle bracket)?
        if(c == Symbol.OpenAngleBracket)
        {
          tokenType = TokenTypeEnum.DictionaryBegin;
          break;
        }

        // Hexadecimal string (single angle bracket).
        tokenType = TokenTypeEnum.Hex;

        buffer = new StringBuilder();
        try
        {
          while(c != Symbol.CloseAngleBracket) // NOT string end.
          {
            if(!isWhitespace(c))
            {buffer.append((char)c);}

            c = stream.readUnsignedByte();
          }
        }
        catch(EOFException e)
        {throw new ParseException("Unexpected EOF (malformed hex string).", e);}
      } break;
      case Symbol.CloseAngleBracket: // Dictionary (end).
      {
        try
        {c = stream.readUnsignedByte();}
        catch(EOFException e)
        {throw new ParseException("Unexpected EOF (malformed dictionary).", e);}
        if(c != Symbol.CloseAngleBracket)
          throw new ParseException("Malformed dictionary.", stream.getPosition());

        tokenType = TokenTypeEnum.DictionaryEnd;
      } break;
      case Symbol.OpenRoundBracket: // Literal string.
      {
        tokenType = TokenTypeEnum.Literal;

        buffer = new StringBuilder();
        int level = 0;
        try
        {
          while(true)
          {
            c = stream.readUnsignedByte();
            if(c == Symbol.OpenRoundBracket)
              level++;
            else if(c == Symbol.CloseRoundBracket)
              level--;
            else if(c == '\\')
            {
              boolean lineBreak = false;
              c = stream.readUnsignedByte();
              switch(c)
              {
                case 'n':
                  c = Symbol.LineFeed;
                  break;
                case 'r':
                  c = Symbol.CarriageReturn;
                  break;
                case 't':
                  c = '\t';
                  break;
                case 'b':
                  c = '\b';
                  break;
                case 'f':
                  c = '\f';
                  break;
                case Symbol.OpenRoundBracket:
                case Symbol.CloseRoundBracket:
                case '\\':
                  break;
                case Symbol.CarriageReturn:
                  lineBreak = true;
                  c = stream.readUnsignedByte();
                  if(c != Symbol.LineFeed)
                    stream.skip(-1);
                  break;
                case Symbol.LineFeed:
                  lineBreak = true;
                  break;
                default:
                {
                  // Is it outside the octal encoding?
                  if(c < '0' || c > '7')
                    break;

                  // Octal.
                  int octal = c - '0';
                  c = stream.readUnsignedByte();
                  // Octal end?
                  if(c < '0' || c > '7')
                  {c = octal; stream.skip(-1); break;}
                  octal = (octal << 3) + c - '0';
                  c = stream.readUnsignedByte();
                  // Octal end?
                  if(c < '0' || c > '7')
                  {c = octal; stream.skip(-1); break;}
                  octal = (octal << 3) + c - '0';
                  c = octal & 0xff;
                  break;
                }
              }
              if(lineBreak)
                continue;
            }
            else if(c == Symbol.CarriageReturn)
            {
              c = stream.readUnsignedByte();
              if(c != Symbol.LineFeed)
              {c = Symbol.LineFeed; stream.skip(-1);}
            }
            if(level == -1)
              break;

            buffer.append((char)c);
          }
        }
        catch(EOFException e)
        {throw new ParseException("Unexpected EOF (malformed literal string).", e);}
      } break;
      case Symbol.Percent: // Comment.
      {
        tokenType = TokenTypeEnum.Comment;

        buffer = new StringBuilder();
        try
        {
          while(true)
          {
            c = stream.readUnsignedByte();
            if(isEOL(c))
              break;

            buffer.append((char)c);
          }
        }
        catch(EOFException e)
        {/* NOOP */}
      } break;
      default: // Keyword.
      {
        tokenType = TokenTypeEnum.Keyword;

        buffer = new StringBuilder();
        try
        {
          do
          {
            buffer.append((char)c);
            c = stream.readUnsignedByte();
          } while(!isDelimiter(c) && !isWhitespace(c));
          stream.skip(-1); // Restores the first byte after the current token.
        }
        catch(EOFException e)
        {/* NOOP */}
      } break;
    }

    if(buffer != null)
    {
      switch(tokenType)
      {
        case Keyword:
        {
          token = buffer.toString();
          if(token.equals(Keyword.False)
            || token.equals(Keyword.True)) // Boolean.
          {
            token = Boolean.parseBoolean((String)token);
            tokenType = TokenTypeEnum.Boolean;
          }
          else if(token.equals(Keyword.Null)) // Null.
          {
            token = null;
            tokenType = TokenTypeEnum.Null;
          }
        } break;
        case Name:
        case Literal:
        case Hex:
        case Comment:
          token = buffer.toString();
          break;
        case Integer:
          token = Integer.parseInt(buffer.toString());
          break;
        case Real:
          token = Double.parseDouble(buffer.toString());
          break;
        default:
        {
          /* NOOP */
        }
      }
    }
    return true;
  }

  /**
    Moves the pointer to the given absolute byte position.
  */
  public void seek(
    long position
    )
  {stream.seek(position);}

  /**
    Moves the pointer to the given relative byte position.
  */
  public void skip(
    long offset
    )
  {stream.skip(offset);}

  /**
    Moves the pointer before the next non-EOL character after the current position.

    @return Whether the stream can be further read.
  */
  public boolean skipEOL(
    )
  {
    try
    {
      int c;
      do
      {c = stream.readUnsignedByte();} while(isEOL(c)); // Keeps going till there's an EOL character.
    }
    catch(EOFException e)
    {return false;}
    stream.skip(-1); // Moves back to the first non-EOL character position.
    return true;
  }

  /**
    Moves the pointer before the next non-whitespace character after the current position.

    @return Whether the stream can be further read.
  */
  public boolean skipWhitespace(
    )
  {
    try
    {
      int c;
      do
      {c = stream.readUnsignedByte();} while(isWhitespace(c)); // Keeps going till there's a whitespace character.
    }
    catch(EOFException e)
    {return false;}
    stream.skip(-1); // Moves back to the first non-whitespace character position.
    return true;
  }

  // <Closeable>
  @Override
  public void close(
    ) throws IOException
  {
    if(stream != null)
    {
      stream.close();
      stream = null;
    }
  }
  // </Closeable>
  // </public>

  // <protected>
  @Override
  protected void finalize(
    ) throws Throwable
  {
    try
    {close();}
    finally
    {super.finalize();}
  }

  protected void setToken(
    Object value
    )
  {token = value;}

  protected void setTokenType(
    TokenTypeEnum value
    )
  {tokenType = value;}
  // </protected>
  // </dynamic>
  // </class>
}
