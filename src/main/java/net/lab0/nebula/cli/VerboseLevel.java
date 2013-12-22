package net.lab0.nebula.cli;

public enum VerboseLevel
{
    ALL,
    TRACE, // detailed debug
    DEBUG,
    PROGRESS, // progression information
    DETAIL, // more precise info
    INFO,
    WARN, // non blocking error
    ERROR,
    FATAL, // non user/program error (system/os error)
    OFF, ;
    
    /**
     * Greater than
     */
    public boolean gt(VerboseLevel other)
    {
        return this.ordinal() > other.ordinal();
    }
    
    /**
     * Less than
     */
    public boolean lt(VerboseLevel other)
    {
        return this.ordinal() < other.ordinal();
    }
    
    /**
     * Greater than or equal to
     */
    public boolean gte(VerboseLevel other)
    {
        return this.ordinal() >= other.ordinal();
    }
    
    /**
     * Less than or equal to
     */
    public boolean lte(VerboseLevel other)
    {
        return this.ordinal() <= other.ordinal();
    }
    
    /**
     * equals
     */
    public boolean eq(VerboseLevel other)
    {
        return this.ordinal() == other.ordinal();
    }
    
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
