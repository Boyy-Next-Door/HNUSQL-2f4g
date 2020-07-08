/*
 * This class provides a more sophisticated tokenizer than that available
 * with StringTokenizer.  In particular, it handles tokenizing of fields
 * in brackets, and will ignore separators in quotes or brackets.
 *
 * $Author: davis $
 * $Date: 2004/12/18 21:29:04 $
 * $Revision: 1.1 $
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA
 *
 * Revision History:
 * 
 * Written by Davis Swan in April, 2004.
 */
package com.sqlmagic.tinysql;

import java.text.*;
import java.util.*;
import java.lang.*;

public class FieldTokenizer
{
   String[] fields;
   int fieldIndex;
/*
 * Split an input string into fields based upon the input separator, ignoring
 * separators that might occur within brackets or quoted string.  If the 
 * separator is (, return strings outside and inside of the brackets.  The
 * parameter returnSep indicates whether or not the actual separator characters
 * themselves should be returned.
 * 根据输入分隔符将输入字符串拆分为字段，忽略括号或带引号的字符串中可能出现的分隔符。
 * 如果分隔符为（，则返回括号内外的字符串。参数returnSep指示是否应返回实际的分隔符本身。
 */
   public FieldTokenizer(String inputString,char separator, boolean returnSep)
   {
      char quoteChar,nextChar,bracketQuoteChar;
      char[] charArray = {' '};
      Vector tempStrings;
      int i,leftBracketCount,rightBracketCount,startPosn,endPosn;
      String tempString;
      boolean debug=false;
      if ( inputString.indexOf(separator) < 0 )
      {
         fields = new String[1];
         fields[0] = inputString;
         if ( inputString.trim().length() == 0 ) return;
         //trim：删除头尾空白符的字符串。
      }
      debug=true;
      if ( debug ) 
         System.out.println("FieldTokenizer: "
         + " separator is " + separator + " string is <" + inputString + ">");
      /*
      tinySQL>create table lala(xiebo int);
      FieldTokenizer:  separator is   string is <create table lala(xiebo int)>
      FieldTokenizer output[0]: <create>
      FieldTokenizer output[1]: <table>
      FieldTokenizer output[2]: <lala(xiebo int)>
      FieldTokenizer:  separator is , string is <lala(xiebo int)>
      FieldTokenizer output[0]: <lala(xiebo int)>
      FieldTokenizer:  separator is ( string is <lala(xiebo int)>
      FieldTokenizer output[0]: <lala>
      FieldTokenizer output[1]: <xiebo int>
      FieldTokenizer:  separator is , string is <xiebo int>
      FieldTokenizer output[0]: <xiebo int>
      FieldTokenizer:  separator is   string is <XIEBO INT>
      FieldTokenizer output[0]: <XIEBO>
      FieldTokenizer output[1]: <INT>
      FieldTokenizer:  separator is ( string is <XIEBO>
      FieldTokenizer output[0]: <XIEBO>
      FieldTokenizer:  separator is ( string is <INT>
      FieldTokenizer output[0]: <INT>
      DONE
       */
      charArray[0] = separator;
      tempStrings = new Vector();
      leftBracketCount = 0;
      rightBracketCount = 0;
      quoteChar = ' ';
      bracketQuoteChar = ' ';
      startPosn = 0;
      endPosn = 0;
      for ( i = 0; i < inputString.length(); i++ )
      {
         nextChar = inputString.charAt(i);
         endPosn = i;
         if ( nextChar == '\'' | nextChar == '"' )
         {
/*
 *          Set the bracketQuoteChar for quotes within a bracket
 *          delimited string.  This will allow handling of brackets
 *          within quoted strings that are embedded within the brackets.
 *          为括号定界字符串中的引号设置bracketQuoteChar。 这将允许处理括号内的带引号的字符串中的括号。
 */
            if ( leftBracketCount > 0 )
            {
               if ( bracketQuoteChar == ' ' )
                  bracketQuoteChar = nextChar;
               else if ( nextChar == bracketQuoteChar )
                  bracketQuoteChar = ' ';
                continue;
            }
            if ( quoteChar == ' ' )
               quoteChar = nextChar; 
            else if ( nextChar == quoteChar )
            {
/*
 *             A matching quote character has been found.  Check for two
 *             adjacent single quotes which represent an embedded single
 *             quote.
 *             找到匹配的引号字符。 检查两个相邻的单引号，它们代表一个嵌入式单引号。
 */
               if ( i < inputString.length() - 1 & quoteChar == '\'' )
               {
                  if ( inputString.charAt(i + 1) == '\'' ) i++;
                  else quoteChar = ' ';
               } else {
                  quoteChar = ' ';
               }
            }
         } else if ( nextChar == '(' | nextChar == ')' ) {
/*
 *          Ignore brackets inside quoted strings.忽略带引号的字符串中的括号。
 */
            if ( quoteChar != ' ' | bracketQuoteChar != ' ' ) continue;
            if ( nextChar == '(' )
            {
               leftBracketCount++;
/*
 *             If bracket is the separator, return the string before the
 *             left bracket.如果方括号是分隔符，则返回左方括号之前的字符串。
 */
               if ( separator == '(' & leftBracketCount == 1 ) 
               {
                  tempString = "";
                  if ( endPosn > startPosn )
                     tempString = inputString.substring(startPosn,endPosn);
                  if ( tempString.trim().length() > 0 )
                     tempStrings.addElement(tempString.trim());
                  if ( returnSep ) tempStrings.addElement("(");
                  startPosn = endPosn + 1;
               }
            } else if ( nextChar == ')' ) {
/*
 *             Handle nested sets of brackets.处理嵌套套括号。
 */
               rightBracketCount++;
               if ( leftBracketCount > 0 &
                    leftBracketCount == rightBracketCount )
               {
                  if ( separator == '('  )
                  {
/*
 *                   If bracket is the separator, return the string between the
 *                   brackets.如果方括号是分隔符，请返回方括号之间的字符串。
 */
                     tempString = "";
                     if ( endPosn > startPosn ) 
                        tempString = inputString.substring(startPosn,endPosn);
                     if ( tempString.trim().length() > 0)
                        tempStrings.addElement(tempString.trim());
                     if ( returnSep ) tempStrings.addElement(")");
                     startPosn = endPosn + 1;
                  }
                  leftBracketCount = 0;
                  rightBracketCount = 0;
               }
            }
/*
 *          If the separator character has been found and we are not within
 *          brackets and we are not within a quoted string (as indicated
 *          by a blank quoteChar value), then build the next output string.
 */
         } else if ( nextChar == separator & leftBracketCount == 0 &
                     quoteChar == ' ' ) {
           
            tempString = "";
            if ( endPosn > startPosn )
               tempString = inputString.substring(startPosn,endPosn).trim();
            if ( tempString.length() > 0 ) tempStrings.addElement(tempString);
            if ( returnSep )
               tempStrings.addElement(new String(charArray));
            startPosn = endPosn + 1;
         }
      }
/*
 *    Pick up the last string if there is one.
 */
      if ( endPosn >= startPosn )
      {
         tempString = inputString.substring(startPosn,endPosn+1).trim();
         if ( tempString.length() > 0 ) 
            tempStrings.addElement(tempString);
      }
/*
 *    Create output string array from Vector.
 */
      if ( tempStrings.size() == 0 ) 
      {
         fields = new String[1];
         fields[0] = inputString;
         if ( debug )
            System.out.println("FieldTokenizer output: <" + inputString + ">");
      } else {
         fields = new String[tempStrings.size()];
         for ( i = 0; i < tempStrings.size(); i++ )
         {
            fields[i] = (String)tempStrings.elementAt(i);
            if ( debug )
               System.out.println("FieldTokenizer output[" + i + "]: <" 
               + fields[i] + ">");
         }
      }
      fieldIndex = 0;
   }
/*
 * Method to return the fields as an array of strings.
 */
   public String[] getFields()
   {
      return fields;
   }
/*
 * Method to return the count of fields.
 */
   public int countFields()
   {
      return fields.length;
   }
/*
 * Methods to return a particular field.  A default value can be provided.
 */
   public String getField(int inputIndex)
   {
      return getField(inputIndex,"NULL");
   }
   public String getField(int inputIndex,String defaultString)
   {
      if ( inputIndex < 0 | inputIndex >= fields.length )
         return defaultString;
      else
         return fields[inputIndex];
   }  
   public int getInt(int inputIndex,int defaultInt)
   {
      String numStr;
      int outputInt;
      if ( inputIndex < 0 | inputIndex >= fields.length )
         return defaultInt;
      else
      {
         numStr = getField(inputIndex);
         try 
         {
            return Integer.parseInt(numStr);
         } catch (Exception e) {
            return defaultInt;
         }
      }
   }  
/*
 * Methods used to get fields sequentially.
 */
   public boolean hasMoreFields()
   {
      if ( fieldIndex < fields.length ) return true;
      else return false;
   }
   public String nextField()
   {
      String returnString;
      if ( fieldIndex < fields.length )
      {
         returnString = fields[fieldIndex];
         fieldIndex++;
      }
      else
         returnString = (String)null;
      return returnString;
   }
}
