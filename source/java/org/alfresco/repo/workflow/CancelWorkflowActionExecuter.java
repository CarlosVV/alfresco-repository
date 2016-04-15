package org.alfresco.repo.workflow;

import java.util.List;

import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author dward
 */
public class CancelWorkflowActionExecuter extends ActionExecuterAbstractBase
{
    protected static Log log = LogFactory.getLog(CancelWorkflowActionExecuter.class);

    public static String NAME = "cancel-workflow";
    
    public static final String PARAM_WORKFLOW_ID_LIST = "workflow-id-list";   // list of workflow IDs
    
    private WorkflowService workflowService;
    
    /**
     * @param workflowService the workflowService to set
     */
    public void setWorkflowService(WorkflowService workflowService)
    {
        this.workflowService = workflowService;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.action.executer.ActionExecuterAbstractBase#executeImpl(org.alfresco.service.cmr.action.Action, org.alfresco.service.cmr.repository.NodeRef)
     */
    @Override
    protected void executeImpl(Action action, NodeRef actionedUponNodeRef)
    {
        @SuppressWarnings("unchecked")
        List<String> workflowIds = (List<String>) action.getParameterValue(PARAM_WORKFLOW_ID_LIST);
        
        if (log.isTraceEnabled()) { log.trace("Cancelling " + (workflowIds == null ? 0 : workflowIds.size()) + " workflows by ID."); }
        
        if (workflowIds != null && !workflowIds.isEmpty())
        {
            this.workflowService.cancelWorkflows(workflowIds);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.action.ParameterizedItemAbstractBase#addParameterDefinitions(java.util.List)
     */
    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList)
    {
        paramList.add(
                new ParameterDefinitionImpl(PARAM_WORKFLOW_ID_LIST,
                                            DataTypeDefinition.ANY,
                                            false,
                                            getParamDisplayLabel(PARAM_WORKFLOW_ID_LIST)));
    }

}
