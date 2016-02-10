package com.frost.bag;

public class BagHelper {
    public static String enclose(String input, String bracket)
    {
        char bracket0 = bracket.charAt (0);
        char bracket1 = bracket.length () > 1 ? bracket.charAt (1) : bracket0;
        return new StringBuilder().append(bracket0).append(input).append(bracket1).toString();
    }

    public static String escape(String input)
    {
        // XXX should this escape quote marks in the string?
        return enclose((input != null) ? input : "", "\"");
    }

    public static String stringify(Object value)
    {
        if (value != null)
        {
            if (value instanceof String)
            {
                // XXX don't forget specific string encoding, to get around needing to escape quotes
                return escape((String) value);
            }
            else if (value.getClass ().isPrimitive ())
            {
                return escape(value.toString());
            }
            else if ((value instanceof BagArray) || (value instanceof BagObject))
            {
                return value.toString();
            }
            // no other type should be stored in the bag classes
        }
        return null;
    }
}

/*
    using System.Globalization;
        using System.Text;

public class BagHelper
{
    #region file helpers

    public static string StringFromFile(string filename)
    {
        // XXX TODO
        return "";
    }

    public static void WriteToFile(object value, string filename)
    {
        string valueString = Stringify(value);
        // now open a file
    }

    #endregion file helpers

    #region query and sort

    public static int Compare(object a, object b, BagArray order)
    {
        // if the structure of both objects match, great... if not, assume a lexical ordering of the
        // string value order = [{"field":"blarg","type":"LEXICAL","order":"DESCENDING"},{"field":"gurgle","type":"NUMERICAL","order":"ASCENDING"}]
        return 0;
    }

    public static bool Match(BagObject query, object value)
    {
        // query = {"field":{"eq":"value"}} query = {"and":{"field":{"=":"value"},"field2":{">":"20"}}}
        return true;
    }

    #endregion query and sort

    #region store and retrieve helpers

    public static string Enclose(string input, string bracket)
    {
        return new StringBuilder().Append(bracket[0]).Append(input).Append(bracket.Length > 1 ? bracket[1] : bracket[0]).ToString();
    }

    public static string Escape(string input)
    {
        // XXX should this escape quote marks in the string?
        return Enclose((input != null) ? input : "", "\"");
    }

    public static object Objectify(object value)
    {
        if (value != null)
        {
            if (value is string)
            {
                return ((string)value);
            }
            else if (value is double)
            {
                return ((double)value).ToString("R", CultureInfo.InvariantCulture);
            }
            else if (value.GetType().IsPrimitive)
        {
            return value.ToString();
        }
        else if ((value is BagArray) || (value is BagObject))
            {
                return value;
            }
            else // any other complex type
            {
                throw new System.Exception("Bags cannot store objects of type:" + value.GetType().ToString());
            }
        }
        return null;
    }

    public static string Stringify(object value)
    {
        if (value != null)
        {
            System.Type type = value.GetType();
            if (type == typeof(string))
            {
                return Escape((string)value);
            }
            else if (type.IsPrimitive)
            {
                return Escape(value.ToString());
            }
            else if ((type == typeof(BagArray)) || (type == typeof(BagObject)) || type.IsPrimitive)
            {
                return value.ToString();
            }
            // no other type should be stored in the bag classes
        }
        return null;
    }

    #endregion store and retrieve helpers
}
*/
