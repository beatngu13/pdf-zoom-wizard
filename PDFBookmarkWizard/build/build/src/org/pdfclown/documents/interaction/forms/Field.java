/*
  Copyright 2008-2012 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.interaction.forms;

import java.util.EnumSet;
import java.util.Stack;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.interaction.actions.ResetForm;
import org.pdfclown.documents.interaction.annotations.Widget;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObject;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfReference;
import org.pdfclown.objects.PdfSimpleObject;
import org.pdfclown.objects.PdfString;
import org.pdfclown.objects.PdfTextString;
import org.pdfclown.util.EnumUtils;

/**
  Interactive form field [PDF:1.6:8.6.2].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2, 12/28/12
*/
@PDF(VersionEnum.PDF12)
public abstract class Field
  extends PdfObjectWrapper<PdfDictionary>
{
  /*
    NOTE: Inheritable attributes are NOT early-collected, as they are NOT part
    of the explicit representation of a field -- they are retrieved everytime clients call.
  */
  // <class>
  // <classes>
  /**
    Field flags [PDF:1.6:8.6.2].
  */
  public enum FlagsEnum
  {
    /**
      The user may not change the value of the field.
    */
    ReadOnly(0x1),
    /**
      The field must have a value at the time it is exported by a submit-form action.
    */
    Required(0x2),
    /**
      The field must not be exported by a submit-form action.
    */
    NoExport(0x4),
    /**
      (Text fields only) The field can contain multiple lines of text.
    */
    Multiline(0x1000),
    /**
      (Text fields only) The field is intended for entering a secure password
      that should not be echoed visibly to the screen.
    */
    Password(0x2000),
    /**
      (Radio buttons only) Exactly one radio button must be selected at all times.
    */
    NoToggleToOff(0x4000),
    /**
      (Button fields only) The field is a set of radio buttons (otherwise, a check box).
      This flag is meaningful only if the Pushbutton flag isn't selected.
    */
    Radio(0x8000),
    /**
      (Button fields only) The field is a pushbutton that does not retain a permanent value.
    */
    Pushbutton(0x10000),
    /**
      (Choice fields only) The field is a combo box (otherwise, a list box).
    */
    Combo(0x20000),
    /**
      (Choice fields only) The combo box includes an editable text box as well as a dropdown list
      (otherwise, it includes only a drop-down list).
    */
    Edit(0x40000),
    /**
      (Choice fields only) The field's option items should be sorted alphabetically.
    */
    Sort(0x80000),
    /**
      (Text fields only) Text entered in the field represents the pathname of a file
      whose contents are to be submitted as the value of the field.
    */
    FileSelect(0x100000),
    /**
      (Choice fields only) More than one of the field's option items may be selected simultaneously.
    */
    MultiSelect(0x200000),
    /**
      (Choice and text fields only) Text entered in the field is not spell-checked.
    */
    DoNotSpellCheck(0x400000),
    /**
      (Text fields only) The field does not scroll to accommodate more text
      than fits within its annotation rectangle.
      Once the field is full, no further text is accepted.
    */
    DoNotScroll(0x800000),
    /**
      (Text fields only) The field is automatically divided into as many equally spaced positions,
      or combs, as the value of MaxLen, and the text is laid out into those combs.
    */
    Comb(0x1000000),
    /**
      (Text fields only) The value of the field should be represented as a rich text string.
    */
    RichText(0x2000000),
    /**
      (Button fields only) A group of radio buttons within a radio button field that use
      the same value for the on state will turn on and off in unison
      (otherwise, the buttons are mutually exclusive).
    */
    RadiosInUnison(0x2000000),
    /**
      (Choice fields only) The new value is committed as soon as a selection is made with the pointing device.
    */
    CommitOnSelChange(0x4000000);

    /**
      Converts an enumeration set into its corresponding bit mask representation.
    */
    public static int toInt(
      EnumSet<FlagsEnum> flags
      )
    {
      int flagsMask = 0;
      for(FlagsEnum flag : flags)
      {flagsMask |= flag.getCode();}

      return flagsMask;
    }

    /**
      Converts a bit mask into its corresponding enumeration representation.
    */
    public static EnumSet<FlagsEnum> toEnumSet(
      int flagsMask
      )
    {
      EnumSet<FlagsEnum> flags = EnumSet.noneOf(FlagsEnum.class);
      for(FlagsEnum flag : FlagsEnum.values())
      {
        if((flagsMask & flag.getCode()) > 0)
        {flags.add(flag);}
      }

      return flags;
    }

    private final int code;

    private FlagsEnum(
      int code
      )
    {this.code = code;}

    public int getCode(
      )
    {return code;}
  }
  // </classes>

  // <static>
  // <interface>
  // <public>
  /**
    Wraps a field reference into a field object.

    @param reference Reference to a field object.
    @return Field object associated to the reference.
  */
  public static final Field wrap(
    PdfReference reference
    )
  {
    if(reference == null)
      return null;

    PdfDictionary dataObject = (PdfDictionary)reference.getDataObject();
    PdfName fieldType = (PdfName)getInheritableAttribute(dataObject, PdfName.FT);
    PdfInteger fieldFlags = (PdfInteger)getInheritableAttribute(dataObject, PdfName.Ff);
    int fieldFlagsValue = (fieldFlags == null ? 0 : fieldFlags.getValue());
    if(fieldType.equals(PdfName.Btn)) // Button.
    {
      if((fieldFlagsValue & FlagsEnum.Pushbutton.getCode()) > 0) // Pushbutton.
        return new PushButton(reference);
      else if((fieldFlagsValue & FlagsEnum.Radio.getCode()) > 0) // Radio.
        return new RadioButton(reference);
      else // Check box.
        return new CheckBox(reference);
    }
    else if(fieldType.equals(PdfName.Tx)) // Text.
      return new TextField(reference);
    else if(fieldType.equals(PdfName.Ch)) // Choice.
    {
      if((fieldFlagsValue & FlagsEnum.Combo.getCode()) > 0) // Combo box.
        return new ComboBox(reference);
      else // List box.
        return new ListBox(reference);
    }
    else if(fieldType.equals(PdfName.Sig)) // Signature.
      return new SignatureField(reference);
    else // Unknown.
      throw new UnsupportedOperationException("Unknown field type: " + fieldType);
  }
  // </public>

  // <private>
  private static PdfDirectObject getInheritableAttribute(
    PdfDictionary dictionary,
    PdfName key
    )
  {
    /*
      NOTE: It moves upward until it finds the inherited attribute.
    */
    do
    {
      PdfDirectObject entry = dictionary.get(key);
      if(entry != null)
        return entry;

      dictionary = (PdfDictionary)dictionary.resolve(PdfName.Parent);
    } while(dictionary != null);
    // Default.
    if(key.equals(PdfName.Ff))
      return PdfInteger.Default;
    else
      return null;
  }
  // </private>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  /**
    Creates a new field within the given document context.
  */
  protected Field(
    PdfName fieldType,
    String name,
    Widget widget
    )
  {
    this(widget.getBaseObject());

    PdfDictionary baseDataObject = getBaseDataObject();
    baseDataObject.put(PdfName.FT, fieldType);
    baseDataObject.put(PdfName.T, new PdfTextString(name));
  }

  protected Field(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the field's behavior in response to trigger events.
  */
  public FieldActions getActions(
    )
  {
    PdfDirectObject actionsObject = getBaseDataObject().get(PdfName.AA);
    return actionsObject != null ? new FieldActions(actionsObject) : null;
  }

  /**
    Gets the default value to which this field reverts when a {@link ResetForm reset-form action} is
    executed.
  */
  public Object getDefaultValue(
    )
  {
    PdfSimpleObject<?> defaultValueObject = (PdfSimpleObject<?>)PdfObject.resolve(getInheritableAttribute(PdfName.DV));
    return defaultValueObject != null ? defaultValueObject.getValue() : null;
  }

  /**
    Gets the field flags.
  */
  public EnumSet<FlagsEnum> getFlags(
    )
  {
    PdfInteger flagsObject = (PdfInteger)PdfObject.resolve(getInheritableAttribute(PdfName.Ff));
    return flagsObject != null ? FlagsEnum.toEnumSet(flagsObject.getRawValue()) : EnumSet.noneOf(FlagsEnum.class);
  }

  /**
    Gets the fully-qualified field name.
  */
  public String getFullName(
    )
  {
    StringBuilder buffer = new StringBuilder();
    {
      Stack<String> partialNameStack = new Stack<String>();
      {
        PdfDictionary parent = getBaseDataObject();
        while(parent != null)
        {
          partialNameStack.push(((PdfTextString)parent.get(PdfName.T)).getValue());
          parent = (PdfDictionary)parent.resolve(PdfName.Parent);
        }
      }
      while(!partialNameStack.isEmpty())
      {
        if(buffer.length() > 0)
        {buffer.append('.');}

        buffer.append(partialNameStack.pop());
      }
    }
    return buffer.toString();
  }

  /**
    Gets the partial field name.
  */
  public String getName(
    )
  {return ((PdfTextString)getInheritableAttribute(PdfName.T)).getValue();} // NOTE: Despite the field name is not a canonical 'inheritable' attribute, sometimes it's not expressed at leaf level.

  /**
    Gets the field value.
  */
  public Object getValue(
    )
  {
    PdfSimpleObject<?> valueObject = (PdfSimpleObject<?>)PdfObject.resolve(getInheritableAttribute(PdfName.V));
    return valueObject != null ? valueObject.getValue() : null;
  }

  /**
    Gets the widget annotations that are associated with this field.
  */
  public FieldWidgets getWidgets(
    )
  {
    /*
      NOTE: Terminal fields MUST be associated at least to one widget annotation.
      If there is only one associated widget annotation and its contents
      have been merged into the field dictionary, 'Kids' entry MUST be omitted.
    */
    PdfDirectObject widgetsObject = getBaseDataObject().get(PdfName.Kids);
    return new FieldWidgets(
      widgetsObject != null
        ? widgetsObject // Annotation array.
        : getBaseObject(), // Merged annotation.
      this
      );
  }

  /**
    Gets whether the field is exported by a submit-form action.
  */
  public boolean isExportable(
    )
  {return !getFlags().contains(FlagsEnum.NoExport);}

  /**
    Gets whether the user may not change the value of the field.
  */
  public boolean isReadOnly(
    )
  {return getFlags().contains(FlagsEnum.ReadOnly);}

  /**
    Gets whether the field must have a value at the time
    it is exported by a submit-form action.
  */
  public boolean isRequired(
    )
  {return getFlags().contains(FlagsEnum.Required);}

  /**
    @see #getActions()
  */
  public void setActions(
    FieldActions value
    )
  {getBaseDataObject().put(PdfName.AA,value.getBaseObject());}

  /**
    @see #isExportable()
  */
  public void setExportable(
    boolean value
    )
  {setFlags(EnumUtils.mask(getFlags(), FlagsEnum.NoExport, !value));}

  /**
    @see #getFlags()
  */
  public void setFlags(
    EnumSet<FlagsEnum> value
    )
  {getBaseDataObject().put(PdfName.Ff, PdfInteger.get(FlagsEnum.toInt(value)));}

  /**
    @see #getName()
  */
  public void setName(
    String value
    )
  {getBaseDataObject().put(PdfName.T, new PdfTextString(value));}

  /**
    @see #isReadOnly()
  */
  public void setReadOnly(
    boolean value
    )
  {setFlags(EnumUtils.mask(getFlags(), FlagsEnum.ReadOnly, value));}

  /**
    @see #isRequired()
  */
  public void setRequired(
    boolean value
    )
  {setFlags(EnumUtils.mask(getFlags(), FlagsEnum.Required, value));}

  /**
    @see #getValue()
  */
  public void setValue(
    Object value
    )
  {getBaseDataObject().put(PdfName.V, new PdfString((String)value));}
  // </public>

  // <protected>
  protected PdfString getDefaultAppearanceState(
    )
  {return (PdfString)getInheritableAttribute(PdfName.DA);}

  protected PdfDirectObject getInheritableAttribute(
    PdfName key
    )
  {return getInheritableAttribute(getBaseDataObject(), key);}
  // </protected>
  // </interface>
  // </dynamic>
  // </class>
}