package org.alfresco.repo.action;

import java.util.Set;

import org.alfresco.repo.action.evaluator.ActionConditionEvaluator;
import org.alfresco.repo.action.executer.ActionExecuter;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterConstraint;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;

/**
 * Runtime action service.  This interface contains methods useful for integration with the action 
 * service at a lower level.
 * 
 * @author Roy Wetherall
 */
public interface RuntimeActionService
{
    /**
     * Post commit method
     */
    void postCommit();
    
    /**
     * Register an action condition evaluator
     * 
     * @param actionConditionEvaluator  action condition evaluator
     */
    void registerActionConditionEvaluator(ActionConditionEvaluator actionConditionEvaluator);
    
    /**
     * Register an action executer
     * 
     * @param actionExecuter    action executer
     */
    void registerActionExecuter(ActionExecuter actionExecuter);
    
    /**
     * Register parameter constraint
     *     
     * @param parameterConstraint  parameter constraint
     */
    void registerParameterConstraint(ParameterConstraint parameterConstraint);
    
    /**
     * Create a new action based on an action node reference
     * 
     * @param actionNodeRef    action node reference
     * @return Action          action object
     */
    Action createAction(NodeRef actionNodeRef);
    
    /**
     * Create a action node reference
     * 
     * @param action            action object
     * @param parentNodeRef     parent node reference
     * @param assocTypeName     association type name
     * @param assocName         association name
     * @return NodeRef          created node reference
     */
    NodeRef createActionNodeRef(Action action, NodeRef parentNodeRef, QName assocTypeName, QName assocName);
    
    /**
     * Save action, used internally to store the details of an action on the aciton node.
     * 
     * @param actionNodeRef    the action node reference
     * @param action        the action 
     */
    void saveActionImpl(NodeRef actionNodeRef, Action action);
    
    /**
     * Perform low-level action execution
     */
    public void executeActionImpl(
            Action action, 
            NodeRef actionedUponNodeRef, 
            boolean checkConditions, 
            boolean executedAsynchronously,
            Set<String> actionChain);
    
    /**
     * Execute an action directly
     * 
     * @param action                the action 
     * @param actionedUponNodeRef   the actioned upon node reference
     */
    public void directActionExecution(Action action, NodeRef actionedUponNodeRef);
        
    /**
     * Optional logging of errors callback for the action executer
     * for the cases when the error might be ignored 
     * or shown in a different manner for the action
     * @param action the action
     * @param logger the logger
     * @param t the exception thrown
     * @param message the proposed message that will be logged
     * @return true if it was handled, false for default handling
     */
    public boolean onLogException(Action action, Log logger, Throwable t, String message);
}
