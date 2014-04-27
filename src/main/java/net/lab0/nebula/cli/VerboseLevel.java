package net.lab0.nebula.cli;

public enum VerboseLevel
{
    /**
     * Log all messages. This is not a valid severity level for messages
     */
    ALL,
    /**
     * Detailed debug
     */
    TRACE,
    /**
     * Debug messages
     */
    DEBUG,
    /**
     * progression information
     */
    PROGRESS,
    /**
     * more precise info
     */
    DETAIL,
    /**
     * Information messages
     */
    INFO,
    /**
     * non blocking error
     */
    WARN,
    /**
     * A programm error
     */
    ERROR,
    /**
     * non user, non program error (system/os error)
     */
    FATAL,
    /**
     * No message at all
     */
    OFF, ;
    
    /**
     * @param other
     *            Antoher severity level
     * @return <code>true</code> if greater than
     */
    public boolean gt(VerboseLevel other)
    {
        return this.ordinal() > other.ordinal();
    }
    
    /**
     * @param other
     *            Antoher severity level
     * @return <code>true</code> if less than
     */
    public boolean lt(VerboseLevel other)
    {
        return this.ordinal() < other.ordinal();
    }
    
    /**
     * @param other
     *            Antoher severity level
     * @return <code>true</code> if greater than or equal to
     */
    public boolean gte(VerboseLevel other)
    {
        return this.ordinal() >= other.ordinal();
    }
    
    /**
     * @param other
     *            Antoher severity level
     * @return <code>true</code> if less than or equal to
     */
    public boolean lte(VerboseLevel other)
    {
        return this.ordinal() <= other.ordinal();
    }
    
    /**
     * @param other
     *            Antoher severity level
     * @return <code>true</code> if equals
     */
    public boolean eq(VerboseLevel other)
    {
        return this.ordinal() == other.ordinal();
    }
    
    /**
     * @return A string of the available severity levels
     */
    public static String toHelp()
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (VerboseLevel value : VerboseLevel.values())
        {
            stringBuilder.append(value.toString());
            if (value.ordinal() < OFF.ordinal())
            {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }
}
