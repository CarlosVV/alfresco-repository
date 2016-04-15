package org.alfresco.util.test.junitrules;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.test.testusers.TestUserComponent;
import org.springframework.context.ApplicationContext;

/**
 * This class is an abstract base class for JUnit rules which manage the lifecycle of <code>cm:person</code>
 * nodes and authentication details for the transient users often required within test code.
 * 
 * @author Neil Mc Erlean
 * @since Odin
 */
public abstract class AbstractPersonRule extends AbstractRule
{
    /**
     * Constructs a person rule with the specified spring context, which will be necessary
     * to actually create and delete the users.
     * 
     * @param appContext the spring app context (needed to get at Alfresco services).
     */
    public AbstractPersonRule(ApplicationContext appContext)
    {
    	super(appContext);
    }
    
    /**
     * Constructs a person rule with a reference to an {@link ApplicationContextInit rule}. This other rule will
     * be used to access the application context and from there the necessary services for the creation and deletion of users.
     * 
     * @param appContextRule a rule which can provide the spring application context.
     */
    public AbstractPersonRule(ApplicationContextInit appContextRule)
    {
    	super(appContextRule);
    }
    
    /**
     * This method creates a user with the specified username.
     * If an authentication for this username does not exist, it is created.
     * If a cm:person for this username does not exist, it is created.
     * This method does not handle transactions.
     * 
     * @param userName the username of the new user.
     * @return  the NodeRef of the created cm:person node.
     */
    protected NodeRef createPerson(final String userName)
    {
        // Get the spring context
        final ApplicationContext ctxt = getApplicationContext();
        
        // Extract required service beans
        final TestUserComponent testUserComponent = (TestUserComponent) ctxt.getBean("testUserComponent");
        
        return testUserComponent.createTestUser(userName);
    }
    
    /**
     * This method deletes the specified user's person and authentication details if they are
     * present in the system.
     * This method does not handle transactions.
     * 
     * @param userName the username of the user to be deleted.
     */
    protected void deletePerson(final String userName)
    {
        // Get the spring context
        final ApplicationContext ctxt = getApplicationContext();
        
        // Extract required service beans
        final TestUserComponent testUserComponent = (TestUserComponent) ctxt.getBean("testUserComponent");
        
        testUserComponent.deleteTestUser(userName);
    }
}
