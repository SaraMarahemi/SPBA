
package Utility;

/**
 *
 * @author Sarah
 */
public class nodePair 
{
    private final int key;
    private final int value;

    public nodePair(int aKey, int aValue)
    {
        key   = aKey;
        value = aValue;
    }

    public int key()   { return key; }
    public int value() { return value; }
}