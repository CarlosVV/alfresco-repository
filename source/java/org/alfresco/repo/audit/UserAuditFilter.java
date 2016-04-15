package org.alfresco.repo.audit;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.util.Pair;
import org.alfresco.util.PropertyCheck;
import org.springframework.beans.factory.InitializingBean;

public class UserAuditFilter implements InitializingBean
{
    private static final char NOT = '~';
    private static final String REG_EXP_SEPARATOR = ";";
    private static final char ESCAPE = '\\';
    private static final String ESCAPED_NOT = ""+ESCAPE+NOT;
    
    private String userFilterPattern;
    private List<Pair<Boolean, Pattern>> listOfPairValue = new ArrayList<Pair<Boolean, Pattern>>();

    /**
     * Default constructor
     */
    public UserAuditFilter()
    {
    }

    /*
     * Set user audit pattern. For example "audit.filter.alfresco-access.transaction.user=~user1;user2;.*"
     * 
     * @param userFilterPattern 'userFilterPattern' is String type. The value of 'userFilterPattern' couldn't empty 
     *                          or have 0 length value. An expression that starts with a '~' indicates that any 
     *                          matching value should be rejected. Each regular expression in the list is separated 
     *                          by a semicolon (';'). 
     */
    public void setUserFilterPattern(String userFilterPattern)
    {
        this.userFilterPattern = userFilterPattern;
    }
    
    public void afterPropertiesSet()
    {
        parseProperties();
    }
    
    private void parseProperties()
    {
        String userPropertyValue = userFilterPattern;
        if (!PropertyCheck.isValidPropertyString(userPropertyValue))
        {
            return;
        }
        
        String[] arrValues = userPropertyValue.split(REG_EXP_SEPARATOR);
        for (String prop : arrValues)
        {
            boolean includeExp = prop.charAt(0) != NOT;

            if (!includeExp || prop.startsWith(ESCAPED_NOT))
            {
                prop = prop.substring(1);
            }
            try
            {
                listOfPairValue.add(new Pair<Boolean, Pattern>(includeExp, Pattern.compile(prop)));
            }
            catch (PatternSyntaxException ex)
            {
                throw new AlfrescoRuntimeException("The 'audit.filter.alfresco-access.transaction.user' property parse exception; see property 'audit.filter.alfresco-access.transaction.user'.", ex);
            }
        }
    }
    
    public boolean acceptUser(String value)
    {
        if (value == null)
        {
            value = "null";
        }
        for (Pair<Boolean, Pattern> val : listOfPairValue)
        {
            if (val.getSecond().matcher(value).matches())
            {
                return val.getFirst();
            }
        }
        return true;
    }
}
